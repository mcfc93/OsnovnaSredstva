package osnovnasredstva.administrator;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    
    public static ObservableList<Prelaznica> prelazniceList = FXCollections.observableArrayList();;
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
        
        //Column.setCellValueFactory(new PropertyValueFactory<>(""));
        //Column.setCellValueFactory(new PropertyValueFactory<>(""));
        
        Util.preventColumnReordering(prelazniceTableView);
    }
    
    @FXML
    void clear(MouseEvent event) {
        traziTextField.clear();
    }

    @FXML
    void pdf(ActionEvent event) {

    }
    
}
