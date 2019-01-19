package osnovnasredstva;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import osnovnasredstva.util.Util;

/**
 *
 * @author mcfc93
 */
public class OsnovnaSredstva extends Application {    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("prijava/PrijavaView.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("osnovnasredstva.css").toExternalForm());
        
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
        
        Util.getNotifications("Warning", "Warning", "Warning").show();
        Util.getNotifications("Information", "Information", "Information").show();
        Util.getNotifications("Confirmation", "Confirmation", "Confirmation").show();
        Util.getNotifications("Error", "Error", "Error").show();
        Util.getNotifications("Default", "Default", "").show();
        
        Util.showBugAlert();
    }
    
    @Override
	public void stop() {
            System.out.println("CLOSE");
            Util.fileHandler.close();
	}

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
