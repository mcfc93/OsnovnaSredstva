package osnovnasredstva.DAO;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
        
import java.security.SecureRandom;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import osnovnasredstva.DTO.Korisnik;
import osnovnasredstva.util.NotFoundException;
import osnovnasredstva.util.Util;

public class KorisnikDAO {

    public Korisnik createValueObject() {
          return new Korisnik();
    }

    public Korisnik getObject(Connection conn, int id) throws NotFoundException, SQLException {
          Korisnik valueObject = createValueObject();
          valueObject.setId(id);
          load(conn, valueObject);
          return valueObject;
    }
   /* public Korisnik getObject(Connection conn, String ime) throws NotFoundException, SQLException {
          Korisnik valueObject = createValueObject();
          valueObject.setId(id);
          load(conn, valueObject);
          return valueObject;
    }*/

    public void load(Connection conn, Korisnik valueObject) throws NotFoundException, SQLException {
          String sql = "SELECT * FROM korisnik WHERE status=true AND (id = ? ) "; 
          PreparedStatement stmt = null;

          try {
               stmt = conn.prepareStatement(sql);
               stmt.setInt(1, valueObject.getId()); 

               singleQuery(conn, stmt, valueObject);

          } finally {
              if (stmt != null)
                  stmt.close();
          }
    }

    public List loadAll(Connection conn) throws SQLException {
          String sql = "SELECT * FROM korisnik WHERE status=true ORDER BY id ASC ";
          List searchResults = listQuery(conn, conn.prepareStatement(sql));
          return searchResults;
    }

    public synchronized void create(Connection conn, Korisnik valueObject) throws SQLException {
          String sql = "";
          PreparedStatement stmt = null;
          ResultSet result = null;

          try {
               sql = "INSERT INTO korisnik ( korisnicko_ime, hash_lozinke, salt, "
               + "tip, status) VALUES (?, ?, ?, ?, ?) ";
               stmt = conn.prepareStatement(sql);

               stmt.setString(1, valueObject.getKorisnickoIme()); 
               stmt.setString(2, valueObject.getHashLozinke()); 
               stmt.setString(3, valueObject.getSalt()); 
               stmt.setInt(4, valueObject.getTip()); 
               stmt.setBoolean(5, valueObject.getStatus()); 

               int rowcount = databaseUpdate(conn, stmt);
               if (rowcount != 1) {
                    //System.out.println("PrimaryKey Error when updating DB!");
                    //*****NOTIFIKACIJA PROZOR UPOZORENJE*****
                    throw new SQLException("PrimaryKey Error when updating DB!");
               }

          } finally {
              if (stmt != null)
                  stmt.close();
          }

          sql = "SELECT last_insert_id()";

          try {
              stmt = conn.prepareStatement(sql);
              result = stmt.executeQuery();

              if (result.next()) {

                   valueObject.setId((int)result.getLong(1));

              } else {
                   //System.out.println("Unable to find primary-key for created object!");
                   //*****NOTIFIKACIJA PROZOR UPOZORENJE*****
                   throw new SQLException("Unable to find primary-key for created object!");
              }
          } finally {
              if (result != null)
                  result.close();
              if (stmt != null)
                  stmt.close();
          }

    }

    public void save(Connection conn, Korisnik valueObject) 
          throws NotFoundException, SQLException {

          String sql = "UPDATE korisnik SET korisnicko_ime = ?, hash_lozinke = ?, salt = ?, "
               + "tip = ?, status = ? WHERE (id = ? ) ";
          PreparedStatement stmt = null;

          try {
              stmt = conn.prepareStatement(sql);
              stmt.setString(1, valueObject.getKorisnickoIme()); 
              stmt.setString(2, valueObject.getHashLozinke()); 
              stmt.setString(3, valueObject.getSalt()); 
              stmt.setInt(4, valueObject.getTip()); 
              stmt.setBoolean(5, valueObject.getStatus()); 

              stmt.setInt(6, valueObject.getId()); 

              int rowcount = databaseUpdate(conn, stmt);
              if (rowcount == 0) {
                   //System.out.println("Object could not be saved! (PrimaryKey not found)");
                   //*****NOTIFIKACIJA PROZOR UPOZORENJE*****
                   throw new NotFoundException("Object could not be saved! (PrimaryKey not found)");
              }
              if (rowcount > 1) {
                   //System.out.println("PrimaryKey Error when updating DB! (Many objects were affected!)");
                   //*****NOTIFIKACIJA PROZOR UPOZORENJE*****
                   throw new SQLException("PrimaryKey Error when updating DB! (Many objects were affected!)");
              }
          } finally {
              if (stmt != null)
                  stmt.close();
          }
    }

    public boolean delete(Connection conn, Korisnik valueObject) 
          throws NotFoundException, SQLException {

          String sql = "UPDATE korisnik SET status=false WHERE (id = ? ) ";
          PreparedStatement stmt = null;

          try {
              stmt = conn.prepareStatement(sql);
              stmt.setInt(1, valueObject.getId()); 

              int rowcount = databaseUpdate(conn, stmt);
              if (rowcount == 0) {
                   //System.out.println("Object could not be deleted (PrimaryKey not found)");
                   //*****NOTIFIKACIJA PROZOR UPOZORENJE*****
                   return false;
                   //throw new NotFoundException("Object could not be deleted! (PrimaryKey not found)");
              }
              if (rowcount > 1) {
                   //System.out.println("PrimaryKey Error when updating DB! (Many objects were deleted!)");
                   //*****NOTIFIKACIJA PROZOR UPOZORENJE*****
                   return false;
                   //throw new SQLException("PrimaryKey Error when updating DB! (Many objects were deleted!)");
              }
          } finally {
              if (stmt != null)
                  stmt.close();
          }
          return true;
    }

    public int countAll(Connection conn) throws SQLException {
          String sql = "SELECT count(*) FROM korisnik";
          PreparedStatement stmt = null;
          ResultSet result = null;
          int allRows = 0;

          try {
              stmt = conn.prepareStatement(sql);
              result = stmt.executeQuery();

              if (result.next())
                  allRows = result.getInt(1);
          } finally {
              if (result != null)
                  result.close();
              if (stmt != null)
                  stmt.close();
          }
          return allRows;
    }

    public List searchMatching(Connection conn, Korisnik valueObject) throws SQLException {
          List searchResults;
          boolean first = true;
          StringBuffer sql = new StringBuffer("SELECT * FROM korisnik WHERE 1=1 ");

          if (valueObject.getId() != 0) {
              if (first) { first = false; }
              sql.append("AND id = ").append(valueObject.getId()).append(" ");
          }

          if (valueObject.getKorisnickoIme() != null) {
              if (first) { first = false; }
              sql.append("AND korisnicko_ime LIKE '").append(valueObject.getKorisnickoIme()).append("%' ");
          }

          if (valueObject.getHashLozinke() != null) {
              if (first) { first = false; }
              sql.append("AND hash_lozinke LIKE '").append(valueObject.getHashLozinke()).append("%' ");
          }

          if (valueObject.getSalt() != null) {
              if (first) { first = false; }
              sql.append("AND salt LIKE '").append(valueObject.getSalt()).append("%' ");
          }

          if (valueObject.getTip() != 0) {
              if (first) { first = false; }
              sql.append("AND tip = ").append(valueObject.getTip()).append(" ");
          }

          if (valueObject.getStatus() != false) {
              if (first) { first = false; }
              sql.append("AND status = ").append(valueObject.getStatus()).append(" ");
          }

          sql.append("ORDER BY id ASC ");

          // Prevent accidential full table results.
          // Use loadAll if all rows must be returned.
          if (first)
               searchResults = new ArrayList();
          else
               searchResults = listQuery(conn, conn.prepareStatement(sql.toString()));

          return searchResults;
    }

    protected int databaseUpdate(Connection conn, PreparedStatement stmt) throws SQLException {
          int result = stmt.executeUpdate();

          return result;
    }

    protected void singleQuery(Connection conn, PreparedStatement stmt, Korisnik valueObject) 
          throws NotFoundException, SQLException {

          ResultSet result = null;

          try {
              result = stmt.executeQuery();

              if (result.next()) {

                   valueObject.setId(result.getInt("id")); 
                   valueObject.setKorisnickoIme(result.getString("korisnicko_ime")); 
                   valueObject.setHashLozinke(result.getString("hash_lozinke")); 
                   valueObject.setSalt(result.getString("salt")); 
                   valueObject.setTip(result.getInt("tip")); 
                   valueObject.setStatus(result.getBoolean("status")); 

              } else {
                    //System.out.println("Korisnik Object Not Found!");
                    //*****NOTIFIKACIJA PROZOR UPOZORENJE*****
                    throw new NotFoundException("Korisnik Object Not Found!");
              }
          } finally {
              if (result != null)
                  result.close();
              if (stmt != null)
                  stmt.close();
          }
    }

    protected List listQuery(Connection conn, PreparedStatement stmt) throws SQLException {
          ArrayList searchResults = new ArrayList();
          ResultSet result = null;

          try {
              result = stmt.executeQuery();

              while (result.next()) {
                   Korisnik temp = createValueObject();

                   temp.setId(result.getInt("id")); 
                   temp.setKorisnickoIme(result.getString("korisnicko_ime")); 
                   temp.setHashLozinke(result.getString("hash_lozinke")); 
                   temp.setSalt(result.getString("salt")); 
                   temp.setTip(result.getInt("tip")); 
                   temp.setStatus(result.getBoolean("status")); 

                   searchResults.add(temp);
              }

          } finally {
              if (result != null)
                  result.close();
              if (stmt != null)
                  stmt.close();
          }

          return (List)searchResults;
    }
    
    public static final String generisiSalt(){
        SecureRandom random=new SecureRandom();
        byte[] bytes=new byte[8];
        random.nextBytes(bytes);
        StringBuilder sb=new StringBuilder();
        for(byte b:bytes)
            sb.append(String.format("%02x",b));
        return sb.toString();
    }
    
    public static final String hash(String lozinka, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((lozinka + salt).getBytes(StandardCharsets.UTF_8));
            //lozinka = Base64.getEncoder().encodeToString(hash);
            StringBuilder sb = new StringBuilder();
            //sb.append(salt).append("#");
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            lozinka=sb.toString();
            System.out.println(lozinka);
        } catch(NoSuchAlgorithmException e) {
            Util.LOGGER.log(Level.SEVERE, e.toString(), e);
        }
        return lozinka;
    }
    
    public static Korisnik prijava(String korisnickoIme, String lozinka) throws SQLException {
        Korisnik korisnik=null;
        //trazenje hash vrijednosti lozinke
        //String salt=generisiSalt();
        //lozinka=hash(lozinka, salt);
        //System.out.println(lozinka);
        String salt=null;
        
        String sql="";
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        try {
            //sql="select id,tip from korisnik where korisnicko_ime=? and hash_lozinke=? and status=?;";
            //s=Util.prepareStatement(c, sql, false, korisnickoIme, lozinka, 1);
            c=Util.getConnection();
            sql="select salt,hash_lozinke,tip from korisnik where korisnicko_ime=? and status=?";
            s=Util.prepareStatement(c, sql, false, korisnickoIme, 1);
            r=s.executeQuery();
            if(r.next()) {
                System.out.println(r.getString(1) + "#" + r.getString(2));
                salt=r.getString("salt");
                lozinka=hash(lozinka, salt);
                if(lozinka.equals(r.getString("hash_lozinke"))) {
                    //ucitavanje podataka o korisniku iz baze
                    korisnik=new Korisnik(korisnickoIme, lozinka, salt, r.getInt("tip"));
                    /*
                    if(0 == r.getInt("tip")) {
                        korisnik.zaposleni=new Administrator();
                        korisnik.zaposleni.selectZaposleni(korisnik.getKorisnickoIme());
                    } else {
                        korisnik.zaposleni=new Radnik();
                        korisnik.zaposleni.selectZaposleni(korisnik.getKorisnickoIme());
                    }
                    */
System.out.println(korisnik);
                }
            }
        } finally {
            Util.close(r,s,c);
        }
        return korisnik;
    }

}
