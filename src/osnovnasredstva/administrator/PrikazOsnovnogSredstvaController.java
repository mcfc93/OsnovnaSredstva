package osnovnasredstva.administrator;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.controlsfx.control.MaskerPane;
import osnovnasredstva.DAO.PrelaznicaDAO;
import osnovnasredstva.DTO.OsnovnoSredstvo;
import osnovnasredstva.DTO.Prelaznica;
import static osnovnasredstva.administrator.PrikazOsobeController.listOsnovnoSredstvo;
import static osnovnasredstva.administrator.PrikazOsobeController.odabranaOsoba;
import osnovnasredstva.prijava.PrijavaController;
import osnovnasredstva.util.Util;

public class PrikazOsnovnogSredstvaController implements Initializable {
    
    public static OsnovnoSredstvo odabranoOS=null;
    private static PrelaznicaDAO prelaznicaDAO = new PrelaznicaDAO();
    
    @FXML
    private AnchorPane menuLine;

    @FXML
    private Button closeButton;

    @FXML
    private JFXButton pdfButton;
    
    private double xOffset=0;
    private double yOffset=0;
    @FXML
    private TextField invBrTextField;
    @FXML
    private TextField vrijednostTextField;
    @FXML
    private TextField stopaAmTextField;
    @FXML
    private TextField nazivTextField;
    @FXML
    private TextField nabVrTextField;
    @FXML
    private TextArea opisTextArea;
    @FXML
    private JFXDatePicker datePicker;
    @FXML
    private TableView<Prelaznica> prelaznicaTableView;
    @FXML
    private TableColumn<?, ?> datumColumn;
    @FXML
    private TableColumn<?, ?> izProstorijeColumn;
    @FXML
    private TableColumn<?, ?> uProstorijuColumn;
    @FXML
    private TableColumn<?, ?> saOsobeColumn;
    @FXML
    private TableColumn<?, ?> naOsobuColumn;
    
    public static ObservableList<Prelaznica> listPrelaznica;
    private static ArrayList<Prelaznica> svePrelaznice = new ArrayList<>();
    @FXML
    private AnchorPane anchorPane;
    
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
        
        listPrelaznica = FXCollections.observableArrayList();
        
        prelaznicaTableView.setPlaceholder(new Label("Nema osnovnih sredstava."));
        prelaznicaTableView.setFocusTraversable(false);
        
        try {
            svePrelaznice.addAll(prelaznicaDAO.loadAll(PrijavaController.konekcija));
        } catch (SQLException e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
        svePrelaznice.forEach(pr ->{
            if(pr.getIdOsnovnogSredstva() == odabranoOS.getId())
                listPrelaznica.add(pr);
        });
        
        invBrTextField.setText(odabranoOS.getInventarniBroj());
        nazivTextField.setText(odabranoOS.getNaziv());
        nabVrTextField.setText(String.valueOf(odabranoOS.getNabavnaVrijednost()));
        stopaAmTextField.setText(String.valueOf(odabranoOS.getStopaAmortizacije()));
        vrijednostTextField.setText(String.valueOf(odabranoOS.getVrijednost()));
        opisTextArea.setText(odabranoOS.getOpis());
        datePicker.setValue(odabranoOS.getDatumNabavke().toLocalDateTime().toLocalDate());
        
        prelaznicaTableView.setItems(listPrelaznica);
        datumColumn.setCellValueFactory(new PropertyValueFactory<>("datumPrelaska"));
        izProstorijeColumn.setCellValueFactory(new PropertyValueFactory<>("idProstorijeIzString"));
        uProstorijuColumn.setCellValueFactory(new PropertyValueFactory<>("idProstorijeUString"));
        saOsobeColumn.setCellValueFactory(new PropertyValueFactory<>("idOsobeSaString"));
        naOsobuColumn.setCellValueFactory(new PropertyValueFactory<>("IdOsobeNaString"));
    }

    @FXML
    void close(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY)) {
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        }
    }

    @FXML
    void pdf(ActionEvent event) {
        MaskerPane progressPane=Util.getMaskerPane(anchorPane);
        String naziv = "PDF/" + odabranoOS.getNaziv() + "_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".pdf";
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
                    document.open();
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph("Detaljan prikaz osnovnog sredstva", catFont));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph("Izvještaj kreirao: " + PrijavaController.korisnik.getKorisnickoIme(), smallBold));
                    document.add(new Paragraph("Datum kreiranja: " + new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss").format(new Date()), smallBold));
                    document.add(new Paragraph(" "));      
                    document.add(new Paragraph("Inventarni broj: " + odabranoOS.getInventarniBroj()));
                    document.add(new Paragraph("Naziv: " + odabranoOS.getNaziv()));
                    document.add(new Paragraph("Nabavna vrijednost: " + odabranoOS.getNabavnaVrijednost()));
                    document.add(new Paragraph("Stopa amortizacije: " + odabranoOS.getStopaAmortizacije()));
                    document.add(new Paragraph("Vrijednost: " + odabranoOS.getVrijednost()));
                    document.add(new Paragraph("Opis: " + odabranoOS.getOpis()));
                    document.add(new Paragraph("Datum nabavke: " + odabranoOS.getDatumNabavke()));
   
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph("Istorija zaduživanja osnovnog sredstva", smallBold));
                    document.add(new Paragraph(" "));
             
                    PdfPTable table = new PdfPTable(5);
                    table.setWidthPercentage(100);
                    PdfPCell cell = new PdfPCell(new Phrase("Datum prelaska"));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase("Iz prostorije"));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase("U prostoriju"));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase("Sa osobe"));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    
                    cell = new PdfPCell(new Phrase("Na osobu"));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    table.setHeaderRows(1);
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                    if(!listPrelaznica.isEmpty()){
                        for(Prelaznica os : listPrelaznica){
                                table.addCell(os.getDatumPrelaska().toString());
                                table.addCell(os.getIdProstorijeIzString());
                                table.addCell(os.getIdProstorijeUString());
                                table.addCell(os.getIdOsobeSaString());
                                table.addCell(os.getIdOsobeNaString()); 
                        }
                }
                    else{
                        table.addCell(" ");
                        table.addCell(" ");
                        table.addCell(" ");
                        table.addCell(" ");
                        table.addCell(" ");
                    }
                    document.add(table);
                    document.close();
                } catch (DocumentException | FileNotFoundException e) {
                    Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                }
                return null;
            }
            @Override
            protected void succeeded(){
                super.succeeded();
                progressPane.setVisible(false);
                Platform.runLater(() -> Util.getNotifications("Obavještenje", "Izvještaj kreiran.", "Information").show());
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
