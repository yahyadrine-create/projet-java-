package dao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Client;
import model.Ordonnance;

 public class OrdonnanceDao implements Idao<Ordonnance>{
	private Connection connection;	
	public OrdonnanceDao(Connection connection) {
        this.connection = connection;
    }
	@Override
    public boolean create(Ordonnance m) {
        String sql = "INSERT INTO ordonnance (num_ord, id_clt,date) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, m.getNum_ord());
            ps.setInt(2, m.getClt().getId_clt());
            ps.setString(3,m.getDate());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
	 @Override
	    public List<Ordonnance> getTous() {
	        List<Ordonnance> liste = new ArrayList<>();
	        String sql = "SELECT * FROM ordonnance";
	        try (Statement st = connection.createStatement();
	             ResultSet rs = st.executeQuery(sql)) {
	            while (rs.next()) {
	                liste.add(mapper(rs));
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return liste;
	    }
	 @Override
	    public Ordonnance getParId(int id) {
	        String sql = "SELECT * FROM ordonance WHERE num_ord = ?";
	        try (PreparedStatement ps = connection.prepareStatement(sql)) {
	            ps.setInt(1, id);
	            ResultSet rs = ps.executeQuery();
	            if (rs.next()) return mapper(rs);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
	 @Override
	 public boolean delete(int numOrd) {
	     String sqlSelectLignes = "SELECT id_med, quantite FROM ligneord WHERE num_ord = ?";
	     String sqlRestockMed   = "UPDATE medicament SET quantite = quantite + ? WHERE id_med = ?";
	     String sqlDeleteLignes = "DELETE FROM ligneord WHERE num_ord = ?";
	     String sqlDeleteOrd    = "DELETE FROM ordonnance WHERE num_ord = ?";

	     try {
	         connection.setAutoCommit(false);

	         // 1. Restituer le stock
	         try (PreparedStatement psSelect = connection.prepareStatement(sqlSelectLignes)) {
	             psSelect.setInt(1, numOrd);
	             ResultSet rs = psSelect.executeQuery();
	             try (PreparedStatement psRestock = connection.prepareStatement(sqlRestockMed)) {
	                 while (rs.next()) {
	                     psRestock.setInt(1, rs.getInt("quantite"));
	                     psRestock.setInt(2, rs.getInt("id_med"));
	                     psRestock.executeUpdate();
	                 }
	             }
	         }

	         // 2. Supprimer les lignes d'abord (contrainte FK)
	         try (PreparedStatement psDeleteLignes = connection.prepareStatement(sqlDeleteLignes)) {
	             psDeleteLignes.setInt(1, numOrd);
	             psDeleteLignes.executeUpdate();
	         }

	         // 3. Supprimer l'ordonnance
	         try (PreparedStatement psDeleteOrd = connection.prepareStatement(sqlDeleteOrd)) {
	             psDeleteOrd.setInt(1, numOrd);
	             psDeleteOrd.executeUpdate();
	         }

	         connection.commit();
	         return true;

	     } catch (SQLException e) {
	         try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
	         e.printStackTrace();
	         return false;
	     } finally {
	         try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
	     }
	 }
	  @Override
	    public boolean update(Ordonnance m) {
	        String sql = "UPDATE ordonnance SET id_clt=?, date=? WHERE num_ord=?";
	        try (PreparedStatement ps = connection.prepareStatement(sql)) {
	            ps.setInt(1, m.getNum_ord());
	            ps.setInt(2, m.getClt().getId_clt());
	            return ps.executeUpdate() > 0;
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
	    public List<Ordonnance> rechercherParNom(String nom) {
	        List<Ordonnance> liste = new ArrayList<>();
	        String sql = "SELECT * FROM ordonnance o"+" JOIN client c ON c.id_clt=o.id_clt"
	        		+ " WHERE c.nom LIKE ?";
	        try (PreparedStatement ps = connection.prepareStatement(sql)) {
	            ps.setString(1, "%" + nom + "%");
	            ResultSet rs = ps.executeQuery();
	            while (rs.next()) liste.add(mapper(rs));
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return liste;
	    }
	  private Ordonnance mapper(ResultSet rs) throws SQLException {
		    Client client = new Client(rs.getInt("id_clt"));
		    client.setId_clt(rs.getInt("id_clt"));
		    
		    Ordonnance ordonnance = new Ordonnance();
		    ordonnance.setClt(client);
		    ordonnance.setNum_ord(rs.getInt("num_ord"));
		    ordonnance.setDate(rs.getString("date"));
		    
		    return ordonnance;
		}
	 }
