package osnovnasredstva.administrator;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author mcfc93
 */
public class DodavanjeOsobeController implements Initializable {

    @FXML
    private AnchorPane menuLine;

    @FXML
    private JFXButton sacuvajButon;
    
    @FXML
    private JFXButton nazadButton;
    
    @FXML
    private JFXTextField imeTextField;

    @FXML
    private JFXTextField prezimeTextField;

    @FXML
    private JFXTextField jmbgTextField;

    @FXML
    private JFXTextField titulaTextField;

    @FXML
    private JFXTextField zaposlenjeTextField;

    @FXML
    private JFXTextField brojTelefonaTextField;

    @FXML
    private JFXTextField emailTextField;

    @FXML
    private JFXTextField adresaTextField;
    
    private double xOffset=0;
    private double yOffset=0;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //DragAndDrop
        menuLine.setOnMousePressed(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                Stage stage=((Stage)((Node)event.getSource()).getScene().getWindow());
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });

        menuLine.setOnMouseDragged(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                Stage stage=((Stage)((Node)event.getSource()).getScene().getWindow());
                if(!stage.isMaximized()) {
                    stage.setX(event.getScreenX() + xOffset);
                    stage.setY(event.getScreenY() + yOffset);
                    stage.setOpacity(0.8);
                }
            }
        });

        menuLine.setOnMouseReleased(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                Stage stage=((Stage)((Node)event.getSource()).getScene().getWindow());
                stage.setOpacity(1.0);
            }
        });
        
        nazadButton.setDefaultButton(true);
    }    
    
    @FXML
    void close(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY)) {
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        }
    }
    
    @FXML
    void sacuvaj(ActionEvent event) {
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }
    
}
