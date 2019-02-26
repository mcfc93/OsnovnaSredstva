package osnovnasredstva.DAO;


import java.sql.*;
import java.util.*;
import osnovnasredstva.DTO.OsnovnoSredstvo;
import osnovnasredstva.util.NotFoundException;


public class OsnovnoSredstvoDAO {

    public OsnovnoSredstvo createValueObject() {
          return new OsnovnoSredstvo();
    }

    public OsnovnoSredstvo getObject(Connection conn, int id) throws NotFoundException, SQLException {
          OsnovnoSredstvo valueObject = createValueObject();
          valueObject.setId(id);
          load(conn, valueObject);
          return valueObject;
    }

    public void load(Connection conn, OsnovnoSredstvo valueObject) throws NotFoundException, SQLException {
          String sql = "SELECT * FROM osnovno_sredstvo WHERE status=true AND (id = ? ) "; 
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
          String sql = "SELECT * FROM osnovno_sredstvo WHERE status=true ORDER BY id ASC ";
          List searchResults = listQuery(conn, conn.prepareStatement(sql));
          return searchResults;
    }
    
   public List loadAll2(Connection conn) throws SQLException {
          String sql = "SELECT * FROM osnovno_sredstvo ORDER BY id ASC ";
          List searchResults = listQuery(conn, conn.prepareStatement(sql));
          return searchResults;
    }
    
    public List loadAll3(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM osnovno_sredstvo WHERE (id_osobe = ? ) AND status=true ORDER BY id ASC ";
        PreparedStatement stmt = null;
        
        stmt=conn.prepareStatement(sql);
        ArrayList searchResults = new ArrayList();
        ResultSet result = null;

          try {
              stmt.setInt(1,id);
              result = stmt.executeQuery();

              while (result.next()) {
                   OsnovnoSredstvo temp = createValueObject();

                   temp.setId(result.getInt("id")); 
                   temp.setInventarniBroj(result.getString("inventarni_broj")); 
                   temp.setNaziv(result.getString("naziv")); 
                   temp.setOpis(result.getString("opis")); 
                   temp.setDatumNabavke(result.getTimestamp("datum_nabavke")); 
                   temp.setNabavnaVrijednost(result.getBigDecimal("nabavna_vrijednost")); 
                   temp.setStopaAmortizacije(result.getInt("stopa_amortizacije")); 
                   temp.setStatus(result.getBoolean("status")); 
                   temp.setIdLokacije(result.getInt("id_lokacije")); 
                   temp.setIdOsobe(result.getInt("id_osobe")); 
                   temp.setIdVrste(result.getInt("id_vrste")); 

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
public List loadAll4(Connection conn, int id) throws SQLException {
        String sql = "SELECT * FROM osnovno_sredstvo WHERE (id_lokacije = ? ) AND status=true ORDER BY id ASC ";
        PreparedStatement stmt = null;
        
        stmt=conn.prepareStatement(sql);
        ArrayList searchResults = new ArrayList();
        ResultSet result = null;

          try {
              stmt.setInt(1,id);
              result = stmt.executeQuery();

              while (result.next()) {
                   OsnovnoSredstvo temp = createValueObject();

                   temp.setId(result.getInt("id")); 
                   temp.setInventarniBroj(result.getString("inventarni_broj")); 
                   temp.setNaziv(result.getString("naziv")); 
                   temp.setOpis(result.getString("opis")); 
                   temp.setDatumNabavke(result.getTimestamp("datum_nabavke")); 
                   temp.setNabavnaVrijednost(result.getBigDecimal("nabavna_vrijednost")); 
                   temp.setStopaAmortizacije(result.getInt("stopa_amortizacije")); 
                   temp.setStatus(result.getBoolean("status")); 
                   temp.setIdLokacije(result.getInt("id_lokacije")); 
                   temp.setIdOsobe(result.getInt("id_osobe")); 
                   temp.setIdVrste(result.getInt("id_vrste")); 

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
    public synchronized void create(Connection conn, OsnovnoSredstvo valueObject) throws SQLException {
          String sql = "";
          PreparedStatement stmt = null;
          ResultSet result = null;

          try {
               sql = "INSERT INTO osnovno_sredstvo ( inventarni_broj, naziv, opis, "
               + "datum_nabavke, nabavna_vrijednost, stopa_amortizacije, "
               + "status, id_lokacije, id_osobe, "
               + "id_vrste) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
               stmt = conn.prepareStatement(sql);

               stmt.setString(1, valueObject.getInventarniBroj()); 
               stmt.setString(2, valueObject.getNaziv()); 
               stmt.setString(3, valueObject.getOpis()); 
               stmt.setTimestamp(4, valueObject.getDatumNabavke()); 
               stmt.setBigDecimal(5, valueObject.getNabavnaVrijednost()); 
               stmt.setInt(6, valueObject.getStopaAmortizacije()); 
               stmt.setBoolean(7, valueObject.getStatus()); 
               stmt.setInt(8, valueObject.getIdLokacije()); 
               stmt.setInt(9, valueObject.getIdOsobe()); 
               stmt.setInt(10, valueObject.getIdVrste()); 

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

    public void save(Connection conn, OsnovnoSredstvo valueObject) 
          throws NotFoundException, SQLException {

          String sql = "UPDATE osnovno_sredstvo SET inventarni_broj = ?, naziv = ?, opis = ?, "
               + "datum_nabavke = ?, nabavna_vrijednost = ?, stopa_amortizacije = ?, "
               + "status = ?, id_lokacije = ?, id_osobe = ?, "
               + "id_vrste = ? WHERE (id = ? ) ";
          PreparedStatement stmt = null;

          try {
              stmt = conn.prepareStatement(sql);
              stmt.setString(1, valueObject.getInventarniBroj()); 
              stmt.setString(2, valueObject.getNaziv()); 
              stmt.setString(3, valueObject.getOpis()); 
              stmt.setTimestamp(4, valueObject.getDatumNabavke()); 
              stmt.setBigDecimal(5, valueObject.getNabavnaVrijednost()); 
              stmt.setInt(6, valueObject.getStopaAmortizacije()); 
              stmt.setBoolean(7, valueObject.getStatus()); 
              stmt.setInt(8, valueObject.getIdLokacije()); 
              stmt.setInt(9, valueObject.getIdOsobe()); 
              stmt.setInt(10, valueObject.getIdVrste()); 

              stmt.setInt(11, valueObject.getId()); 

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

    public void delete(Connection conn, OsnovnoSredstvo valueObject) 
          throws NotFoundException, SQLException {

          String sql = "UPDATE osnovno_sredstvo SET status=false WHERE (id = ? ) ";
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
          String sql = "SELECT count(*) FROM osnovno_sredstvo";
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

    public List searchMatching(Connection conn, OsnovnoSredstvo valueObject) throws SQLException {
          List searchResults;

          boolean first = true;
          StringBuffer sql = new StringBuffer("SELECT * FROM osnovno_sredstvo WHERE 1=1 ");

          if (valueObject.getId() != 0) {
              if (first) { first = false; }
              sql.append("AND id = ").append(valueObject.getId()).append(" ");
          }

          if (valueObject.getInventarniBroj() != null) {
              if (first) { first = false; }
              sql.append("AND inventarni_broj LIKE '").append(valueObject.getInventarniBroj()).append("%' ");
          }

          if (valueObject.getNaziv() != null) {
              if (first) { first = false; }
              sql.append("AND naziv LIKE '").append(valueObject.getNaziv()).append("%' ");
          }

          if (valueObject.getOpis() != null) {
              if (first) { first = false; }
              sql.append("AND opis LIKE '").append(valueObject.getOpis()).append("%' ");
          }

          if (valueObject.getDatumNabavke() != null) {
              if (first) { first = false; }
              sql.append("AND datum_nabavke = '").append(valueObject.getDatumNabavke()).append("' ");
          }

          if (valueObject.getNabavnaVrijednost() != null) {
              if (first) { first = false; }
              sql.append("AND nabavna_vrijednost = '").append(valueObject.getNabavnaVrijednost()).append("' ");
          }

          if (valueObject.getStopaAmortizacije() != 0) {
              if (first) { first = false; }
              sql.append("AND stopa_amortizacije = ").append(valueObject.getStopaAmortizacije()).append(" ");
          }

          if (valueObject.getStatus() != false) {
              if (first) { first = false; }
              sql.append("AND status = ").append(valueObject.getStatus()).append(" ");
          }

          if (valueObject.getIdLokacije() != 0) {
              if (first) { first = false; }
              sql.append("AND id_lokacije = ").append(valueObject.getIdLokacije()).append(" ");
          }

          if (valueObject.getIdOsobe() != 0) {
              if (first) { first = false; }
              sql.append("AND id_osobe = ").append(valueObject.getIdOsobe()).append(" ");
          }

          if (valueObject.getIdVrste() != 0) {
              if (first) { first = false; }
              sql.append("AND id_vrste = ").append(valueObject.getIdVrste()).append(" ");
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

    protected void singleQuery(Connection conn, PreparedStatement stmt, OsnovnoSredstvo valueObject) 
          throws NotFoundException, SQLException {

          ResultSet result = null;

          try {
              result = stmt.executeQuery();

              if (result.next()) {

                   valueObject.setId(result.getInt("id")); 
                   valueObject.setInventarniBroj(result.getString("inventarni_broj")); 
                   valueObject.setNaziv(result.getString("naziv")); 
                   valueObject.setOpis(result.getString("opis")); 
                   valueObject.setDatumNabavke(result.getTimestamp("datum_nabavke")); 
                   valueObject.setNabavnaVrijednost(result.getBigDecimal("nabavna_vrijednost")); 
                   valueObject.setStopaAmortizacije(result.getInt("stopa_amortizacije")); 
                   valueObject.setStatus(result.getBoolean("status")); 
                   valueObject.setIdLokacije(result.getInt("id_lokacije")); 
                   valueObject.setIdOsobe(result.getInt("id_osobe")); 
                   valueObject.setIdVrste(result.getInt("id_vrste")); 

              } else {
                    //System.out.println("OsnovnoSredstvo Object Not Found!");
                    //*****NOTIFIKACIJA PROZOR UPOZORENJE*****
                    throw new NotFoundException("OsnovnoSredstvo Object Not Found!");
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
                   OsnovnoSredstvo temp = createValueObject();

                   temp.setId(result.getInt("id")); 
                   temp.setInventarniBroj(result.getString("inventarni_broj")); 
                   temp.setNaziv(result.getString("naziv")); 
                   temp.setOpis(result.getString("opis")); 
                   temp.setDatumNabavke(result.getTimestamp("datum_nabavke")); 
                   temp.setNabavnaVrijednost(result.getBigDecimal("nabavna_vrijednost")); 
                   temp.setStopaAmortizacije(result.getInt("stopa_amortizacije")); 
                   temp.setStatus(result.getBoolean("status")); 
                   temp.setIdLokacije(result.getInt("id_lokacije")); 
                   temp.setIdOsobe(result.getInt("id_osobe")); 
                   temp.setIdVrste(result.getInt("id_vrste")); 

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

