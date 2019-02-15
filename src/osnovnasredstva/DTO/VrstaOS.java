package osnovnasredstva.DTO;

// *** DA LI TREBA STATUS OVDE? ***

public class VrstaOS {

    private int id;
    private String naziv;
    private String opis;

    public VrstaOS () {
    }

    public VrstaOS(String naziv, String opis) {
        this.naziv = naziv;
        this.opis = opis;
    }

    public VrstaOS(int id, String naziv, String opis) {
        this.id = id;
        this.naziv = naziv;
        this.opis = opis;
    }

    public int getId() {
          return this.id;
    }
    public void setId(int idIn) {
          this.id = idIn;
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

    @Override
    public String toString() {
        return naziv!=null? naziv: "Svi";
    }
    
}
