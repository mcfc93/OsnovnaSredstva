package osnovnasredstva.prijava;

import com.jfoenix.controls.JFXCheckBox;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.MaskerPane;
import osnovnasredstva.util.Util;

/**
 *
 * @author mcfc93
 */
public class PrijavaViewController implements Initializable {
    
    @FXML
    private AnchorPane anchorPane;
	
    @FXML
    private TextField korisnickoImeTextField;

    @FXML
    private PasswordField lozinkaTextField;
    
    @FXML
    private JFXCheckBox zapamtiMeCheckBox;
    
    @FXML
    private Label greskaTextLabel;
    
    @FXML
    private Label greskaBackgroundLabel;

    @FXML
    private Button prijavaButton;
    
    private double xOffset=0;
    private double yOffset=0;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        greskaTextLabel.setVisible(false);
	greskaBackgroundLabel.setVisible(false);
	zapamtiMeCheckBox.setSelected(true);
	prijavaButton.setDefaultButton(true);
        
        //DragAndDrop
        anchorPane.setOnMousePressed(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                Stage stage=((Stage)((Node)event.getSource()).getScene().getWindow());
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });

        anchorPane.setOnMouseDragged(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                Stage stage=((Stage)((Node)event.getSource()).getScene().getWindow());
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
                stage.setOpacity(0.8);
            }
        });

        anchorPane.setOnMouseReleased(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                Stage stage=((Stage)((Node)event.getSource()).getScene().getWindow());
                stage.setOpacity(1.0);
            }
        });
        
        prijavaButton.disableProperty().bind(
	    korisnickoImeTextField.textProperty().isEmpty().or(lozinkaTextField.textProperty().isEmpty())
	);
        
        korisnickoImeTextField.setText("mcfc93");
        lozinkaTextField.setText("student");
    }
    
    @FXML
    void prijava(ActionEvent event) {
        if("mcfc93".equals(korisnickoImeTextField.getText()) && "student".equals(lozinkaTextField.getText())) {
            MaskerPane progressPane=Util.getMaskerPane(anchorPane);
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    System.out.println(Thread.currentThread());
                    progressPane.setVisible(true);
                    Thread.sleep(1000);
                    return null;
                }
                @Override
                protected void succeeded(){
                    super.succeeded();
                    progressPane.setVisible(false);
                    ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/AdministratorView.fxml"));
                        Scene scene = new Scene(root);
                        scene.getStylesheets().add(getClass().getResource("/osnovnasredstva/osnovnasredstva.css").toExternalForm());
                        Stage stage=new Stage();
                        stage.setScene(scene);
                        stage.setResizable(false);
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.show();
                    } catch(IOException e) {
                        Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                    }
                }
            };
            new Thread(task).start();
        } else {
            greskaTextLabel.setText("Korisničko ime ili lozinka pogrešni!");
            greskaTextLabel.setVisible(true);
            greskaBackgroundLabel.setVisible(true);
            korisnickoImeTextField.clear();
            lozinkaTextField.clear();
            //korisnickoImeTextField.requestFocus();
        }
    }
    
    void odjava(ActionEvent event) {
        ((Stage)anchorPane.getScene().getWindow()).show();
    }

    @FXML
    void sakrijLabelu(MouseEvent event) {
        greskaTextLabel.setVisible(false);
        greskaBackgroundLabel.setVisible(false);
    }

    @FXML
    void close(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY)) {
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        }
    }

    @FXML
    void minimize(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY)) {
            ((Stage)((Node)event.getSource()).getScene().getWindow()).setIconified(true);
        }
    }
    
}
