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

 // LigneOrdDao.java — méthode create() corrigée

    @Override
    public boolean create(LigneOrd m) {
        String sqlInsert     = "INSERT INTO ligneord (num_ord, id_med, quantite) VALUES (?, ?, ?)";
        String sqlVerifMed   = "SELECT quantite, prix FROM medicament WHERE id_med = ?";
        String sqlUpdateStock = "UPDATE medicament SET quantite = quantite - ? WHERE id_med = ? AND quantite >= ?";
        String sqlGetClient  = "SELECT id_clt FROM ordonnance WHERE num_ord = ?";
        String sqlUpdateCredit = "UPDATE client SET credit = credit + ? WHERE id_clt = ?";

        try {
            connection.setAutoCommit(false);

            // 1. Vérifier stock suffisant et récupérer le prix du médicament
            double prixUnitaire = 0;
            try (PreparedStatement psVerif = connection.prepareStatement(sqlVerifMed)) {
                psVerif.setInt(1, m.getMed().getId_med());
                ResultSet rs = psVerif.executeQuery();
                if (!rs.next()) {
                    connection.rollback();
                    return false; // médicament introuvable
                }
                int stockActuel = rs.getInt("quantite");
                prixUnitaire = rs.getDouble("prix");
                if (stockActuel < m.getQuantite()) {
                    connection.rollback();
                    return false; // stock insuffisant
                }
            }

            // 2. Insérer la ligne d'ordonnance
            try (PreparedStatement psInsert = connection.prepareStatement(sqlInsert)) {
                psInsert.setInt(1, m.getOrd().getNum_ord());
                psInsert.setInt(2, m.getMed().getId_med());
                psInsert.setInt(3, m.getQuantite());
                psInsert.executeUpdate();
            }

            // 3. Décrémenter le stock du médicament
            try (PreparedStatement psStock = connection.prepareStatement(sqlUpdateStock)) {
                psStock.setInt(1, m.getQuantite());
                psStock.setInt(2, m.getMed().getId_med());
                psStock.setInt(3, m.getQuantite());
                int rows = psStock.executeUpdate();
                if (rows == 0) {
                    connection.rollback(); // stock insuffisant (race condition)
                    return false;
                }
            }

            // 4. Récupérer l'id du client lié à l'ordonnance
            int idClient = -1;
            try (PreparedStatement psGetClt = connection.prepareStatement(sqlGetClient)) {
                psGetClt.setInt(1, m.getOrd().getNum_ord());
                ResultSet rs = psGetClt.executeQuery();
                if (rs.next()) {
                    idClient = rs.getInt("id_clt");
                }
            }

            // 5. Augmenter le crédit du client (quantité × prix)
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

 // LigneOrdDao.java — méthode update() corrigée

    @Override
    public boolean update(LigneOrd m) {
        String sqlAncienne = "SELECT quantite FROM ligneord WHERE num_ord = ? AND id_med = ?";
        String sqlUpdate   = "UPDATE ligneord SET quantite = ? WHERE num_ord = ? AND id_med = ?";
        String sqlStock    = "UPDATE medicament SET quantite = quantite + ? WHERE id_med = ?";
        // + diff peut être négatif (augmentation) ou positif (diminution)

        try {
            connection.setAutoCommit(false);

            // 1. Récupérer l'ancienne quantité
            int ancienneQte = 0;
            try (PreparedStatement psAncienne = connection.prepareStatement(sqlAncienne)) {
                psAncienne.setInt(1, m.getOrd().getNum_ord());
                psAncienne.setInt(2, m.getMed().getId_med());
                ResultSet rs = psAncienne.executeQuery();
                if (rs.next()) ancienneQte = rs.getInt("quantite");
            }

            // 2. Calculer la différence : positif = on rend du stock, négatif = on en prend
            int diff = ancienneQte - m.getQuantite();

            // 3. Mettre à jour la ligne d'ordonnance
            try (PreparedStatement psUpdate = connection.prepareStatement(sqlUpdate)) {
                psUpdate.setInt(1, m.getQuantite());
                psUpdate.setInt(2, m.getOrd().getNum_ord());
                psUpdate.setInt(3, m.getMed().getId_med());
                psUpdate.executeUpdate();
            }

            // 4. Ajuster le stock (diff > 0 => on restitue, diff < 0 => on retire)
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

    @Override public List<LigneOrd> getTous() { return null; } // Peu utile ici
    @Override public LigneOrd getParId(int id) { return null; }
 // Supprimer une seule ligne et restituer son stock
    @Override
    public boolean delete(int idMed) {
        // Peu utilisé seul, mais on l'implémente proprement
        return false;
    }

    // Supprimer toutes les lignes d'une ordonnance ET restituer le stock
    public boolean deleteParOrdonnance(int numOrd) {
        String sqlSelectLignes = "SELECT id_med, quantite FROM ligneord WHERE num_ord = ?";
        String sqlRestockMed   = "UPDATE medicament SET quantite = quantite + ? WHERE id_med = ?";
        String sqlDeleteLignes = "DELETE FROM ligneord WHERE num_ord = ?";

        try {
            connection.setAutoCommit(false);

            // 1. Récupérer toutes les lignes pour restituer le stock
            try (PreparedStatement psSelect = connection.prepareStatement(sqlSelectLignes)) {
                psSelect.setInt(1, numOrd);
                ResultSet rs = psSelect.executeQuery();
                try (PreparedStatement psRestock = connection.prepareStatement(sqlRestockMed)) {
                    while (rs.next()) {
                        psRestock.setInt(1, rs.getInt("quantite")); // restituer
                        psRestock.setInt(2, rs.getInt("id_med"));
                        psRestock.executeUpdate();
                    }
                }
            }

            // 2. Supprimer toutes les lignes de l'ordonnance
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

            // 1. Récupérer la quantité et le prix pour restituer le stock et ajuster le crédit
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
                    return false; // ligne introuvable
                }
            }

            // 2. Supprimer la ligne
            try (PreparedStatement psDelete = connection.prepareStatement(sqlDeleteLigne)) {
                psDelete.setInt(1, numOrd);
                psDelete.setInt(2, idMed);
                psDelete.executeUpdate();
            }

            // 3. Restituer le stock du médicament
            try (PreparedStatement psRestock = connection.prepareStatement(sqlRestock)) {
                psRestock.setInt(1, qte);
                psRestock.setInt(2, idMed);
                psRestock.executeUpdate();
            }

            // 4. Récupérer le client lié à l'ordonnance
            int idClient = -1;
            try (PreparedStatement psGetClt = connection.prepareStatement(sqlGetClient)) {
                psGetClt.setInt(1, numOrd);
                ResultSet rs = psGetClt.executeQuery();
                if (rs.next()) {
                    idClient = rs.getInt("id_clt");
                }
            }

            // 5. Diminuer le crédit du client (quantité × prix)
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