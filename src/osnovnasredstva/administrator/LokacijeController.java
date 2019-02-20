package osnovnasredstva.administrator;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleButton;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
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
    private TableColumn<?, ?> opisColumn;

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
    
    
    private static ZgradaDAO zgradaDAO = new ZgradaDAO();
    
    private static ProstorijaDAO prostorijaDAO = new ProstorijaDAO();
    
    @FXML
    private JFXToggleButton postaniNadzornikToggleButton;
    
    private static ObservableList<Zgrada> zgradeList;
    
    public static ObservableList<Prostorija> lokacijeList;
    private static FilteredList<Prostorija> filteredList;
    SortedList<Prostorija> sortedList;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clearImageView.setVisible(false);
		
        traziTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            filteredList.setPredicate(prostorija -> prostorija.getNaziv().toLowerCase().contains(newValue.toLowerCase()));
            if(!newValue.isEmpty()) {
                clearImageView.setVisible(true);
            } else {
                clearImageView.setVisible(false);
            }
        });
        
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

        zgradeList = FXCollections.observableArrayList();
        lokacijeList = FXCollections.observableArrayList();
        filteredList = new FilteredList(lokacijeList);
        sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(lokacijeTableView.comparatorProperty());
        
        MaskerPane progressPane=Util.getMaskerPane(anchorPane);
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
                    zgradeList.add(0, new Zgrada());
                    zgradaComboBox.getItems().addAll(zgradeList);
                    zgradaComboBox.getSelectionModel().selectFirst();
                });
            }
        }).start();
        
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
                new Thread(new Task<Void>() {
                    @Override
                    protected Void call() {
                        progressPane.setVisible(true);
                        filteredList.setPredicate(prostorija -> zgradaComboBox.getValue().getNaziv() != null? prostorija.getIdZgrade() == zgradaComboBox.getValue().getId(): true);
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
        opisColumn.setCellValueFactory(new PropertyValueFactory<>("opis"));
        
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
                    	button.setTooltip(new Tooltip("Prikaži?"));
                    	button.getTooltip().setAutoHide(false);
                    	setGraphic(button);
                    	button.setOnMouseClicked(event -> {
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
                    	button.setTooltip(new Tooltip("Izmjeni?"));
                    	button.getTooltip().setAutoHide(false);
                    	setGraphic(button);
                    	button.setOnMouseClicked(event -> {
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
                    	button.setTooltip(new Tooltip("Obriši?"));
                    	button.getTooltip().setAutoHide(false);
                    	setGraphic(button);
                    	button.setOnMouseClicked(event -> {
                            if(Util.showConfirmationAlert()) {
                                try {
                                    prostorijaDAO.delete(PrijavaController.konekcija, item);
                                    lokacijeList.remove(item);
                                    ProstorijaDAO.getProstorijeList().remove(item);
                                    //getTableView().getItems().remove(item);
                                    lokacijeTableView.refresh();
                                    System.out.println("Obrisano: " + item);
                                    Util.getNotifications("Obavještenje", "Lokacija obrisana.", "Information").show();
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
        
        Util.preventColumnReordering(lokacijeTableView);
        
        sifraColumn.setMinWidth(50);
        sifraColumn.setMaxWidth(1000);
        
        nazivColumn.setMinWidth(100);
        nazivColumn.setMaxWidth(3000);
        
        opisColumn.setMinWidth(100);
        opisColumn.setMaxWidth(4000);
        
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
            lokacijeTableView.refresh();
        });
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
        } else {
            izmjeniColumn.setVisible(true);
            obrisiColumn.setVisible(true);
            dodajProstorijuButton.setVisible(true);
            dodajZgraduButton.setVisible(true);
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
                ZgradaDAO.getZgradeList().add(DodavanjeZgradeController.zgrada);
                zgradeList.add(DodavanjeZgradeController.zgrada);
                zgradaComboBox.getItems().add(DodavanjeZgradeController.zgrada);
            }
        } catch(IOException e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }
    
    
}
