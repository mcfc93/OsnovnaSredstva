package osnovnasredstva.administrator;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import osnovnasredstva.DAO.VrstaOSDAO;
import osnovnasredstva.DTO.VrstaOS;
import osnovnasredstva.prijava.PrijavaController;
import osnovnasredstva.util.NotFoundException;
import osnovnasredstva.util.Util;

public class DodavanjeVrsteOSController implements Initializable {
    
    public static VrstaOS odabranaVrsta;
    public static boolean izmjena = false;

    private static VrstaOSDAO vrstaOSDAO = new VrstaOSDAO();
    
    public static VrstaOS vrstaOS=null;
    
    @FXML
    private AnchorPane menuLine;
    
    @FXML
    private Label naslovLabel;

    @FXML
    private JFXButton sacuvajButton;
    
    @FXML
    private JFXButton nazadButton;
    
    private double xOffset=0;
    private double yOffset=0;
    @FXML
    private Button closeButton;
    @FXML
    private JFXTextField nazivTextField;
    @FXML
    private JFXTextArea opisTextArea;

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
        
        sacuvajButton.setDefaultButton(true);
        naslovLabel.setText("Dodavanje vrste osnovnog sredstva");
        
        nazivTextField.getValidators().addAll(Util.requiredFieldValidator(nazivTextField), Util.lengthValidator(nazivTextField, 255));
        opisTextArea.getValidators().addAll(Util.lengthValidator(opisTextArea, 1024));
        
        opisTextArea.focusedProperty().addListener((observable, oldValue, newValue)->{
            if(!newValue) {
                opisTextArea.validate();
            }
        });
        opisTextArea.textProperty().addListener((observable, oldValue, newValue)->{
            opisTextArea.validate();
        });
        
        vrstaOS=null;
        
        
        if(izmjena) {
            naslovLabel.setText("Izmjena vrste osnovnog sredstva");
            nazivTextField.setText(odabranaVrsta.getNaziv());
            opisTextArea.setText(odabranaVrsta.getOpis());
        }
    }    
    
    @FXML
    void close(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY)) {
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        }
    }
    
    @FXML
    void sacuvaj(ActionEvent event) {
        if(nazivTextField.validate() & opisTextArea.validate()){
            if(izmjena) {
                odabranaVrsta.setNaziv(nazivTextField.getText());
                odabranaVrsta.setOpis(opisTextArea.getText().trim());
                try {
                    vrstaOSDAO.save(PrijavaController.konekcija, odabranaVrsta);
                    Platform.runLater(() -> Util.getNotifications("Obavještenje", "Vrsta osnovnog sredstva izmjenjena.", "Information").show());
                } catch (SQLException | NotFoundException e) {
                    Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                    Util.showBugAlert();
                }
            } else {
                try {
                    vrstaOS=new VrstaOS(nazivTextField.getText().trim(), opisTextArea.getText().trim());
                    vrstaOSDAO.create(PrijavaController.konekcija, vrstaOS);
                    Platform.runLater(() -> Util.getNotifications("Obavještenje", "Vrsta osnovnog sredstva dodana.", "Information").show());
                } catch (SQLException e) {
                    Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                    Util.showBugAlert();
                }
            }
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        }
    }
}
