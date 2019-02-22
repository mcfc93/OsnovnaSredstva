package osnovnasredstva.administrator;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DateCell;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import osnovnasredstva.DAO.OsnovnoSredstvoDAO;
import osnovnasredstva.DAO.OsobaDAO;
import osnovnasredstva.DAO.ProstorijaDAO;
import osnovnasredstva.DAO.VrstaOSDAO;
import osnovnasredstva.DAO.ZgradaDAO;
import osnovnasredstva.DTO.OsnovnoSredstvo;
import osnovnasredstva.DTO.Osoba;
import osnovnasredstva.DTO.Prostorija;
import osnovnasredstva.DTO.VrstaOS;
import osnovnasredstva.prijava.PrijavaController;
import osnovnasredstva.util.NotFoundException;
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
    private JFXComboBox<Prostorija> lokacijaComboBox;
    
    Prostorija temp;
    Osoba tmp;
    
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
        
        vrstaComboBox.setCellFactory(param -> {
            ListCell<VrstaOS> cell = new ListCell<VrstaOS>() {
                @Override
                protected void updateItem(VrstaOS item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setGraphic(null);
                    } else {
                        setText(item.getNaziv());
                    }
                }
            };
            return cell;
        });
        
        vrstaComboBox.setConverter(new StringConverter<VrstaOS>() {
            @Override
            public String toString(VrstaOS object) {
                return object.getNaziv();
            }
            @Override
            public VrstaOS fromString(String string) {
                return vrstaComboBox.getItems().stream().filter(v -> v.getNaziv().equals(string)).findFirst().orElse(null);
            }
        });
        
        osobaComboBox.setCellFactory(param -> {
            ListCell<Osoba> cell = new ListCell<Osoba>() {
                @Override
                protected void updateItem(Osoba item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setGraphic(null);
                    } else {
                        setText(item.getIme() + " " + item.getPrezime());
                    }
                    
                }
            };
            return cell;
        });
        
        osobaComboBox.setConverter(new StringConverter<Osoba>() {
            @Override
            public String toString(Osoba object) {
                return object.getIme() + " " + object.getPrezime();
            }
            @Override
            public Osoba fromString(String string) {
                return osobaComboBox.getItems().stream().filter(o -> (o.getIme() + " " + o.getPrezime()).equals(string)).findFirst().orElse(null);
            }
        });
        
        lokacijaComboBox.setCellFactory(param -> {
            ListCell<Prostorija> cell = new ListCell<Prostorija>() {
                @Override
                protected void updateItem(Prostorija item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setGraphic(null);
                    } else {
                        setText(item.getNaziv() + " (" + ZgradaDAO.getZgradeList().stream().filter(z -> z.getId() == item.getIdZgrade()).findFirst().get().getNaziv() + ")");
                    }
                    
                }
            };
            return cell;
        });
        
        lokacijaComboBox.setConverter(new StringConverter<Prostorija>() {
            @Override
            public String toString(Prostorija object) {
                return object.getNaziv() + " (" + ZgradaDAO.getZgradeList().stream().filter(z -> z.getId() == object.getIdZgrade()).findFirst().get().getNaziv() + ")";
            }
            @Override
            public Prostorija fromString(String string) {
                return lokacijaComboBox.getItems().stream().filter(p -> p.getNaziv().equals(string)).findFirst().orElse(null);
            }
        });
        
        vrstaComboBox.getItems().setAll(VrstaOSDAO.getVrsteOSList());
        osobaComboBox.getItems().setAll(OsobaDAO.getOsobeList());
        lokacijaComboBox.getItems().setAll(ProstorijaDAO.getProstorijeList());
        
        if(izmjena) {
            invertarniBrojTextField.setText(odabranoOS.getInventarniBroj());
            nazivTextField.setText(odabranoOS.getNaziv());
            opisTextArea.setText(odabranoOS.getOpis());
            vrstaComboBox.getSelectionModel().select(vrstaComboBox.getItems().stream().filter(vrsta -> vrsta.getId() == odabranoOS.getIdVrste()).findFirst().orElse(null));
            datumNabavkeDatePicker.setValue(odabranoOS.getDatumNabavke().toLocalDateTime().toLocalDate());
            nabavnaVrijednostTextField.setText(odabranoOS.getNabavnaVrijednost().toString());
            stopaAmortizacijeTextField.setText(String.valueOf(odabranoOS.getStopaAmortizacije()));
            osobaComboBox.getSelectionModel().select(osobaComboBox.getItems().stream().filter(osoba -> osoba.getId() == odabranoOS.getIdOsobe()).findFirst().orElse(null));
            lokacijaComboBox.getSelectionModel().select(lokacijaComboBox.getItems().stream().filter(lokacija -> lokacija.getId() == odabranoOS.getIdLokacije()).findFirst().orElse(null));
        }
            tmp = osobaComboBox.getValue();
            temp = lokacijaComboBox.getValue();
            
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
                odabranoOS.setInventarniBroj(invertarniBrojTextField.getText());
                odabranoOS.setNaziv(nazivTextField.getText());
                odabranoOS.setOpis(opisTextArea.getText().trim());
                odabranoOS.setDatumNabavke(Timestamp.valueOf(LocalDateTime.of(datumNabavkeDatePicker.getValue(), LocalTime.now())));
                odabranoOS.setNabavnaVrijednost(BigDecimal.valueOf(Double.parseDouble(nabavnaVrijednostTextField.getText())));
                odabranoOS.setStopaAmortizacije(Integer.parseInt(stopaAmortizacijeTextField.getText()));
                odabranoOS.setIdLokacije(lokacijaComboBox.getSelectionModel().getSelectedItem().getId());
                odabranoOS.setIdOsobe(osobaComboBox.getSelectionModel().getSelectedItem().getId());
                odabranoOS.setIdVrste(vrstaComboBox.getSelectionModel().getSelectedItem().getId());
                if(!tmp.equals(osobaComboBox.getValue()) || !temp.equals(lokacijaComboBox.getValue())){
                    
                }
                try {
                    osnovnoSredstvoDAO.save(PrijavaController.konekcija, odabranoOS);
                    Platform.runLater(() -> Util.getNotifications("Obavještenje", "Osnovno sredstvo izmjenjeno.", "Information").show());
                } catch (SQLException | NotFoundException e) {
                    Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                    Util.showBugAlert();
                }
            } else {
                odabranoOS=new OsnovnoSredstvo(invertarniBrojTextField.getText(), nazivTextField.getText(), opisTextArea.getText().trim(), Timestamp.valueOf(LocalDateTime.of(datumNabavkeDatePicker.getValue(), LocalTime.now())), BigDecimal.valueOf(Double.parseDouble(nabavnaVrijednostTextField.getText())), Integer.parseInt(stopaAmortizacijeTextField.getText()), lokacijaComboBox.getSelectionModel().getSelectedItem().getId(), osobaComboBox.getSelectionModel().getSelectedItem().getId(), vrstaComboBox.getSelectionModel().getSelectedItem().getId());
                try {
                    osnovnoSredstvoDAO.create(PrijavaController.konekcija, odabranoOS);
                    OsnovnaSredstvaController.osnovnaSredstvaList.add(odabranoOS);
                    Platform.runLater(() -> Util.getNotifications("Obavještenje", "Osnovno sredstvo dodano.", "Information").show());
                } catch (SQLException e) {
                    Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                    Util.showBugAlert();
                }
            }
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        }
    }
    
}
