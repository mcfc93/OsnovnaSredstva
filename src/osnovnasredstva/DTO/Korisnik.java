package osnovnasredstva.DTO;

public class Korisnik {

    private int id;
    private String korisnickoIme;
    private String hashLozinke;
    private String salt;
    private int tip;
    private boolean status;

    public Korisnik () {
    }
    
    public Korisnik (String korisnickoIme, String hashLozinke, String salt, int tip) {
        this.korisnickoIme = korisnickoIme;
        this.hashLozinke = hashLozinke;
        this.salt = salt;
        this.tip = tip;
        this.status=true;
    }

    public int getId() {
        return this.id;
    }
    public void setId(int idIn) {
          this.id = idIn;
    }

    public String getKorisnickoIme() {
          return this.korisnickoIme;
    }
    public void setKorisnickoIme(String korisnickoImeIn) {
          this.korisnickoIme = korisnickoImeIn;
    }

    public String getHashLozinke() {
          return this.hashLozinke;
    }
    public void setHashLozinke(String hashLozinkeIn) {
          this.hashLozinke = hashLozinkeIn;
    }

    public String getSalt() {
          return this.salt;
    }
    public void setSalt(String saltIn) {
          this.salt = saltIn;
    }

    public int getTip() {
          return this.tip;
    }
    public void setTip(int tipIn) {
          this.tip = tipIn;
    }

    public boolean getStatus() {
          return this.status;
    }
    public void setStatus(boolean statusIn) {
          this.status = statusIn;
    }

    @Override
    public String toString() {
        return "Korisnik{" + "id=" + id + ", korisnickoIme=" + korisnickoIme + ", hashLozinke=" + hashLozinke + ", salt=" + salt + ", tip=" + tip + ", status=" + status + '}';
    }
   
}
