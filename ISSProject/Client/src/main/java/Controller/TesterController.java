package Controller;

import Domain.Bug;
import Domain.BugImportance;
import Domain.Developer;
import Domain.Tester;
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
    ListView listUnresolvedBugs;

    @FXML
    TextField nameNewBugField;

    @FXML
    ComboBox importanceNewBugField;

    @FXML
    TextArea descriptionNewBugField;



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
}
