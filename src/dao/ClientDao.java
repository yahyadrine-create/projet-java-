package dao;
import java.sql.*;
import model.Client;
import java.util.ArrayList;
import java.util.List;

public class ClientDao implements Idao<Client> {
	
	private Connection connection;
	
	public ClientDao(Connection connection) {
        this.connection = connection;
    }
	@Override
    public boolean create(Client m) {
        String sql = "INSERT INTO client (id_clt, nom, prenom, age, credit, num_tel) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, m.getId_clt());
            ps.setString(2, m.getNom());
            ps.setString(3, m.getPrenom());
            ps.setInt(4, m.getAge());
            ps.setDouble(5, m.getCredit());
            ps.setLong(6, m.getNum_tel());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
	  @Override
	    public List<Client> getTous() {
	        List<Client> liste = new ArrayList<>();
	        String sql = "SELECT * FROM client";
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
	    public Client getParId(int id) {
	        String sql = "SELECT * FROM client WHERE id_clt = ?";
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
	    public boolean delete(int id) {
	        String sql = "DELETE FROM client WHERE id_clt = ?";
	        try (PreparedStatement ps = connection.prepareStatement(sql)) {
	            ps.setInt(1, id);
	            return ps.executeUpdate() > 0;
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
	  @Override
	    public boolean update(Client m) {
	        String sql = "UPDATE client SET nom=?, prenom=?, age=?, credit=?, num_tel=? WHERE id_clt=?";
	        try (PreparedStatement ps = connection.prepareStatement(sql)) {
	            ps.setString(1, m.getNom());
	            ps.setString(2, m.getPrenom());
	            ps.setInt(3, m.getAge());
	            ps.setDouble(4, m.getCredit());
	            ps.setInt(5, m.getNum_tel());
	            ps.setInt(6, m.getId_clt());
	            return ps.executeUpdate() > 0;
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false;
	        }
	    }
	    public List<Client> rechercherParNom(String nom) {
	        List<Client> liste = new ArrayList<>();
	        String sql = "SELECT * FROM client WHERE nom LIKE ?";
	        try (PreparedStatement ps = connection.prepareStatement(sql)) {
	            ps.setString(1, "%" + nom + "%");
	            ResultSet rs = ps.executeQuery();
	            while (rs.next()) liste.add(mapper(rs));
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return liste;
	    }
	    public List<Client> rechercherParPrenom(String prenom) {
	        List<Client> liste = new ArrayList<>();
	        String sql = "SELECT * FROM client WHERE prenom = ?";
	        try (PreparedStatement ps = connection.prepareStatement(sql)) {
	            ps.setString(1, prenom);
	            ResultSet rs = ps.executeQuery();
	            while (rs.next()) liste.add(mapper(rs));
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return liste;
	    }
	  private Client mapper(ResultSet rs) throws SQLException {
	        return new Client(
	            rs.getInt("id_clt"),
	            rs.getString("nom"),
	            rs.getString("prenom"),
	            rs.getInt("age"),
	            rs.getDouble("credit"),
	            rs.getInt("num_tel")
	        );
	    }
	  
}
