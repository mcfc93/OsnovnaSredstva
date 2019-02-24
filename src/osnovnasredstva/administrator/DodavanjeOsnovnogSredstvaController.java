package osnovnasredstva.administrator;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.base.ValidatorBase;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import org.controlsfx.control.MaskerPane;
import osnovnasredstva.DAO.OsnovnoSredstvoDAO;
import osnovnasredstva.DAO.PrelaznicaDAO;
import osnovnasredstva.DTO.OsnovnoSredstvo;
import osnovnasredstva.DTO.Osoba;
import osnovnasredstva.DTO.Prelaznica;
import osnovnasredstva.DTO.Prostorija;
import osnovnasredstva.DTO.VrstaOS;
import osnovnasredstva.prijava.PrijavaController;
import osnovnasredstva.util.NotFoundException;
import osnovnasredstva.util.Util;

public class DodavanjeOsnovnogSredstvaController implements Initializable {
    
    public static OsnovnoSredstvo odabranoOS;
    public static boolean izmjena=false;
    private static OsnovnoSredstvoDAO osnovnoSredstvoDAO=new OsnovnoSredstvoDAO();
    public static Prelaznica prelaznica;
    private static PrelaznicaDAO prelaznicaDAO = new PrelaznicaDAO();
    
    @FXML
    private AnchorPane menuLine;
    
    @FXML
    private Label naslovLabel;

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
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Button closeButton;

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
                //setDisable(empty || item.compareTo(LocalDate.now()) < 0 );
                if (item.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    setTooltip(new Tooltip("Nedelja"));
                    setTextFill(Color.RED);
                }
            }
        });
        datumNabavkeDatePicker.setEditable(false);
        
        invertarniBrojTextField.getValidators().addAll(Util.requiredFieldValidator(invertarniBrojTextField), Util.inventarniBrojValidator(invertarniBrojTextField), postojiInventarniBrojValidator(invertarniBrojTextField), Util.lengthValidator(invertarniBrojTextField, 255));
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
                        setText(item.getNaziv() + " (" + OsnovnaSredstvaController.zgradeList.stream().filter(z -> z.getId() == item.getIdZgrade()).findFirst().get().getNaziv() + ")");
                    }
                    
                }
            };
            return cell;
        });
        
        lokacijaComboBox.setConverter(new StringConverter<Prostorija>() {
            @Override
            public String toString(Prostorija object) {
                return object.getNaziv() + " (" + OsnovnaSredstvaController.zgradeList.stream().filter(z -> z.getId() == object.getIdZgrade()).findFirst().get().getNaziv() + ")";
            }
            @Override
            public Prostorija fromString(String string) {
                return lokacijaComboBox.getItems().stream().filter(p -> p.getNaziv().equals(string)).findFirst().orElse(null);
            }
        });
        /*
        vrstaComboBox.getItems().setAll(VrstaOSDAO.getVrsteOSList());
        osobaComboBox.getItems().setAll(OsobaDAO.getOsobeList());
        lokacijaComboBox.getItems().setAll(ProstorijaDAO.getProstorijeList());
        */
        vrstaComboBox.getItems().setAll(OsnovnaSredstvaController.vrstaOsnovnogSredstvaList);
        osobaComboBox.getItems().setAll(OsobeController.osobeList);
        lokacijaComboBox.getItems().setAll(OsnovnaSredstvaController.prostorijeList);
        
        naslovLabel.setText("Dodavanje osnovnog sredstva");
        if(izmjena) {
            naslovLabel.setText("Izmjena osnovnog sredstva");
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
    
    public static ValidatorBase postojiInventarniBrojValidator(JFXTextField textField) {
        ValidatorBase postojiInventarniBrojValidator = new ValidatorBase("Zauzeto") {
            @Override
            protected void eval() {
                if(!textField.getText().isEmpty() && OsnovnaSredstvaController.osnovnaSredstvaList.stream().anyMatch(k -> k.getInventarniBroj().equalsIgnoreCase(textField.getText()))) {
                    hasErrors.set(true);
                    if(izmjena && odabranoOS.getInventarniBroj().equalsIgnoreCase(textField.getText())) {
                        hasErrors.set(false);
                    }
                } else {
                    hasErrors.set(false);
                }
            }
        };
        postojiInventarniBrojValidator.setIcon(new ImageView());
        return postojiInventarniBrojValidator;
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
                    //prelaznica = new Prelaznica(new Timestamp(System.currentTimeMillis()), "", temp.getId(), lokacijaComboBox.getValue().getId(), tmp.getId(), osobaComboBox.getValue().getId(), odabranoOS.getId());
                    prelaznica = new Prelaznica(new Timestamp((System.currentTimeMillis()/1000) *1000 ), "", temp.getId(), lokacijaComboBox.getValue().getId(), tmp.getId(), osobaComboBox.getValue().getId(), odabranoOS.getId());
                    try {
                        ////////////napravi onaj svoj GUI//////////////
                        Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/DodavanjePrelazniceNapomenaView.fxml"));
                        Scene scene = new Scene(root);
                        Stage stage=new Stage();
                        stage.setScene(scene);
                        stage.setResizable(false);
                        stage.initStyle(StageStyle.UNDECORATED);
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.showAndWait();
                        
                        prelaznicaDAO.create(PrijavaController.konekcija, prelaznica);
                        Platform.runLater(() -> Util.getNotifications("Obavještenje", "Prelaznica kreirana.", "Information").show());
                        generisiPDF(prelaznica);
                    } catch (IOException | SQLException e) {
                        Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                    }
                    
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
    public void generisiPDF(Prelaznica pr){
        MaskerPane progressPane=Util.getMaskerPane(anchorPane);
        String naziv = "PDF/prelaznica/" + "Prelaznica_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(pr.getDatumPrelaska()) + ".pdf";
        
        new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                progressPane.setVisible(true);
                Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
                Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.RED);
                Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
                Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
                try {
                    Document document = new Document(PageSize.A4.rotate());
                    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(naziv));
                    HeaderFooterPageEvent event = new HeaderFooterPageEvent();
                    writer.setPageEvent(event);
                    BaseFont baseFont = null;
                    baseFont = BaseFont.createFont(BaseFont.HELVETICA,BaseFont.CP1257,BaseFont.EMBEDDED);
                    Font font = new Font(baseFont);
                    document.open();
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph("Prelaznica", catFont));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph("Prelaznicu kreirao: " + PrijavaController.korisnik.getKorisnickoIme(), smallBold));
                    document.add(new Paragraph("Datum kreiranja: " + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(pr.getDatumPrelaska()), smallBold));
                    document.add(new Paragraph(" "));      
                    document.add(new Paragraph(" "));
                    
                    OsnovnaSredstvaController.osnovnaSredstvaList.forEach(os ->{
                        if(os.getId() == pr.getIdOsnovnogSredstva()){
                            try {
                                document.add(new Paragraph(new Chunk("Osnovno sredstvo: " + os.getNaziv(), font)));
                            } catch (DocumentException e) {
                                Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                            }
                        }
                    });
                    OsobeController.osobeList.forEach(os ->{
                        if(pr.getIdOsobeSa() == os.getId()){
                            try {
                                document.add(new Paragraph(new Chunk("Prethodno zaduženo kod: " + os.getIme() + " " + os.getPrezime(), font)));
                            } catch (DocumentException e) {
                                Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                            }
                        }
                    });
                    OsobeController.osobeList.forEach(os ->{
                        if(pr.getIdOsobeNa()== os.getId()){
                            try {
                                document.add(new Paragraph(new Chunk("Trenutno zaduženo kod: " + os.getIme() + " " + os.getPrezime(), font)));
                            } catch (DocumentException e) {
                                Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                            }
                        }
                    });
                    
                    LokacijeController.lokacijeList.forEach(lo -> {
                        if(pr.getIdProstorijeIz() == lo.getId()){
                            try {
                                document.add(new Paragraph(new Chunk("Prethodna prostorija: " + lo, font)));
                            } catch (DocumentException e) {
                                Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                            }
                        }
                    });
                    LokacijeController.lokacijeList.forEach(lo -> {
                        if(pr.getIdProstorijeU()== lo.getId()){
                            try {
                                document.add(new Paragraph(new Chunk("Trenutna prostorija: " + lo, font)));
                            } catch (DocumentException e) {
                                Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                            }
                        }
                    });
                    
                    document.close();
                } catch (DocumentException | FileNotFoundException e) {
                    Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                } catch (IOException ex) {
                    Logger.getLogger(DodavanjeOsnovnogSredstvaController.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
            @Override
            protected void succeeded(){
                super.succeeded();
                progressPane.setVisible(false);
               
                if(Desktop.isDesktopSupported()) {
                    new Thread(() -> {
                        try {
                            Desktop.getDesktop().open(new File(naziv));
                        } catch (IOException e) {
                            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                        }
                    }).start();
                }
            }
        }).start();
    }
}
