package dao;

import model.Medicament;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicamentDao implements Idao<Medicament> {

    private Connection connection;

    public MedicamentDao(Connection connection) {
        this.connection = connection;
    }
    @Override
    public boolean create(Medicament m) {
        String sql = "INSERT INTO medicament (id_med, nom, prix, quantite, date_expiration, categorie) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, m.getId_med());
            ps.setString(2, m.getNom());
            ps.setDouble(3, m.getPrix());
            ps.setInt(4, m.getQuantite());
            ps.setString(5, m.getDateExpiratino());
            ps.setString(6, m.getCategorie());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public List<Medicament> getTous() {
        List<Medicament> liste = new ArrayList<>();
        String sql = "SELECT * FROM medicament";
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
    public Medicament getParId(int id) {
        String sql = "SELECT * FROM medicament WHERE id_med = ?";
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
    public boolean update(Medicament m) {
        String sql = "UPDATE medicament SET nom=?, quantite=?, date-expiration=?, prix=?, categorie=? WHERE id_med=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, m.getNom());
            ps.setInt(2, m.getQuantite());
            ps.setString(3, m.getDateExpiratino());
            ps.setDouble(4, m.getPrix());
            ps.setString(5, m.getCategorie());
            ps.setInt(6, m.getId_med());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM medicament WHERE id_med = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<Medicament> rechercherParNom(String nom) {
        List<Medicament> liste = new ArrayList<>();
        String sql = "SELECT * FROM medicament WHERE nom LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + nom + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapper(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }
    public List<Medicament> rechercherParCategorie(String categorie) {
        List<Medicament> liste = new ArrayList<>();
        String sql = "SELECT * FROM medicament WHERE categorie = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, categorie);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) liste.add(mapper(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }
    private Medicament mapper(ResultSet rs) throws SQLException {
        return new Medicament(
            rs.getInt("id_med"),
            rs.getString("nom"),
            rs.getInt("quantite"),
            rs.getString("date_expiration"),
            rs.getDouble("prix"),
            rs.getString("categorie")
        );
    }
}