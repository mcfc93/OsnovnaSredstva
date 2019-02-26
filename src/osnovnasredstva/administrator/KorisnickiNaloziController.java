package osnovnasredstva.administrator;

import com.jfoenix.controls.JFXButton;
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
import osnovnasredstva.DAO.KorisnikDAO;
import osnovnasredstva.DTO.Korisnik;
import osnovnasredstva.prijava.PrijavaController;
import osnovnasredstva.util.NotFoundException;
import osnovnasredstva.util.Util;

public class KorisnickiNaloziController implements Initializable {

    private static KorisnikDAO korisnikDAO = new KorisnikDAO();

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TableView<Korisnik> korisnickiNaloziTableView;

    @FXML
    private TableColumn<?, ?> korisnickoImeColumn;

    @FXML
    private TableColumn<?, ?> tipColumn;

    @FXML
    private TableColumn<Korisnik, Korisnik> prikaziColumn;

    @FXML
    private TableColumn<Korisnik, Korisnik> izmjeniColumn;

    @FXML
    private TableColumn<Korisnik, Korisnik> obrisiColumn;

    @FXML
    private JFXButton dodajButton;

    @FXML
    private TextField traziTextField;

    @FXML
    private ImageView clearImageView;

    public static ObservableList<Korisnik> korisnickiNaloziList = FXCollections.observableArrayList();
    private static FilteredList<Korisnik> filteredList;
    private SortedList<Korisnik> sortedList;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clearImageView.setVisible(false);
		
        traziTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
            filteredList.setPredicate(korisnik -> korisnik.getKorisnickoIme().toLowerCase().startsWith(newValue.toLowerCase()));
            if(!newValue.isEmpty()) {
                clearImageView.setVisible(true);
            } else {
                clearImageView.setVisible(false);
            }
        });

        korisnickiNaloziList.clear();
        filteredList = new FilteredList<>(korisnickiNaloziList);
        sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(korisnickiNaloziTableView.comparatorProperty());
        
        MaskerPane progressPane=Util.getMaskerPane(anchorPane);
        new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                progressPane.setVisible(true);
                korisnickiNaloziList.clear();
                try {
                    korisnickiNaloziList.addAll(korisnikDAO.loadAll(PrijavaController.konekcija));
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
                    korisnickiNaloziTableView.refresh();
                });
            }
        }).start();
        
        if(PrijavaController.korisnik.getTip() != PrijavaController.korisnik.getPrivilegijaTip()) {
            PrijavaController.korisnik.setPrivilegijaTip(PrijavaController.korisnik.getTip());
        }
        
        korisnickiNaloziTableView.setItems(sortedList);
        korisnickiNaloziTableView.setPlaceholder(new Label("Nema korisničkih naloga."));
        korisnickiNaloziTableView.setFocusTraversable(false);
        
        korisnickoImeColumn.setCellValueFactory(new PropertyValueFactory<>("korisnickoIme"));
        tipColumn.setCellValueFactory(new PropertyValueFactory<>("tipString"));
        prikaziColumn.setVisible(false);
        /*
        prikaziColumn.setCellValueFactory(
            param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );
        
        prikaziColumn.setCellFactory(tableCell -> {
            TableCell<Korisnik, Korisnik> cell = new TableCell<Korisnik, Korisnik>() {
                private final Button button = new Button("");
                @Override
                protected void updateItem(Korisnik item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        button.getStyleClass().addAll("buttonTable", "buttonTableShow");
                    	button.setTooltip(new Tooltip("Prikaži"));
                    	button.getTooltip().setAutoHide(false);
                    	setGraphic(button);
                    	button.setOnMouseClicked(event -> {
                            //
                        });
                    } else {
                    	setGraphic(null);
                    }
                }
            };
            return cell;
        });
        */
        izmjeniColumn.setCellValueFactory(
            param -> new ReadOnlyObjectWrapper<>(param.getValue())
        );
        
        izmjeniColumn.setCellFactory(tableCell -> {
            TableCell<Korisnik, Korisnik> cell = new TableCell<Korisnik, Korisnik>() {
                private final Button button = new Button("");
                @Override
                protected void updateItem(Korisnik item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        button.getStyleClass().addAll("buttonTable", "buttonTableEdit");
                    	button.setTooltip(new Tooltip("Izmjeni"));
                    	button.getTooltip().setAutoHide(false);
                    	setGraphic(button);
                    	button.setOnMouseClicked(event -> {
                            try {
                                korisnickiNaloziTableView.getSelectionModel().select(item);
                                DodavanjeKorisnickogNalogaController.odabraniKorisnik=item;
                                DodavanjeKorisnickogNalogaController.izmjena=true;
                                
                                Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/DodavanjeKorisnickogNalogaView.fxml"));
                                Scene scene = new Scene(root);
                                Stage stage=new Stage();
                                stage.setScene(scene);
                                stage.setResizable(false);
                                stage.initStyle(StageStyle.UNDECORATED);
                                stage.initModality(Modality.APPLICATION_MODAL);
                                stage.showAndWait();
                                
                                DodavanjeKorisnickogNalogaController.izmjena=false;
                                korisnickiNaloziTableView.refresh();
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
            TableCell<Korisnik, Korisnik> cell = new TableCell<Korisnik, Korisnik>() {
                private final Button button = new Button("");
                @Override
                protected void updateItem(Korisnik item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        button.getStyleClass().addAll("buttonTable", "buttonTableDelete");
                    	button.setTooltip(new Tooltip("Obriši"));
                    	button.getTooltip().setAutoHide(false);
                    	setGraphic(button);
                    	button.setOnMouseClicked(event -> {
                            korisnickiNaloziTableView.getSelectionModel().select(item);
                            if(Util.showConfirmationAlert()) {
                                try {
                                    korisnikDAO.delete(PrijavaController.konekcija, item);
                                    korisnickiNaloziList.remove(item);
                                    //getTableView().getItems().remove(item);
                                    korisnickiNaloziTableView.refresh();
                                    //System.out.println("Obrisano: " + item);
                                    Util.getNotifications("Obavještenje", "Korisnički nalog obrisan.", "Information").show();
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
        
        Util.preventColumnReordering(korisnickiNaloziTableView);
        
        korisnickoImeColumn.setMinWidth(100);
        korisnickoImeColumn.setMaxWidth(4000);
        
        tipColumn.setMinWidth(100);
        tipColumn.setMaxWidth(1000);
        
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
            Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/DodavanjeKorisnickogNalogaView.fxml"));
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
