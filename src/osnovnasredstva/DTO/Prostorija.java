package osnovnasredstva.DTO;

import java.sql.SQLException;
import java.util.logging.Level;
import osnovnasredstva.DAO.ZgradaDAO;
import osnovnasredstva.prijava.PrijavaController;
import osnovnasredstva.util.NotFoundException;
import osnovnasredstva.util.Util;

public class Prostorija {

    private int id;
    private String sifra;
    private String naziv;
    private String opis;
    private boolean status;
    private int idZgrade;

    public Prostorija () {
    }

    public Prostorija(String sifra, String naziv, String opis, int idZgrade) {
        this.sifra = sifra;
        this.naziv = naziv;
        this.opis = opis;
        this.status = true;
        this.idZgrade = idZgrade;
    }

    public Prostorija(int id, String sifra, String naziv, String opis, boolean status, int idZgrade) {
        this.id = id;
        this.sifra = sifra;
        this.naziv = naziv;
        this.opis = opis;
        this.status = status;
        this.idZgrade = idZgrade;
    }

    public int getId() {
          return this.id;
    }
    public void setId(int idIn) {
          this.id = idIn;
    }

    public String getSifra() {
          return this.sifra;
    }
    public void setSifra(String sifraIn) {
          this.sifra = sifraIn;
    }

    public String getNaziv() {
          return this.naziv;
    }
    public void setNaziv(String nazivIn) {
          this.naziv = nazivIn;
    }

    public String getOpis() {
          return this.opis;
    }
    public void setOpis(String opisIn) {
          this.opis = opisIn;
    }

    public boolean getStatus() {
          return this.status;
    }
    public void setStatus(boolean statusIn) {
          this.status = statusIn;
    }

    public int getIdZgrade() {
          return this.idZgrade;
    }
    public void setIdZgrade(int idZgradeIn) {
          this.idZgrade = idZgradeIn;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Prostorija other = (Prostorija) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        ZgradaDAO zgDAO = new ZgradaDAO();
        Zgrada zg = new Zgrada();
        zg.setId(idZgrade);
        try {
            zgDAO.load(PrijavaController.konekcija, zg);
        } catch (NotFoundException | SQLException e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
        return naziv + " (" + zg.getNaziv() + ")";
    }
    
    
    
}
