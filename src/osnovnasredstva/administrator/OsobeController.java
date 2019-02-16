package osnovnasredstva.administrator;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.MaskerPane;
import osnovnasredstva.DAO.OsobaDAO;
import osnovnasredstva.DTO.Osoba;
import osnovnasredstva.prijava.PrijavaController;
import osnovnasredstva.util.NotFoundException;
import osnovnasredstva.util.Util;

public class OsobeController implements Initializable {

    private static OsobaDAO osobaDAO = new OsobaDAO();
    
    public static int tip;
    
    @FXML
    private AnchorPane anchorPane;
    
    @FXML
    private TableView<Osoba> osobeTableView;

    @FXML
    private TableColumn<?, ?> imeColumn;

    @FXML
    private TableColumn<?, ?> prezimeColumn;

    @FXML
    private TableColumn<?, ?> jmbgColumn;

    @FXML
    private TableColumn<?, ?> titulaColumn;

    @FXML
    private TableColumn<Osoba, Osoba> prikaziColumn;

    @FXML
    private TableColumn<Osoba, Osoba> izmjeniColumn;

    @FXML
    private TableColumn<Osoba, Osoba> obrisiColumn;


    @FXML
    private JFXButton pdfButton;

    @FXML
    private JFXButton dodajOsobuButton;

    @FXML
    private TextField traziTextField;

    @FXML
    private ImageView clearImageView;
    
    @FXML
    private JFXToggleButton postaniNadzornikToggleButton;
    
    public static ObservableList<Osoba> osobeList;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clearImageView.setVisible(false);
		
        traziTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            if(!newValue.isEmpty()) {
                clearImageView.setVisible(true);
            } else {
                clearImageView.setVisible(false);
            }
        });

        if(PrijavaController.korisnik.getTip()==1) {
            izmjeniColumn.setVisible(false);
            obrisiColumn.setVisible(false);
            dodajOsobuButton.setVisible(false);
            postaniNadzornikToggleButton.setVisible(false);
        } else {
            if(PrijavaController.korisnik.getTip() != PrijavaController.korisnik.getPrivilegijaTip()) {
                PrijavaController.korisnik.setPrivilegijaTip(PrijavaController.korisnik.getTip());
            }
        }

        
        osobeList = FXCollections.observableArrayList();
        
        MaskerPane progressPane=Util.getMaskerPane(anchorPane);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                progressPane.setVisible(true);
                try {
                    osobeList.addAll(osobaDAO.loadAll(PrijavaController.konekcija));
                } catch (SQLException e) {
                    Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                }
                return null;
            }
            @Override
            protected void succeeded(){
                super.succeeded();
                progressPane.setVisible(false);
            }
        };
        new Thread(task).start();
        
        
        osobeTableView.setItems(osobeList);
        osobeTableView.setPlaceholder(new Label("Nema osoba u tabeli."));
        osobeTableView.setFocusTraversable(false);
        imeColumn.setCellValueFactory(new PropertyValueFactory<>("ime"));
        prezimeColumn.setCellValueFactory(new PropertyValueFactory<>("prezime"));
        //if(PrijavaController.korisnik.getTip()==0) {
            jmbgColumn.setCellValueFactory(new PropertyValueFactory<>("jmbg"));
        //} else {
        //    jmbgColumn.setCellValueFactory(new PropertyValueFactory<>("jmbgValue"));
        //}
        titulaColumn.setCellValueFactory(new PropertyValueFactory<>("titula"));
        //prikaziColumn.setCellValueFactory(new PropertyValueFactory<>("pregled"));
        //izmjeniColumn.setCellValueFactory(new PropertyValueFactory<>("izmjeni"));
        //obrisiColumn.setCellValueFactory(new PropertyValueFactory<>("obrisi"));
        
        prikaziColumn.setVisible(true);
        
        prikaziColumn.setCellValueFactory(
            param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );
        
        prikaziColumn.setCellFactory(tableCell -> {
            TableCell<Osoba, Osoba> cell = new TableCell<Osoba, Osoba>() {
                private final Button button = new Button("");
                @Override
                protected void updateItem(Osoba item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        //System.out.println(item);
                        //postavljanje CSS
                    	button.getStyleClass().addAll("buttonTable", "buttonTableShow");
                    	//postavljanje opisa
                    	button.setTooltip(new Tooltip("Prikaži?"));
                    	button.getTooltip().setAutoHide(false);
                    	//button.getTooltip().setShowDelay(Duration.seconds(0.5));
                    	//dodavanje u kolonu
                    	setGraphic(button);
                    	button.setOnMouseClicked(event -> {
                            //Osoba o=getTableView().getItems().get(getIndex());
                            System.out.println(item);
                            
                            try {
                                PrikazOsobeController.odabranaOsoba=item;
                                
                                Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/PrikazOsobeView.fxml"));
                                Scene scene = new Scene(root);
                                scene.getStylesheets().add(getClass().getResource("/osnovnasredstva/osnovnasredstva.css").toExternalForm());
                                Stage stage=new Stage();
                                stage.setScene(scene);
                                stage.setResizable(false);
                                stage.initStyle(StageStyle.UNDECORATED);
                                stage.initModality(Modality.APPLICATION_MODAL);
                                stage.showAndWait();
                            } catch(IOException e) {
                                Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                            }
                        });
                    } else {
                    	setGraphic(null);
                    }
                }
            };
            return cell;
        });
        
        izmjeniColumn.setCellValueFactory(
            param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );
        
        izmjeniColumn.setCellFactory(tableCell -> {
            TableCell<Osoba, Osoba> cell = new TableCell<Osoba, Osoba>() {
                private final Button button = new Button("");
                @Override
                protected void updateItem(Osoba item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        //System.out.println(item);
                        //postavljanje CSS
                    	button.getStyleClass().addAll("buttonTable", "buttonTableEdit");
                    	//postavljanje opisa
                    	button.setTooltip(new Tooltip("Izmjeni?"));
                    	button.getTooltip().setAutoHide(false);
                    	//button.getTooltip().setShowDelay(Duration.seconds(0.5));  //since JDK9
                    	//dodavanje u kolonu
                    	setGraphic(button);
                    	button.setOnMouseClicked(event -> {
                            //Osoba o=getTableView().getItems().get(getIndex());
                            System.out.println(item);
                            
                            try {
                                DodavanjeOsobeController.odabranaOsoba=item;
                                DodavanjeOsobeController.izmjena=true;
                                
                                Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/DodavanjeOsobeView.fxml"));
                                Scene scene = new Scene(root);
                                Stage stage=new Stage();
                                stage.setScene(scene);
                                stage.setResizable(false);
                                stage.initStyle(StageStyle.UNDECORATED);
                                stage.initModality(Modality.APPLICATION_MODAL);
                                stage.showAndWait();
                                
                                DodavanjeOsobeController.izmjena=false;
                                osobeTableView.refresh();
                            } catch(IOException e) {
                                Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                            }
                        });
                    } else {
                    	setGraphic(null);
                    }
                }
            };
            return cell;
        });
        
        obrisiColumn.setCellValueFactory(
            param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );
        
        obrisiColumn.setCellFactory(tableCell -> {
            TableCell<Osoba, Osoba> cell = new TableCell<Osoba, Osoba>() {
                private final Button button = new Button("");
                @Override
                protected void updateItem(Osoba item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        //System.out.println(item);
                        //postavljanje CSS
                    	button.getStyleClass().addAll("buttonTable", "buttonTableDelete");
                    	//postavljanje opisa
                    	button.setTooltip(new Tooltip("Obriši?"));
                    	button.getTooltip().setAutoHide(false);
                    	//button.getTooltip().setShowDelay(Duration.seconds(0.5));
                    	//dodavanje u kolonu
                    	setGraphic(button);
                    	button.setOnMouseClicked(event -> {
                            if(Util.showConfirmationAlert()) {
                                try {
                                    osobaDAO.delete(PrijavaController.konekcija, item);
                                    osobeList.remove(item);
                                    //getTableView().getItems().remove(item);
                                    osobeTableView.refresh();
                                    System.out.println("Obrisano: " + item);
                                    Util.getNotifications("Obavještenje", "Osoba obrisana.", "Information").show();
                                } catch (SQLException | NotFoundException e) {
                                    Util.showBugAlert();
                                    Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                                }
                            }
                        });
                    } else {
                    	setGraphic(null);
                    }
                }
            };
            return cell;
        });
        
        prikaziColumn.setText("");
        prikaziColumn.setMinWidth(35);
        prikaziColumn.setMaxWidth(35);
        prikaziColumn.setResizable(false);
        prikaziColumn.setSortable(false);
        izmjeniColumn.setText("");
        izmjeniColumn.setMinWidth(35);
        izmjeniColumn.setMaxWidth(35);
        izmjeniColumn.setResizable(false);
        izmjeniColumn.setSortable(false);
        obrisiColumn.setText("");
        obrisiColumn.setMinWidth(35);
        obrisiColumn.setMaxWidth(35);
        obrisiColumn.setResizable(false);
        obrisiColumn.setSortable(false);
        
        postaniNadzornikToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                PrijavaController.korisnik.setPrivilegijaTip(1);
            } else {
                PrijavaController.korisnik.setPrivilegijaTip(0);
            }
            postaviPrivilegije();
            osobeTableView.refresh();
        });
    }
    
    @FXML
    void clear(MouseEvent event) {
        traziTextField.clear();
    }
    
    @FXML
    void dodaj(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/DodavanjeOsobeView.fxml"));
            Scene scene = new Scene(root);
            Stage stage=new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch(IOException e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }
    
    private void postaviPrivilegije() {
        if(PrijavaController.korisnik.getPrivilegijaTip()==1) {
            izmjeniColumn.setVisible(false);
            obrisiColumn.setVisible(false);
            dodajOsobuButton.setVisible(false);
        } else {
            izmjeniColumn.setVisible(true);
            obrisiColumn.setVisible(true);
            dodajOsobuButton.setVisible(true);
        }
    }
    
    ///////////////Create PDF//////////////////////////////////
    ///////////////////////////////////////////////////////////
    
    @FXML
    private void pdf(ActionEvent event) {
            Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
            Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.RED);
            Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
            Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        try {
            
            
            Document document = new Document(PageSize.A4.rotate());
            String naziv = "PDF/OsobeIzvjestaj_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".pdf";
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(naziv));
            document.open();
             document.add(new Paragraph(" "));
            // Lets write a big header
            document.add(new Paragraph("Izvještaj svih osoba", catFont));

            //addEmptyLine(preface, 1);
             document.add(new Paragraph(" "));
            // Will create: Report generated by: _name, _date
            document.add(new Paragraph(
                    "Izvještaj kreirao: " + PrijavaController.korisnik.getKorisnickoIme(),
                    smallBold));
            document.add(new Paragraph(
                    "Datum kreiranja: " + new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss").format(new Date()), 
                    smallBold));
            
            document.add(new Paragraph(" "));
            //addEmptyLine(preface, 1);
            document.add(new Paragraph(
                    "Tabela svih osoba",
                    smallBold));

             document.add(new Paragraph(" "));

             
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell(new Phrase("Ime"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("Prezime"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase("JMBG"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("Adresa"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("Titula"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("Zaposlenje"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("Broj telefona"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            cell = new PdfPCell(new Phrase("E-mail"));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            
            table.setHeaderRows(1);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            for(Osoba os : osobeList){
                table.addCell(os.getIme());
                table.addCell(os.getPrezime());
                table.addCell(os.getJmbg());
                table.addCell(os.getAdresa());
                table.addCell(os.getTitula());
                table.addCell(os.getZaposlenje());
                table.addCell(os.getTelefon());
                table.addCell(os.getEmail());     
            }
            
            //document.add(preface);
            document.add(table);
            document.close();
            Util.getNotifications("Obavještenje", "Izvještaj kreiran.", "Information").show();
            Desktop desktop = Desktop.getDesktop();
            desktop.open(new File(naziv));
            
        } catch (Exception e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }
     
}
