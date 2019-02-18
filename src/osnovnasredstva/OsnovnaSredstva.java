package osnovnasredstva;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import osnovnasredstva.util.Util;

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
    }
    
    @Override
    public void stop() {
        System.out.println("CLOSE");
        Util.fileHandler.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
