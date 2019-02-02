package osnovnasredstva.DAO;

import java.sql.*;
import java.util.*;
import osnovnasredstva.DTO.Prostorija;
import osnovnasredstva.util.NotFoundException;


public class ProstorijaDAO {

    public Prostorija createValueObject() {
          return new Prostorija();
    }

    public Prostorija getObject(Connection conn, int id) throws NotFoundException, SQLException {
          Prostorija valueObject = createValueObject();
          valueObject.setId(id);
          load(conn, valueObject);
          return valueObject;
    }

    public void load(Connection conn, Prostorija valueObject) throws NotFoundException, SQLException {
          String sql = "SELECT * FROM prostorija WHERE status=true AND (id = ? ) "; 
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
          String sql = "SELECT * FROM prostorija WHERE status=true ORDER BY id ASC ";
          List searchResults = listQuery(conn, conn.prepareStatement(sql));

          return searchResults;
    }

    public synchronized void create(Connection conn, Prostorija valueObject) throws SQLException {
          String sql = "";
          PreparedStatement stmt = null;
          ResultSet result = null;

          try {
               sql = "INSERT INTO prostorija ( sifra, naziv, opis, "
               + "status, id_zgrade) VALUES (?, ?, ?, ?, ?) ";
               stmt = conn.prepareStatement(sql);

               stmt.setString(1, valueObject.getSifra()); 
               stmt.setString(2, valueObject.getNaziv()); 
               stmt.setString(3, valueObject.getOpis()); 
               stmt.setBoolean(4, valueObject.getStatus()); 
               stmt.setInt(5, valueObject.getIdZgrade()); 

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

    public void save(Connection conn, Prostorija valueObject) 
          throws NotFoundException, SQLException {

          String sql = "UPDATE prostorija SET sifra = ?, naziv = ?, opis = ?, "
               + "status = ?, id_zgrade = ? WHERE (id = ? ) ";
          PreparedStatement stmt = null;

          try {
              stmt = conn.prepareStatement(sql);
              stmt.setString(1, valueObject.getSifra()); 
              stmt.setString(2, valueObject.getNaziv()); 
              stmt.setString(3, valueObject.getOpis()); 
              stmt.setBoolean(4, valueObject.getStatus()); 
              stmt.setInt(5, valueObject.getIdZgrade()); 

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

    public void delete(Connection conn, Prostorija valueObject) 
          throws NotFoundException, SQLException {

          String sql = "UPDATE prostorija SET status=false WHERE (id = ? ) ";
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
    }

    public int countAll(Connection conn) throws SQLException {
          String sql = "SELECT count(*) FROM prostorija";
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

    public List searchMatching(Connection conn, Prostorija valueObject) throws SQLException {
          List searchResults;

          boolean first = true;
          StringBuffer sql = new StringBuffer("SELECT * FROM prostorija WHERE 1=1 ");

          if (valueObject.getId() != 0) {
              if (first) { first = false; }
              sql.append("AND id = ").append(valueObject.getId()).append(" ");
          }

          if (valueObject.getSifra() != null) {
              if (first) { first = false; }
              sql.append("AND sifra LIKE '").append(valueObject.getSifra()).append("%' ");
          }

          if (valueObject.getNaziv() != null) {
              if (first) { first = false; }
              sql.append("AND naziv LIKE '").append(valueObject.getNaziv()).append("%' ");
          }

          if (valueObject.getOpis() != null) {
              if (first) { first = false; }
              sql.append("AND opis LIKE '").append(valueObject.getOpis()).append("%' ");
          }

          if (valueObject.getStatus() != false) {
              if (first) { first = false; }
              sql.append("AND status = ").append(valueObject.getStatus()).append(" ");
          }

          if (valueObject.getIdZgrade() != 0) {
              if (first) { first = false; }
              sql.append("AND id_zgrade = ").append(valueObject.getIdZgrade()).append(" ");
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

    protected void singleQuery(Connection conn, PreparedStatement stmt, Prostorija valueObject) 
          throws NotFoundException, SQLException {

          ResultSet result = null;

          try {
              result = stmt.executeQuery();

              if (result.next()) {

                   valueObject.setId(result.getInt("id")); 
                   valueObject.setSifra(result.getString("sifra")); 
                   valueObject.setNaziv(result.getString("naziv")); 
                   valueObject.setOpis(result.getString("opis")); 
                   valueObject.setStatus(result.getBoolean("status")); 
                   valueObject.setIdZgrade(result.getInt("id_zgrade")); 

              } else {
                    //System.out.println("Prostorija Object Not Found!");
                    //*****NOTIFIKACIJA PROZOR UPOZORENJE*****
                    throw new NotFoundException("Prostorija Object Not Found!");
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
                   Prostorija temp = createValueObject();

                   temp.setId(result.getInt("id")); 
                   temp.setSifra(result.getString("sifra")); 
                   temp.setNaziv(result.getString("naziv")); 
                   temp.setOpis(result.getString("opis")); 
                   temp.setStatus(result.getBoolean("status")); 
                   temp.setIdZgrade(result.getInt("id_zgrade")); 

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
