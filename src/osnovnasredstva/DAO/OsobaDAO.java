package osnovnasredstva.DAO;

import java.sql.*;
import java.util.*;
import osnovnasredstva.DTO.Osoba;
import osnovnasredstva.util.NotFoundException;

public class OsobaDAO {

    public Osoba createValueObject() {
          return new Osoba();
    }

    public Osoba getObject(Connection conn, int id) throws NotFoundException, SQLException {
          Osoba valueObject = createValueObject();
          valueObject.setId(id);
          load(conn, valueObject);
          return valueObject;
    }

    public void load(Connection conn, Osoba valueObject) throws NotFoundException, SQLException {
          String sql = "SELECT * FROM osoba WHERE status=true AND (id = ? ) "; 
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
          String sql = "SELECT * FROM osoba WHERE status=true ORDER BY id ASC ";
          List searchResults = listQuery(conn, conn.prepareStatement(sql));

          return searchResults;
    }

    public synchronized void create(Connection conn, Osoba valueObject) throws SQLException {
          String sql = "";
          PreparedStatement stmt = null;
          ResultSet result = null;

          try {
               sql = "INSERT INTO osoba ( ime, prezime, titula, "
               + "jmbg, zaposlenje, telefon, "
               + "email, adresa, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ";
               stmt = conn.prepareStatement(sql);

               stmt.setString(1, valueObject.getIme()); 
               stmt.setString(2, valueObject.getPrezime()); 
               stmt.setString(3, valueObject.getTitula()); 
               stmt.setString(4, valueObject.getJmbg()); 
               stmt.setString(5, valueObject.getZaposlenje()); 
               stmt.setString(6, valueObject.getTelefon()); 
               stmt.setString(7, valueObject.getEmail()); 
               stmt.setString(8, valueObject.getAdresa()); 
               stmt.setBoolean(9, valueObject.getStatus()); 

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

    public void save(Connection conn, Osoba valueObject) 
          throws NotFoundException, SQLException {

          String sql = "UPDATE osoba SET ime = ?, prezime = ?, titula = ?, "
               + "jmbg = ?, zaposlenje = ?, telefon = ?, "
               + "email = ?, adresa = ?, status = ? WHERE (id = ? ) ";
          PreparedStatement stmt = null;

          try {
              stmt = conn.prepareStatement(sql);
              stmt.setString(1, valueObject.getIme()); 
              stmt.setString(2, valueObject.getPrezime()); 
              stmt.setString(3, valueObject.getTitula()); 
              stmt.setString(4, valueObject.getJmbg()); 
              stmt.setString(5, valueObject.getZaposlenje()); 
              stmt.setString(6, valueObject.getTelefon()); 
              stmt.setString(7, valueObject.getEmail()); 
              stmt.setString(8, valueObject.getAdresa()); 
              stmt.setBoolean(9, valueObject.getStatus()); 

              stmt.setInt(10, valueObject.getId()); 

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

    public boolean delete(Connection conn, Osoba valueObject) 
          throws NotFoundException, SQLException {

          String sql = "UPDATE osoba SET status=false WHERE (id = ? ) ";
          PreparedStatement stmt = null;

          try {
              stmt = conn.prepareStatement(sql);
              stmt.setInt(1, valueObject.getId()); 

              int rowcount = databaseUpdate(conn, stmt);
              if (rowcount == 0) {
                   //System.out.println("Object could not be deleted (PrimaryKey not found)");
                   //*****NOTIFIKACIJA PROZOR UPOZORENJE*****
                   throw new NotFoundException("Object could not be deleted! (PrimaryKey not found)");
              }
              if (rowcount > 1) {
                   //System.out.println("PrimaryKey Error when updating DB! (Many objects were deleted!)");
                   //*****NOTIFIKACIJA PROZOR UPOZORENJE*****
                   throw new SQLException("PrimaryKey Error when updating DB! (Many objects were deleted!)");
              }
          } finally {
              if (stmt != null)
                  stmt.close();
          }
          return true;
    }

    public int countAll(Connection conn) throws SQLException {
          String sql = "SELECT count(*) FROM osoba";
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

    public List searchMatching(Connection conn, Osoba valueObject) throws SQLException {
          List searchResults;

          boolean first = true;
          StringBuffer sql = new StringBuffer("SELECT * FROM osoba WHERE 1=1 ");

          if (valueObject.getId() != 0) {
              if (first) { first = false; }
              sql.append("AND id = ").append(valueObject.getId()).append(" ");
          }

          if (valueObject.getIme() != null) {
              if (first) { first = false; }
              sql.append("AND ime LIKE '").append(valueObject.getIme()).append("%' ");
          }

          if (valueObject.getPrezime() != null) {
              if (first) { first = false; }
              sql.append("AND prezime LIKE '").append(valueObject.getPrezime()).append("%' ");
          }

          if (valueObject.getTitula() != null) {
              if (first) { first = false; }
              sql.append("AND titula LIKE '").append(valueObject.getTitula()).append("%' ");
          }

          if (valueObject.getJmbg() != null) {
              if (first) { first = false; }
              sql.append("AND jmbg LIKE '").append(valueObject.getJmbg()).append("%' ");
          }

          if (valueObject.getZaposlenje() != null) {
              if (first) { first = false; }
              sql.append("AND zaposlenje LIKE '").append(valueObject.getZaposlenje()).append("%' ");
          }

          if (valueObject.getTelefon() != null) {
              if (first) { first = false; }
              sql.append("AND telefon LIKE '").append(valueObject.getTelefon()).append("%' ");
          }

          if (valueObject.getEmail() != null) {
              if (first) { first = false; }
              sql.append("AND email LIKE '").append(valueObject.getEmail()).append("%' ");
          }

          if (valueObject.getAdresa() != null) {
              if (first) { first = false; }
              sql.append("AND adresa LIKE '").append(valueObject.getAdresa()).append("%' ");
          }

          if (valueObject.getStatus() != false) {
              if (first) { first = false; }
              sql.append("AND status LIKE '").append(valueObject.getStatus()).append("%' ");
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

    protected void singleQuery(Connection conn, PreparedStatement stmt, Osoba valueObject) 
          throws NotFoundException, SQLException {

          ResultSet result = null;

          try {
              result = stmt.executeQuery();

              if (result.next()) {

                   valueObject.setId(result.getInt("id")); 
                   valueObject.setIme(result.getString("ime")); 
                   valueObject.setPrezime(result.getString("prezime")); 
                   valueObject.setTitula(result.getString("titula")); 
                   valueObject.setJmbg(result.getString("jmbg")); 
                   valueObject.setZaposlenje(result.getString("zaposlenje")); 
                   valueObject.setTelefon(result.getString("telefon")); 
                   valueObject.setEmail(result.getString("email")); 
                   valueObject.setAdresa(result.getString("adresa")); 
                   valueObject.setStatus(result.getBoolean("status")); 

              } else {
                    //System.out.println("Osoba Object Not Found!");
                    //*****NOTIFIKACIJA PROZOR UPOZORENJE*****
                    throw new NotFoundException("Osoba Object Not Found!");
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
                   Osoba temp = createValueObject();

                   temp.setId(result.getInt("id")); 
                   temp.setIme(result.getString("ime")); 
                   temp.setPrezime(result.getString("prezime")); 
                   temp.setTitula(result.getString("titula")); 
                   temp.setJmbg(result.getString("jmbg")); 
                   temp.setZaposlenje(result.getString("zaposlenje")); 
                   temp.setTelefon(result.getString("telefon")); 
                   temp.setEmail(result.getString("email")); 
                   temp.setAdresa(result.getString("adresa")); 
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


}

