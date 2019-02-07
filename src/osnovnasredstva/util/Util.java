package osnovnasredstva.util;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.controlsfx.control.MaskerPane;
import org.controlsfx.control.Notifications;

/**
 *
 * @author mcfc93
 */
public class Util {
    public static Properties PROPERTY = new Properties();
    public static final Logger LOGGER = Logger.getLogger("Logger");
    public static FileHandler fileHandler = null;
    
    public static final String LOG_PATH="logs/error.log";
    public static final String PROPERTY_PATH="config.properties";
    
    static {
        //postavljanje Logger-a
        try {
            File f=new File("logs");
            if(!f.exists()) {
                    f.mkdirs();
            }
            //new FileHandler(System.getProperty("user.home") + File.separator + "error.log", true);
            fileHandler = new FileHandler(LOG_PATH, true);	//append
            LOGGER.addHandler(Util.fileHandler);
            Util.fileHandler.setFormatter(new SimpleFormatter());    //za formatiran ispis
            //LOGGER.setUseParentHandlers(false);               //da ne prikazuje Exception na Console
        } catch (SecurityException | IOException e) {
            System.err.println(e.getMessage());
        }

        //ucitavanje properties fajla
        try {
            //.load(new FileInputStream(System.getProperty("user.home") + File.separator + "config.properties"));
            Util.PROPERTY.load(new FileInputStream(Util.PROPERTY_PATH));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }

System.out.println(Util.PROPERTY);
    }
    
    //BAZA
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(Util.PROPERTY.getProperty("jdbc.url"), Util.PROPERTY.getProperty("db.username"), Util.PROPERTY.getProperty("db.password"));
    }

    public static void close(Connection c) {
        if (c != null) {
            try {
                c.close();
            } catch (SQLException e) {
                Util.LOGGER.log(Level.SEVERE, e.toString(), e);
            }
        }
    }

    public static void close(Statement s) {
        if (s != null) {
            try {
                s.close();
            } catch (SQLException e) {
                Util.LOGGER.log(Level.SEVERE, e.toString(), e);
            }
        }
    }

    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                Util.LOGGER.log(Level.SEVERE, e.toString(), e);
            }
        }
    }

    public static void close(ResultSet rs, Statement s, Connection c) {
        close(rs);
        close(s);
        close(c);
    }

    public static void close(Statement s, Connection c) {
        close(s);
        close(c);
    }

    public static PreparedStatement prepareStatement(Connection c, String sql,
        boolean retGenKeys, Object... values) throws SQLException {
        PreparedStatement ps = c.prepareStatement(sql,
                                    retGenKeys ? Statement.RETURN_GENERATED_KEYS
                                        : Statement.NO_GENERATED_KEYS);
        for (int i = 0; i < values.length; i++)
            ps.setObject(i + 1, values[i]);
        return ps;
    }
    
    public static void showBugAlert() {
        Alert alert=new Alert(AlertType.ERROR);
        alert.setTitle("Greška");
        alert.setHeaderText("NEOČEKIVANO PONAŠANJE!");
        alert.setContentText("Kontakrirajte Administratora sistema.");

        alert.getDialogPane().getStylesheets().add(Util.class.getResource("/osnovnasredstva/osnovnasredstva.css").toExternalForm());
        alert.getDialogPane().getStyleClass().addAll("alert", "alertBug");

        alert.showAndWait();
    }
    
    public static MaskerPane getMaskerPane(Pane pane) {
        MaskerPane progressPane = new MaskerPane();
        progressPane.setText("Molimo sačekajte...");
        progressPane.setVisible(false);
        pane.getChildren().add(progressPane);
        if(pane instanceof AnchorPane) {
            AnchorPane.setTopAnchor(progressPane,0.0);
            AnchorPane.setBottomAnchor(progressPane,0.0);
            AnchorPane.setLeftAnchor(progressPane,0.0);
            AnchorPane.setRightAnchor(progressPane,0.0);
        }
        return progressPane;
    }
	
    public static Notifications getNotifications(String title, String text, String type) {
        switch (type) {
            case "Warning": type="osnovnasredstva/img/alert-warning.png"; break;
            case "Information": type="osnovnasredstva/img/alert-information.png"; break;
            case "Confirmation": type="osnovnasredstva/img/alert-confirmation.png"; break;
            case "Error": type="osnovnasredstva/img/alert-error.png"; break;
            default: type="osnovnasredstva/img/alert-information.png";
        }
        Notifications notificationBuilder = Notifications.create()
            .title(title)
            .text(text)
            .graphic(new ImageView(type))
            .hideAfter(Duration.seconds(5))
            .position(Pos.BOTTOM_RIGHT);
        return notificationBuilder;
    }
    
    //VALIDATORI
	public static ValidatorBase requiredFieldValidator(JFXTextField textField) {
    	ValidatorBase requiredFieldValidator = new RequiredFieldValidator();
	    requiredFieldValidator.setMessage("Obavezan unos");
	    requiredFieldValidator.setIcon(new ImageView());
	    textField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)->{
	        if(!newValue) {
	        	textField.validate();
	        }
	    });
	    textField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
	        	textField.validate();
	    });
	    return requiredFieldValidator;
    }
	
	public static ValidatorBase requiredFieldValidator(JFXPasswordField passwordField) {
    	ValidatorBase requiredFieldValidator = new RequiredFieldValidator();
	    requiredFieldValidator.setMessage("Obavezan unos");
	    requiredFieldValidator.setIcon(new ImageView());
	    
	    
	    passwordField.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)->{
	        if(!newValue) {
	        	passwordField.validate();
	        }
	    });
	    
	    
	    passwordField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
	        //if(textField.getText().trim().isEmpty()) {
	        	passwordField.validate();
	        //} else {
	        //	textField.resetValidation();
	        //}
	    });
	    
	    
	    return requiredFieldValidator;
        }
        
        public static ValidatorBase samePasswordValidator(JFXPasswordField passwordField1, JFXPasswordField passwordField2) {
            ValidatorBase samePasswordValidator = new ValidatorBase("Ne poklapa se") {
                @Override
                protected void eval() {
                        if(!passwordField1.getText().trim().isEmpty()
                                && !passwordField2.getText().trim().isEmpty()
                                        && !passwordField1.getText().equals(passwordField2.getText())) {
                         hasErrors.set(true);
                } else {
                         hasErrors.set(false);
                }
                }
            };
            samePasswordValidator.setIcon(new ImageView());
            return samePasswordValidator;
        }
        
        public static ValidatorBase passwordValidator(JFXPasswordField passwordField) {
            ValidatorBase passwordValidator = new ValidatorBase("Minimalno 6 karaktera") {
                @Override
                protected void eval() {
                        if(!passwordField.getText().trim().isEmpty()
                                && passwordField.getText().length() < 6) {
                         hasErrors.set(true);
                } else {
                         hasErrors.set(false);
                }
            }
        };
        passwordValidator.setIcon(new ImageView());
        return passwordValidator;
    }
        
    public static ValidatorBase lengthValidator(JFXTextField textField, int length) {
        ValidatorBase lengthValidator = new ValidatorBase("Predugačak unos") {
            @Override
            protected void eval() {
                    if(!textField.getText().isEmpty()
                            && textField.getText().length() > length) {
                    hasErrors.set(true);
            } else {
                     hasErrors.set(false);
            }
        }
    };
    lengthValidator.setIcon(new ImageView());
    return lengthValidator;
    }
}
