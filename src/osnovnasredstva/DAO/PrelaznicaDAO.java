package osnovnasredstva.DAO;

import java.sql.*;
import java.util.*;
import osnovnasredstva.DTO.Prelaznica;
import osnovnasredstva.util.NotFoundException;


public class PrelaznicaDAO {

    public Prelaznica createValueObject() {
          return new Prelaznica();
    }

    public Prelaznica getObject(Connection conn, int id) throws NotFoundException, SQLException {
          Prelaznica valueObject = createValueObject();
          valueObject.setId(id);
          load(conn, valueObject);
          return valueObject;
    }

    public void load(Connection conn, Prelaznica valueObject) throws NotFoundException, SQLException {
          String sql = "SELECT * FROM prelaznica WHERE status=true AND (id = ? ) "; 
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
          String sql = "SELECT * FROM prelaznica WHERE status=true ORDER BY id ASC ";
          List searchResults = listQuery(conn, conn.prepareStatement(sql));

          return searchResults;
    }

    public synchronized void create(Connection conn, Prelaznica valueObject) throws SQLException {
          String sql = "";
          PreparedStatement stmt = null;
          ResultSet result = null;

          try {
               sql = "INSERT INTO prelaznica ( datum_prelaska, napomena, status, "
               + "id_prostorije_iz, id_prostorije_u, id_osobe_sa, "
               + "id_osobe_na, id_osnovnog_sredstva) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ";
               stmt = conn.prepareStatement(sql);

               stmt.setTimestamp(1, valueObject.getDatumPrelaska()); 
               stmt.setString(2, valueObject.getNapomena()); 
               stmt.setBoolean(3, valueObject.getStatus()); 
               stmt.setInt(4, valueObject.getIdProstorijeIz()); 
               stmt.setInt(5, valueObject.getIdProstorijeU()); 
               stmt.setInt(6, valueObject.getIdOsobeSa()); 
               stmt.setInt(7, valueObject.getIdOsobeNa()); 
               stmt.setInt(8, valueObject.getIdOsnovnogSredstva()); 

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

    public void save(Connection conn, Prelaznica valueObject) 
          throws NotFoundException, SQLException {

          String sql = "UPDATE prelaznica SET datum_prelaska = ?, napomena = ?, status = ?, "
               + "id_prostorije_iz = ?, id_prostorije_u = ?, id_osobe_sa = ?, "
               + "id_osobe_na = ?, id_osnovnog_sredstva = ? WHERE (id = ? ) ";
          PreparedStatement stmt = null;

          try {
              stmt = conn.prepareStatement(sql);
              stmt.setTimestamp(1, valueObject.getDatumPrelaska()); 
              stmt.setString(2, valueObject.getNapomena()); 
              stmt.setBoolean(3, valueObject.getStatus()); 
              stmt.setInt(4, valueObject.getIdProstorijeIz()); 
              stmt.setInt(5, valueObject.getIdProstorijeU()); 
              stmt.setInt(6, valueObject.getIdOsobeSa()); 
              stmt.setInt(7, valueObject.getIdOsobeNa()); 
              stmt.setInt(8, valueObject.getIdOsnovnogSredstva()); 

              stmt.setInt(9, valueObject.getId()); 

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

    public void delete(Connection conn, Prelaznica valueObject) 
          throws NotFoundException, SQLException {

          String sql = "UPDATE prelaznica SET status=false WHERE (id = ? ) ";
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
          String sql = "SELECT count(*) FROM prelaznica";
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

    public List searchMatching(Connection conn, Prelaznica valueObject) throws SQLException {
          List searchResults;

          boolean first = true;
          StringBuffer sql = new StringBuffer("SELECT * FROM prelaznica WHERE 1=1 ");

          if (valueObject.getId() != 0) {
              if (first) { first = false; }
              sql.append("AND id = ").append(valueObject.getId()).append(" ");
          }

          if (valueObject.getDatumPrelaska() != null) {
              if (first) { first = false; }
              sql.append("AND datum_prelaska = '").append(valueObject.getDatumPrelaska()).append("' ");
          }

          if (valueObject.getNapomena() != null) {
              if (first) { first = false; }
              sql.append("AND napomena LIKE '").append(valueObject.getNapomena()).append("%' ");
          }

          if (valueObject.getStatus() != false) {
              if (first) { first = false; }
              sql.append("AND status = ").append(valueObject.getStatus()).append(" ");
          }

          if (valueObject.getIdProstorijeIz() != 0) {
              if (first) { first = false; }
              sql.append("AND id_prostorije_iz = ").append(valueObject.getIdProstorijeIz()).append(" ");
          }

          if (valueObject.getIdProstorijeU() != 0) {
              if (first) { first = false; }
              sql.append("AND id_prostorije_u = ").append(valueObject.getIdProstorijeU()).append(" ");
          }

          if (valueObject.getIdOsobeSa() != 0) {
              if (first) { first = false; }
              sql.append("AND id_osobe_sa = ").append(valueObject.getIdOsobeSa()).append(" ");
          }

          if (valueObject.getIdOsobeNa() != 0) {
              if (first) { first = false; }
              sql.append("AND id_osobe_na = ").append(valueObject.getIdOsobeNa()).append(" ");
          }

          if (valueObject.getIdOsnovnogSredstva() != 0) {
              if (first) { first = false; }
              sql.append("AND id_osnovnog_sredstva = ").append(valueObject.getIdOsnovnogSredstva()).append(" ");
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

    protected void singleQuery(Connection conn, PreparedStatement stmt, Prelaznica valueObject) 
          throws NotFoundException, SQLException {

          ResultSet result = null;

          try {
              result = stmt.executeQuery();

              if (result.next()) {

                   valueObject.setId(result.getInt("id")); 
                   valueObject.setDatumPrelaska(result.getTimestamp("datum_prelaska")); 
                   valueObject.setNapomena(result.getString("napomena")); 
                   valueObject.setStatus(result.getBoolean("status")); 
                   valueObject.setIdProstorijeIz(result.getInt("id_prostorije_iz")); 
                   valueObject.setIdProstorijeU(result.getInt("id_prostorije_u")); 
                   valueObject.setIdOsobeSa(result.getInt("id_osobe_sa")); 
                   valueObject.setIdOsobeNa(result.getInt("id_osobe_na")); 
                   valueObject.setIdOsnovnogSredstva(result.getInt("id_osnovnog_sredstva")); 

              } else {
                    //System.out.println("Prelaznica Object Not Found!");
                    //*****NOTIFIKACIJA PROZOR UPOZORENJE*****
                    throw new NotFoundException("Prelaznica Object Not Found!");
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
                   Prelaznica temp = createValueObject();

                   temp.setId(result.getInt("id")); 
                   temp.setDatumPrelaska(result.getTimestamp("datum_prelaska")); 
                   temp.setNapomena(result.getString("napomena")); 
                   temp.setStatus(result.getBoolean("status")); 
                   temp.setIdProstorijeIz(result.getInt("id_prostorije_iz")); 
                   temp.setIdProstorijeU(result.getInt("id_prostorije_u")); 
                   temp.setIdOsobeSa(result.getInt("id_osobe_sa")); 
                   temp.setIdOsobeNa(result.getInt("id_osobe_na")); 
                   temp.setIdOsnovnogSredstva(result.getInt("id_osnovnog_sredstva")); 

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
