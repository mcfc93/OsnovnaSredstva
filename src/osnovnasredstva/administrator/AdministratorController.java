package osnovnasredstva.administrator;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author mcfc93
 */
public class AdministratorController implements Initializable {
    
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private AnchorPane dataAnchorPane;

    @FXML
    private Label informacijeLabel;

    @FXML
    private Button odjavaButton;
    
    @FXML
    private ToggleGroup toggleGroup;
	
    @FXML
    private ToggleButton xButton;

    @FXML
    private ToggleButton yButton;

    @FXML
    private ToggleButton zButton;

    @FXML
    private ToggleButton wButton;

    private double xOffset=0;
    private double yOffset=0;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //DragAndDrop
        anchorPane.setOnMousePressed(event -> {
            Stage stage=((Stage)((Node)event.getSource()).getScene().getWindow());
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });

        anchorPane.setOnMouseDragged(event -> {
            Stage stage=((Stage)((Node)event.getSource()).getScene().getWindow());
            if(!stage.isMaximized()) {
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
                stage.setOpacity(0.8);
            }
        });

        anchorPane.setOnMouseReleased(event -> {
            Stage stage=((Stage)((Node)event.getSource()).getScene().getWindow());
            stage.setOpacity(1.0);
        });

        //toggleGroup
        toggleGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            if (newValue == null) {
                oldValue.setSelected(true);
            }
        });
        
        x(null);
    }    
    
    @FXML
    void close(MouseEvent event) {
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    void minimize(MouseEvent event) {
        Stage stage=((Stage)((Node)event.getSource()).getScene().getWindow());
        stage.setIconified(true);
    }

    @FXML
    void maximize(MouseEvent event) {
        Stage stage=((Stage)((Node)event.getSource()).getScene().getWindow());
        if(!stage.isMaximized()) {
                stage.setMaximized(true);
        } else {
                stage.setMaximized(false);
        }
    }
    
    @FXML
    void doubleClick(MouseEvent event) {
        if(event.getClickCount() > 1) {
            maximize(event);
        }
    }
    
    @FXML
    void x(ActionEvent event) {
    	System.out.println("X");
    }

    @FXML
    void y(ActionEvent event) {
    	System.out.println("Y");
    }

    @FXML
    void z(ActionEvent event) {
    	System.out.println("Z");
        /*
    	try {
            Parent root = FXMLLoader.load(getClass().getResource(".fxml"));
            AnchorPane.setTopAnchor(root,0.0);
            AnchorPane.setBottomAnchor(root,0.0);
            AnchorPane.setLeftAnchor(root,0.0);
            AnchorPane.setRightAnchor(root,0.0);
            dataAnchorPane.getChildren().removeAll();
            dataAnchorPane.getChildren().setAll(root);
        } catch(Exception e) {
            e.printStackTrace();
        }
        */
    }
    
    @FXML
    void odjava(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/prijava/PrijavaView.fxml"));

            Scene scene = new Scene(root);
            //scene.getStylesheets().add(getClass().getResource("osnovnasredstva.css").toExternalForm());
            
            Stage stage=new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
            
            ((Stage)anchorPane.getScene().getWindow()).close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
