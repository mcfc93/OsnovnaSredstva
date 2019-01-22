package osnovnasredstva.util;

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
}
