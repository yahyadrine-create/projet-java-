package controleur;

import dao.MedicamentDao;
import model.Medicament;
import java.sql.Connection;
import java.util.List;

public class MedicamentControler {
    private MedicamentDao medDao;

    public MedicamentControler(Connection connection) {
        this.medDao = new MedicamentDao(connection);
    }

    public boolean ajouter(Medicament m) {
        return medDao.create(m);
    }

    public boolean modifier(Medicament m) {
        return medDao.update(m);
    }

    public boolean supprimer(int id) {
        return medDao.delete(id);
    }

    public List<Medicament> listerTout() {
        return medDao.getTous();
    }

    public List<Medicament> chercher(String nom) {
        return medDao.rechercherParNom(nom);
    }

    public Medicament trouverParId(int id) {
        return medDao.getParId(id);
    }
}