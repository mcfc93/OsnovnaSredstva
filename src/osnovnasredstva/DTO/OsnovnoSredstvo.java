package osnovnasredstva.DTO;

import java.sql.*;
import java.math.*;
import java.time.LocalDate;
import java.time.Period;
import osnovnasredstva.administrator.LokacijeController;
import osnovnasredstva.administrator.OsnovnaSredstvaController;
import osnovnasredstva.administrator.OsobeController;

public class OsnovnoSredstvo {

    private int id;
    private String inventarniBroj;
    private String naziv;
    private String opis;
    private Timestamp datumNabavke;
    private BigDecimal nabavnaVrijednost;
    private int stopaAmortizacije;
    private boolean status;
    private int idLokacije;
    private int idOsobe;
    private int idVrste;

    public OsnovnoSredstvo () {
    }

    public OsnovnoSredstvo(String inventarniBroj, String naziv, String opis, Timestamp datumNabavke, BigDecimal nabavnaVrijednost, int stopaAmortizacije, int idLokacije, int idOsobe, int idVrste) {
        this.inventarniBroj = inventarniBroj;
        this.naziv = naziv;
        this.opis = opis;
        this.datumNabavke = datumNabavke;
        this.nabavnaVrijednost = nabavnaVrijednost;
        this.stopaAmortizacije = stopaAmortizacije;
        this.status = true;
        this.idLokacije = idLokacije;
        this.idOsobe = idOsobe;
        this.idVrste = idVrste;
    }

    public OsnovnoSredstvo(int id, String inventarniBroj, String naziv, String opis, Timestamp datumNabavke, BigDecimal nabavnaVrijednost, int stopaAmortizacije, boolean status, int idLokacije, int idOsobe, int idVrste) {
        this.id = id;
        this.inventarniBroj = inventarniBroj;
        this.naziv = naziv;
        this.opis = opis;
        this.datumNabavke = datumNabavke;
        this.nabavnaVrijednost = nabavnaVrijednost;
        this.stopaAmortizacije = stopaAmortizacije;
        this.status = status;
        this.idLokacije = idLokacije;
        this.idOsobe = idOsobe;
        this.idVrste = idVrste;
    }

    public int getId() {
          return this.id;
    }
    public void setId(int idIn) {
          this.id = idIn;
    }

    public String getInventarniBroj() {
          return this.inventarniBroj;
    }
    public void setInventarniBroj(String inventarniBrojIn) {
          this.inventarniBroj = inventarniBrojIn;
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

    public Timestamp getDatumNabavke() {
          return this.datumNabavke;
    }
    public void setDatumNabavke(Timestamp datumNabavkeIn) {
          this.datumNabavke = datumNabavkeIn;
    }

    public BigDecimal getNabavnaVrijednost() {
          return this.nabavnaVrijednost;
    }
    public void setNabavnaVrijednost(BigDecimal nabavnaVrijednostIn) {
          this.nabavnaVrijednost = nabavnaVrijednostIn;
    }

    public int getStopaAmortizacije() {
          return this.stopaAmortizacije;
    }
    public void setStopaAmortizacije(int stopaAmortizacijeIn) {
          this.stopaAmortizacije = stopaAmortizacijeIn;
    }

    public boolean getStatus() {
          return this.status;
    }
    public void setStatus(boolean statusIn) {
          this.status = statusIn;
    }

    public int getIdLokacije() {
          return this.idLokacije;
    }
    public void setIdLokacije(int idLokacijeIn) {
          this.idLokacije = idLokacijeIn;
    }

    public int getIdOsobe() {
          return this.idOsobe;
    }
    public void setIdOsobe(int idOsobeIn) {
          this.idOsobe = idOsobeIn;
    }

    public int getIdVrste() {
          return this.idVrste;
    }
    public void setIdVrste(int idVrsteIn) {
          this.idVrste = idVrsteIn;
    }
    
    public String getIdVrsteString() {
        for(VrstaOS vrsta: OsnovnaSredstvaController.vrstaOsnovnogSredstvaList) {
            if(vrsta.getId() == this.getIdVrste())
                return vrsta.getNaziv();
        }
        return "NEPOZNATO";
    }
    /*
    public BigDecimal getVrijednost(){
        int godine=Period.between(datumNabavke.toLocalDateTime().toLocalDate(), LocalDate.now()).getYears();
        return nabavnaVrijednost.subtract(nabavnaVrijednost.multiply(new BigDecimal((double)godine/stopaAmortizacije))).setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }
    */
    public BigDecimal getVrijednost(){
        int godine=Period.between(datumNabavke.toLocalDateTime().toLocalDate(), LocalDate.now()).getYears();
        BigDecimal x = nabavnaVrijednost.subtract(nabavnaVrijednost.multiply(new BigDecimal((double)godine/stopaAmortizacije))).setScale(2, BigDecimal.ROUND_HALF_EVEN);
        if(x.compareTo(new BigDecimal(0)) < 0)
            return new BigDecimal(0);
        else
            return x;
    }
    
    public String getProstorijaZgrada() {
        Prostorija prostorija = LokacijeController.prostorijeList.stream().filter(lokacija -> lokacija.getId() == getIdLokacije()).findFirst().orElse(null);
        Zgrada zgrada = null;
        if(prostorija != null) {
            zgrada = LokacijeController.zgradeList.stream().filter(z -> z.getId() == prostorija.getIdZgrade()).findFirst().orElse(null);
        }
        return (prostorija!=null ?prostorija.getNaziv():"NEPOZNATO") + " (" + (zgrada!=null?zgrada.getNaziv():"NEPOZNATO") + ")";
    }
    
    public String getImePrezimeOsobe() {
        return OsobeController.osobeList.stream().filter(o -> o.getId() == idOsobe).findFirst().get().getImePrezime();
    }
}
