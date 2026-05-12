package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Administrateur;
import model.Utilisateur;

/**
 * DAO (accès données) — toutes les requêtes SQL sont ici.
 * Ni logique métier, ni composants graphiques.
 */
public class AdministrateurDao implements Idao<Administrateur> {

    private Connection connection;

    public AdministrateurDao(Connection connection) {
        this.connection = connection;
    }

    // =========================================================================
    // CRUD
    // =========================================================================

    @Override
    public boolean create(Administrateur m) {
        String sqlUser =
            "INSERT INTO utilisateur(cin,nom,prenom,num_tel,login,password,role) " +
            "VALUES(?,?,?,?,?,?,?)";
        String sqlAdmin =
            "INSERT INTO administrateur(cin) VALUES(?)";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement psUser = connection.prepareStatement(sqlUser)) {
                psUser.setInt(1, m.getUlt().getCin());
                psUser.setString(2, m.getUlt().getNom());
                psUser.setString(3, m.getUlt().getPrenom());
                psUser.setInt(4, m.getUlt().getNum_tel());
                psUser.setString(5, m.getUlt().getLogin());
                psUser.setString(6, m.getUlt().getPasswd());
                psUser.setString(7, "administrateur");
                psUser.executeUpdate();
            }

            try (PreparedStatement psAdmin = connection.prepareStatement(sqlAdmin)) {
                psAdmin.setInt(1, m.getUlt().getCin());
                psAdmin.executeUpdate();
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
    public List<Administrateur> getTous() {
        List<Administrateur> liste = new ArrayList<>();
        String sql =
            "SELECT u.* FROM utilisateur u " +
            "JOIN administrateur a ON u.cin = a.cin";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) liste.add(mapper(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    @Override
    public boolean update(Administrateur m) {
        String sql =
            "UPDATE utilisateur " +
            "SET nom=?, prenom=?, num_tel=?, login=? " +
            "WHERE cin=?";

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
        String sqlAdmin = "DELETE FROM administrateur WHERE cin=?";
        String sqlUser  = "DELETE FROM utilisateur WHERE cin=?";

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
            try { connection.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    @Override
    public Administrateur getParId(int id) {
        String sql =
            "SELECT u.* FROM utilisateur u " +
            "JOIN administrateur a ON u.cin = a.cin " +
            "WHERE u.cin=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapper(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // =========================================================================
    // DASHBOARD — toutes les requêtes de statistiques sont ici (plus dans la Vue)
    // =========================================================================

    public int getNombreMedicaments() {
        return queryInt("SELECT COUNT(*) FROM medicament");
    }

    public int getNombreClients() {
        return queryInt("SELECT COUNT(*) FROM client");
    }

    public int getNombreOrdonnances() {
        return queryInt("SELECT COUNT(*) FROM ordonnance");
    }

    public double getCATotal() {
        String sql =
            "SELECT COALESCE(SUM(l.quantite * m.prix), 0) " +
            "FROM ligneord l " +
            "JOIN medicament m ON m.id_med = l.id_med";
        return queryDouble(sql);
    }

    public List<String[]> getTopVentes(int limit) {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT m.nom, COALESCE(SUM(l.quantite), 0) AS total " +
            "FROM medicament m " +
            "LEFT JOIN ligneord l ON l.id_med = m.id_med " +
            "GROUP BY m.id_med, m.nom " +
            "ORDER BY total DESC " +
            "LIMIT ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{ rs.getString("nom"), String.valueOf(rs.getLong("total")) });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * CA par mois sur les 12 derniers mois.
     * Si aucun résultat, bascule automatiquement sur le fallback sans filtre date.
     */
    public List<String[]> getCAParMois() {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT DATE_FORMAT(o.date, '%b %Y') AS mois_label, " +
            "       DATE_FORMAT(o.date, '%Y-%m')  AS mois_tri, " +
            "       COALESCE(SUM(l.quantite * m.prix), 0) AS ca " +
            "FROM ordonnance o " +
            "JOIN ligneord   l ON l.num_ord = o.num_ord " +
            "JOIN medicament m ON m.id_med  = l.id_med " +
            "WHERE o.date >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH) " +
            "GROUP BY mois_tri, mois_label " +
            "ORDER BY mois_tri ASC";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{ rs.getString("mois_label"), String.valueOf(rs.getDouble("ca")) });
            }
            // Fallback : si aucune donnée dans les 12 derniers mois
            if (list.isEmpty()) {
                list.addAll(getCAParMoisSansFiltreDate());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            list.addAll(getCAParMoisSansFiltreDate());
        }
        return list;
    }

    /** Fallback — toutes les données sans filtre de date (limité à 12 mois) */
    private List<String[]> getCAParMoisSansFiltreDate() {
        List<String[]> list = new ArrayList<>();
        String sql =
            "SELECT DATE_FORMAT(o.date, '%b %Y') AS mois_label, " +
            "       DATE_FORMAT(o.date, '%Y-%m')  AS mois_tri, " +
            "       COALESCE(SUM(l.quantite * m.prix), 0) AS ca " +
            "FROM ordonnance o " +
            "JOIN ligneord   l ON l.num_ord = o.num_ord " +
            "JOIN medicament m ON m.id_med  = l.id_med " +
            "GROUP BY mois_tri, mois_label " +
            "ORDER BY mois_tri ASC " +
            "LIMIT 12";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[]{ rs.getString("mois_label"), String.valueOf(rs.getDouble("ca")) });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    

    private int queryInt(String sql) {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private double queryDouble(String sql) {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /** Expose la connection pour les vues filles (usage limité). */
    public Connection getConnection() { return connection; }

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