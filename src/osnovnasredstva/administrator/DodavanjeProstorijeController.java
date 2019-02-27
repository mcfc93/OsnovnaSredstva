package osnovnasredstva.administrator;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.base.ValidatorBase;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import osnovnasredstva.DAO.ProstorijaDAO;
import osnovnasredstva.DTO.Prostorija;
import osnovnasredstva.DTO.Zgrada;
import osnovnasredstva.prijava.PrijavaController;
import osnovnasredstva.util.NotFoundException;
import osnovnasredstva.util.Util;

public class DodavanjeProstorijeController implements Initializable {
    
    public static Prostorija odabranaProstorija;
    public static boolean izmjena=false;
    
    private static ProstorijaDAO prostorijaDAO = new ProstorijaDAO();
    
    @FXML
    private AnchorPane menuLine;
    
    @FXML
    private Label naslovLabel;

    @FXML
    private Button closeButton;

    @FXML
    private JFXTextField sifraTextField;

    @FXML
    private JFXTextField nazivTextField;

    @FXML
    private JFXComboBox<Zgrada> zgradaComboBox;

    @FXML
    private JFXTextArea opisTextArea;

    @FXML
    private JFXButton sacuvajButton;

    @FXML
    private JFXButton nazadButton;
    
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
        
        nazivTextField.getValidators().addAll(Util.requiredFieldValidator(nazivTextField), Util.nazivValidator(nazivTextField), Util.lengthValidator(nazivTextField, 255));
        sifraTextField.getValidators().addAll(Util.requiredFieldValidator(sifraTextField), postojiProstorijaUZgradiValidator(sifraTextField, zgradaComboBox), Util.sifraValidator(sifraTextField), Util.lengthValidator(sifraTextField, 255));
        zgradaComboBox.getValidators().addAll(Util.requiredFieldValidator(zgradaComboBox));
        opisTextArea.getValidators().addAll(Util.lengthValidator(opisTextArea, 1024));
        
        opisTextArea.focusedProperty().addListener((observable, oldValue, newValue)->{
            if(!newValue) {
                opisTextArea.validate();
            }
        });
        opisTextArea.textProperty().addListener((observable, oldValue, newValue)->{
            opisTextArea.validate();
        });

        zgradaComboBox.setCellFactory(param -> {
            ListCell<Zgrada> cell = new ListCell<Zgrada>() {
                @Override
                protected void updateItem(Zgrada item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setGraphic(null);
                    } else {
                        setText(item.getNaziv() + " (" + item.getSifra() + ")");
                    }
                    
                }
            };
            return cell;
        });
        
        zgradaComboBox.setConverter(new StringConverter<Zgrada>() {
            @Override
            public String toString(Zgrada object) {
                return object.getNaziv();
            }
            @Override
            public Zgrada fromString(String string) {
                return zgradaComboBox.getItems().stream().filter(z -> z.getNaziv().equals(string)).findFirst().orElse(null);
            }
        });
        
        zgradaComboBox.setVisibleRowCount(5);
        zgradaComboBox.getItems().setAll(LokacijeController.zgradeList);
        
        naslovLabel.setText("Dodavanaje prostorije");
        if(izmjena) {
            naslovLabel.setText("Izmjena prostorije");
            sifraTextField.setText(odabranaProstorija.getSifra());
            nazivTextField.setText(odabranaProstorija.getNaziv());
            opisTextArea.setText(odabranaProstorija.getOpis());
            zgradaComboBox.getSelectionModel().select(zgradaComboBox.getItems().stream().filter(zgrada -> zgrada.getId() == odabranaProstorija.getIdZgrade()).findFirst().orElse(null));
        }
    }
    
    public static ValidatorBase postojiProstorijaUZgradiValidator(JFXTextField textField, JFXComboBox<Zgrada> comboBox) {
        ValidatorBase postojiProstorijaUZgradiValidator = new ValidatorBase("Prostorija postoji u zgradi") {
            @Override
            protected void eval() {
                if(!textField.getText().isEmpty() && LokacijeController.prostorijeList.stream().anyMatch(p -> p.getSifra().equalsIgnoreCase(textField.getText()) && comboBox.getSelectionModel().getSelectedItem() != null && p.getIdZgrade() == comboBox.getSelectionModel().getSelectedItem().getId())) {
                    hasErrors.set(true);
                    if(izmjena && odabranaProstorija.getSifra().equalsIgnoreCase(textField.getText()) && odabranaProstorija.getIdZgrade() == comboBox.getSelectionModel().getSelectedItem().getId()) {
                        hasErrors.set(false);
                    }
                } else {
                    hasErrors.set(false);
                }
            }
        };
        postojiProstorijaUZgradiValidator.setIcon(new ImageView());
        
        comboBox.valueProperty().addListener((observable, oldValue, newValue)->{
            if(newValue != null) {
                textField.validate();
            }
        });
        
        return postojiProstorijaUZgradiValidator;
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
                & zgradaComboBox.validate()
                    & opisTextArea.validate()){
            
            if(izmjena) {
                odabranaProstorija.setSifra(sifraTextField.getText());
                odabranaProstorija.setNaziv(nazivTextField.getText());
                odabranaProstorija.setOpis(opisTextArea.getText().trim());
                odabranaProstorija.setIdZgrade(zgradaComboBox.getSelectionModel().getSelectedItem().getId());
                try {
                    prostorijaDAO.save(PrijavaController.konekcija, odabranaProstorija);
                    Platform.runLater(() -> Util.getNotifications("Obavještenje", "Prostorija izmjenjena.", "Information").show());
                } catch (SQLException | NotFoundException e) {
                    Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                    Util.showBugAlert();
                }
            } else {
                odabranaProstorija=new Prostorija(sifraTextField.getText(), nazivTextField.getText(), opisTextArea.getText().trim(), zgradaComboBox.getSelectionModel().getSelectedItem().getId());
                try {
                    prostorijaDAO.create(PrijavaController.konekcija, odabranaProstorija);
                    LokacijeController.prostorijeList.add(odabranaProstorija);
                    Platform.runLater(() -> Util.getNotifications("Obavještenje", "Prostorija dodana.", "Information").show());
                } catch (SQLException e) {
                    Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                    Util.showBugAlert();
                }
            }
            /*
            new Thread() {
                @Override
                public void run() {
                    ProstorijaDAO.loadProstorije();
                }
            }.start();
            */
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        }
    }
    
}
