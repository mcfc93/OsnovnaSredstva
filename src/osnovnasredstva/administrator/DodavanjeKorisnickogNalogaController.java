package osnovnasredstva.administrator;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import osnovnasredstva.DAO.KorisnikDAO;
import osnovnasredstva.DTO.Korisnik;
import static osnovnasredstva.administrator.KorisnickiNaloziController.korisnickiNaloziList;
import osnovnasredstva.prijava.PrijavaController;
import osnovnasredstva.util.Util;

/**
 *
 * @author mcfc93
 */
public class DodavanjeKorisnickogNalogaController implements Initializable {
    
    public static Korisnik odabraniKorisnik;
    public static boolean izmjena=false;
    private static KorisnikDAO korisnikDAO = new KorisnikDAO();

    @FXML
    private AnchorPane menuLine;

    @FXML
    private JFXButton sacuvajButon;
    
    @FXML
    private JFXButton nazadButton;
    
    @FXML
    private JFXTextField korisnickoImeTextField;

    @FXML
    private JFXPasswordField lozinka1PasswordField;

    @FXML
    private JFXPasswordField lozinka2PasswordField;

    @FXML
    private JFXRadioButton administratorRadioButton;

    @FXML
    private ToggleGroup tipNaloga;

    @FXML
    private JFXRadioButton nadzornikRadioButton;
    
    private double xOffset=0;
    private double yOffset=0;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //DragAndDrop
        menuLine.setOnMousePressed(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                Stage stage=((Stage)((Node)event.getSource()).getScene().getWindow());
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });

        menuLine.setOnMouseDragged(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                Stage stage=((Stage)((Node)event.getSource()).getScene().getWindow());
                if(!stage.isMaximized()) {
                    stage.setX(event.getScreenX() + xOffset);
                    stage.setY(event.getScreenY() + yOffset);
                    stage.setOpacity(0.8);
                }
            }
        });

        menuLine.setOnMouseReleased(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                Stage stage=((Stage)((Node)event.getSource()).getScene().getWindow());
                stage.setOpacity(1.0);
            }
        });
        

        nazadButton.setDefaultButton(true);
        korisnickoImeTextField.getValidators().addAll(Util.requiredFieldValidator(korisnickoImeTextField), Util.lengthValidator(korisnickoImeTextField, 20));
        lozinka1PasswordField.getValidators().addAll(Util.requiredFieldValidator(lozinka1PasswordField), Util.passwordValidator(lozinka1PasswordField));
        lozinka2PasswordField.getValidators().addAll(Util.requiredFieldValidator(lozinka2PasswordField), Util.passwordValidator(lozinka1PasswordField), Util.samePasswordValidator(lozinka1PasswordField, lozinka2PasswordField));
        
        if(izmjena) {
            korisnickoImeTextField.setText(odabraniKorisnik.getKorisnickoIme());
            if(odabraniKorisnik.getTip()==0) {
                administratorRadioButton.setSelected(true);
            } else {
                nadzornikRadioButton.setSelected(true);
            }
        }
    }    
    
    @FXML
    void close(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY)) {
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        }
    }
    
    @FXML
    void sacuvaj(ActionEvent event) {
        if(korisnickoImeTextField.validate()
                & lozinka1PasswordField.validate()
                    & lozinka2PasswordField.validate()) {
            if(izmjena) {
                odabraniKorisnik.setKorisnickoIme(korisnickoImeTextField.getText().trim());
                //odabraniKorisnik.setHashLozinke();
                odabraniKorisnik.setTip(administratorRadioButton.isSelected()? 0: 1);
            }
            else{

                String salt = KorisnikDAO.generisiSalt();
                odabraniKorisnik = new Korisnik(korisnickoImeTextField.getText().trim(), 
                    KorisnikDAO.hash(lozinka1PasswordField.getText(), salt), salt,(administratorRadioButton.isSelected()? 0: 1));
                try {
                    korisnikDAO.create(PrijavaController.konekcija, odabraniKorisnik);
                } catch (SQLException e) {
                    Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                }
            }
            //upisivanje u bazu
            korisnickiNaloziList.add(DodavanjeKorisnickogNalogaController.odabraniKorisnik);



            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        }
    }
}
