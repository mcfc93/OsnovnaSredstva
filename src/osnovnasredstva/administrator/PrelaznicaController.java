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
import com.jfoenix.controls.JFXToggleButton;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.MaskerPane;
import osnovnasredstva.DAO.OsnovnoSredstvoDAO;
import osnovnasredstva.DAO.OsobaDAO;
import osnovnasredstva.DAO.PrelaznicaDAO;
import osnovnasredstva.DAO.ProstorijaDAO;
import osnovnasredstva.DAO.ZgradaDAO;
import osnovnasredstva.DTO.OsnovnoSredstvo;
import osnovnasredstva.DTO.Osoba;
import osnovnasredstva.DTO.Prelaznica;
import osnovnasredstva.DTO.Prostorija;
import osnovnasredstva.DTO.Zgrada;
import osnovnasredstva.prijava.PrijavaController;
import osnovnasredstva.util.Util;

public class PrelaznicaController implements Initializable {
    
    private static PrelaznicaDAO prelaznicaDAO = new PrelaznicaDAO();
    private static OsobaDAO osobaDAO = new OsobaDAO();
    private static ProstorijaDAO prostorijaDAO = new ProstorijaDAO();
    private static ZgradaDAO zgradaDAO = new ZgradaDAO();
    private static OsnovnoSredstvoDAO osnovnoSredstvoDAO = new OsnovnoSredstvoDAO();

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TableView<Prelaznica> prelazniceTableView;

    @FXML
    private JFXButton pdfButton;

    @FXML
    private TextField traziTextField;

    @FXML
    private ImageView clearImageView;

    @FXML
    private JFXToggleButton postaniNadzornikToggleButton;
    @FXML
    private TableColumn<?, ?> nazivColumn;
    @FXML
    private TableColumn<?, ?> datumColumn;
    @FXML
    private TableColumn<Prelaznica, Prelaznica> otvoriColumn;
    
    public static ObservableList<OsnovnoSredstvo> osnovnaSredstvaList = FXCollections.observableArrayList();
    public static ObservableList<Osoba> osobeList = FXCollections.observableArrayList();
    public static ObservableList<Prostorija> prostorijeList = FXCollections.observableArrayList();
    public static ObservableList<Zgrada> zgradeList = FXCollections.observableArrayList();
    
    public static ObservableList<Prelaznica> prelazniceList = FXCollections.observableArrayList();
    private static FilteredList<Prelaznica> filteredList;
    SortedList<Prelaznica> sortedList;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clearImageView.setVisible(false);
		
        traziTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            filteredList.setPredicate(prelaznica -> prelaznica.getNaziv().toLowerCase().contains(newValue.toLowerCase()));
            if(!newValue.isEmpty()) {
                clearImageView.setVisible(true);
            } else {
                clearImageView.setVisible(false);
            }
        });
        
        osnovnaSredstvaList.clear();
        prelazniceList.clear();
        osobeList.clear();
        prostorijeList.clear();
        zgradeList.clear();
        filteredList = new FilteredList<>(prelazniceList);
        sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(prelazniceTableView.comparatorProperty());
        
        MaskerPane progressPane=Util.getMaskerPane(anchorPane);
        new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                progressPane.setVisible(true);
                try {
                    prelazniceList.addAll(prelaznicaDAO.loadAll(PrijavaController.konekcija));
                    osnovnaSredstvaList.addAll(osnovnoSredstvoDAO.loadAll2(PrijavaController.konekcija));
                    osobeList.addAll(osobaDAO.loadAll2(PrijavaController.konekcija));
                    prostorijeList.addAll(prostorijaDAO.loadAll2(PrijavaController.konekcija));
                    zgradeList.addAll(zgradaDAO.loadAll2(PrijavaController.konekcija));
                } catch (SQLException e) {
                    Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                }
                return null;
            }
            @Override
            protected void succeeded(){
                super.succeeded();
                progressPane.setVisible(false);
                Platform.runLater(() -> {
                    prelazniceTableView.refresh();
                });
            }
        }).start();
        
        if(PrijavaController.korisnik.getTip() != PrijavaController.korisnik.getPrivilegijaTip()) {
            PrijavaController.korisnik.setPrivilegijaTip(PrijavaController.korisnik.getTip());
        }
        
        prelazniceTableView.setItems(sortedList);
        prelazniceTableView.setPlaceholder(new Label("Nema korisničkih naloga."));
        prelazniceTableView.setFocusTraversable(false);
        
        nazivColumn.setCellValueFactory(new PropertyValueFactory<>("naziv"));
        datumColumn.setCellValueFactory(new PropertyValueFactory<>("datum"));
        
        otvoriColumn.setVisible(true);
        
        otvoriColumn.setCellValueFactory(
            param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );
        
        otvoriColumn.setCellFactory(tableCell -> {
            TableCell<Prelaznica, Prelaznica> cell = new TableCell<Prelaznica, Prelaznica>() {
                private final Button button = new Button("");
                @Override
                protected void updateItem(Prelaznica item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                    	button.getStyleClass().addAll("buttonTable", "buttonTableShow");
                    	button.setTooltip(new Tooltip("Otvori"));
                    	button.getTooltip().setAutoHide(false);
                    	setGraphic(button);
                    	button.setOnMouseClicked(event -> {
                            prelazniceTableView.getSelectionModel().select(item);
                            otvoriPrelaznicu(item);
                        });
                    } else {
                    	setGraphic(null);
                    }
                }
            };
            return cell;
        });

        Util.preventColumnReordering(prelazniceTableView);
        
        nazivColumn.setMinWidth(100);
        nazivColumn.setMaxWidth(5000);
                
        datumColumn.setMinWidth(100);
        datumColumn.setMaxWidth(3000);
        
        otvoriColumn.setText("");
        otvoriColumn.setMinWidth(35);
        otvoriColumn.setMaxWidth(35);
        otvoriColumn.setResizable(false);
        otvoriColumn.setSortable(false);
        
        prelazniceTableView.setOnMouseClicked( event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                if(event.getClickCount() == 2) {
                    otvoriPrelaznicu(prelazniceTableView.getSelectionModel().getSelectedItem());
                }
            }
        });
    }
    
    private void otvoriPrelaznicu(Prelaznica pr) {
        String naziv = "PDF/prelaznica/" + "Prelaznica_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(pr.getDatumPrelaska()) + ".pdf";
        File file = new File(naziv);
        if(!file.exists()){
            MaskerPane progressPane=Util.getMaskerPane(anchorPane);
        //String naziv = "PDF/prelaznica/" + "Prelaznica_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(pr.getDatumPrelaska()) + ".pdf";
        
        new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                progressPane.setVisible(true);
                Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
                Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.RED);
                Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
                Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
                try {
                    Document document = new Document(PageSize.A4);
                    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(naziv));
                    HeaderFooterPageEvent event = new HeaderFooterPageEvent();
                    writer.setPageEvent(event);
                    BaseFont baseFont = null;
                    baseFont = BaseFont.createFont(BaseFont.HELVETICA,BaseFont.CP1250,BaseFont.EMBEDDED);
                    Font font = new Font(baseFont);
                    document.open();
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph("Prelaznica", catFont));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph("Prelaznicu kreirao: " + PrijavaController.korisnik.getKorisnickoIme(), smallBold));
                    document.add(new Paragraph("Datum i vrijeme kreiranja: " + new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss").format(pr.getDatumPrelaska()), smallBold));
                    document.add(new Paragraph(" "));      
                    document.add(new Paragraph(" "));
                    
                    
                    osnovnaSredstvaList.forEach(os ->{
                        if(os.getId() == pr.getIdOsnovnogSredstva()){
                            try {
                                document.add(new Paragraph(new Chunk("Naziv osnovnog sredstva: " + os.getNaziv(), font)));
                                document.add(new Paragraph(new Chunk("Inventarni broj: " + os.getInventarniBroj(), font)));
                            } catch (DocumentException e) {
                                Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                            }
                        }
                    });
                    osobeList.forEach(os ->{
                        if(pr.getIdOsobeSa() == os.getId()){
                            try {
                                document.add(new Paragraph(new Chunk("Prethodno zaduženo kod: " + os.getIme() + " " + os.getPrezime(), font)));
                            } catch (DocumentException e) {
                                Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                            }
                        }
                    });
                    osobeList.forEach(os ->{
                        if(pr.getIdOsobeNa()== os.getId()){
                            try {
                                document.add(new Paragraph(new Chunk("Trenutno zaduženo kod: " + os.getIme() + " " + os.getPrezime(), font)));
                            } catch (DocumentException e) {
                                Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                            }
                        }
                    });
                    
                    prostorijeList.forEach(lo -> {
                        if(pr.getIdProstorijeIz() == lo.getId()){
                            try {
                                document.add(new Paragraph(new Chunk("Prethodna prostorija: " + lo.getNaziv() + " (" + zgradeList.stream().filter(z -> z.getId() == lo.getIdZgrade()).findFirst().get().getNaziv() + ")", font)));
                            } catch (DocumentException e) {
                                Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                            }
                        }
                    });
                    prostorijeList.forEach(lo -> {
                        if(pr.getIdProstorijeU()== lo.getId()){
                            try {
                                document.add(new Paragraph(new Chunk("Trenutna prostorija: " + lo.getNaziv() + " (" + zgradeList.stream().filter(z -> z.getId() == lo.getIdZgrade()).findFirst().get().getNaziv() + ")", font)));
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
                            Desktop.getDesktop().open(file);
                        } catch (IOException e) {
                            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                        }
                    }).start();
                }
            }
        }).start();
        }
        else{
            if(Desktop.isDesktopSupported()) {
                new Thread(() -> {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (IOException e) {
                        Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                    }
                }).start();
            }
        }
    }
    
    @FXML
    void clear(MouseEvent event) {
        traziTextField.clear();
    }
    
}
