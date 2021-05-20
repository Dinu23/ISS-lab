package Controller;

import Domain.Developer;
import Domain.Employee;
import Domain.Tester;
import Services.IServices;
import Services.ServiceException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class LoginController  extends UnicastRemoteObject implements Serializable {
    private IServices service;
    private Stage primaryStage;

    @FXML
    TextField userField;

    @FXML
    PasswordField passwordField;

    @FXML
    AnchorPane loginView;

    public LoginController() throws RemoteException {
    }

    public void setService(IServices service) {
        this.service = service;
    }

    @FXML
    public void loginAction(){
        String username = userField.getText();
        String password = passwordField.getText();
        try {
            Employee employee = service.findEmployee(username, password);

            if(employee != null) {
                if (employee instanceof Developer) {
                    DeveloperController developerController = new DeveloperController();
                    developerController.setCurrentDeveloper((Developer) employee);
                    service.addObserver(employee,developerController);
                    developerController.setService(service);
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getClassLoader().getResource("DeveloperPage.fxml"));
                    loader.setController(developerController);
                    Parent root = loader.load();
                    Stage stage = new Stage();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setTitle("Developer Page");
                    stage.show();
                    primaryStage.hide();

                }
                else if (employee instanceof Tester){
                    TesterController testerController = new TesterController();
                    testerController.setCurrentTester((Tester) employee);
                    service.addObserver(employee,testerController);
                    testerController.setService(service);
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getClassLoader().getResource("TesterPage.fxml"));
                    loader.setController(testerController);
                    Parent root = loader.load();
                    Stage stage = new Stage();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setTitle("Tester Page");
                    stage.show();
                    primaryStage.hide();
                }

            }
            else {
                Alert alert=new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login error");
                alert.setContentText("Invalid credentials");
                alert.showAndWait();
            }
            userField.clear();
            passwordField.clear();

        }
        catch (ServiceException | IOException ex){
            ;
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
