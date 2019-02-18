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
import osnovnasredstva.DAO.OsnovnoSredstvoDAO;
import osnovnasredstva.DAO.VrstaOSDAO;
import osnovnasredstva.DTO.OsnovnoSredstvo;
import osnovnasredstva.DTO.VrstaOS;
import osnovnasredstva.prijava.PrijavaController;
import osnovnasredstva.util.NotFoundException;
import osnovnasredstva.util.Util;

public class OsnovnaSredstvaController implements Initializable {
    
    private static OsnovnoSredstvoDAO osnovnoSredstvoDAO = new OsnovnoSredstvoDAO();
    private static VrstaOSDAO vrstaOSDAO = new VrstaOSDAO();
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TableView<OsnovnoSredstvo> osnovnaSredstvaTableView;

    @FXML
    private TableColumn<?, ?> invertarniBrojColumn;

    @FXML
    private TableColumn<?, ?> nazivColumn;

    @FXML
    private TableColumn<?, ?> vrstaColumn;

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
    
    public static ObservableList<OsnovnoSredstvo> osnovnaSredstvaList;
    public static ObservableList<VrstaOS> vrstaOsnovnogSredstvaList;
    
    @FXML
    private JFXToggleButton postaniNadzornikToggleButton;
    
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
            dodajButton.setVisible(false);
            dodajVrstuButton.setVisible(false);
            postaniNadzornikToggleButton.setVisible(false);
        } else {
            if(PrijavaController.korisnik.getTip() != PrijavaController.korisnik.getPrivilegijaTip()) {
                PrijavaController.korisnik.setPrivilegijaTip(PrijavaController.korisnik.getTip());
            }
        }
        
        vrstaOsnovnogSredstvaList = FXCollections.observableArrayList();
        osnovnaSredstvaList = FXCollections.observableArrayList();
        /*
        MaskerPane progressPane=Util.getMaskerPane(anchorPane);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                progressPane.setVisible(true);
                try {
                    vrstaOsnovnogSredstvaList.addAll(vrstaOSDAO.loadAll(PrijavaController.konekcija));
                    //osnovnaSredstvaList.addAll(osnovnoSredstvoDAO.loadAll(PrijavaController.konekcija)); 
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
                    vrstaOsnovnogSredstvaList.add(0, new VrstaOS());
                    vrstaComboBox.getItems().addAll(vrstaOsnovnogSredstvaList);
                    vrstaComboBox.getSelectionModel().selectFirst();
                });
            }
        };
        new Thread(task).start();
        */
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
            osnovnaSredstvaList.clear();
            MaskerPane progressPane=Util.getMaskerPane(anchorPane);
            new Thread(new Task<Void>() {
                @Override
                protected Void call() {
                    progressPane.setVisible(true);
                    try {
                        //osnovnaSredstvaList.clear();
                        if(vrstaComboBox.getValue().getNaziv() != null)
                            osnovnaSredstvaList.addAll(osnovnoSredstvoDAO.loadAll2(PrijavaController.konekcija,vrstaComboBox.getValue().getId()));
                        else
                            osnovnaSredstvaList.addAll(osnovnoSredstvoDAO.loadAll(PrijavaController.konekcija));
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

            //osnovnaSredstvaTableView.setItems(osnovnaSredstvaList);
            //if(osnovnaSredstvaList.isEmpty())
            //    osnovnaSredstvaTableView.setPlaceholder(new Label("Nema osnovnih sredstava za odabranu vrstu."));
        });
        
        vrstaOsnovnogSredstvaList.addAll(VrstaOSDAO.getVrsteOSList());
        vrstaOsnovnogSredstvaList.add(0, new VrstaOS());
        vrstaComboBox.getItems().addAll(vrstaOsnovnogSredstvaList);
        vrstaComboBox.getSelectionModel().selectFirst();

        
        //osnovnaSredstvaList=FXCollections.observableArrayList();
        osnovnaSredstvaTableView.setItems(osnovnaSredstvaList);
        osnovnaSredstvaTableView.setPlaceholder(new Label("Nema osnovnih sredstava za odabranu vrstu."));
        osnovnaSredstvaTableView.setFocusTraversable(false);
        
        invertarniBrojColumn.setCellValueFactory(new PropertyValueFactory<>("inventarniBroj"));
        nazivColumn.setCellValueFactory(new PropertyValueFactory<>("naziv"));
        vrstaColumn.setCellValueFactory(new PropertyValueFactory<>("idVrsteString"));
        
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
                            //OsnovnoSredstvo o=getTableView().getItems().get(getIndex());
                            System.out.println(item);
                            
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
                                    osnovnoSredstvoDAO.delete(PrijavaController.konekcija, item);
                                    osnovnaSredstvaList.remove(item);
                                    //getTableView().getItems().remove(item);
                                    osnovnaSredstvaTableView.refresh();
                                    System.out.println("Obrisano: " + item);
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
            osnovnaSredstvaTableView.refresh();
        });
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
                VrstaOSDAO.getVrsteOSList().add(DodavanjeVrsteOSController.vrstaOS);
                vrstaOsnovnogSredstvaList.add(DodavanjeVrsteOSController.vrstaOS);
                vrstaComboBox.getItems().add(DodavanjeVrsteOSController.vrstaOS);
            }
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
        } else {
            izmjeniColumn.setVisible(true);
            obrisiColumn.setVisible(true);
            dodajButton.setVisible(true);
            dodajVrstuButton.setVisible(true);
        }
    }
}
