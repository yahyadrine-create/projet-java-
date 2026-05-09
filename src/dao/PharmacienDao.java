package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Pharmacien;
import model.Utilisateur;

public class PharmacienDao implements Idao<Pharmacien> {
    private Connection connection;

    public PharmacienDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean create(Pharmacien p) {
        // Requêtes pour les deux tables : utilisateur et pharmacien
        String sqlUser = "INSERT INTO utilisateur (cin, nom, prenom, num_tel, login, password, role) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlPhar = "INSERT INTO pharmacien (cin) VALUES (?)";

        try {
            // Utilisation d'une transaction pour garantir l'intégrité des données
            connection.setAutoCommit(false);

            // 1. Insertion dans la table utilisateur
            try (PreparedStatement psUser = connection.prepareStatement(sqlUser)) {
                psUser.setInt(1, p.getUlt().getCin());
                psUser.setString(2, p.getUlt().getNom());
                psUser.setString(3, p.getUlt().getPrenom());
                psUser.setInt(4, p.getUlt().getNum_tel());
                psUser.setString(5, p.getUlt().getLogin());
                psUser.setString(6, p.getUlt().getPasswd());
                psUser.setString(7, "pharmacien"); // On fixe le rôle à pharmacien[cite: 1]
                psUser.executeUpdate();
            }

            // 2. Insertion dans la table pharmacien (similaire à image_48ef39.png)[cite: 1]
            try (PreparedStatement psPhar = connection.prepareStatement(sqlPhar)) {
                psPhar.setInt(1, p.getUlt().getCin());
                psPhar.executeUpdate();
            }

            connection.commit(); // Validation de la transaction[cite: 1]
            return true;

        } catch (SQLException e) {
            try {
                connection.rollback(); // Annulation en cas d'erreur[cite: 1]
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
    public List<Pharmacien> getTous() {
        List<Pharmacien> liste = new ArrayList<>();
        // Jointure pour récupérer les infos de l'utilisateur qui sont des pharmaciens[cite: 1]
        String sql = "SELECT u.* FROM utilisateur u " +
                     "JOIN pharmacien p ON u.cin = p.cin " +
                     "WHERE u.role = 'pharmacien'";
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
    public boolean update(Pharmacien p) {
        // Mise à jour des informations dans la table utilisateur[cite: 1]
        String sql = "UPDATE utilisateur SET nom=?, prenom=?, num_tel=?, login=? WHERE cin=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, p.getUlt().getNom());
            ps.setString(2, p.getUlt().getPrenom());
            ps.setInt(3, p.getUlt().getNum_tel());
            ps.setString(4, p.getUlt().getLogin());
            ps.setInt(5, p.getUlt().getCin());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        // Suppression dans les deux tables (pharmacien puis utilisateur)[cite: 1]
        String sqlPhar = "DELETE FROM pharmacien WHERE cin = ?";
        String sqlUser = "DELETE FROM utilisateur WHERE cin = ?";
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement ps1 = connection.prepareStatement(sqlPhar)) {
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
    public Pharmacien getParId(int id) {
        String sql = "SELECT u.* FROM utilisateur u " +
                     "JOIN pharmacien p ON u.cin = p.cin WHERE u.cin = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapper(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Pharmacien mapper(ResultSet rs) throws SQLException {
        Utilisateur ult = new Utilisateur(
            rs.getInt("cin"),
            rs.getString("nom"),
            rs.getString("prenom"),
            rs.getString("password"),
            rs.getInt("num_tel"),
            rs.getString("login"),
            rs.getString("role")
        );
        Pharmacien phar = new Pharmacien();
        phar.setUlt(ult);
        return phar;
    }
}