package controleur;

import java.sql.Connection;

import dao.UtilisateurDao;
import model.Utilisateur;

public class RegistreControleur {
    private final UtilisateurDao utilisateurDao;

    public RegistreControleur(Connection connection) {
        this.utilisateurDao = new UtilisateurDao(connection);
    }

    public boolean inscrire(Utilisateur u) {
        if (u == null) return false;
        if (u.getLogin() == null || u.getLogin().trim().isEmpty()) return false;
        if (u.getPasswd() == null || u.getPasswd().trim().isEmpty()) return false;
        if (u.getRole() == null || u.getRole().trim().isEmpty()) return false;
        return utilisateurDao.create(u);
    }
}

