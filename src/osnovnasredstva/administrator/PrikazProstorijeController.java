package osnovnasredstva.administrator;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXButton;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
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
import osnovnasredstva.DAO.OsnovnoSredstvoDAO;
import osnovnasredstva.DTO.OsnovnoSredstvo;
import osnovnasredstva.DTO.Prostorija;
import osnovnasredstva.prijava.PrijavaController;
import osnovnasredstva.util.Util;

public class PrikazProstorijeController implements Initializable {
    
    public static Prostorija odabranaProstorija=null;
    private static OsnovnoSredstvoDAO osnovnoSredstvoDAO = new OsnovnoSredstvoDAO();
    @FXML
    private AnchorPane menuLine;

    @FXML
    private Button closeButton;

    @FXML
    private JFXButton pdfButton;
    
    private double xOffset=0;
    private double yOffset=0;
    @FXML
    private TextField sifraTextField;
    @FXML
    private TextField nazivTextField;
    @FXML
    private TextField zgradaTextField;
    @FXML
    private TextArea opisTextArea;
    @FXML
    private TableView<OsnovnoSredstvo> osnovnaSredstvaTableView;
    @FXML
    private TableColumn<?, ?> invBrColumn;
    @FXML
    private TableColumn<?, ?> nazivColumn;
    @FXML
    private TableColumn<?, ?> opisColumn;
    @FXML
    private TableColumn<?, ?> vrijednostColumn;
    
    public static ObservableList<OsnovnoSredstvo> osnovnaSredstvaList = FXCollections.observableArrayList();
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
        
         osnovnaSredstvaTableView.setPlaceholder(new Label("Prostorija je prazna"));
         
         sifraTextField.setText(odabranaProstorija.getSifra());
         nazivTextField.setText(odabranaProstorija.getNaziv());
         opisTextArea.setText(odabranaProstorija.getOpis());
         LokacijeController.zgradeList.forEach((zg) -> {
             if(odabranaProstorija.getIdZgrade() == zg.getId()){
                 zgradaTextField.setText(zg.getNaziv());
             }
         });
         
        osnovnaSredstvaList.clear();
        
        try {
            osnovnaSredstvaList.addAll(osnovnoSredstvoDAO.loadAll4(PrijavaController.konekcija, odabranaProstorija.getId()));
        } catch (SQLException e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
        
        osnovnaSredstvaTableView.setItems(osnovnaSredstvaList);
        osnovnaSredstvaTableView.setFocusTraversable(false);
        invBrColumn.setCellValueFactory(new PropertyValueFactory<>("inventarniBroj"));
        nazivColumn.setCellValueFactory(new PropertyValueFactory<>("naziv"));
        opisColumn.setCellValueFactory(new PropertyValueFactory<>("opis"));
        vrijednostColumn.setCellValueFactory(new PropertyValueFactory<>("vrijednost"));
        
        Util.preventColumnReordering(osnovnaSredstvaTableView);
        
        invBrColumn.setMinWidth(100);
        invBrColumn.setMaxWidth(1000);
        
        nazivColumn.setMinWidth(100);
        nazivColumn.setMaxWidth(3000);
        
        opisColumn.setMinWidth(100);
        opisColumn.setMaxWidth(4000);
        
        vrijednostColumn.setMinWidth(100);
        vrijednostColumn.setMaxWidth(2000);
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
        String naziv = "PDF/" + odabranaProstorija.getNaziv() + "_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".pdf";
        
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
                    document.add(new Paragraph("Detaljan prikaz prostorije", catFont));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph("Izvještaj kreirao: " + PrijavaController.korisnik.getKorisnickoIme(), smallBold));
                    document.add(new Paragraph("Datum kreiranja: " + new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss").format(new Date()), smallBold));
                    document.add(new Paragraph(" "));      
                    document.add(new Paragraph("Šifra: " + odabranaProstorija.getSifra()));
                    document.add(new Paragraph("Naziv: " + odabranaProstorija.getNaziv()));
                    LokacijeController.zgradeList.forEach((zg) -> {
                        if(odabranaProstorija.getIdZgrade() == zg.getId()){
                            try {
                                document.add(new Paragraph("Zgrada: " + zg.getNaziv()));
                            } catch (DocumentException e) {
                                Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                            }
                        }
                    });
                    
                    document.add(new Paragraph("Opis: " + odabranaProstorija.getOpis()));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph("Osnovna sredstva u prostoriji", smallBold));
                    document.add(new Paragraph(" "));
             
                    PdfPTable table = new PdfPTable(4);
                    table.setWidthPercentage(100);
                    PdfPCell cell = new PdfPCell(new Phrase("Invertarni broj"));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase("Naziv"));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase("Opis"));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase("Vrijednost"));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    table.setHeaderRows(1);
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                    if(!osnovnaSredstvaList.isEmpty()){
                        for(OsnovnoSredstvo os : osnovnaSredstvaList){
                                table.addCell(os.getInventarniBroj());
                                table.addCell(os.getNaziv());
                                table.addCell(os.getOpis());
                                table.addCell(os.getVrijednost().toString());                       
                        }
                }
                    else{
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
class HeaderFooterPageEvent extends PdfPageEventHelper {

        /*@Override
        public void onStartPage(PdfWriter writer, Document document) {
            //String img = "Capture.png";
            //Image image;
            //try {
               // image = Image.getInstance(img);
                //image.setAlignment(Element.ALIGN_CENTER);
               // image.setAbsolutePosition(20, 800);
                //image.scalePercent(30f, 30f);
                //writer.getDirectContent().addImage(image, true);
                //Paragraph p = new Paragraph("Evidencija osnovnih sredstava");
                //p.setAlignment(Element.ALIGN_CENTER);
                //document.add(new Paragraph("Title").setAlignment(Element.ALIGN_CENTER));
                //document.add(p);
                //document.add(new LineSeparator());
                //document.add(Chunk.NEWLINE);
                //document.add(new Chunk(new LineSeparator()));
                //final LineSeparator lineSeparator = new LineSeparator();
                //lineSeparator.drawLine(pdfCB, leftX, rightX, y);
            //} catch (DocumentException e) {
                //log.error("L'image logo-tp-50x50.png a provoqué une erreur.", e);
            //}
            
            //ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Top Left"), 30, 800, 0);
            //ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Top Right"), 550, 800, 0);
            
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Evidencija osnovnih sredstava", FontFactory.getFont(FontFactory.TIMES_ITALIC)), (document.right()-document.left())/2+document.leftMargin(), document.top()+10, 0);
            try {
                document.add(new LineSeparator());
            } catch (DocumentException ex) {
                Logger.getLogger(HeaderFooterPageEvent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            //ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("http://www.xxxx-your_example.com/"), 110, 30, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("" + document.getPageNumber()), document.right() - 15, document.bottom() - 10, 0);
        }

    }
    


