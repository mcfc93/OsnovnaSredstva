package osnovnasredstva.DTO;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import osnovnasredstva.DAO.OsnovnoSredstvoDAO;
import osnovnasredstva.DAO.OsobaDAO;
import osnovnasredstva.DAO.ProstorijaDAO;
import osnovnasredstva.administrator.LokacijeController;
import osnovnasredstva.administrator.OsnovnaSredstvaController;
import osnovnasredstva.administrator.OsobeController;
import osnovnasredstva.administrator.PrelaznicaController;
import osnovnasredstva.administrator.PrikazOsnovnogSredstvaController;
import osnovnasredstva.prijava.PrijavaController;
import osnovnasredstva.util.NotFoundException;
import osnovnasredstva.util.Util;

public class Prelaznica {

    private int id;
    private Timestamp datumPrelaska;
    private String napomena;
    private boolean status;
    private int idProstorijeIz;
    private int idProstorijeU;
    private int idOsobeSa;
    private int idOsobeNa;
    private int idOsnovnogSredstva;

    public Prelaznica () {
    }

    public Prelaznica(Timestamp datumPrelaska, String napomena, int idProstorijeIz, int idProstorijeU, int idOsobeSa, int idOsobeNa, int idOsnovnogSredstva) {
        this.datumPrelaska = datumPrelaska;
        this.napomena = napomena;
        this.status = true;
        this.idProstorijeIz = idProstorijeIz;
        this.idProstorijeU = idProstorijeU;
        this.idOsobeSa = idOsobeSa;
        this.idOsobeNa = idOsobeNa;
        this.idOsnovnogSredstva = idOsnovnogSredstva;
    }

    public Prelaznica(int id, Timestamp datumPrelaska, String napomena, boolean status, int idProstorijeIz, int idProstorijeU, int idOsobeSa, int idOsobeNa, int idOsnovnogSredstva) {
        this.id = id;
        this.datumPrelaska = datumPrelaska;
        this.napomena = napomena;
        this.status = status;
        this.idProstorijeIz = idProstorijeIz;
        this.idProstorijeU = idProstorijeU;
        this.idOsobeSa = idOsobeSa;
        this.idOsobeNa = idOsobeNa;
        this.idOsnovnogSredstva = idOsnovnogSredstva;
    }

    public int getId() {
          return this.id;
    }
    public void setId(int idIn) {
          this.id = idIn;
    }

    public Timestamp getDatumPrelaska() {
          return this.datumPrelaska;
    }
    
    public String getDatum() {
          return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(this.datumPrelaska);
    }
    
    public void setDatumPrelaska(Timestamp datumPrelaskaIn) {
          this.datumPrelaska = datumPrelaskaIn;
    }

    public String getNapomena() {
          return this.napomena;
    }
    public void setNapomena(String napomenaIn) {
          this.napomena = napomenaIn;
    }

    public boolean getStatus() {
          return this.status;
    }
    public void setStatus(boolean statusIn) {
          this.status = statusIn;
    }

    public int getIdProstorijeIz() {
          return this.idProstorijeIz;
    }
    public void setIdProstorijeIz(int idProstorijeIzIn) {
          this.idProstorijeIz = idProstorijeIzIn;
    }

    public int getIdProstorijeU() {
          return this.idProstorijeU;
    }
    public void setIdProstorijeU(int idProstorijeUIn) {
          this.idProstorijeU = idProstorijeUIn;
    }

    public int getIdOsobeSa() {
          return this.idOsobeSa;
    }
    public void setIdOsobeSa(int idOsobeSaIn) {
          this.idOsobeSa = idOsobeSaIn;
    }

    public int getIdOsobeNa() {
          return this.idOsobeNa;
    }
    public void setIdOsobeNa(int idOsobeNaIn) {
          this.idOsobeNa = idOsobeNaIn;
    }

    public int getIdOsnovnogSredstva() {
          return this.idOsnovnogSredstva;
    }
    public void setIdOsnovnogSredstva(int idOsnovnogSredstvaIn) {
          this.idOsnovnogSredstva = idOsnovnogSredstvaIn;
    }

    public String getIdProstorijeIzString(){
        /*
        ProstorijaDAO prostorijaDAO = new ProstorijaDAO();
        Prostorija prostorija = new Prostorija();
        prostorija.setId(idProstorijeIz);
        try {
            prostorijaDAO.load(PrijavaController.konekcija, prostorija);
        } catch (NotFoundException | SQLException e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
        return prostorija.toString();
        */
        Prostorija prostorija = LokacijeController.prostorijeList.stream().filter(p -> p.getId() == idProstorijeIz).findFirst().orElse(null);
        Zgrada zgrada = null;
        if(prostorija != null) {
            zgrada = LokacijeController.zgradeList.stream().filter(z -> z.getId() == prostorija.getIdZgrade()).findFirst().orElse(null);
        }
        return (prostorija!=null ?prostorija.getNaziv():"NEPOZNATO") + " (" + (zgrada!=null?zgrada.getNaziv():"NEPOZNATO") + ")";
    }
    
    public String getIdProstorijeUString(){
        /*
        ProstorijaDAO prostorijaDAO = new ProstorijaDAO();
        Prostorija prostorija = new Prostorija();
        prostorija.setId(idProstorijeU);
        try {
            prostorijaDAO.load(PrijavaController.konekcija, prostorija);
        } catch (NotFoundException | SQLException e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
        return prostorija.toString();
        */
        Prostorija prostorija = LokacijeController.prostorijeList.stream().filter(p -> p.getId() == idProstorijeU).findFirst().orElse(null);
        Zgrada zgrada = null;
        if(prostorija != null) {
            zgrada = LokacijeController.zgradeList.stream().filter(z -> z.getId() == prostorija.getIdZgrade()).findFirst().orElse(null);
        }
        return (prostorija!=null ?prostorija.getNaziv():"NEPOZNATO") + " (" + (zgrada!=null?zgrada.getNaziv():"NEPOZNATO") + ")";
    }
    
    public String getIdOsobeSaString(){
        /*
        OsobaDAO osobaDAO = new OsobaDAO();
        Osoba osoba = new Osoba();
        osoba.setId(idOsobeSa);
        try {
            osobaDAO.load(PrijavaController.konekcija, osoba);
        } catch (NotFoundException | SQLException e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
        return osoba.getIme() + " " + osoba.getPrezime();
        */
        return OsobeController.osobeList.stream().filter(o -> o.getId() == idOsobeSa).findFirst().get().getImePrezime();
    }
    
    public String getIdOsobeNaString(){
        /*
        OsobaDAO osobaDAO = new OsobaDAO();
        Osoba osoba = new Osoba();
        osoba.setId(idOsobeNa);
        try {
            osobaDAO.load(PrijavaController.konekcija, osoba);
        } catch (NotFoundException | SQLException e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
        return osoba.getIme() + " " + osoba.getPrezime();
        */
        return OsobeController.osobeList.stream().filter(o -> o.getId() == idOsobeNa).findFirst().get().getImePrezime();
    }
    public String getNaziv(){
        /*
        OsnovnoSredstvoDAO osDAO = new OsnovnoSredstvoDAO();
        OsnovnoSredstvo os = new OsnovnoSredstvo();
        os.setId(idOsnovnogSredstva);
        try {
            osDAO.load(PrijavaController.konekcija, os);
        } catch (NotFoundException | SQLException e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
        
        return "Prelaznica_" + os.getNaziv();
        */
        //return "Prelaznica_" + PrelaznicaController.osnovnaSredstvaList.stream().filter(os -> os.getId() == idOsnovnogSredstva).findFirst().get().getNaziv();
        OsnovnoSredstvo tmp = PrelaznicaController.osnovnaSredstvaList.stream().filter(os -> os.getId() == idOsnovnogSredstva).findFirst().orElse(null);
        return "Prelaznica_" + (tmp != null?tmp.getNaziv():"NEPOZNATO");
    }
}
