package osnovnasredstva.administrator;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
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
    private TextField traziTextField;

    @FXML
    private ImageView clearImageView;

    @FXML
    private TableColumn<?, ?> nazivColumn;
    @FXML
    private TableColumn<Prelaznica, Prelaznica> otvoriColumn;
    
    public static ObservableList<OsnovnoSredstvo> osnovnaSredstvaList = FXCollections.observableArrayList();
    public static ObservableList<Osoba> osobeList = FXCollections.observableArrayList();
    public static ObservableList<Prostorija> prostorijeList = FXCollections.observableArrayList();
    public static ObservableList<Zgrada> zgradeList = FXCollections.observableArrayList();
    
    public static ObservableList<Prelaznica> prelazniceList = FXCollections.observableArrayList();
    private static FilteredList<Prelaznica> filteredList;
    SortedList<Prelaznica> sortedList;
    @FXML
    private TableColumn<?, ?> invBrColumn;
    @FXML
    private TableColumn<?, ?> saColumn;
    @FXML
    private TableColumn<?, ?> naColumn;
    @FXML
    private TableColumn<?, ?> izColumn;
    @FXML
    private TableColumn<?, ?> uColumn;
    
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
        
        filteredList = new FilteredList<>(prelazniceList);
        sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(prelazniceTableView.comparatorProperty());
        
        MaskerPane progressPane=Util.getMaskerPane(anchorPane);
        new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                progressPane.setVisible(true);
                try {
                    synchronized(prelazniceList) {
                        osnovnaSredstvaList.clear();
                        prelazniceList.clear();
                        osobeList.clear();
                        prostorijeList.clear();
                        zgradeList.clear();
                        osnovnaSredstvaList.addAll(osnovnoSredstvoDAO.loadAll2(PrijavaController.konekcija));
                        osobeList.addAll(osobaDAO.loadAll2(PrijavaController.konekcija));
                        prostorijeList.addAll(prostorijaDAO.loadAll2(PrijavaController.konekcija));
                        zgradeList.addAll(zgradaDAO.loadAll2(PrijavaController.konekcija));
                        prelazniceList.addAll(prelaznicaDAO.loadAll(PrijavaController.konekcija));
                    }
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
        prelazniceTableView.setPlaceholder(new Label("Nema prelaznica."));
        prelazniceTableView.setFocusTraversable(false);
        
        nazivColumn.setCellValueFactory(new PropertyValueFactory<>("naziv"));
        invBrColumn.setCellValueFactory(new PropertyValueFactory<>("InvBr"));
        saColumn.setCellValueFactory(new PropertyValueFactory<>("SaOsobe"));
        naColumn.setCellValueFactory(new PropertyValueFactory<>("NaOsobu"));
        izColumn.setCellValueFactory(new PropertyValueFactory<>("IzProstorije"));
        uColumn.setCellValueFactory(new PropertyValueFactory<>("UProstoriju"));
        
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
        
       // nazivColumn.setMinWidth(100);
       // nazivColumn.setMaxWidth(5000);
                
      //  datumColumn.setMinWidth(100);
      //  datumColumn.setMaxWidth(3000);
        
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
        String naziv = "PDF/prelaznice/" + "Prelaznica_" + pr.getId() + "_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(pr.getDatumPrelaska()) + ".pdf";
        File file = new File(naziv);
        if(!file.exists()){
            MaskerPane progressPane=Util.getMaskerPane(anchorPane);
        //String naziv = "PDF/prelaznice/" + "Prelaznica_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(pr.getDatumPrelaska()) + ".pdf";
        
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
                    
                    document.add(new Paragraph("Podaci o osnovnom sredstvu:", smallBold));
                    
                    OsnovnaSredstvaController.osnovnaSredstvaList.forEach(os ->{
                        if(os.getId() == pr.getIdOsnovnogSredstva()){
                            try {
                                document.add(new Paragraph(new Chunk("     Naziv osnovnog sredstva: " + os.getNaziv(), font)));
                                document.add(new Paragraph(new Chunk("     Inventarni broj: " + os.getInventarniBroj(), font)));
                                document.add(new Paragraph(new Chunk("     Datum nabavke: " + new SimpleDateFormat("dd.MM.yyyy").format(os.getDatumNabavke()), font)));
                                document.add(new Paragraph(new Chunk("     Trenutna vrijednost [KM]: " + os.getVrijednost(), font)));
                                document.add(new Paragraph(new Chunk("     Stopa amortizacije: " + os.getStopaAmortizacije(), font)));
                                document.add(new Paragraph(new Chunk("     Nabavna vrijednost [KM]: " + os.getNabavnaVrijednost(), font)));
                            } catch (DocumentException e) {
                                Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                            }
                        }
                    });
                    document.add(new Paragraph(new Chunk("     Datum i vrijeme prelaska: " + new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss").format(pr.getDatumPrelaska()), font)));
                    document.add(new Paragraph(" "));
                    
                    PdfPTable table = new PdfPTable(4);
                    table.setWidthPercentage(100);
                    PdfPCell cell = new PdfPCell(new Phrase("Sa osobe"));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    
                    cell = new PdfPCell(new Phrase("Na osobu"));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    
                    cell = new PdfPCell(new Phrase("Iz prostorije"));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    
                    cell = new PdfPCell(new Phrase("U prostoriju"));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    
                    table.setHeaderRows(1);
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                    
                    OsobeController.osobeList.forEach(os ->{
                        if(pr.getIdOsobeSa() == os.getId()){
                            table.addCell(new Phrase(new Chunk(os.getImePrezime(), font)));
                        }
                    });
                    OsobeController.osobeList.forEach(os ->{
                        if(pr.getIdOsobeNa()== os.getId()){
                            table.addCell(new Phrase(new Chunk(os.getImePrezime(), font)));
                        }
                    });
                    
                    LokacijeController.prostorijeList.forEach(lo -> {
                        if(pr.getIdProstorijeIz() == lo.getId()){
                            table.addCell(new Phrase(new Chunk(lo.toString(), font)));
                        }
                    });
                    LokacijeController.prostorijeList.forEach(lo -> {
                        if(pr.getIdProstorijeU()== lo.getId()){
                            table.addCell(new Phrase(new Chunk(lo.toString(), font)));
                        }
                    });
                    document.add(table);
                    
                    document.add(new Paragraph(new Chunk("Napomena: " + pr.getNapomena(), font)));
                    
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
