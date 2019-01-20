package osnovnasredstva.beans;

/**
 *
 * @author mcfc93
 */
public class Osoba {
    private String ime;
    private String prezime;
    private String jmbg;
    private String titula;

    public Osoba(String ime, String prezime, String jmbg, String titula) {
        this.ime = ime;
        this.prezime = prezime;
        this.jmbg = jmbg;
        this.titula = titula;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getJmbg() {
        return jmbg;
    }

    public void setJmbg(String jmbg) {
        this.jmbg = jmbg;
    }

    public String getTitula() {
        return titula;
    }

    public void setTitula(String titula) {
        this.titula = titula;
    }

    @Override
    public String toString() {
        return "Osoba{" + "ime=" + ime + ", prezime=" + prezime + ", jmbg=" + jmbg + ", titula=" + titula + '}';
    }
    
}
