import Controller.LoginController;
import Services.IServices;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StartClient extends Application {
    private static int defaultPort=5555;
    private static String defaultServer="localhost";
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            ApplicationContext factory = new ClassPathXmlApplicationContext("classpath:spring-client.xml");

            IServices server=(IServices)factory.getBean("service");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginPage.fxml"));

            Parent root = loader.load();
            LoginController ctrl = loader.getController();
            ctrl.setService(server);
            ctrl.setPrimaryStage(primaryStage);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login Page");
            primaryStage.show();

        }catch(Exception e){
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error ");
            alert.setContentText("Error while starting app "+e);
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
