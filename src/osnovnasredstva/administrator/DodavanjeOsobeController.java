package osnovnasredstva.administrator;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import osnovnasredstva.DAO.OsobaDAO;
import osnovnasredstva.DTO.Osoba;
import osnovnasredstva.prijava.PrijavaController;
import osnovnasredstva.util.NotFoundException;
import osnovnasredstva.util.Util;
import static osnovnasredstva.administrator.OsobeController.osobeList;

public class DodavanjeOsobeController implements Initializable {

    
    public static Osoba odabranaOsoba;
    public static boolean izmjena=false;
    private static OsobaDAO osobaDAO = new OsobaDAO();

    @FXML
    private AnchorPane menuLine;

    @FXML
    private JFXButton sacuvajButon;
    
    @FXML
    private JFXButton nazadButton;
    
    @FXML
    private JFXTextField imeTextField;

    @FXML
    private JFXTextField prezimeTextField;

    @FXML
    private JFXTextField jmbgTextField;

    @FXML
    private JFXTextField titulaTextField;

    @FXML
    private JFXTextField zaposlenjeTextField;

    @FXML
    private JFXTextField brojTelefonaTextField;

    @FXML
    private JFXTextField emailTextField;

    @FXML
    private JFXTextField adresaTextField;
    
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
        
        imeTextField.getValidators().addAll(Util.requiredFieldValidator(imeTextField), Util.nameValidator(imeTextField), Util.lengthValidator(imeTextField, 255));
        prezimeTextField.getValidators().addAll(Util.requiredFieldValidator(prezimeTextField), Util.surnameValidator(prezimeTextField), Util.lengthValidator(prezimeTextField, 255));
        jmbgTextField.getValidators().addAll(Util.requiredFieldValidator(jmbgTextField), Util.jmbgValidator(jmbgTextField));
        titulaTextField.getValidators().addAll(Util.requiredFieldValidator(titulaTextField), Util.lengthValidator(titulaTextField, 255));
        zaposlenjeTextField.getValidators().addAll(Util.requiredFieldValidator(zaposlenjeTextField), Util.lengthValidator(zaposlenjeTextField, 255));
        brojTelefonaTextField.getValidators().addAll(Util.requiredFieldValidator(brojTelefonaTextField), Util.phoneValidator(brojTelefonaTextField), Util.lengthValidator(brojTelefonaTextField, 16));
        emailTextField.getValidators().addAll(Util.requiredFieldValidator(emailTextField), Util.emailValidator(emailTextField), Util.lengthValidator(emailTextField, 50));
        adresaTextField.getValidators().addAll(Util.requiredFieldValidator(adresaTextField), Util.lengthValidator(adresaTextField, 255));
        
        if(izmjena) {
            imeTextField.setText(odabranaOsoba.getIme());
            prezimeTextField.setText(odabranaOsoba.getPrezime());
            jmbgTextField.setText(odabranaOsoba.getJmbg());
            titulaTextField.setText(odabranaOsoba.getTitula());
            zaposlenjeTextField.setText(odabranaOsoba.getZaposlenje());
            brojTelefonaTextField.setText(odabranaOsoba.getTelefon());
            emailTextField.setText(odabranaOsoba.getEmail());
            adresaTextField.setText(odabranaOsoba.getAdresa());
            
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
        if(imeTextField.validate()
            & prezimeTextField.validate()
                & jmbgTextField.validate()
                    & adresaTextField.validate()
                        & titulaTextField.validate()
                            & zaposlenjeTextField.validate()
                                & brojTelefonaTextField.validate()
                                    & emailTextField.validate()) {
            if(izmjena) {
                odabranaOsoba.setIme(imeTextField.getText());
                odabranaOsoba.setPrezime(prezimeTextField.getText());
                odabranaOsoba.setTitula(titulaTextField.getText());
                odabranaOsoba.setJmbg(jmbgTextField.getText());
                odabranaOsoba.setZaposlenje(zaposlenjeTextField.getText());
                odabranaOsoba.setTelefon(brojTelefonaTextField.getText());
                odabranaOsoba.setEmail(emailTextField.getText());
                odabranaOsoba.setAdresa(adresaTextField.getText());                
                try {
                    osobaDAO.save(PrijavaController.konekcija, odabranaOsoba);
                } catch (SQLException | NotFoundException e) {
                    Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                }
            } else {
                odabranaOsoba = new Osoba(imeTextField.getText(), prezimeTextField.getText(), titulaTextField.getText(), jmbgTextField.getText(), zaposlenjeTextField.getText(), brojTelefonaTextField.getText(), emailTextField.getText(), adresaTextField.getText());
                try {
                    osobaDAO.create(PrijavaController.konekcija, odabranaOsoba);
                } catch (SQLException e) {
                    Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                }
                osobeList.add(DodavanjeOsobeController.odabranaOsoba);
            }


            ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
        }
    }
    
}
