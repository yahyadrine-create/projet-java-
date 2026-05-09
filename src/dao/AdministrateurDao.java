package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Administrateur;
import model.Utilisateur;

public class AdministrateurDao implements Idao<Administrateur> {
    private Connection connection;

    public AdministrateurDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean create(Administrateur m) {
        // Requêtes pour les deux tables
        String sqlUser = "INSERT INTO utilisateur (cin, nom, prenom, num_tel, login, password, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlAdmin = "INSERT INTO administrateur (cin) VALUES (?)";

        try {
            // Désactiver l'auto-commit pour gérer la transaction
            connection.setAutoCommit(false);

            // 1. Insertion dans la table utilisateur
            try (PreparedStatement psUser = connection.prepareStatement(sqlUser)) {
                psUser.setInt(1, m.getUlt().getCin());
                psUser.setString(2, m.getUlt().getNom());
                psUser.setString(3, m.getUlt().getPrenom());
                psUser.setInt(4, m.getUlt().getNum_tel());
                psUser.setString(5, m.getUlt().getLogin());
                psUser.setString(6, m.getUlt().getPasswd());
                psUser.setString(7, "administrateur"); // Forcer le rôle admin[cite: 1]
                psUser.executeUpdate();
            }

            // 2. Insertion dans la table administrateur (image_48ef39.png)[cite: 1]
            try (PreparedStatement psAdmin = connection.prepareStatement(sqlAdmin)) {
                psAdmin.setInt(1, m.getUlt().getCin());
                psAdmin.executeUpdate();
            }

            // Valider les deux insertions[cite: 1]
            connection.commit();
            return true;

        } catch (SQLException e) {
            try {
                // En cas d'erreur sur l'une des tables, on annule tout[cite: 1]
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
    public List<Administrateur> getTous() {
        List<Administrateur> liste = new ArrayList<>();
        // Jointure pour récupérer les infos complètes[cite: 1]
        String sql = "SELECT u.* FROM utilisateur u " +
                     "JOIN administrateur a ON u.cin = a.cin " +
                     "WHERE u.role = 'administrateur'";
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
    public boolean update(Administrateur m) {
        // Mise à jour des informations dans la table parente 'utilisateur'[cite: 1]
        String sql = "UPDATE utilisateur SET nom=?, prenom=?, num_tel=?, login=? WHERE cin=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, m.getUlt().getNom());
            ps.setString(2, m.getUlt().getPrenom());
            ps.setInt(3, m.getUlt().getNum_tel());
            ps.setString(4, m.getUlt().getLogin());
            ps.setInt(5, m.getUlt().getCin());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        // La suppression dans 'administrateur' se fait en premier à cause des contraintes[cite: 1]
        String sqlAdmin = "DELETE FROM administrateur WHERE cin = ?";
        String sqlUser = "DELETE FROM utilisateur WHERE cin = ?";
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement ps1 = connection.prepareStatement(sqlAdmin)) {
                ps1.setInt(1, id);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = connection.prepareStatement(sqlUser)) {
                ps2.setInt(1, id);
                ps2.executeUpdate();
            }
            connection.commit();
            return true;
        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    @Override
    public Administrateur getParId(int id) {
        String sql = "SELECT u.* FROM utilisateur u " +
                     "JOIN administrateur a ON u.cin = a.cin WHERE u.cin = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapper(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Administrateur mapper(ResultSet rs) throws SQLException {
        Utilisateur ult = new Utilisateur(
            rs.getInt("cin"),
            rs.getString("nom"),
            rs.getString("prenom"),
            rs.getString("password"),
            rs.getInt("num_tel"),
            rs.getString("login"),
            rs.getString("role")
        );
        Administrateur admin = new Administrateur();
        admin.setUlt(ult);
        return admin;
    }
}