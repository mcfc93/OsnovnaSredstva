package osnovnasredstva.DTO;

public class Zgrada {

    private int id;
    private String sifra;
    private String naziv;
    private String opis;
    private boolean status;

    public Zgrada () {
    }

    public Zgrada(String sifra, String naziv, String opis) {
        this.sifra = sifra;
        this.naziv = naziv;
        this.opis = opis;
        this.status = true;
    }

    public Zgrada(int id, String sifra, String naziv, String opis, boolean status) {
        this.id = id;
        this.sifra = sifra;
        this.naziv = naziv;
        this.opis = opis;
        this.status = status;
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
    /*
    @Override
    public String toString() {
        return getNaziv()!=null? getNaziv(): "Svi";
    }
    */
}
