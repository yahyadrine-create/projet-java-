package dao;
import java.sql.*;
import model.*;
import java.util.ArrayList;
import java.util.List;

public class LigneOrdDao implements Idao<LigneOrd> {
    private Connection connection;
    
    public LigneOrdDao(Connection connection) {
        this.connection = connection;
    }


    @Override
    public boolean create(LigneOrd m) {
        String sqlInsert     = "INSERT INTO ligneord (num_ord, id_med, quantite) VALUES (?, ?, ?)";
        String sqlVerifMed   = "SELECT quantite, prix FROM medicament WHERE id_med = ?";
        String sqlUpdateStock = "UPDATE medicament SET quantite = quantite - ? WHERE id_med = ? AND quantite >= ?";
        String sqlGetClient  = "SELECT id_clt FROM ordonnance WHERE num_ord = ?";
        String sqlUpdateCredit = "UPDATE client SET credit = credit + ? WHERE id_clt = ?";

        try {
            connection.setAutoCommit(false);

            double prixUnitaire = 0;
            try (PreparedStatement psVerif = connection.prepareStatement(sqlVerifMed)) {
                psVerif.setInt(1, m.getMed().getId_med());
                ResultSet rs = psVerif.executeQuery();
                if (!rs.next()) {
                    connection.rollback();
                    return false; 
                }
                int stockActuel = rs.getInt("quantite");
                prixUnitaire = rs.getDouble("prix");
                if (stockActuel < m.getQuantite()) {
                    connection.rollback();
                    return false; 
                }
            }

            
            try (PreparedStatement psInsert = connection.prepareStatement(sqlInsert)) {
                psInsert.setInt(1, m.getOrd().getNum_ord());
                psInsert.setInt(2, m.getMed().getId_med());
                psInsert.setInt(3, m.getQuantite());
                psInsert.executeUpdate();
            }

            
            try (PreparedStatement psStock = connection.prepareStatement(sqlUpdateStock)) {
                psStock.setInt(1, m.getQuantite());
                psStock.setInt(2, m.getMed().getId_med());
                psStock.setInt(3, m.getQuantite());
                int rows = psStock.executeUpdate();
                if (rows == 0) {
                    connection.rollback(); 
                    return false;
                }
            }

            int idClient = -1;
            try (PreparedStatement psGetClt = connection.prepareStatement(sqlGetClient)) {
                psGetClt.setInt(1, m.getOrd().getNum_ord());
                ResultSet rs = psGetClt.executeQuery();
                if (rs.next()) {
                    idClient = rs.getInt("id_clt");
                }
            }

            if (idClient != -1) {
                double montant = m.getQuantite() * prixUnitaire;
                try (PreparedStatement psCredit = connection.prepareStatement(sqlUpdateCredit)) {
                    psCredit.setDouble(1, montant);
                    psCredit.setInt(2, idClient);
                    psCredit.executeUpdate();
                }
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
    public boolean update(LigneOrd m) {
        String sqlAncienne = "SELECT quantite FROM ligneord WHERE num_ord = ? AND id_med = ?";
        String sqlUpdate   = "UPDATE ligneord SET quantite = ? WHERE num_ord = ? AND id_med = ?";
        String sqlStock    = "UPDATE medicament SET quantite = quantite + ? WHERE id_med = ?";

        try {
            connection.setAutoCommit(false);

            int ancienneQte = 0;
            try (PreparedStatement psAncienne = connection.prepareStatement(sqlAncienne)) {
                psAncienne.setInt(1, m.getOrd().getNum_ord());
                psAncienne.setInt(2, m.getMed().getId_med());
                ResultSet rs = psAncienne.executeQuery();
                if (rs.next()) ancienneQte = rs.getInt("quantite");
            }

            int diff = ancienneQte - m.getQuantite();

            try (PreparedStatement psUpdate = connection.prepareStatement(sqlUpdate)) {
                psUpdate.setInt(1, m.getQuantite());
                psUpdate.setInt(2, m.getOrd().getNum_ord());
                psUpdate.setInt(3, m.getMed().getId_med());
                psUpdate.executeUpdate();
            }

            try (PreparedStatement psStock = connection.prepareStatement(sqlStock)) {
                psStock.setInt(1, diff);
                psStock.setInt(2, m.getMed().getId_med());
                psStock.executeUpdate();
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
    public List<LigneOrd> getParOrdonnance(int numOrd) {
        List<LigneOrd> liste = new ArrayList<>();
        String sql = "SELECT * FROM ligneord WHERE num_ord = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, numOrd);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapper(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public LigneOrd mapper(ResultSet rs) throws SQLException {
        Ordonnance ord = new Ordonnance();
        ord.setNum_ord(rs.getInt("num_ord"));
        Medicament med = new Medicament();
        med.setId_med(rs.getInt("id_med"));
        return new LigneOrd(ord, med, rs.getInt("quantite"));
    }

    @Override public List<LigneOrd> getTous() { return null; } 
    @Override public LigneOrd getParId(int id) { return null; }
    @Override
    public boolean delete(int idMed) {
        return false;
    }

    public boolean deleteParOrdonnance(int numOrd) {
        String sqlSelectLignes = "SELECT id_med, quantite FROM ligneord WHERE num_ord = ?";
        String sqlRestockMed   = "UPDATE medicament SET quantite = quantite + ? WHERE id_med = ?";
        String sqlDeleteLignes = "DELETE FROM ligneord WHERE num_ord = ?";

        try {
            connection.setAutoCommit(false);

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

            try (PreparedStatement psDelete = connection.prepareStatement(sqlDeleteLignes)) {
                psDelete.setInt(1, numOrd);
                psDelete.executeUpdate();
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
    public boolean deleteLigne(int numOrd, int idMed) {
        String sqlSelectQte   = "SELECT l.quantite, m.prix FROM ligneord l JOIN medicament m ON m.id_med = l.id_med WHERE l.num_ord = ? AND l.id_med = ?";
        String sqlDeleteLigne = "DELETE FROM ligneord WHERE num_ord = ? AND id_med = ?";
        String sqlRestock     = "UPDATE medicament SET quantite = quantite + ? WHERE id_med = ?";
        String sqlGetClient   = "SELECT id_clt FROM ordonnance WHERE num_ord = ?";
        String sqlUpdateCredit = "UPDATE client SET credit = credit - ? WHERE id_clt = ?";

        try {
            connection.setAutoCommit(false);

            int qte = 0;
            double prixUnitaire = 0;
            try (PreparedStatement psSelect = connection.prepareStatement(sqlSelectQte)) {
                psSelect.setInt(1, numOrd);
                psSelect.setInt(2, idMed);
                ResultSet rs = psSelect.executeQuery();
                if (rs.next()) {
                    qte = rs.getInt("quantite");
                    prixUnitaire = rs.getDouble("prix");
                } else {
                    connection.rollback();
                    return false; 
                }
            }

            
            try (PreparedStatement psDelete = connection.prepareStatement(sqlDeleteLigne)) {
                psDelete.setInt(1, numOrd);
                psDelete.setInt(2, idMed);
                psDelete.executeUpdate();
            }

            try (PreparedStatement psRestock = connection.prepareStatement(sqlRestock)) {
                psRestock.setInt(1, qte);
                psRestock.setInt(2, idMed);
                psRestock.executeUpdate();
            }

            int idClient = -1;
            try (PreparedStatement psGetClt = connection.prepareStatement(sqlGetClient)) {
                psGetClt.setInt(1, numOrd);
                ResultSet rs = psGetClt.executeQuery();
                if (rs.next()) {
                    idClient = rs.getInt("id_clt");
                }
            }

            if (idClient != -1) {
                double montant = qte * prixUnitaire;
                try (PreparedStatement psCredit = connection.prepareStatement(sqlUpdateCredit)) {
                    psCredit.setDouble(1, montant);
                    psCredit.setInt(2, idClient);
                    psCredit.executeUpdate();
                }
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
    public double calculerCoutTotal(int numOrd) {
        String sql = "SELECT SUM(l.quantite * m.prix) AS total " +
                     "FROM ligneord l " +
                     "JOIN medicament m ON m.id_med = l.id_med " +
                     "WHERE l.num_ord = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, numOrd);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}