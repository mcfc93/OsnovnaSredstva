package osnovnasredstva.administrator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
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
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.MaskerPane;
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
    private ToggleButton backupButton;
    @FXML
    private Button closeButton;
    @FXML
    private Button maximizeButton;
    @FXML
    private Button minimizeButton;
    
    @FXML
    private ToggleButton tmpButton;

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
            } else {
                tmpButton=toggleGroup.getToggles().indexOf(newValue) != 6? (ToggleButton)newValue : (ToggleButton)oldValue; 
            }
        });
        
        /*
        new Thread() {
            @Override
            public void run() {
                if(PrijavaController.korisnik.getTip() == 0) {
                    KorisnikDAO.loadUsernames();
                }
                VrstaOSDAO.loadVrsteOS();
                ProstorijaDAO.loadProstorije();
                ZgradaDAO.loadZgrade();
                OsobaDAO.loadOsobe();
            }
        }.start();
        */
        
        if(PrijavaController.korisnik.getTip() == 1) {
            korisnickiNaloziButton.setVisible(false);
            backupButton.setVisible(false);
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
    void backup(ActionEvent event) {
        //dataAnchorPane.getChildren().removeAll();
       // dataAnchorPane.getChildren().setAll();
       //ToggleButton tmpButton = (ToggleButton)toggleGroup.getSelectedToggle();
        //System.out.println(tmpButton.getText());
       //int index = toggleGroup.getToggles().indexOf(tmpButton);
        //System.out.println(index);
       //toggleGroup.getToggles().get(index).setSelected(false);
       tmpButton.setSelected(false);
       
       MaskerPane progressPane=Util.getMaskerPane(gridPane);
       FileChooser fileChooser = new FileChooser();
       fileChooser.setTitle("Save As");
        fileChooser.getExtensionFilters().addAll(
         new ExtensionFilter("SQL Files", "*.sql"));
        File path = new File(System.getProperty("user.home"));//fileChooser.showSaveDialog(null);
        fileChooser.setInitialDirectory(path);
        File file=fileChooser.showSaveDialog(((Node)event.getSource()).getScene().getWindow());
       new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                //progressPane.setVisible(true);
                
                
                String executeCmd = "";
                if(file != null){
                Process runtimeProcess;
                Process proc;
                String s="";
                String tmp="";
                try {
                    /*
                    ProcessBuilder pb1 = new ProcessBuilder("cmd", "/c", "where mysqldump");
                    //pb1.redirectError();
                    Process p = pb1.start();
                    BufferedReader reader = 
                        new BufferedReader(new InputStreamReader(p.getInputStream()));
                    while ((s = reader.readLine()) != null) {
                        System.out.println(s);
                        tmp = new String(s);
                    }
                    System.out.println(tmp);
                    int exitCode = p.waitFor();
                    */
                    String com = "\"" + Util.PROPERTY.getProperty("db.dump") + "\"" + " --user=" + Util.PROPERTY.getProperty("db.username") + " --password=" + Util.PROPERTY.getProperty("db.password") + " " + Util.PROPERTY.getProperty("db.schema") + " >  " + file.getAbsolutePath();
                    //System.out.println(com);
                    ProcessBuilder pb2 = new ProcessBuilder("cmd", "/c", com);
                    Process p2 = pb2.start();

                    int processComplete = p2.waitFor();
                    if(processComplete == 0){
                        Platform.runLater(() -> Util.getNotifications("Obavještenje", "Backup baze napravljen.", "Confirmation").show());
                    } else {
                        Platform.runLater(() -> Util.getNotifications("Greška", "Greška prilikom pravljenja backupa.", "Error").show());
                        }
                    } catch (IOException | InterruptedException e) {
                        Util.LOGGER.log(Level.SEVERE, e.toString(), e);
                    }
                    //System.out.println(file.getAbsolutePath());
                }
                return null;
            }
            @Override
            protected void succeeded(){
                super.succeeded();
                progressPane.setVisible(false);
                Platform.runLater(() -> {
                    tmpButton.setSelected(true);
                    backupButton.setSelected(false);
                });
            }
        }).start();
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
