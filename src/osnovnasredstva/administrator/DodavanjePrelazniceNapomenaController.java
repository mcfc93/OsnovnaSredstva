/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package osnovnasredstva.administrator;

import com.jfoenix.controls.JFXTextArea;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author milos
 */
public class DodavanjePrelazniceNapomenaController implements Initializable {

    @FXML
    private Button OkButton;
    @FXML
    private AnchorPane menuLine;
    @FXML
    private JFXTextArea opisTextArea;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    @FXML
    private void handleOkButton(ActionEvent event) {
        DodavanjeOsnovnogSredstvaController.prelaznica.setNapomena(opisTextArea.getText());
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }
    
}
