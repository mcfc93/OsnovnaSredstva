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
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleButton;
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
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import org.controlsfx.control.MaskerPane;
import osnovnasredstva.DAO.OsnovnoSredstvoDAO;
import osnovnasredstva.DTO.OsnovnoSredstvo;
import osnovnasredstva.DTO.VrstaOS;
import osnovnasredstva.prijava.PrijavaController;
import osnovnasredstva.util.NotFoundException;
import osnovnasredstva.util.Util;

public class OsnovnaSredstvaController implements Initializable {
    
    private static OsnovnoSredstvoDAO osnovnoSredstvoDAO = new OsnovnoSredstvoDAO();
    //private static VrstaOSDAO vrstaOSDAO = new VrstaOSDAO();
    //private static ProstorijaDAO prostorijaDAO = new ProstorijaDAO();
    //private static ZgradaDAO zgradaDAO = new ZgradaDAO();
    
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TableView<OsnovnoSredstvo> osnovnaSredstvaTableView;

    @FXML
    private TableColumn<?, ?> invertarniBrojColumn;

    @FXML
    private TableColumn<?, ?> nazivColumn;

    @FXML
    private TableColumn<OsnovnoSredstvo, Integer> vrstaColumn;

    @FXML
    private TableColumn<OsnovnoSredstvo, OsnovnoSredstvo> prikaziColumn;

    @FXML
    private TableColumn<OsnovnoSredstvo, OsnovnoSredstvo> izmjeniColumn;

    @FXML
    private TableColumn<OsnovnoSredstvo, OsnovnoSredstvo> obrisiColumn;

    @FXML
    private JFXButton dodajButton;

    @FXML
    private TextField traziTextField;

    @FXML
    private ImageView clearImageView;

    @FXML
    private JFXButton pdfButton;

    @FXML
    private JFXComboBox<VrstaOS> vrstaComboBox;

    @FXML
    private JFXButton dodajVrstuButton;
    
    @FXML
    private JFXButton izmjeniButton;
    
    public static ObservableList<VrstaOS> vrstaOsnovnogSredstvaList = FXCollections.observableArrayList();
    //public static ObservableList<Zgrada> zgradeList = FXCollections.observableArrayList();
    //public static ObservableList<Prostorija> prostorijeList = FXCollections.observableArrayList();
    
    public static ObservableList<OsnovnoSredstvo> osnovnaSredstvaList = FXCollections.observableArrayList();
    private static FilteredList<OsnovnoSredstvo> filteredList;
    private SortedList<OsnovnoSredstvo> sortedList;
    
    @FXML
    private JFXToggleButton postaniNadzornikToggleButton;
    @FXML
    private TableColumn<?, ?> vrijednostColumn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clearImageView.setVisible(false);
		
        traziTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            filteredList.setPredicate(osnovnoSredstvo -> osnovnoSredstvo.getNaziv().toLowerCase().startsWith(newValue.toLowerCase()));
            filteredList.setPredicate(osnovnoSredstvo -> vrstaComboBox.getValue().getNaziv() != null? osnovnoSredstvo.getIdVrste()== vrstaComboBox.getValue().getId() && osnovnoSredstvo.getNaziv().toLowerCase().startsWith(newValue.toLowerCase()): osnovnoSredstvo.getNaziv().toLowerCase().startsWith(newValue.toLowerCase()));

            //osnovnaSredstvaTableView.refresh();
            if(!newValue.isEmpty()) {
                clearImageView.setVisible(true);
            } else {
                clearImageView.setVisible(false);
            }
        });
        
        izmjeniButton.setVisible(false);
        if(PrijavaController.korisnik.getTip()==1) {
            izmjeniColumn.setVisible(false);
            obrisiColumn.setVisible(false);
            dodajButton.setVisible(false);
            dodajVrstuButton.setVisible(false);
            postaniNadzornikToggleButton.setVisible(false);
        } else {
            if(PrijavaController.korisnik.getTip() != PrijavaController.korisnik.getPrivilegijaTip()) {
                PrijavaController.korisnik.setPrivilegijaTip(PrijavaController.korisnik.getTip());
            }
        }
        
        //vrstaOsnovnogSredstvaList.clear();
        //osnovnaSredstvaList.clear();
        //zgradeList.clear();
        //prostorijeList.clear();
        filteredList = new FilteredList(osnovnaSredstvaList);
        sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(osnovnaSredstvaTableView.comparatorProperty());
        
        MaskerPane progressPane=Util.getMaskerPane(anchorPane);
        /*
        new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                progressPane.setVisible(true);
                try {
                    vrstaOsnovnogSredstvaList.addAll(vrstaOSDAO.loadAll(PrijavaController.konekcija));
                    osnovnaSredstvaList.addAll(osnovnoSredstvoDAO.loadAll(PrijavaController.konekcija));
                    zgradeList.addAll(zgradaDAO.loadAll(PrijavaController.konekcija));
                    prostorijeList.addAll(prostorijaDAO.loadAll(PrijavaController.konekcija));
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
                    //vrstaOsnovnogSredstvaList.addAll(VrstaOSDAO.getVrsteOSList());
                    vrstaComboBox.getItems().add(0, new VrstaOS());
                    vrstaComboBox.getItems().addAll(vrstaOsnovnogSredstvaList);
                    vrstaComboBox.getSelectionModel().selectFirst();
                });
            }
        }).start();
        */
                    vrstaComboBox.getItems().add(0, new VrstaOS());
                    vrstaComboBox.getItems().addAll(vrstaOsnovnogSredstvaList);
                    vrstaComboBox.getSelectionModel().selectFirst();
        vrstaComboBox.setVisibleRowCount(5);

        vrstaComboBox.setCellFactory(param -> {
            ListCell<VrstaOS> cell = new ListCell<VrstaOS>() {
                @Override
                protected void updateItem(VrstaOS item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        //setText("Svi");
                        setGraphic(null);
                    } else {
                        if(item.getNaziv()!=null) {
                            setText(item.getNaziv());
                        } else {
                            setText("Sve");
                        }
                    }
                }
            };
            return cell;
        });
        
        vrstaComboBox.setConverter(new StringConverter<VrstaOS>() {
            //ispis u ComboBox selected item value
            @Override
            public String toString(VrstaOS object) {
                return object.getNaziv()!=null? object.getNaziv(): "Sve";
            }
            //Editable ComboBox pretraga
            @Override
            public VrstaOS fromString(String string) {
                return vrstaComboBox.getItems().stream().filter(v -> v.getNaziv().equals(string)).findFirst().orElse(null);
            }
        });
        
        vrstaComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                if(vrstaComboBox.getValue().getNaziv() != null) {
                    izmjeniButton.setVisible(true);
                } else {
                    izmjeniButton.setVisible(false);
                }
                
                new Thread(new Task<Void>() {
                    @Override
                    protected Void call() {
                        progressPane.setVisible(true);
                        
                        filteredList.setPredicate(osnovnoSredstvo -> vrstaComboBox.getValue().getNaziv() != null? osnovnoSredstvo.getIdVrste()== vrstaComboBox.getValue().getId(): true);
                        
                        Platform.runLater(() -> {
                            traziTextField.clear();
                        });
                        /*
                        try {
                            osnovnaSredstvaList.clear();
                            if(vrstaComboBox.getValue().getNaziv() != null)
                                osnovnaSredstvaList.addAll(osnovnoSredstvoDAO.loadAll2(PrijavaController.konekcija,vrstaComboBox.getValue().getId()));
                            else
                                osnovnaSredstvaList.addAll(osnovnoSredstvoDAO.loadAll(PrijavaController.konekcija));
                        } catch (SQLException e) {
                            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                        }
                        */
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
            }
        });
        /*
        vrstaOsnovnogSredstvaList.addAll(VrstaOSDAO.getVrsteOSList());
        vrstaOsnovnogSredstvaList.add(0, new VrstaOS());
        vrstaComboBox.getItems().addAll(vrstaOsnovnogSredstvaList);
        vrstaComboBox.getSelectionModel().selectFirst();
        */
        
        //osnovnaSredstvaList=FXCollections.observableArrayList();
        osnovnaSredstvaTableView.setItems(sortedList);
        osnovnaSredstvaTableView.setPlaceholder(new Label("Nema osnovnih sredstava za odabranu vrstu."));
        osnovnaSredstvaTableView.setFocusTraversable(false);
        
        invertarniBrojColumn.setCellValueFactory(new PropertyValueFactory<>("inventarniBroj"));
        nazivColumn.setCellValueFactory(new PropertyValueFactory<>("naziv"));
        vrstaColumn.setCellValueFactory(new PropertyValueFactory<>("idVrsteString"));
        vrijednostColumn.setCellValueFactory(new PropertyValueFactory<>("vrijednost"));
        /*
        vrstaColumn.setCellFactory(tableCell -> {
            TableCell<OsnovnoSredstvo, Integer> cell = new TableCell<OsnovnoSredstvo, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if(!empty) {
                        try {
                            setText(vrstaOsnovnogSredstvaList.stream().filter(vrsta -> vrsta.getId() == item).findFirst().get().getNaziv());
                        } catch(NullPointerException e) {
                            setText("NEPOZNATO");
                        }
                    }
                }
            };
            return cell;
        });
        */
        prikaziColumn.setCellValueFactory(
            param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );
        
        prikaziColumn.setCellFactory(tableCell -> {
            TableCell<OsnovnoSredstvo, OsnovnoSredstvo> cell = new TableCell<OsnovnoSredstvo, OsnovnoSredstvo>() {
                private final Button button = new Button("");
                @Override
                protected void updateItem(OsnovnoSredstvo item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                    	button.getStyleClass().addAll("buttonTable", "buttonTableShow");
                    	button.setTooltip(new Tooltip("Prikaži"));
                    	button.getTooltip().setAutoHide(false);
                    	setGraphic(button);
                    	button.setOnMouseClicked(event -> {
                            osnovnaSredstvaTableView.getSelectionModel().select(item);
                            prikaziOS(item);
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
            TableCell<OsnovnoSredstvo, OsnovnoSredstvo> cell = new TableCell<OsnovnoSredstvo, OsnovnoSredstvo>() {
                private final Button button = new Button("");
                @Override
                protected void updateItem(OsnovnoSredstvo item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                    	button.getStyleClass().addAll("buttonTable", "buttonTableEdit");
                    	button.setTooltip(new Tooltip("Izmjeni"));
                    	button.getTooltip().setAutoHide(false);
                    	setGraphic(button);
                    	button.setOnMouseClicked(event -> {
                            osnovnaSredstvaTableView.getSelectionModel().select(item);
                            try {
                                DodavanjeOsnovnogSredstvaController.odabranoOS=item;
                                DodavanjeOsnovnogSredstvaController.izmjena=true;
                                
                                Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/DodavanjeOsnovnogSredstvaView.fxml"));
                                Scene scene = new Scene(root);
                                Stage stage=new Stage();
                                stage.setScene(scene);
                                stage.setResizable(false);
                                stage.initStyle(StageStyle.UNDECORATED);
                                stage.initModality(Modality.APPLICATION_MODAL);
                                stage.showAndWait();
                                
                                DodavanjeOsnovnogSredstvaController.izmjena=false;
                                osnovnaSredstvaTableView.refresh();
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
            TableCell<OsnovnoSredstvo, OsnovnoSredstvo> cell = new TableCell<OsnovnoSredstvo, OsnovnoSredstvo>() {
                private final Button button = new Button("");
                @Override
                protected void updateItem(OsnovnoSredstvo item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                    	button.getStyleClass().addAll("buttonTable", "buttonTableDelete");
                    	button.setTooltip(new Tooltip("Obriši"));
                    	button.getTooltip().setAutoHide(false);
                    	setGraphic(button);
                    	button.setOnMouseClicked(event -> {
                            osnovnaSredstvaTableView.getSelectionModel().select(item);
                            if(Util.showConfirmationAlert()) {
                                try {
                                    osnovnoSredstvoDAO.delete(PrijavaController.konekcija, item);
                                    osnovnaSredstvaList.remove(item);
                                    //getTableView().getItems().remove(item);
                                    osnovnaSredstvaTableView.refresh();
                                    //System.out.println("Obrisano: " + item);
                                    Util.getNotifications("Obavještenje", "Osnovno sredstvo obrisano.", "Information").show();
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
        
        Util.preventColumnReordering(osnovnaSredstvaTableView);
        
        invertarniBrojColumn.setMinWidth(75);
        invertarniBrojColumn.setMaxWidth(2000);
        
        nazivColumn.setMinWidth(100);
        nazivColumn.setMaxWidth(4000);
        
        vrstaColumn.setMinWidth(75);
        vrstaColumn.setMaxWidth(1500);
        
        vrijednostColumn.setMinWidth(100);
        vrijednostColumn.setMaxWidth(3000);
        
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
        
        osnovnaSredstvaTableView.setOnMouseClicked( event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                if(event.getClickCount() == 2) {
                    prikaziOS(osnovnaSredstvaTableView.getSelectionModel().getSelectedItem());
                }
            }
        });
        
        postaniNadzornikToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                PrijavaController.korisnik.setPrivilegijaTip(1);
            } else {
                PrijavaController.korisnik.setPrivilegijaTip(0);
            }
            postaviPrivilegije();
            osnovnaSredstvaTableView.refresh();
        });
    }
    
    private void prikaziOS(OsnovnoSredstvo item) {
        try {
            PrikazOsnovnogSredstvaController.odabranoOS=item;

            Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/PrikazOsnovnogSredstvaView.fxml"));
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
    }
    
    @FXML
    void clear(MouseEvent event) {
        traziTextField.clear();
    }
    
    @FXML
    void dodaj(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/DodavanjeOsnovnogSredstvaView.fxml"));
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

    @FXML
    void dodajVrstu(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/DodavanjeVrsteOSView.fxml"));
            Scene scene = new Scene(root);
            Stage stage=new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            if(DodavanjeVrsteOSController.vrstaOS != null) {
                //VrstaOSDAO.getVrsteOSList().add(DodavanjeVrsteOSController.vrstaOS);
                vrstaOsnovnogSredstvaList.add(DodavanjeVrsteOSController.vrstaOS);
                vrstaComboBox.getItems().add(DodavanjeVrsteOSController.vrstaOS);
            }
        } catch(IOException e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }
    
    @FXML
    void izmjeniVrstu(ActionEvent event) {
        try {
            DodavanjeVrsteOSController.odabranaVrsta = vrstaComboBox.getValue();
            DodavanjeVrsteOSController.izmjena = true;
            
            Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/DodavanjeVrsteOSView.fxml"));
            Scene scene = new Scene(root);
            Stage stage=new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            DodavanjeVrsteOSController.izmjena = false;
            osnovnaSredstvaTableView.refresh();
            int index=vrstaComboBox.getSelectionModel().getSelectedIndex();
            vrstaComboBox.getItems().clear();
            vrstaComboBox.getItems().add(0, new VrstaOS());
            vrstaComboBox.getItems().addAll(vrstaOsnovnogSredstvaList);
            vrstaComboBox.getSelectionModel().select(index);
        } catch(IOException e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }
    
    private void postaviPrivilegije() {
        if(PrijavaController.korisnik.getPrivilegijaTip()==1) {
            izmjeniColumn.setVisible(false);
            obrisiColumn.setVisible(false);
            dodajButton.setVisible(false);
            dodajVrstuButton.setVisible(false);
            izmjeniButton.setVisible(false);
        } else {
            izmjeniColumn.setVisible(true);
            obrisiColumn.setVisible(true);
            dodajButton.setVisible(true);
            dodajVrstuButton.setVisible(true);
            izmjeniButton.setVisible(true);
        }
    }

    @FXML
    private void pdf(ActionEvent event) {
        MaskerPane progressPane=Util.getMaskerPane(anchorPane);
        String naziv = "PDF/osnovna_sredstva/" + "OsnovnaSredstvaIzvjestaj_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".pdf";
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
                    document.add(new Paragraph("Izvještaj svih osnovnih sredstava", catFont));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph("Izvještaj kreirao: " + PrijavaController.korisnik.getKorisnickoIme(), smallBold));
                    document.add(new Paragraph("Datum i vrijeme kreiranja: " + new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss").format(new Date()), smallBold));
                    document.add(new Paragraph(" "));      
                    document.add(new Paragraph("Tabela svih osnovnih sredstava:", smallBold));
                    document.add(new Paragraph(" "));  
             
                    PdfPTable table = new PdfPTable(9);
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
                    
                    cell = new PdfPCell(new Phrase("Datum nabavke"));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    
                    cell = new PdfPCell(new Phrase("Nabavna vrijednost [KM]"));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    
                    cell = new PdfPCell(new Phrase("Stopa amortizacije"));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    cell = new PdfPCell(new Phrase("Trenutna vrijednost [KM]"));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    
                    cell = new PdfPCell(new Phrase("Lokacija"));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    
                    cell = new PdfPCell(new Phrase("Zadužena osoba"));
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
                                table.addCell(new SimpleDateFormat("dd.MM.yyyy").format(os.getDatumNabavke()));
                                table.addCell(os.getNabavnaVrijednost().toString());
                                table.addCell(String.valueOf(os.getStopaAmortizacije()));
                                table.addCell(os.getVrijednost().toString());      
                                table.addCell(new Phrase(new Chunk(LokacijeController.prostorijeList.stream().filter( pr -> pr.getId() == os.getIdLokacije()).findFirst().get().toString(),font)));
                                table.addCell(new Phrase(new Chunk(OsobeController.osobeList.stream().filter(o -> o.getId() == os.getIdOsobe()).findFirst().get().getImePrezime(),font)));
                        }
                }
                    else{
                        cell = new PdfPCell(new Phrase(new Chunk("Nema podataka o osnovnim sredstvima",font)));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setColspan(9);
                        table.addCell(cell);
                    }
                    document.add(table);
                    document.close();
                } catch (DocumentException | FileNotFoundException e) {
                    Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                } catch (IOException ex) {
                    Logger.getLogger(OsnovnaSredstvaController.class.getName()).log(Level.SEVERE, null, ex);
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
