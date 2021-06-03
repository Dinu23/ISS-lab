package Controller;

import Domain.Bug;
import Domain.BugVersion;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

public class VersionController {
    Bug bug;
    
    @FXML
    Text bugNameLabel;

    @FXML
    Text bugImportanceLabel;

    @FXML
    ListView listVersions;


    public VersionController() {
    }

    public void setBug(Bug bug) {
        this.bug = bug;
    }


    @FXML
    public void initialize() {
        bugNameLabel.setText(bug.getName());
        bugImportanceLabel.setText(bug.getBugImportance().name());
        generateListVersions();
    }

    public void generateListVersions(){

//        if(bug.get() != null) {
//            this.bug.get().stream().forEach(x -> {
//                listVersions.getItems().add(x.getVersion() + ": " + x.getDescription());
//            });
//        }

    }

}
