package osnovnasredstva.DTO;

import java.io.Serializable;

public class Korisnik implements Serializable {

    private int id;
    private String korisnickoIme;
    private String hashLozinke;
    private String salt;
    private int tip;
    private int privilegijaTip;
    private boolean status;

    public Korisnik () {
    }
    
    public Korisnik (String korisnickoIme, String hashLozinke, String salt, int tip) {
        this.korisnickoIme = korisnickoIme;
        this.hashLozinke = hashLozinke;
        this.salt = salt;
        this.tip = tip;
        this.privilegijaTip = tip;
        this.status=true;
    }

    public Korisnik(int id, String korisnickoIme, String hashLozinke, String salt, int tip, boolean status) {
        this.id = id;
        this.korisnickoIme = korisnickoIme;
        this.hashLozinke = hashLozinke;
        this.salt = salt;
        this.tip = tip;
        this.privilegijaTip = tip;
        this.status = status;
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

    public String getTipString() {
          if(tip == 0)
              return "Administrator";
          else
              return "Nadzornik";
    }

    public int getTip() {
        return tip;
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
    
    public int getPrivilegijaTip() {
        return privilegijaTip;
    }

    public void setPrivilegijaTip(int privilegijaTip) {
        this.privilegijaTip = privilegijaTip;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final Korisnik other = (Korisnik) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Korisnik{" + "id=" + id + ", korisnickoIme=" + korisnickoIme + ", hashLozinke=" + hashLozinke + ", salt=" + salt + ", tip=" + tip + ", status=" + status + '}';
    }

}
