package osnovnasredstva.administrator;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleButton;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.controlsfx.control.MaskerPane;
import osnovnasredstva.DAO.ProstorijaDAO;
import osnovnasredstva.DAO.ZgradaDAO;
import osnovnasredstva.DTO.Prostorija;
import osnovnasredstva.DTO.Zgrada;
import osnovnasredstva.prijava.PrijavaController;
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
    private JFXButton dodajButton;

    @FXML
    private TextField traziTextField;

    @FXML
    private ImageView clearImageView;

    @FXML
    private JFXButton pdfButton;

    @FXML
    private JFXComboBox<Zgrada> zgradaComboBox;
    
    private ObservableList<Prostorija> lokacijeList;
    
    private ObservableList<Zgrada> zgradeList;
    
    private static ZgradaDAO zgradaDAO = new ZgradaDAO();
    
    private static ProstorijaDAO prostorijaDAO = new ProstorijaDAO();
    
    @FXML
    private JFXToggleButton postaniNadzornikToggleButton;
    
    /**
     * Initializes the controller class.
     */
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
            postaniNadzornikToggleButton.setVisible(false);
        } else {
            if(PrijavaController.korisnik.getTip() != PrijavaController.korisnik.getPrivilegijaTip()) {
                PrijavaController.korisnik.setPrivilegijaTip(PrijavaController.korisnik.getTip());
            }
        }

        zgradeList=FXCollections.observableArrayList();
        lokacijeList=FXCollections.observableArrayList();
        
        MaskerPane progressPane=Util.getMaskerPane(anchorPane);
        Task<Void> task = new Task<Void>() {
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
        };
        new Thread(task).start();

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
                            setText("Svi");
                        }
                    }
                }
            };
            return cell;
        });
        
        
        zgradaComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            
                //lokacijeTableView.getItems().clear();
                lokacijeList.clear();
                MaskerPane progressPaneZgrade=Util.getMaskerPane(anchorPane);
                Task<Void> taskZgrade = new Task<Void>() {
                    @Override
                    protected Void call() {
                        progressPaneZgrade.setVisible(true);
                        try {
                            if(zgradaComboBox.getValue().getNaziv() != null)
                                lokacijeList.addAll(prostorijaDAO.loadAll2(PrijavaController.konekcija,zgradaComboBox.getValue().getId()));
                            else
                                lokacijeList.addAll(prostorijaDAO.loadAll(PrijavaController.konekcija));
                            } catch (SQLException e) {
                            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                        }
                        return null;
                    }
                    
                    @Override
                    protected void succeeded(){
                        super.succeeded();
                        progressPaneZgrade.setVisible(false);
                    }
                };
                new Thread(taskZgrade).start();

                lokacijeTableView.setItems(lokacijeList);           
                if(lokacijeList.isEmpty())
                    lokacijeTableView.setPlaceholder(new Label("Nema prostorija u odabranoj zgradi."));
        });
        
        
        lokacijeTableView.setItems(lokacijeList);
        lokacijeTableView.setPlaceholder(new Label("Odaberite prvo zgradu."));
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
                            //Zgrada o=getTableView().getItems().get(getIndex());
                            System.out.println(item);
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
                            //Zgrada o=getTableView().getItems().get(getIndex());
                            System.out.println(item);
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
                            lokacijeList.remove(item);
                            //getTableView().getItems().remove(item);
                            lokacijeTableView.refresh();
                            System.out.println("Obrisano: " + item);
		            Util.getNotifications("Obavještenje", "Lokacija obrisana.", "Information").show();
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
            dodajButton.setVisible(false);
        } else {
            izmjeniColumn.setVisible(true);
            obrisiColumn.setVisible(true);
            dodajButton.setVisible(true);
        }
    }
}
