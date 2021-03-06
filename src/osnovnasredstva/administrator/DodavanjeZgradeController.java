package osnovnasredstva.administrator;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.base.ValidatorBase;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import osnovnasredstva.DAO.ZgradaDAO;
import osnovnasredstva.DTO.Zgrada;
import osnovnasredstva.prijava.PrijavaController;
import osnovnasredstva.util.NotFoundException;
import osnovnasredstva.util.Util;

public class DodavanjeZgradeController implements Initializable {
    
    public static Zgrada odabranaZgrda;
    public static boolean izmjena = false;
    
    private static ZgradaDAO zgradaDAO = new ZgradaDAO();
    
    public static Zgrada zgrada=null;

    @FXML
    private AnchorPane menuLine;
    
    @FXML
    private Label naslovLabel;

    @FXML
    private Button closeButton;

    @FXML
    private JFXButton sacuvajButton;

    @FXML
    private JFXButton nazadButton;

    @FXML
    private JFXTextField nazivTextField;

    @FXML
    private JFXTextArea opisTextArea;

    @FXML
    private JFXTextField sifraTextField;
    
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
        
        sacuvajButton.setDefaultButton(true);
        naslovLabel.setText("Dodavanje zgrade");
        
        nazivTextField.getValidators().addAll(Util.requiredFieldValidator(nazivTextField), postojiNazivValidator(nazivTextField), Util.nazivValidator(nazivTextField), Util.lengthValidator(nazivTextField, 255));
        sifraTextField.getValidators().addAll(Util.requiredFieldValidator(sifraTextField), postojiZgradaValidator(sifraTextField), Util.sifraValidator(sifraTextField), Util.lengthValidator(sifraTextField, 255));
        opisTextArea.getValidators().addAll(Util.lengthValidator(opisTextArea, 1024));
        
        opisTextArea.focusedProperty().addListener((observable, oldValue, newValue)->{
            if(!newValue) {
                opisTextArea.validate();
            }
        });
        opisTextArea.textProperty().addListener((observable, oldValue, newValue)->{
            opisTextArea.validate();
        });
        
        zgrada=null;
        
        
        if(izmjena) {
            naslovLabel.setText("Izmjena zgrade");
            sifraTextField.setText(odabranaZgrda.getSifra());
            nazivTextField.setText(odabranaZgrda.getNaziv());
            opisTextArea.setText(odabranaZgrda.getOpis());
        }
    }
    
    public static ValidatorBase postojiZgradaValidator(JFXTextField textField) {
        ValidatorBase postojiZgradaValidator = new ValidatorBase("Zauzeto") {
            @Override
            protected void eval() {
                if(!textField.getText().isEmpty() && LokacijeController.zgradeList.stream().anyMatch(z -> z.getSifra().equalsIgnoreCase(textField.getText()))) {
                    hasErrors.set(true);
                    if(izmjena && odabranaZgrda.getSifra().equalsIgnoreCase(textField.getText())) {
                        hasErrors.set(false);
                    }
                } else {
                    hasErrors.set(false);
                }
            }
        };
        postojiZgradaValidator.setIcon(new ImageView());
        return postojiZgradaValidator;
    }
    
    public static ValidatorBase postojiNazivValidator(JFXTextField textField) {
        ValidatorBase postojiNazivValidator = new ValidatorBase("Zauzeto") {
            @Override
            protected void eval() {
                if(!textField.getText().isEmpty() && LokacijeController.zgradeList.stream().anyMatch(z -> z.getNaziv().equalsIgnoreCase(textField.getText()))) {
                    hasErrors.set(true);
                    if(izmjena && odabranaZgrda.getNaziv().equalsIgnoreCase(textField.getText())) {
                        hasErrors.set(false);
                    }
                } else {
                    hasErrors.set(false);
                }
            }
        };
        postojiNazivValidator.setIcon(new ImageView());
        return postojiNazivValidator;
    }
    
    @FXML
    void close(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY)) {
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        }
    }

    @FXML
    void sacuvaj(ActionEvent event) {
        if(sifraTextField.validate() 
            & nazivTextField.validate()
                & opisTextArea.validate()){
            if(izmjena) {
                odabranaZgrda.setSifra(sifraTextField.getText().trim());
                odabranaZgrda.setNaziv(nazivTextField.getText());
                odabranaZgrda.setOpis(opisTextArea.getText().trim());
                try {
                    zgradaDAO.save(PrijavaController.konekcija, odabranaZgrda);
                    Platform.runLater(() -> Util.getNotifications("Obavještenje", "Zgrada izmjenjena.", "Information").show());
                } catch (SQLException | NotFoundException e) {
                    Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                    Util.showBugAlert();
                }
            } else {
                try {
                    zgrada=new Zgrada(sifraTextField.getText(), nazivTextField.getText(), opisTextArea.getText().trim());
                    zgradaDAO.create(PrijavaController.konekcija, zgrada);
                    Platform.runLater(() -> Util.getNotifications("Obavještenje", "Zgrada dodana.", "Information").show());
                } catch (SQLException e) {
                    Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                    Util.showBugAlert();
                }
            }
            /*
            new Thread() {
                @Override
                public void run() {
                    ZgradaDAO.loadZgrade();
                }
            }.start();
            */
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        }
    }
    
}
