package Controller;

import Domain.Bug;
import Domain.Developer;
import Domain.Employee;
import Services.IObserver;
import Services.IServices;
import Services.ServiceException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.Console;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DeveloperController  extends UnicastRemoteObject implements Serializable, IObserver {
    Developer currentDeveloper;
    IServices service;

    @FXML
    Text nameDevField;

    @FXML
    ListView unresolvedBugList;
    ObservableList<Bug> unresolvedBugListObs =  FXCollections.observableArrayList();

    @FXML
    ListView ongoingBugList;
    ObservableList<Bug> ongoingBugListObs = FXCollections.observableArrayList();

    @FXML
    ListView finishListBugs;
    ObservableList<Bug> finishListBugsObs = FXCollections.observableArrayList();

    public DeveloperController() throws RemoteException {
    }

    public void setCurrentDeveloper(Developer currentDeveloper) {
        this.currentDeveloper = currentDeveloper;
    }

    public void setService(IServices service) {
        this.service = service;
    }

    @FXML
    public void initialize() {
        nameDevField.setText(currentDeveloper.getName());
        unresolvedBugList.setCellFactory(new UnresolvedBugCellFactory());
        ongoingBugList.setCellFactory(new OngoingBugCellFactory());
        finishListBugs.setCellFactory(new FinishedBugCellFactory());
        generateUnresolvedBugList();
        generateOngoingBugList();
        generateFinishedBugList();
    }

    public void generateUnresolvedBugList() {
        try {
            List<Bug>list= Stream.concat(
                    StreamSupport.stream(service.findAllUnresolvedBugs().spliterator(), false),
                    StreamSupport.stream(service.findAllNewBugs().spliterator(), false)
            ).collect(Collectors.toList());
            if(list == null){
                list = new ArrayList<>();
            }
            unresolvedBugListObs.addAll(list);
            unresolvedBugList.getItems().setAll(unresolvedBugListObs);

        } catch (ServiceException ex) {

        }


    }

    public void generateOngoingBugList() {
        if (currentDeveloper.getWorkingBugs() != null) {
            ongoingBugListObs.addAll(currentDeveloper.getWorkingBugs().stream().collect(Collectors.toList()));
            ongoingBugList.getItems().setAll(ongoingBugListObs);
        }
    }
    public void generateFinishedBugList(){
        try {
            List<Bug>list= service.findAllFinishedBugs().stream().collect(Collectors.toList());
            if(list == null){
                list = new ArrayList<>();
            }
            finishListBugsObs.addAll(list);
            finishListBugs.getItems().setAll(finishListBugsObs);

        } catch (ServiceException ex) {

        }
    }


    @Override
    public void newBug(Bug bug) throws RemoteException {
        Platform.runLater(() -> {
            unresolvedBugListObs.add(bug);
            unresolvedBugList.getItems().setAll(unresolvedBugListObs);
        });
    }

    @Override
    public void bugToOngoing(Bug bug) throws RemoteException {
        Platform.runLater(() -> {
            System.out.println(bug.getID());
            unresolvedBugListObs.removeIf(x->{
                return x.getID().equals( bug.getID());
            });
            unresolvedBugListObs.forEach(x->System.out.println(x.getID()));
            unresolvedBugList.getItems().setAll(unresolvedBugListObs);
        });
    }

    @Override
    public void bugToOnTesting(Bug bug) throws RemoteException {

    }

    @Override
    public void bugToUnresolved(Bug bug) throws RemoteException {
        Platform.runLater(() -> {
            unresolvedBugListObs.add(bug);
            unresolvedBugList.getItems().setAll(unresolvedBugListObs);
        });
    }
    @Override
    public void bugToFinished(Bug bug) throws RemoteException {
        finishListBugsObs.add(bug);
        finishListBugs.getItems().setAll(finishListBugsObs);
    }


    public class UnresolvedBugCellFactory implements Callback<ListView<Bug>, ListCell<Bug>> {

        @Override
        public ListCell<Bug> call(ListView<Bug> param) {
            return new UnresolvedBugCell();
        }
    }

    public class UnresolvedBugCell extends ListCell<Bug> {

        @FXML
        private Label bugNameLabel;

        @FXML
        private Label bugImportanceLabel;

        @FXML
        private Button bugDetailsButton;

        @FXML
        private Button bugAcceptButton;

        public UnresolvedBugCell() {
            loadFXML();
        }

        private void loadFXML() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("UnresolvedCell.fxml"));
                loader.setController(this);
                loader.setRoot(this);
                loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void updateItem(Bug item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            } else {
                bugNameLabel.setText(item.getName());
                bugImportanceLabel.setText(item.getBugImportance().toString());
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                bugAcceptButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            Bug bug = service.updateBugToOngoing(item);
                            ongoingBugListObs.add(bug);
                            ongoingBugList.getItems().setAll(ongoingBugListObs);
                            Employee employee = service.addBugToList(currentDeveloper, bug);
                            currentDeveloper = (Developer) employee;
                        } catch (ServiceException e) {
                            e.printStackTrace();
                        }

                    }
                });
                bugDetailsButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        try {
                            VersionController versionController = new VersionController();
                            versionController.setBug((Bug)item);
                            FXMLLoader loader = new FXMLLoader();
                            loader.setLocation(getClass().getClassLoader().getResource("VersionsPage.fxml"));
                            loader.setController(versionController);
                            Parent root = loader.load();
                            Stage stage = new Stage();
                            Scene scene = new Scene(root);
                            stage.setScene(scene);
                            stage.setTitle("Developer Page");
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        }
    }

        public class OngoingBugCellFactory implements Callback<ListView<Bug>, ListCell<Bug>> {

            @Override
            public ListCell<Bug> call(ListView<Bug> param) {
                return new OnGoingBugCell();
            }
        }

        public class OnGoingBugCell extends ListCell<Bug> {

            @FXML
            private Label bugNameLabel;

            @FXML
            private Label bugImportanceLabel;

            @FXML
            private Button bugDetailsButton;

            @FXML
            private Button bugSendButton;

            public OnGoingBugCell() {
                loadFXML();
            }

            private void loadFXML() {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("OngoingCell.fxml"));
                    loader.setController(this);
                    loader.setRoot(this);
                    loader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            protected void updateItem(Bug item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                } else {
                    bugNameLabel.setText(item.getName());
                    bugImportanceLabel.setText(item.getBugImportance().toString());
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    bugSendButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            try {
                                Bug bug = service.sendBugForTesting(item);
                                ongoingBugListObs.removeIf(x->{
                                    return x.getID().equals(bug.getID());
                                });
                                ongoingBugList.getItems().setAll(ongoingBugListObs);
                                Employee employee = service.removeBugFromList(currentDeveloper, bug);
                                currentDeveloper = (Developer) employee;
                            } catch (ServiceException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    bugDetailsButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {

                            try {
                                VersionController versionController = new VersionController();
                                versionController.setBug((Bug)item);
                                FXMLLoader loader = new FXMLLoader();
                                loader.setLocation(getClass().getClassLoader().getResource("VersionsPage.fxml"));
                                loader.setController(versionController);
                                Parent root = loader.load();
                                Stage stage = new Stage();
                                Scene scene = new Scene(root);
                                stage.setScene(scene);
                                stage.setTitle("Developer Page");
                                stage.show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }
                    });
                }
            }
        }
    public class FinishedBugCellFactory implements Callback<ListView<Bug>, ListCell<Bug>> {

        @Override
        public ListCell<Bug> call(ListView<Bug> param) {
            return new FinishedBugCell();
        }
    }

    public class FinishedBugCell extends ListCell<Bug> {

        @FXML
        private Label bugNameLabel;

        @FXML
        private Label bugImportanceLabel;

        @FXML
        private Button bugDetailsButton;


        public FinishedBugCell() {
            loadFXML();
        }

        private void loadFXML() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ResolvedCell.fxml"));
                loader.setController(this);
                loader.setRoot(this);
                loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void updateItem(Bug item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            } else {
                bugNameLabel.setText(item.getName());
                bugImportanceLabel.setText(item.getBugImportance().toString());
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                bugDetailsButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {

                        try {
                            VersionController versionController = new VersionController();
                            versionController.setBug((Bug)item);
                            FXMLLoader loader = new FXMLLoader();
                            loader.setLocation(getClass().getClassLoader().getResource("VersionsPage.fxml"));
                            loader.setController(versionController);
                            Parent root = loader.load();
                            Stage stage = new Stage();
                            Scene scene = new Scene(root);
                            stage.setScene(scene);
                            stage.setTitle("Developer Page");
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        }
    }
}
