package osnovnasredstva.administrator;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.BoundingBox;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import osnovnasredstva.DAO.KorisnikDAO;
import osnovnasredstva.DAO.OsobaDAO;
import osnovnasredstva.DAO.ProstorijaDAO;
import osnovnasredstva.DAO.VrstaOSDAO;
import osnovnasredstva.DAO.ZgradaDAO;
import osnovnasredstva.prijava.PrijavaController;
import osnovnasredstva.util.Util;

public class AdministratorController implements Initializable {
    
    @FXML
    private GridPane gridPane;
    
    @FXML
    private AnchorPane menuLine;

    @FXML
    private AnchorPane dataAnchorPane;

    @FXML
    private Label informacijeLabel;
    
    @FXML
    private ToggleGroup toggleGroup;
    
    BoundingBox originalStageSize;
    boolean maximized=false;

    private double xOffset=0;
    private double yOffset=0;
    
    @FXML
    private ToggleButton osobeButton;
    @FXML
    private ToggleButton lokacijeButton;
    @FXML
    private ToggleButton osnovnaSredstvaButton;
    @FXML
    private ToggleButton korisnickiNaloziButton;
    @FXML
    private Button closeButton;
    @FXML
    private Button maximizeButton;
    @FXML
    private Button minimizeButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        informacijeLabel.setText(PrijavaController.korisnik.getKorisnickoIme());
        //DragAndDrop
        menuLine.setOnMousePressed(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                Stage stage=((Stage)((Node)event.getSource()).getScene().getWindow());
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });

        menuLine.setOnMouseDragged(event -> {
            if(!maximized && event.getButton().equals(MouseButton.PRIMARY)) {
                Stage stage=((Stage)((Node)event.getSource()).getScene().getWindow());
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
                stage.setOpacity(0.8);
            }
        });

        menuLine.setOnMouseReleased(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                Stage stage=((Stage)((Node)event.getSource()).getScene().getWindow());
                stage.setOpacity(1.0);
            }
        });

        //toggleGroup
        toggleGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            if (newValue == null) {
                oldValue.setSelected(true);
            }
        });
        
        
        new Thread() {
            @Override
            public void run() {
                if(PrijavaController.korisnik.getTip() == 0) {
                    KorisnikDAO.loadUsernames();
                }
                /*
                VrstaOSDAO.loadVrsteOS();
                ProstorijaDAO.loadProstorije();
                ZgradaDAO.loadZgrade();
                OsobaDAO.loadOsobe();
                */
            }
        }.start();
        
        
        if(PrijavaController.korisnik.getTip() == 1) {
            korisnickiNaloziButton.setVisible(false);
        }
        
        x(null);
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
    
    @FXML
    void maximize(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY)) {
            Stage stage=((Stage)((Node)event.getSource()).getScene().getWindow());
            //stage.setMaximized(!stage.isMaximized());
            if(!maximized) {
                originalStageSize = new BoundingBox(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
                Screen screen = Screen.getScreensForRectangle(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight()).get(0);
                Rectangle2D bounds = screen.getVisualBounds();
                stage.setX(bounds.getMinX());
                stage.setY(bounds.getMinY());
                stage.setWidth(bounds.getWidth());
                stage.setHeight(bounds.getHeight());
            } else {
                stage.setX(originalStageSize.getMinX());
                stage.setY(originalStageSize.getMinY());
                stage.setWidth(originalStageSize.getWidth());
                stage.setHeight(originalStageSize.getHeight());
            }
            maximized=!maximized;
        }
    }
    
    @FXML
    void doubleClick(MouseEvent event) {
        if(event.getButton().equals(MouseButton.PRIMARY)) {
            if(event.getClickCount() == 2) {
                maximize(event);
            }
        }
    }
    
    @FXML
    void x(ActionEvent event) {
    	try {
            Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/OsobeView.fxml"));
            AnchorPane.setTopAnchor(root,0.0);
            AnchorPane.setBottomAnchor(root,0.0);
            AnchorPane.setLeftAnchor(root,0.0);
            AnchorPane.setRightAnchor(root,0.0);
            dataAnchorPane.getChildren().removeAll();
            dataAnchorPane.getChildren().setAll(root);
        } catch(IOException e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

    @FXML
    void y(ActionEvent event) {
    	try {
            Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/LokacijeView.fxml"));
            AnchorPane.setTopAnchor(root,0.0);
            AnchorPane.setBottomAnchor(root,0.0);
            AnchorPane.setLeftAnchor(root,0.0);
            AnchorPane.setRightAnchor(root,0.0);
            dataAnchorPane.getChildren().removeAll();
            dataAnchorPane.getChildren().setAll(root);
        } catch(IOException e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }

    @FXML
    void z(ActionEvent event) {
    	try {
            Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/OsnovnaSredstvaView.fxml"));
            AnchorPane.setTopAnchor(root,0.0);
            AnchorPane.setBottomAnchor(root,0.0);
            AnchorPane.setLeftAnchor(root,0.0);
            AnchorPane.setRightAnchor(root,0.0);
            dataAnchorPane.getChildren().removeAll();
            dataAnchorPane.getChildren().setAll(root);
        } catch(IOException e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }
    
    @FXML
    void w(ActionEvent event) {
    	try {
            Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/KorisnickiNaloziView.fxml"));
            AnchorPane.setTopAnchor(root,0.0);
            AnchorPane.setBottomAnchor(root,0.0);
            AnchorPane.setLeftAnchor(root,0.0);
            AnchorPane.setRightAnchor(root,0.0);
            dataAnchorPane.getChildren().removeAll();
            dataAnchorPane.getChildren().setAll(root);
        } catch(IOException e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }
    
    @FXML
    void v(ActionEvent event) {
    	try {
            Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/administrator/PrelaznicaView.fxml"));
            AnchorPane.setTopAnchor(root,0.0);
            AnchorPane.setBottomAnchor(root,0.0);
            AnchorPane.setLeftAnchor(root,0.0);
            AnchorPane.setRightAnchor(root,0.0);
            dataAnchorPane.getChildren().removeAll();
            dataAnchorPane.getChildren().setAll(root);
        } catch(IOException e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }
    
    @FXML
    void odjava(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/osnovnasredstva/prijava/PrijavaView.fxml"));

            Scene scene = new Scene(root);
            //scene.getStylesheets().add(getClass().getResource("osnovnasredstva.css").toExternalForm());
            
            Stage stage=new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
            
            ((Stage)gridPane.getScene().getWindow()).close();
        } catch(IOException e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
    }
}
