package osnovnasredstva.prijava;

import com.jfoenix.controls.JFXCheckBox;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Base64;
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
    private Label greskaLabel;
    
    @FXML
    private Label copyrightLabel;

    @FXML
    private Button prijavaButton;
    
    private double xOffset=0;
    private double yOffset=0;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //ucitavanje nalog.ser, ako postoji
    	File f=new File(KorisnikDAO.SER_FILE);
    	if(f.exists()) {
            try (ObjectInputStream ois = 
                new ObjectInputStream(
                    new FileInputStream(f.getAbsolutePath())
                )
            ) {
                PrijavaController.korisnik=(Korisnik)ois.readObject();
                korisnickoImeTextField.setText(korisnik.getKorisnickoIme());
                lozinkaTextField.setText(new String(Base64.getDecoder().decode(korisnik.getHashLozinke())));
            } catch(IOException | ClassNotFoundException e) {
                Util.LOGGER.log(Level.SEVERE, e.toString(), e);
            }
    	}
        
        greskaLabel.setVisible(false);
        zapamtiMeCheckBox.setSelected(true);
        prijavaButton.setDefaultButton(true);
        greskaLabel.setText("Korisničko ime ili lozinka pogrešni!");
        copyrightLabel.setText("Copyright © " + LocalDate.now().getYear() + ". Sva prava zadržana.");
        
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
        
        korisnickoImeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
        	if(!newValue.isEmpty()) {
        		greskaLabel.setVisible(false);
        	}
        });
        
        lozinkaTextField.textProperty().addListener((observable, oldValue, newValue) -> {
        	if(!newValue.isEmpty()) {
        		greskaLabel.setVisible(false);
        	}
        });
    }
    
    @FXML
    void prijava(ActionEvent event) {
        MaskerPane progressPane=Util.getMaskerPane(anchorPane);
        new Thread(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                progressPane.setVisible(true);
                Thread.sleep(500);
                
                try {
                    if((korisnik=KorisnikDAO.prijava(korisnickoImeTextField.getText(), lozinkaTextField.getText(), zapamtiMeCheckBox.isSelected())) != null) {

                        Platform.runLater(() -> {
                            korisnickoImeTextField.clear();
                            lozinkaTextField.clear();
                        });
                        try{
                            konekcija = Util.getConnection();
                        } catch(SQLException e){
                            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                        }
                        Platform.runLater(() -> {
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
                        });
                    } else {
                        Platform.runLater(() -> {
                            greskaLabel.setVisible(true);
                            korisnickoImeTextField.clear();
                            lozinkaTextField.clear();
                            korisnickoImeTextField.requestFocus();
                        });
                    }
                } catch(Exception e) {
                    Platform.runLater(() -> {
                        Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                        Alert alert=new Alert(AlertType.ERROR);
                        alert.setTitle("Greška");
                        alert.setHeaderText(null);
                        alert.setContentText("Neuspješno povezivanje sa bazom.");

                        alert.getDialogPane().getStylesheets().add(Util.class.getResource("/osnovnasredstva/osnovnasredstva.css").toExternalForm());
                        alert.getDialogPane().getStyleClass().add("alert");

                        alert.showAndWait();
                    });
                }
                
                return null;
            }
            @Override
            protected void succeeded(){
                super.succeeded();
                progressPane.setVisible(false);
            }
        }).start();
    }
    
    void odjava(ActionEvent event) {
        ((Stage)anchorPane.getScene().getWindow()).show();
    }

    @FXML
    void sakrijLabelu(MouseEvent event) {
        greskaLabel.setVisible(false);
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
