package osnovnasredstva.administrator;

import com.jfoenix.controls.JFXButton;
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

    private static VrstaOSDAO vrstaDAO = new VrstaOSDAO();
    @FXML
    private AnchorPane menuLine;

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
    private JFXTextField opisTextField;

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
        
        nazivTextField.getValidators().addAll(Util.requiredFieldValidator(nazivTextField), Util.lengthValidator(nazivTextField, 255));
        opisTextField.getValidators().addAll(Util.requiredFieldValidator(opisTextField), Util.lengthValidator(opisTextField, 1024));
    }    
    
    @FXML
    void close(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY)) {
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        }
    }
    
    @FXML
    void sacuvaj(ActionEvent event) {
        
        if(nazivTextField.validate() & opisTextField.validate()){
            try {
                vrstaDAO.create(PrijavaController.konekcija, new VrstaOS(nazivTextField.getText().trim(), opisTextField.getText().trim()));
                Platform.runLater(() -> Util.getNotifications("Obavještenje", "Vrsta osnovnog sredstva dodana.", "Information"));
            }catch (SQLException e) {
                Util.LOGGER.log(Level.SEVERE, e.toString(), e);
            }
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        }
    }
}
