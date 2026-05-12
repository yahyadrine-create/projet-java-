package controleur;

import dao.AdministrateurDao;
import model.Administrateur;

import java.sql.Connection;
import java.util.List;

/**
 * Contrôleur (C) — seul intermédiaire entre la Vue et le DAO.
 * La Vue ne connaît que cette classe ; elle n'accède jamais à la BDD directement.
 */
public class AdministrateurControle {

    private AdministrateurDao adminDao;

    public AdministrateurControle(Connection connection) {
        this.adminDao = new AdministrateurDao(connection);
    }

    // =========================================================================
    // CRUD — exposés à la Vue
    // =========================================================================

    public boolean creer(Administrateur admin) {
        return adminDao.create(admin);
    }

    public List<Administrateur> getTous() {
        return adminDao.getTous();
    }

    public Administrateur getParId(int id) {
        return adminDao.getParId(id);
    }

    public boolean modifier(Administrateur admin) {
        return adminDao.update(admin);
    }

    public boolean supprimer(int id) {
        return adminDao.delete(id);
    }

    // =========================================================================
    // DASHBOARD — exposés à la Vue
    // =========================================================================

    public int getNombreMedicaments() {
        return adminDao.getNombreMedicaments();
    }

    public int getNombreClients() {
        return adminDao.getNombreClients();
    }

    public int getNombreOrdonnances() {
        return adminDao.getNombreOrdonnances();
    }

    public double getCATotal() {
        return adminDao.getCATotal();
    }

    public List<String[]> getTopVentes(int limit) {
        return adminDao.getTopVentes(limit);
    }

    public List<String[]> getCAParMois() {
        return adminDao.getCAParMois();
    }

    public Connection getConnection() {
        return adminDao.getConnection();
    }
}