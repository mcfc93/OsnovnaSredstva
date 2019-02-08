package osnovnasredstva.administrator;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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

/**
 *
 * @author mcfc93
 */
public class OsobeController implements Initializable {

    private static OsobaDAO osobaDAO = new OsobaDAO();
    
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
    private JFXButton nazadButton;

    @FXML
    private JFXButton pdfButton;

    @FXML
    private JFXButton dodajOsobuButton;

    @FXML
    private TextField traziTextField;

    @FXML
    private ImageView clearImageView;
    
    private ObservableList<Osoba> osobeList;
    
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

        
        osobeList = FXCollections.observableArrayList();
        
        MaskerPane progressPane=Util.getMaskerPane(anchorPane);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                System.out.println(Thread.currentThread());
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
        jmbgColumn.setCellValueFactory(new PropertyValueFactory<>("jmbg"));
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
                                Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/PrikazOsobeView.fxml"));
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
                            /*
                            osobeList.remove(item);
                            //getTableView().getItems().remove(item);
                            osobeTableView.refresh();
                            System.out.println("Obrisano: " + item);
		            Util.getNotifications("Obavještenje", "Osoba obrisana.", "Information").show();
                            */
                            try {
                                    if(osobaDAO.delete(PrijavaController.konekcija, item)){
                                        osobeList.remove(item);
                                        //getTableView().getItems().remove(item);
                                        osobeTableView.refresh();
                                        System.out.println("Obrisano: " + item);
                                        Util.getNotifications("Obavještenje", "Korisnički nalog obrisan.", "Information").show();
                                    }
                                } catch (SQLException | NotFoundException e) {
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
}
