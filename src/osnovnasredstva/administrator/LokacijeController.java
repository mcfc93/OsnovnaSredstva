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
import osnovnasredstva.DAO.ProstorijaDAO;
import osnovnasredstva.DAO.ZgradaDAO;
import osnovnasredstva.DTO.Prostorija;
import osnovnasredstva.DTO.Zgrada;
import osnovnasredstva.prijava.PrijavaController;
import osnovnasredstva.util.NotFoundException;
import osnovnasredstva.util.Util;

public class LokacijeController implements Initializable {
    
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TableView<Prostorija> lokacijeTableView;

    @FXML
    private TableColumn<?, ?> sifraColumn;

    @FXML
    private TableColumn<?, ?> nazivColumn;

    @FXML
    private TableColumn<?, ?> zgradaColumn;

    @FXML
    private TableColumn<Prostorija, Prostorija> prikaziColumn;

    @FXML
    private TableColumn<Prostorija, Prostorija> izmjeniColumn;

    @FXML
    private TableColumn<Prostorija, Prostorija> obrisiColumn;

    @FXML
    private JFXButton dodajProstorijuButton;
    
    @FXML
    private JFXButton dodajZgraduButton;

    @FXML
    private TextField traziTextField;

    @FXML
    private ImageView clearImageView;

    @FXML
    private JFXButton pdfButton;

    @FXML
    private JFXComboBox<Zgrada> zgradaComboBox;
    
    @FXML
    private JFXButton izmjeniButton;
    
    
    private static ZgradaDAO zgradaDAO = new ZgradaDAO();
    
    private static ProstorijaDAO prostorijaDAO = new ProstorijaDAO();
    
    @FXML
    private JFXToggleButton postaniNadzornikToggleButton;
    
    public static ObservableList<Zgrada> zgradeList = FXCollections.observableArrayList();
    
    public static ObservableList<Prostorija> prostorijeList = FXCollections.observableArrayList();
    private static FilteredList<Prostorija> filteredList;
    private SortedList<Prostorija> sortedList;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clearImageView.setVisible(false);
		
        traziTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            //filteredList.setPredicate(prostorija -> prostorija.getNaziv().toLowerCase().startsWith(newValue.toLowerCase()));
            filteredList.setPredicate(prostorija -> zgradaComboBox.getValue().getNaziv() != null? prostorija.getIdZgrade() == zgradaComboBox.getValue().getId() && prostorija.getNaziv().toLowerCase().contains(newValue.toLowerCase()): prostorija.getNaziv().toLowerCase().contains(newValue.toLowerCase()));
            
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
            dodajProstorijuButton.setVisible(false);
            dodajZgraduButton.setVisible(false);
            postaniNadzornikToggleButton.setVisible(false);
        } else {
            if(PrijavaController.korisnik.getTip() != PrijavaController.korisnik.getPrivilegijaTip()) {
                PrijavaController.korisnik.setPrivilegijaTip(PrijavaController.korisnik.getTip());
            }
        }

        //zgradeList.clear();
        //lokacijeList.clear();
        filteredList = new FilteredList(prostorijeList);
        sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(lokacijeTableView.comparatorProperty());

        MaskerPane progressPane=Util.getMaskerPane(anchorPane);
        /*
        new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                progressPane.setVisible(true);
                try {
                    zgradeList.addAll(zgradaDAO.loadAll(PrijavaController.konekcija));
                    lokacijeList.addAll(prostorijaDAO.loadAll(PrijavaController.konekcija));
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
                    zgradaComboBox.getItems().add(0, new Zgrada());
                    zgradaComboBox.getItems().addAll(zgradeList);
                    zgradaComboBox.getSelectionModel().selectFirst();
                });
            }
        }).start();
        */
                zgradaComboBox.getItems().add(0, new Zgrada());
                zgradaComboBox.getItems().addAll(zgradeList);
                zgradaComboBox.getSelectionModel().selectFirst();
        
        zgradaComboBox.setVisibleRowCount(5);

        //zgradeList.add(0, null);
        
        zgradaComboBox.setCellFactory(param -> {
            ListCell<Zgrada> cell = new ListCell<Zgrada>() {
                @Override
                protected void updateItem(Zgrada item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        //setText("Svi");
                        setGraphic(null);
                    } else {
                        if(item.getNaziv()!=null) {
                            setText(item.getNaziv()+ " (" +item.getSifra() + ")");
                        } else {
                            setText("Sve");
                        }
                    }
                }
            };
            return cell;
        });
        
        zgradaComboBox.setConverter(new StringConverter<Zgrada>() {
            //ispis u ComboBox selected item value
            @Override
            public String toString(Zgrada object) {
                return object.getNaziv()!=null? object.getNaziv(): "Sve";
            }
            //Editable ComboBox pretraga
            @Override
            public Zgrada fromString(String string) {
                return zgradaComboBox.getItems().stream().filter(z -> z.getNaziv().equals(string)).findFirst().orElse(null);
            }
        });
        
        zgradaComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                if(zgradaComboBox.getValue().getNaziv() != null) {
                    izmjeniButton.setVisible(true);
                } else {
                    izmjeniButton.setVisible(false);
                }
                new Thread(new Task<Void>() {
                    @Override
                    protected Void call() {
                        progressPane.setVisible(true);
                        
                        filteredList.setPredicate(prostorija -> zgradaComboBox.getValue().getNaziv() != null? prostorija.getIdZgrade() == zgradaComboBox.getValue().getId(): true);
                        
                        Platform.runLater(() -> {
                            traziTextField.clear();
                        });
                        /*
                        try {
                            lokacijeList.clear();
                            if(zgradaComboBox.getValue().getNaziv() != null)
                                lokacijeList.addAll(prostorijaDAO.loadAll2(PrijavaController.konekcija,zgradaComboBox.getValue().getId()));
                            else
                                lokacijeList.addAll(prostorijaDAO.loadAll(PrijavaController.konekcija));
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
                            lokacijeTableView.refresh();
                        });
                    }
                }).start();

            }
        });
        
        lokacijeTableView.setItems(sortedList);
        lokacijeTableView.setPlaceholder(new Label("Nema prostorija u odabranoj zgradi."));
        lokacijeTableView.setFocusTraversable(false);
        
        sifraColumn.setCellValueFactory(new PropertyValueFactory<>("sifra"));
        nazivColumn.setCellValueFactory(new PropertyValueFactory<>("naziv"));
        zgradaColumn.setCellValueFactory(new PropertyValueFactory<>("nazivZgrade"));
        
        prikaziColumn.setCellValueFactory(
            param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );
        
        prikaziColumn.setCellFactory(tableCell -> {
            TableCell<Prostorija, Prostorija> cell = new TableCell<Prostorija, Prostorija>() {
                private final Button button = new Button("");
                @Override
                protected void updateItem(Prostorija item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        button.getStyleClass().addAll("buttonTable", "buttonTableShow");
                    	button.setTooltip(new Tooltip("Prikaži"));
                    	button.getTooltip().setAutoHide(false);
                    	setGraphic(button);
                    	button.setOnMouseClicked(event -> {
                            lokacijeTableView.getSelectionModel().select(item);
                            prikaziLokaciju(item);
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
            TableCell<Prostorija, Prostorija> cell = new TableCell<Prostorija, Prostorija>() {
                private final Button button = new Button("");
                @Override
                protected void updateItem(Prostorija item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        button.getStyleClass().addAll("buttonTable", "buttonTableEdit");
                    	button.setTooltip(new Tooltip("Izmjeni"));
                    	button.getTooltip().setAutoHide(false);
                    	setGraphic(button);
                    	button.setOnMouseClicked(event -> {
                            lokacijeTableView.getSelectionModel().select(item);
                            try {
                                DodavanjeProstorijeController.odabranaProstorija=item;
                                DodavanjeProstorijeController.izmjena=true;
                                
                                Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/DodavanjeProstorijeView.fxml"));
                                Scene scene = new Scene(root);
                                Stage stage=new Stage();
                                stage.setScene(scene);
                                stage.setResizable(false);
                                stage.initStyle(StageStyle.UNDECORATED);
                                stage.initModality(Modality.APPLICATION_MODAL);
                                stage.showAndWait();
                                
                                DodavanjeProstorijeController.izmjena=false;
                                lokacijeTableView.refresh();
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
            TableCell<Prostorija, Prostorija> cell = new TableCell<Prostorija, Prostorija>() {
                private final Button button = new Button("");
                @Override
                protected void updateItem(Prostorija item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        button.getStyleClass().addAll("buttonTable", "buttonTableDelete");
                    	button.setTooltip(new Tooltip("Obriši"));
                    	button.getTooltip().setAutoHide(false);
                    	setGraphic(button);
                    	button.setOnMouseClicked(event -> {
                            lokacijeTableView.getSelectionModel().select(item);
                            if(Util.showConfirmationAlert()) {
                                if(!OsnovnaSredstvaController.osnovnaSredstvaList.stream().anyMatch(os -> item.getId() == os.getIdLokacije())){
                                try {
                                    prostorijaDAO.delete(PrijavaController.konekcija, item);
                                    prostorijeList.remove(item);
                                    //ProstorijaDAO.getProstorijeList().remove(item);
                                    //getTableView().getItems().remove(item);
                                    lokacijeTableView.refresh();
                                    //System.out.println("Obrisano: " + item);
                                    Util.getNotifications("Obavještenje", "Lokacija obrisana.", "Information").show();
                                } catch (SQLException | NotFoundException e) {
                                    Util.showBugAlert();
                                    Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                                    }
                                }
                                else{
                                    Util.getNotifications("Greška", "Prostorija nije prazna.", "Warning").show();
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
        
        Util.preventColumnReordering(lokacijeTableView);
        
        sifraColumn.setMinWidth(50);
        sifraColumn.setMaxWidth(1000);
        
        nazivColumn.setMinWidth(100);
        nazivColumn.setMaxWidth(3000);
        
        zgradaColumn.setMinWidth(100);
        zgradaColumn.setMaxWidth(4000);
        
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
        
        lokacijeTableView.setOnMouseClicked( event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                if(event.getClickCount() == 2) {
                    prikaziLokaciju(lokacijeTableView.getSelectionModel().getSelectedItem());
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
            lokacijeTableView.refresh();
        });
    }
    
    private void prikaziLokaciju(Prostorija item) {
        try {
            PrikazProstorijeController.odabranaProstorija=item;

            Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/PrikazProstorijeView.fxml"));
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
    
    private void postaviPrivilegije() {
        if(PrijavaController.korisnik.getPrivilegijaTip()==1) {
            izmjeniColumn.setVisible(false);
            obrisiColumn.setVisible(false);
            dodajProstorijuButton.setVisible(false);
            dodajZgraduButton.setVisible(false);
            izmjeniButton.setVisible(false);
        } else {
            izmjeniColumn.setVisible(true);
            obrisiColumn.setVisible(true);
            dodajProstorijuButton.setVisible(true);
            dodajZgraduButton.setVisible(true);
            izmjeniButton.setVisible(true);
        }
    }

    @FXML
    private void dodajProstoriju(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/DodavanjeProstorijeView.fxml"));
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
    private void dodajZgradu(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/DodavanjeZgradeView.fxml"));
            Scene scene = new Scene(root);
            Stage stage=new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            if(DodavanjeZgradeController.zgrada != null) {
                //ZgradaDAO.getZgradeList().add(DodavanjeZgradeController.zgrada);
                zgradeList.add(DodavanjeZgradeController.zgrada);
                zgradaComboBox.getItems().add(DodavanjeZgradeController.zgrada);
            }
        } catch(IOException e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }
    
    @FXML
    private void izmjeniZgradu(ActionEvent event) {
        try {
            DodavanjeZgradeController.odabranaZgrda = zgradaComboBox.getValue();
            DodavanjeZgradeController.izmjena = true;
            
            Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/DodavanjeZgradeView.fxml"));
            Scene scene = new Scene(root);
            Stage stage=new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            DodavanjeZgradeController.izmjena = false;
            lokacijeTableView.refresh();
            int index=zgradaComboBox.getSelectionModel().getSelectedIndex();
            zgradaComboBox.getItems().clear();
            zgradaComboBox.getItems().add(0, new Zgrada());
            zgradaComboBox.getItems().addAll(zgradeList);
            zgradaComboBox.getSelectionModel().select(index);
        } catch(IOException e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

    @FXML
    private void pdf(ActionEvent event) {
        MaskerPane progressPane=Util.getMaskerPane(anchorPane);
        String naziv = "PDF/prostorije/" + "ProstorijeIzvjestaj_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".pdf";
        
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
                    document.add(new Paragraph("Izvještaj svih prostorija", catFont));
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph("Izvještaj kreirao: " + PrijavaController.korisnik.getKorisnickoIme(), smallBold));
                    document.add(new Paragraph("Datum i vrijeme kreiranja: " + new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss").format(new Date()), smallBold));
                    document.add(new Paragraph(" "));      
                    
                    
                    document.add(new Paragraph(" "));
                    document.add(new Paragraph("Tabela svih prostorija:", smallBold));
                    document.add(new Paragraph(" "));
             
                    PdfPTable table = new PdfPTable(4);
                    table.setWidthPercentage(100);
                    PdfPCell cell = new PdfPCell(new Phrase("Šifra"));
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

                    cell = new PdfPCell(new Phrase("Zgrada"));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);

                    table.setHeaderRows(1);
                    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                    if(!prostorijeList.isEmpty()){
                        prostorijeList.forEach(pr ->{
                                table.addCell(pr.getSifra());
                                table.addCell(new Phrase(new Chunk(pr.getNaziv(), font)));
                                table.addCell(new Phrase(new Chunk(pr.getOpis(), font)));
                          zgradeList.forEach(zg ->{
                              if(pr.getIdZgrade() == zg.getId())
                                  table.addCell(zg.getNaziv());
                          });
                          
                        });
                }
                    else{
                        cell = new PdfPCell(new Phrase(new Chunk("Nema podataka o prostorijama",font)));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setColspan(4);
                        table.addCell(cell);
                    }
                    document.add(table);
                    document.close();
                } catch (DocumentException | FileNotFoundException e) {
                    Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                } catch (IOException ex) {
                    Logger.getLogger(LokacijeController.class.getName()).log(Level.SEVERE, null, ex);
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
