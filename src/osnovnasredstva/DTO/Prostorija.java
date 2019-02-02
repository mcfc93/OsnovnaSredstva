package osnovnasredstva.DTO;

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

}
