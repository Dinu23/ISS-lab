package Controller;

import Domain.Bug;
import Domain.Developer;
import Services.IObserver;
import Services.IServices;
import Services.ServiceException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
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


    public DeveloperController() throws RemoteException {
    }

    public void setCurrentDeveloper(Developer currentDeveloper) {
        this.currentDeveloper = currentDeveloper;
    }

    public void setService(IServices service) {
        this.service = service;
    }

    @FXML
    public void initialize(){
        nameDevField.setText(currentDeveloper.getName());
        unresolvedBugList.setCellFactory(new UnresolvedBugCellFactory()) ;
        generateUnresolvedBugList();
    }

    public void generateUnresolvedBugList() {
        try {

            List<Bug> list = Stream.concat(
                    StreamSupport.stream(service.findAllUnresolvedBugs().spliterator(), false),
                    StreamSupport.stream(service.findAllNewBugs().spliterator(), false)
                    ).collect(Collectors.toList());

            if(list != null){
                unresolvedBugList.getItems().addAll(list);
            }
        }
        catch (ServiceException ex){

        }


    }

    @Override
    public void newBug(Bug bug) throws RemoteException {
        Platform.runLater(()->{ unresolvedBugList.getItems().add(bug);});
    }

    public class UnresolvedBugCellFactory implements Callback<ListView<Bug>, ListCell<Bug>> {

        @Override
        public ListCell<Bug> call(ListView<Bug> param) {
            return new BugCell();
        }
    }
    public class BugCell extends ListCell<Bug> {

        @FXML
        private Label bugNameLabel;

        @FXML
        private Label bugImportanceLabel;

        @FXML
        private Button bugDetailsButton;

        @FXML
        private Button bugAcceptButton;

        public BugCell() {
            loadFXML();
        }

        private void loadFXML() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("UnresolvedCell.fxml"));
                loader.setController(this);
                loader.setRoot(this);
                loader.load();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void updateItem(Bug item, boolean empty) {
            super.updateItem(item, empty);

            if(empty || item == null) {
                setText(null);
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
            else {
                bugNameLabel.setText(item.getName());
                bugImportanceLabel.setText(item.getBugImportance().toString());
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        }
    }
}
