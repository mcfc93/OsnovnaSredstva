package osnovnasredstva.administrator;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DateCell;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import osnovnasredstva.DAO.OsnovnoSredstvoDAO;
import osnovnasredstva.DAO.VrstaOSDAO;
import osnovnasredstva.DTO.OsnovnoSredstvo;
import osnovnasredstva.DTO.Osoba;
import osnovnasredstva.DTO.VrstaOS;
import osnovnasredstva.DTO.Zgrada;
import osnovnasredstva.util.Util;

public class DodavanjeOsnovnogSredstvaController implements Initializable {
    
    public static OsnovnoSredstvo odabranoOS;
    public static boolean izmjena=false;
    private static OsnovnoSredstvoDAO osnovnoSredstvoDAO=new OsnovnoSredstvoDAO();
    
    @FXML
    private AnchorPane menuLine;

    @FXML
    private JFXButton sacuvajButton;
    
    @FXML
    private JFXButton nazadButton;
    
    @FXML
    private JFXTextField invertarniBrojTextField;

    @FXML
    private JFXTextField nazivTextField;

    @FXML
    private JFXTextArea opisTextArea;

    @FXML
    private JFXDatePicker datumNabavkeDatePicker;

    @FXML
    private JFXTextField nabavnaVrijednostTextField;

    @FXML
    private JFXTextField stopaAmortizacijeTextField;

    @FXML
    private JFXComboBox<VrstaOS> vrstaComboBox;

    @FXML
    private JFXComboBox<Osoba> osobaComboBox;

    @FXML
    private JFXComboBox<Zgrada> lokacijaComboBox;
    
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
        
        datumNabavkeDatePicker.setValue(LocalDate.now());
		
        datumNabavkeDatePicker.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(empty || item.compareTo(LocalDate.now()) < 0 );
                if (item.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    setTooltip(new Tooltip("Nedelja"));
                    setTextFill(Color.RED);
                }
            }
        });
        datumNabavkeDatePicker.setEditable(false);
        
        invertarniBrojTextField.getValidators().addAll(Util.requiredFieldValidator(invertarniBrojTextField), Util.lengthValidator(invertarniBrojTextField, 255));
        nazivTextField.getValidators().addAll(Util.requiredFieldValidator(nazivTextField), Util.lengthValidator(nazivTextField, 255));
        opisTextArea.getValidators().addAll(Util.requiredFieldValidator(opisTextArea), Util.lengthValidator(opisTextArea, 1024));
        vrstaComboBox.getValidators().addAll(Util.requiredFieldValidator(vrstaComboBox));
        vrstaComboBox.setVisibleRowCount(5);
        datumNabavkeDatePicker.getValidators().addAll(Util.requiredFieldValidator(datumNabavkeDatePicker));
        nabavnaVrijednostTextField.getValidators().addAll(Util.requiredFieldValidator(nabavnaVrijednostTextField), Util.naturalDoubleValidator(nabavnaVrijednostTextField), Util.lengthValidator(nabavnaVrijednostTextField, 10));
        stopaAmortizacijeTextField.getValidators().addAll(Util.requiredFieldValidator(stopaAmortizacijeTextField), Util.naturalNumberValidator(stopaAmortizacijeTextField), Util.lengthValidator(stopaAmortizacijeTextField, 10));
        osobaComboBox.getValidators().addAll(Util.requiredFieldValidator(osobaComboBox));
        osobaComboBox.setVisibleRowCount(5);
        lokacijaComboBox.getValidators().addAll(Util.requiredFieldValidator(lokacijaComboBox));
        lokacijaComboBox.setVisibleRowCount(5);
        
        vrstaComboBox.getItems().setAll(VrstaOSDAO.getVrsteOSList());
        
        if(izmjena) {
            invertarniBrojTextField.setText(odabranoOS.getInventarniBroj());
            nazivTextField.setText(odabranoOS.getNaziv());
            opisTextArea.setText(odabranoOS.getOpis());
            vrstaComboBox.getSelectionModel().select(vrstaComboBox.getItems().stream().filter(vrsta -> vrsta.getId() == odabranoOS.getIdVrste()).findFirst().get());
            datumNabavkeDatePicker.setValue(odabranoOS.getDatumNabavke().toLocalDateTime().toLocalDate());
            nabavnaVrijednostTextField.setText(odabranoOS.getNabavnaVrijednost().toString());
            stopaAmortizacijeTextField.setText(String.valueOf(odabranoOS.getStopaAmortizacije()));
            //osobaComboBox.getSelectionModel().select(osobaComboBox.getItems().stream().filter(osoba -> osoba.getId() == odabranoOS.getIdOsobe()).findFirst().get());
            //lokacijaComboBox.getSelectionModel().select(lokacijaComboBox.getItems().stream().filter(lokacija -> lokacija.getId() == odabranoOS.getIdLokacije()).findFirst().get());
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
        if(invertarniBrojTextField.validate()
            & nazivTextField.validate()
                & opisTextArea.validate()
                    & vrstaComboBox.validate()
                        & datumNabavkeDatePicker.validate()
                            & nabavnaVrijednostTextField.validate()
                                & stopaAmortizacijeTextField.validate()
                                    & osobaComboBox.validate()
                                        & lokacijaComboBox.validate()) {
            if(izmjena) {
                
            } else {
                
            }
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        }
    }
    
}
