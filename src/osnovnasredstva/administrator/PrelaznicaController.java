package osnovnasredstva.administrator;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
import org.controlsfx.control.MaskerPane;
import osnovnasredstva.DAO.PrelaznicaDAO;
import osnovnasredstva.DTO.Prelaznica;
import osnovnasredstva.prijava.PrijavaController;
import osnovnasredstva.util.Util;

public class PrelaznicaController implements Initializable {
    
    private static PrelaznicaDAO prelaznicaDAO = new PrelaznicaDAO();

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TableView<Prelaznica> prelazniceTableView;

    @FXML
    private JFXButton pdfButton;

    @FXML
    private TextField traziTextField;

    @FXML
    private ImageView clearImageView;

    @FXML
    private JFXToggleButton postaniNadzornikToggleButton;
    @FXML
    private TableColumn<?, ?> nazivColumn;
    @FXML
    private TableColumn<?, ?> datumColumn;
    @FXML
    private TableColumn<Prelaznica, Prelaznica> otvoriColumn;
    
    public static ObservableList<Prelaznica> prelazniceList = FXCollections.observableArrayList();
    private static FilteredList<Prelaznica> filteredList;
    SortedList<Prelaznica> sortedList;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clearImageView.setVisible(false);
		
        traziTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            //filteredList.setPredicate(prelaznica -> prelaznica.get().contains(newValue.));
            if(!newValue.isEmpty()) {
                clearImageView.setVisible(true);
            } else {
                clearImageView.setVisible(false);
            }
        });
        
        prelazniceList.clear();
        filteredList = new FilteredList<>(prelazniceList);
        sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(prelazniceTableView.comparatorProperty());
        
        MaskerPane progressPane=Util.getMaskerPane(anchorPane);
        new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                progressPane.setVisible(true);
                try {
                    prelazniceList.addAll(prelaznicaDAO.loadAll(PrijavaController.konekcija));
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
                    prelazniceTableView.refresh();
                });
            }
        }).start();
        
        if(PrijavaController.korisnik.getTip() != PrijavaController.korisnik.getPrivilegijaTip()) {
            PrijavaController.korisnik.setPrivilegijaTip(PrijavaController.korisnik.getTip());
        }
        
        prelazniceTableView.setItems(sortedList);
        prelazniceTableView.setPlaceholder(new Label("Nema korisničkih naloga."));
        prelazniceTableView.setFocusTraversable(false);
        
        nazivColumn.setCellValueFactory(new PropertyValueFactory<>("naziv"));
        datumColumn.setCellValueFactory(new PropertyValueFactory<>("datum"));
        
        otvoriColumn.setVisible(true);
        
        otvoriColumn.setCellValueFactory(
            param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );
        
        otvoriColumn.setCellFactory(tableCell -> {
            TableCell<Prelaznica, Prelaznica> cell = new TableCell<Prelaznica, Prelaznica>() {
                private final Button button = new Button("");
                @Override
                protected void updateItem(Prelaznica item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                    	button.getStyleClass().addAll("buttonTable", "buttonTableShow");
                    	button.setTooltip(new Tooltip("Otvori"));
                    	button.getTooltip().setAutoHide(false);
                    	setGraphic(button);
                    	button.setOnMouseClicked(event -> {
                            String naziv = "PDF/prelaznica/" + "Prelaznica_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(item.getDatumPrelaska()) + ".pdf";
                            if(Desktop.isDesktopSupported()) {
                                new Thread(() -> {
                                    try {
                                        Desktop.getDesktop().open(new File(naziv));
                                    } catch (IOException e) {
                                        Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                                    }
                                }).start();
                            }
                            
                        });
                    } else {
                    	setGraphic(null);
                    }
                }
            };
            return cell;
        });
        
        
        
        //Column.setCellValueFactory(new PropertyValueFactory<>(""));
        //Column.setCellValueFactory(new PropertyValueFactory<>(""));
        
        
        Util.preventColumnReordering(prelazniceTableView);
    }
    
    @FXML
    void clear(MouseEvent event) {
        traziTextField.clear();
    }
    
}
