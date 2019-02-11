package osnovnasredstva.DTO;

import osnovnasredstva.prijava.PrijavaController;

public class Osoba {

    private int id;
    private String ime;
    private String prezime;
    private String titula;
    private String jmbg;
    private String zaposlenje;
    private String telefon;
    private String email;
    private String adresa;
    private boolean status;

    public Osoba () {
    }

    public Osoba(String ime, String prezime, String titula, String jmbg, String zaposlenje, String telefon, String email, String adresa) {
        this.ime = ime;
        this.prezime = prezime;
        this.titula = titula;
        this.jmbg = jmbg;
        this.zaposlenje = zaposlenje;
        this.telefon = telefon;
        this.email = email;
        this.adresa = adresa;
        this.status = true;
    }

    public Osoba(int id, String ime, String prezime, String titula, String jmbg, String zaposlenje, String telefon, String email, String adresa, boolean status) {
        this.id = id;
        this.ime = ime;
        this.prezime = prezime;
        this.titula = titula;
        this.jmbg = jmbg;
        this.zaposlenje = zaposlenje;
        this.telefon = telefon;
        this.email = email;
        this.adresa = adresa;
        this.status = status;
    }

    public int getId() {
          return this.id;
    }
    public void setId(int idIn) {
          this.id = idIn;
    }

    public String getIme() {
          return this.ime;
    }
    public void setIme(String imeIn) {
          this.ime = imeIn;
    }

    public String getPrezime() {
          return this.prezime;
    }
    public void setPrezime(String prezimeIn) {
          this.prezime = prezimeIn;
    }

    public String getTitula() {
          return this.titula;
    }
    public void setTitula(String titulaIn) {
          this.titula = titulaIn;
    }

    public String getJmbg() {
          return PrijavaController.korisnik.getTip()==0? this.jmbg: this.jmbg.substring(0, 7) + "******";
    }
    public void setJmbg(String jmbgIn) {
          this.jmbg = jmbgIn;
    }

    public String getZaposlenje() {
          return this.zaposlenje;
    }
    public void setZaposlenje(String zaposlenjeIn) {
          this.zaposlenje = zaposlenjeIn;
    }

    public String getTelefon() {
          return this.telefon;
    }
    public void setTelefon(String telefonIn) {
          this.telefon = telefonIn;
    }

    public String getEmail() {
          return this.email;
    }
    public void setEmail(String emailIn) {
          this.email = emailIn;
    }

    public String getAdresa() {
          return this.adresa;
    }
    public void setAdresa(String adresaIn) {
          this.adresa = adresaIn;
    }

    public boolean getStatus() {
          return this.status;
    }
    public void setStatus(boolean statusIn) {
          this.status = statusIn;
    }
    /*
    public String getJmbgValue() {
          return this.jmbg.substring(0, 7) + "******";
    }
    */
    @Override
    public String toString() {
        return "Osoba{" + "id=" + id + ", ime=" + ime + ", prezime=" + prezime + ", titula=" + titula + ", jmbg=" + jmbg + ", zaposlenje=" + zaposlenje + ", telefon=" + telefon + ", email=" + email + ", adresa=" + adresa + ", status=" + status + '}';
    }
    
}
