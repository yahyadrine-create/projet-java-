package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Utilisateur;

public class UtilisateurDao implements Idao<Utilisateur> {
    private Connection connection;
    
    public UtilisateurDao(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public boolean create(Utilisateur m) {
        String sqlUser = "INSERT INTO utilisateur (cin, nom, prenom, passwd, num_tel, login, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        String sqlAdmin = "INSERT INTO administrateur (cin) VALUES (?)";
        String sqlPhar = "INSERT INTO pharmacien (cin) VALUES (?)";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(sqlUser)) {
                ps.setInt(1, m.getCin());
                ps.setString(2, m.getNom());
                ps.setString(3, m.getPrenom());
                ps.setString(4, m.getPasswd());
                ps.setInt(5, m.getNum_tel());
                ps.setString(6, m.getLogin());
                ps.setString(7, m.getRole()); 
                ps.executeUpdate();
            }

            String role = m.getRole() == null ? "" : m.getRole().trim();
            if ("administrateur".equalsIgnoreCase(role)) {
                try (PreparedStatement psAdmin = connection.prepareStatement(sqlAdmin)) {
                    psAdmin.setInt(1, m.getCin());
                    psAdmin.executeUpdate();
                }
            } else if ("pharmacien".equalsIgnoreCase(role)) {
                try (PreparedStatement psPhar = connection.prepareStatement(sqlPhar)) {
                    psPhar.setInt(1, m.getCin());
                    psPhar.executeUpdate();
                }
            } else {
                throw new SQLException("Rôle invalide: " + role);
            }

            connection.commit(); 
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback(); 
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean update(Utilisateur m) {
        String sql = "UPDATE utilisateur SET nom=?, prenom=?, passwd=?, num_tel=?, login=?, role=? WHERE cin=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, m.getNom());
            ps.setString(2, m.getPrenom());
            ps.setString(3, m.getPasswd());
            ps.setInt(4, m.getNum_tel());
            ps.setString(5, m.getLogin());
            ps.setString(6, m.getRole());
            ps.setInt(7, m.getCin());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Utilisateur login(String login, String password) {
        String sql = "SELECT * FROM utilisateur WHERE login = ? AND passwd = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapper(rs);
            }   
        } catch (SQLException e) {
            e.printStackTrace();
        }   
        return null;
    }

    @Override
    public List<Utilisateur> getTous() {
        List<Utilisateur> liste = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) liste.add(mapper(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return liste;
    }

    @Override
    public Utilisateur getParId(int id) {
        String sql = "SELECT * FROM utilisateur WHERE cin = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapper(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM utilisateur WHERE cin = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    public List<Utilisateur> rechercherParNom(String nom) {

    	List<Utilisateur> liste = new ArrayList<>();

    	String sql = "SELECT * FROM utilisateur WHERE nom LIKE ?";

    	try (PreparedStatement ps = connection.prepareStatement(sql)) {

    	ps.setString(1, "%" + nom + "%");

    	ResultSet rs = ps.executeQuery();

    	while (rs.next()) liste.add(mapper(rs));

    	} catch (SQLException e) {

    	e.printStackTrace();

    	}

    	return liste;

    	}
    private Utilisateur mapper(ResultSet rs) throws SQLException {
        return new Utilisateur(
            rs.getInt("cin"),
            rs.getString("nom"),
            rs.getString("prenom"),
            rs.getString("passwd"),
            rs.getInt("num_tel"),
            rs.getString("login"), 
            rs.getString("role")
        );
    }
}