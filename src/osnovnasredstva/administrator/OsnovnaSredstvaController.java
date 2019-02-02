package osnovnasredstva.administrator;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import osnovnasredstva.DTO.OsnovnoSredstvo;
import osnovnasredstva.util.Util;

/**
 * 
 * @author mcfc93
 */
public class OsnovnaSredstvaController implements Initializable {

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
    private JFXButton nazadButton;

    @FXML
    private JFXButton dodajButton;

    @FXML
    private TextField traziTextField;

    @FXML
    private ImageView clearImageView;

    @FXML
    private JFXButton pdfButton;

    @FXML
    private JFXComboBox<?> vrstaComboBox;

    @FXML
    private JFXButton dodajVrstuButton;
    
    private ObservableList<OsnovnoSredstvo> osnovnaSredstvaList;
    
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

        osnovnaSredstvaList=FXCollections.observableArrayList(
                new OsnovnoSredstvo("A", "A", "A", null, null, 0, 0, 0, 0)
        );
        osnovnaSredstvaTableView.setItems(osnovnaSredstvaList);
        osnovnaSredstvaTableView.setPlaceholder(new Label("Odaberite prvo zgradu."));
        osnovnaSredstvaTableView.setFocusTraversable(false);
        
        invertarniBrojColumn.setCellValueFactory(new PropertyValueFactory<>("ime"));
        nazivColumn.setCellValueFactory(new PropertyValueFactory<>("prezime"));
        vrstaColumn.setCellValueFactory(new PropertyValueFactory<>("jmbg"));
        
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
                            //OsnovnoSredstvo o=getTableView().getItems().get(getIndex());
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
                            osnovnaSredstvaList.remove(item);
                            //getTableView().getItems().remove(item);
                            osnovnaSredstvaTableView.refresh();
                            System.out.println("Obrisano: " + item);
		            Util.getNotifications("Obavještenje", "Osnovno sredstvo obrisano.", "Information").show();
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
        } catch(IOException e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }
    
}
