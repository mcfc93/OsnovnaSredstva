package osnovnasredstva.prijava;

import com.jfoenix.controls.JFXCheckBox;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.MaskerPane;
import osnovnasredstva.DTO.Korisnik;
import osnovnasredstva.DAO.KorisnikDAO;
import osnovnasredstva.util.Util;

public class PrijavaController implements Initializable {
    
    public static Korisnik korisnik=null;
    private static final String SER_FILE="logs/korisnik.ser";
    public static Connection konekcija;
    
    @FXML
    private AnchorPane anchorPane;
	
    @FXML
    private TextField korisnickoImeTextField;

    @FXML
    private PasswordField lozinkaTextField;
    
    @FXML
    private JFXCheckBox zapamtiMeCheckBox;
    
    @FXML
    private Label greskaTextLabel;
    
    @FXML
    private Label greskaBackgroundLabel;

    @FXML
    private Button prijavaButton;
    
    private double xOffset=0;
    private double yOffset=0;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /*
        //ucitavanje nalog.ser, ako postojis
    	File f=new File(SER_FILE);
    	if(f.exists()) {
            try (ObjectInputStream ois = 
                new ObjectInputStream(
                    new FileInputStream(f.getAbsolutePath())
                )
            ) {
                PrijavaController.korisnik=(Korisnik)ois.readObject();
                korisnickoImeTextField.setText(korisnik.getKorisnickoIme());
                lozinkaTextField.setText(korisnik.getLozinka());
System.out.println("nalog.ser");
            } catch(IOException e | ClassNotFoundException e) {
                Util.LOGGER.log(Level.SEVERE, e.toString(), e);
            }
    	}
        */
        greskaTextLabel.setVisible(false);
	greskaBackgroundLabel.setVisible(false);
	zapamtiMeCheckBox.setSelected(true);
	prijavaButton.setDefaultButton(true);
        
        if(korisnickoImeTextField.getText().trim().isEmpty()) {
            //focus se moze traziti samo nakon sto se Stage inicijalizuje
            Platform.runLater(() -> korisnickoImeTextField.requestFocus());
        } else {
            Platform.runLater(() -> lozinkaTextField.requestFocus());
        }
        
        //DragAndDrop
        anchorPane.setOnMousePressed(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                Stage stage=((Stage)((Node)event.getSource()).getScene().getWindow());
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });

        anchorPane.setOnMouseDragged(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                Stage stage=((Stage)((Node)event.getSource()).getScene().getWindow());
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
                stage.setOpacity(0.8);
            }
        });

        anchorPane.setOnMouseReleased(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                Stage stage=((Stage)((Node)event.getSource()).getScene().getWindow());
                stage.setOpacity(1.0);
            }
        });
        
        prijavaButton.disableProperty().bind(
	    korisnickoImeTextField.textProperty().isEmpty().or(lozinkaTextField.textProperty().isEmpty())
	);
        
        korisnickoImeTextField.setText("admin");
        lozinkaTextField.setText("student");
        /*
        Platform.runLater(() -> {
        Window parent = korisnickoImeTextField.getScene().getWindow();
        Tooltip tooltip=new Tooltip("Opis");
        korisnickoImeTextField.setTooltip(tooltip);
        
        korisnickoImeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null && !korisnickoImeTextField.getText().isEmpty()) {
                korisnickoImeTextField.getTooltip().show(parent,
                            parent.getX() + korisnickoImeTextField.localToScene(0, 0).getX() +
                                korisnickoImeTextField.getScene().getX(),
                            parent.getY() + korisnickoImeTextField.localToScene(0, 0).getY() +
                                korisnickoImeTextField.getScene().getY() + ((Region)korisnickoImeTextField).getHeight());
            } else {
                korisnickoImeTextField.getTooltip().hide();
            }
        });
        });
        */
    }
    
    @FXML
    void prijava(ActionEvent event) {
        try {
            if((korisnik=KorisnikDAO.prijava(korisnickoImeTextField.getText(), lozinkaTextField.getText())) != null) {
            //if("mcfc93".equals(korisnickoImeTextField.getText()) && "student".equals(lozinkaTextField.getText())) {

                /*
                //Serijalizacija ako je cekirano Remember me
                if(zapamtiMeCheckBox.isSelected()) {
                    System.out.println("REMEMBER ME");
                    try (ObjectOutputStream oos = 
                            new ObjectOutputStream(
                                new FileOutputStream(SER_FILE)
                            )
                        )
                    {
                        // *****************************
                        // * TREBA NESTO DRUGO ODRADITI
                        // * TRENUTNO DOSTUPNA LOZINKA
                        // * 
                        // *****************************
                        korsnik.setLozinka(lozinkaTextField.getText());

                        oos.writeObject(korisnik);
                    } catch(IOException e) {
                        Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                    }
                } else {
                    File f=new File(SER_FILE);
                    if(f.exists()) {
                        f.delete();
                    }
                }

                korisnik.setLozinka(Korisnik.hash(lozinkaTextField.getText()));
                korisnickoImeTextField.clear();
                lozinkaTextField.clear();
                */

                MaskerPane progressPane=Util.getMaskerPane(anchorPane);
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        //System.out.println(Thread.currentThread());
                        progressPane.setVisible(true);
                        Thread.sleep(1000);
                        return null;
                    }
                    @Override
                    protected void succeeded(){
                        super.succeeded();
                        progressPane.setVisible(false);
                        try{
                            konekcija = Util.getConnection();
                        } catch(SQLException e){
                            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                        }
                        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
                        try {
                            Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/AdministratorView.fxml"));
                            Scene scene = new Scene(root);
                            scene.getStylesheets().add(getClass().getResource("/osnovnasredstva/osnovnasredstva.css").toExternalForm());
                            Stage stage=new Stage();
                            stage.setScene(scene);
                            stage.setResizable(false);
                            stage.initStyle(StageStyle.UNDECORATED);
                            stage.show();
                        } catch(IOException e) {
                            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                        }
                    }
                };
                new Thread(task).start();
            } else {
                greskaTextLabel.setText("Korisničko ime ili lozinka pogrešni!");
                greskaTextLabel.setVisible(true);
                greskaBackgroundLabel.setVisible(true);
                korisnickoImeTextField.clear();
                lozinkaTextField.clear();
                //korisnickoImeTextField.requestFocus();
            }
        } catch(Exception e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
            Alert alert=new Alert(AlertType.ERROR);
            alert.setTitle("Greška");
            alert.setHeaderText(null);
            alert.setContentText("Neuspješno povezivanje sa bazom.");

            alert.getDialogPane().getStylesheets().add(Util.class.getResource("/osnovnasredstva/osnovnasredstva.css").toExternalForm());
            alert.getDialogPane().getStyleClass().add("alert");

            alert.showAndWait(); 
        }
    }
    
    void odjava(ActionEvent event) {
        ((Stage)anchorPane.getScene().getWindow()).show();
    }

    @FXML
    void sakrijLabelu(MouseEvent event) {
        greskaTextLabel.setVisible(false);
        greskaBackgroundLabel.setVisible(false);
    }

    @FXML
    void close(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY)) {
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        }
    }

    @FXML
    void minimize(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY)) {
            ((Stage)((Node)event.getSource()).getScene().getWindow()).setIconified(true);
        }
    }
    
}
