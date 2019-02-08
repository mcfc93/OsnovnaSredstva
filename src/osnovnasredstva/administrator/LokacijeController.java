package osnovnasredstva.administrator;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import osnovnasredstva.DAO.ProstorijaDAO;
import osnovnasredstva.DAO.ZgradaDAO;
import osnovnasredstva.DTO.Prostorija;
import osnovnasredstva.DTO.Zgrada;
import static osnovnasredstva.administrator.KorisnickiNaloziController.korisnickiNaloziList;
import osnovnasredstva.prijava.PrijavaController;
import osnovnasredstva.util.Util;

/**
 * 
 * @author mcfc93
 */
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
    private JFXButton nazadButton;

    @FXML
    private JFXButton dodajLokacijuButton;

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

        zgradeList=FXCollections.observableArrayList();
        lokacijeList=FXCollections.observableArrayList();
        try {
            zgradeList.addAll(zgradaDAO.loadAll(PrijavaController.konekcija));
            lokacijeList.addAll(prostorijaDAO.loadAll(PrijavaController.konekcija));
        } catch (SQLException ex) {
            Logger.getLogger(LokacijeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        zgradaComboBox.getItems().addAll(zgradeList);
        zgradaComboBox.setOnAction(e -> {try {
            lokacijeTableView.getItems().clear();
            lokacijeList.clear();
            lokacijeList.addAll(prostorijaDAO.loadAll2(PrijavaController.konekcija,zgradaComboBox.getValue().getId()));
            lokacijeTableView.setItems(lokacijeList);
            if(lokacijeList.isEmpty())
                lokacijeTableView.setPlaceholder(new Label("Nema prostorija u odabranoj zgradi."));
            } catch (SQLException ex) {
                Logger.getLogger(LokacijeController.class.getName()).log(Level.SEVERE, null, ex);
            }
        
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
    }    
    
    @FXML
    void clear(MouseEvent event) {
        traziTextField.clear();
    }
}
