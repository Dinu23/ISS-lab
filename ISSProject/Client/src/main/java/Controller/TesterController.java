package Controller;

import Domain.*;
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


import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TesterController  extends UnicastRemoteObject implements Serializable, IObserver {
    Tester currentTester;
    IServices service;


    @FXML
    Text nameField;

    @FXML
    TextField nameNewBugField;

    @FXML
    ComboBox importanceNewBugField;

    @FXML
    TextArea descriptionNewBugField;

    @FXML
    ListView listOnTestingBugs;
    ObservableList<Bug> listOnTestingBugsObs = FXCollections.observableArrayList();

    @FXML
    ListView finishListBugs;
    ObservableList<Bug> finishListBugsObs = FXCollections.observableArrayList();

    public TesterController() throws RemoteException {
    }

    public void setCurrentTester(Tester currentTester) {
        this.currentTester = currentTester;
    }

    public void setService(IServices service) {
        this.service = service;
    }


    @FXML
    public void initialize(){
        nameField.setText(currentTester.getName());
        List<BugImportance> levels = new ArrayList<>();
        levels.add(BugImportance.CRITICAL);
        levels.add(BugImportance.HIGH);
        levels.add(BugImportance.MEDIUM);
        levels.add(BugImportance.LOW);
        importanceNewBugField.getItems().addAll(levels);
        listOnTestingBugs.setCellFactory(new OnTestingBugCellFactory());
        finishListBugs.setCellFactory(new FinishedBugCellFactory());
        generateOnTestingBugList();
        generateFinishedBugList();
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
    private void generateOnTestingBugList(){
        List<Bug> list = null;
        try {
            list = service.findAllOnTestingBugs().stream().collect(Collectors.toList());
            if(list == null){
                list = new ArrayList<>();
            }
            listOnTestingBugsObs.addAll(list);
            listOnTestingBugs.getItems().setAll(listOnTestingBugsObs);
        } catch (ServiceException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void addNewBugAction(){
        String bugName = nameNewBugField.getText();
        BugImportance bugImportance = (BugImportance) importanceNewBugField.getSelectionModel().getSelectedItem();
        String bugDescription = descriptionNewBugField.getText();

        if(bugName.length() !=0 && bugDescription.length() !=0){
            try {
                service.addNewBug(bugName, bugImportance, bugDescription);
            }
            catch (ServiceException ex){

            }
            nameNewBugField.clear();
            descriptionNewBugField.clear();
        }
    }

    @Override
    public void newBug(Bug bug) throws RemoteException {

    }

    @Override
    public void bugToOngoing(Bug bug) throws RemoteException {

    }

    @Override
    public void bugToOnTesting(Bug bug) throws RemoteException {
        listOnTestingBugsObs.add(bug);
        listOnTestingBugs.getItems().setAll(listOnTestingBugsObs);
    }

    @Override
    public void bugToUnresolved(Bug bug) throws RemoteException {
        listOnTestingBugsObs.removeIf(x->{
            return x.getID().equals(bug.getID());
        });
        listOnTestingBugs.getItems().setAll(listOnTestingBugsObs);

    }

    @Override
    public void bugToFinished(Bug bug) throws RemoteException {
        listOnTestingBugsObs.removeIf(x->{
            return x.getID().equals(bug.getID());
        });
        listOnTestingBugs.getItems().setAll(listOnTestingBugsObs);
        finishListBugsObs.add(bug);
        finishListBugs.getItems().setAll(finishListBugsObs);
    }

    public class OnTestingBugCellFactory implements Callback<ListView<Bug>, ListCell<Bug>> {

        @Override
        public ListCell<Bug> call(ListView<Bug> param) {
            return new OnTestingBugCell();
        }
    }

    public class OnTestingBugCell extends ListCell<Bug> {

        @FXML
        private Label bugNameLabel;

        @FXML
        private Label bugImportanceLabel;

        @FXML
        private Button bugResendButton;

        @FXML
        private Button bugAcceptButton;

        public OnTestingBugCell() {
            loadFXML();
        }

        private void loadFXML() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("OnTestingCell.fxml"));
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
                bugResendButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            service.resendBug(item);
                        } catch (ServiceException e) {
                            e.printStackTrace();
                        }
                    }
                });
                bugAcceptButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        try {
                            service.finishBug(item);
                        } catch (ServiceException e) {
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
