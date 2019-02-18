package osnovnasredstva.administrator;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.base.ValidatorBase;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import osnovnasredstva.DAO.KorisnikDAO;
import osnovnasredstva.DTO.Korisnik;
import static osnovnasredstva.administrator.KorisnickiNaloziController.korisnickiNaloziList;
import osnovnasredstva.prijava.PrijavaController;
import osnovnasredstva.util.NotFoundException;
import osnovnasredstva.util.Util;

public class DodavanjeKorisnickogNalogaController implements Initializable {
    
    public static Korisnik odabraniKorisnik;
    public static boolean izmjena=false;
    private static KorisnikDAO korisnikDAO = new KorisnikDAO();

    @FXML
    private AnchorPane menuLine;

    @FXML
    private JFXButton sacuvajButton;
    
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
        

        sacuvajButton.setDefaultButton(true);
        
        korisnickoImeTextField.getValidators().addAll(Util.requiredFieldValidator(korisnickoImeTextField), usernameValidator(korisnickoImeTextField), Util.lengthValidator(korisnickoImeTextField, 255));
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
    
    public static ValidatorBase usernameValidator(JFXTextField textField) {
        ValidatorBase postalCodeValidator = new ValidatorBase("Zauzeto") {
            @Override
            protected void eval() {
                if(!textField.getText().isEmpty() && KorisnikDAO.getUsernameList().contains(textField.getText())) {
                    hasErrors.set(true);
                    if(izmjena && odabraniKorisnik.getKorisnickoIme().equals(textField.getText())) {
                        hasErrors.set(false);
                    }
                } else {
                    hasErrors.set(false);
                }
            }
        };
        postalCodeValidator.setIcon(new ImageView());
        return postalCodeValidator;
    }
    
    @FXML
    void close(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY)) {
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        }
    }
    
    @FXML
    void sacuvaj(ActionEvent event) {
        if((korisnickoImeTextField.validate())
                & lozinka1PasswordField.validate()
                    & lozinka2PasswordField.validate()) {
            if(izmjena) {
                String salt = KorisnikDAO.generisiSalt();
                odabraniKorisnik.setKorisnickoIme(korisnickoImeTextField.getText().trim());
                odabraniKorisnik.setHashLozinke(KorisnikDAO.hash(lozinka1PasswordField.getText(), salt));
                odabraniKorisnik.setSalt(salt);
                odabraniKorisnik.setTip(administratorRadioButton.isSelected()? 0: 1);
                try {
                    korisnikDAO.save(PrijavaController.konekcija, odabraniKorisnik);
                    Platform.runLater(() -> Util.getNotifications("Obavještenje", "Korisnički nalog izmjenjen.", "Information").show());
                    if(PrijavaController.korisnik.equals(odabraniKorisnik)) {
                        PrijavaController.korisnik=odabraniKorisnik;
                        //System.out.println(PrijavaController.korisnik);
                    }
                } catch (SQLException | NotFoundException e) {
                    Util.showBugAlert();
                    Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                }
            }
            else{

                String salt = KorisnikDAO.generisiSalt();
                odabraniKorisnik = new Korisnik(korisnickoImeTextField.getText().trim(), 
                    KorisnikDAO.hash(lozinka1PasswordField.getText(), salt), salt,(administratorRadioButton.isSelected()? 0: 1));
                try {
                    korisnikDAO.create(PrijavaController.konekcija, odabraniKorisnik);
                    korisnickiNaloziList.add(DodavanjeKorisnickogNalogaController.odabraniKorisnik);
                    Platform.runLater(() -> Util.getNotifications("Obavještenje", "Korisnički nalog dodan.", "Information").show());
                } catch (SQLException e) {
                    
                    //Platform.runLater(()->Util.getNotifications("Greška", "Korisničko ime zauzeto.", "Error").show());
                    Util.showBugAlert();
                    Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                }
                

            }
            new Thread() {
                @Override
                public void run() {
                    KorisnikDAO.loadUsernames();
                }
            }.start();
            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        }
    }
}
