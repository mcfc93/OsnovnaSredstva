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
    private Label naslovLabel;

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
    private TableColumn<?, ?> osobaColumn;
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
        
        naslovLabel.setText("Detaljan prikaz prostorije");
        
        osnovnaSredstvaTableView.setPlaceholder(new Label("Prostorija je prazna"));

        sifraTextField.setText(odabranaProstorija.getSifra());
        nazivTextField.setText(odabranaProstorija.getNaziv());
        opisTextArea.setText(odabranaProstorija.getOpis());
        zgradaTextField.setText(odabranaProstorija.getNazivZgrade());
         
        osnovnaSredstvaList.clear();
        
        MaskerPane progressPane=Util.getMaskerPane(anchorPane);
        new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                progressPane.setVisible(true);
                try {
                    osnovnaSredstvaList.addAll(osnovnoSredstvoDAO.loadAll4(PrijavaController.konekcija, odabranaProstorija.getId()));
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
                    osnovnaSredstvaTableView.refresh();
                });
            }
        }).start();
        
        osnovnaSredstvaTableView.setItems(osnovnaSredstvaList);
        osnovnaSredstvaTableView.setFocusTraversable(false);
        invBrColumn.setCellValueFactory(new PropertyValueFactory<>("inventarniBroj"));
        nazivColumn.setCellValueFactory(new PropertyValueFactory<>("naziv"));
        osobaColumn.setCellValueFactory(new PropertyValueFactory<>("imePrezimeOsobe"));
        vrijednostColumn.setCellValueFactory(new PropertyValueFactory<>("vrijednost"));
        
        Util.preventColumnReordering(osnovnaSredstvaTableView);
        
        invBrColumn.setMinWidth(100);
        invBrColumn.setMaxWidth(1000);
        
        nazivColumn.setMinWidth(100);
        nazivColumn.setMaxWidth(3000);
        
        osobaColumn.setMinWidth(100);
        osobaColumn.setMaxWidth(4000);
        
        vrijednostColumn.setMinWidth(100);
        vrijednostColumn.setMaxWidth(3500);
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
        String naziv = "PDF/prostorije/" + odabranaProstorija.getNaziv() + "_" + odabranaProstorija.getId() + "_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".pdf";
        
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
                    document.add(new Paragraph("Detaljan prikaz prostorije", catFont));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph("Izvještaj kreirao: " + PrijavaController.korisnik.getKorisnickoIme(), smallBold));
                    document.add(new Paragraph("Datum i vrijeme kreiranja: " + new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss").format(new Date()), smallBold));
                    document.add(new Paragraph(" "));  
                    document.add(new Paragraph("Osnovni podaci o prostoriji:", smallBold));
                    document.add(new Paragraph("     Šifra: " + odabranaProstorija.getSifra()));
                    document.add(new Paragraph(new Chunk("     Naziv: " + odabranaProstorija.getNaziv(), font)));
                    LokacijeController.zgradeList.forEach((zg) -> {
                        if(odabranaProstorija.getIdZgrade() == zg.getId()){
                            try {
                                document.add(new Paragraph(new Chunk("     Zgrada: " + zg.getNaziv(), font)));
                            } catch (DocumentException e) {
                                Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                            }
                        }
                    });
                    
                    document.add(new Paragraph(new Chunk("     Opis: " + odabranaProstorija.getOpis(), font)));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph("Osnovna sredstva u prostoriji:", smallBold));
                    document.add(new Paragraph(" "));
             
                    PdfPTable table = new PdfPTable(5);
                    table.setWidthPercentage(100);
                    PdfPCell cell = new PdfPCell(new Phrase("Inventarni broj"));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase("Naziv"));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase("Opis"));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    
                    cell = new PdfPCell(new Phrase(new Chunk("Zadužena osoba", font)));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase("Trenutna vrijednost [KM]"));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    table.setHeaderRows(1);
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                    if(!osnovnaSredstvaList.isEmpty()){
                        for(OsnovnoSredstvo os : osnovnaSredstvaList){
                            table.addCell(os.getInventarniBroj());
                            table.addCell(new Phrase(new Chunk(os.getNaziv(), font)));
                            table.addCell(new Phrase(new Chunk(os.getOpis(), font)));
                            table.addCell(new Phrase(new Chunk(os.getImePrezimeOsobe(), font)));
                            table.addCell(os.getVrijednost().toString());                       
                        }
                    } else{
                        cell = new PdfPCell(new Phrase(new Chunk("Nema podataka o zaduživanjima",font)));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setColspan(5);
                        table.addCell(cell);
                    }
                    document.add(table);
                    document.close();
                } catch (DocumentException | FileNotFoundException e) {
                    Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                } catch (IOException ex) {
                    Logger.getLogger(PrikazProstorijeController.class.getName()).log(Level.SEVERE, null, ex);
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
    


