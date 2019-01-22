package osnovnasredstva.administrator;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
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
import osnovnasredstva.beans.Osoba;
import osnovnasredstva.util.Util;

/**
 *
 * @author w7
 */
public class KorisnickiNaloziController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TableView<Osoba> korisnickiNaloziTableView;

    @FXML
    private TableColumn<?, ?> korisnickoImeColumn;

    @FXML
    private TableColumn<?, ?> hashColumn;

    @FXML
    private TableColumn<?, ?> tipColumn;

    @FXML
    private TableColumn<Osoba, Osoba> prikaziColumn;

    @FXML
    private TableColumn<Osoba, Osoba> izmjeniColumn;

    @FXML
    private TableColumn<Osoba, Osoba> obrisiColumn;

    @FXML
    private JFXButton dodajButton;

    @FXML
    private TextField traziTextField;

    @FXML
    private ImageView clearImageView;

    private ObservableList<Osoba> korisnickiNaloziList;
    
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

        korisnickiNaloziList=FXCollections.observableArrayList(
                new Osoba("A", "A", "A", "A"),
                new Osoba("B", "B", "B", "B"),
                new Osoba("C", "C", "C", "C"),
                new Osoba("A", "A", "A", "A"),
                new Osoba("B", "B", "B", "B"),
                new Osoba("C", "C", "C", "C"),
                new Osoba("A", "A", "A", "A"),
                new Osoba("B", "B", "B", "B"),
                new Osoba("C", "C", "C", "C"),
                new Osoba("A", "A", "A", "A"),
                new Osoba("B", "B", "B", "B"),
                new Osoba("C", "C", "C", "C")
        );
        korisnickiNaloziTableView.setItems(korisnickiNaloziList);
        korisnickiNaloziTableView.setPlaceholder(new Label("Odaberite prvo zgradu."));
        korisnickiNaloziTableView.setFocusTraversable(false);
        
        korisnickoImeColumn.setCellValueFactory(new PropertyValueFactory<>("ime"));
        hashColumn.setCellValueFactory(new PropertyValueFactory<>("prezime"));
        tipColumn.setCellValueFactory(new PropertyValueFactory<>("jmbg"));
        
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
                            korisnickiNaloziList.remove(item);
                            //getTableView().getItems().remove(item);
                            korisnickiNaloziTableView.refresh();
                            System.out.println("Obrisano: " + item);
		            Util.getNotifications("Obavještenje", "Korisnički nalog obrisan.", "Information").show();
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
