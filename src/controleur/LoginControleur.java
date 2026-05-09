package controleur;

import java.sql.Connection;

import dao.UtilisateurDao;
import model.Utilisateur;

public class LoginControleur {
    private final UtilisateurDao utilisateurDao;

    public LoginControleur(Connection connection) {
        this.utilisateurDao = new UtilisateurDao(connection);
    }

    public Utilisateur authentifier(String login, String motDePasse) {
        if (login == null || login.trim().isEmpty()) return null;
        if (motDePasse == null || motDePasse.trim().isEmpty()) return null;
        return utilisateurDao.login(login.trim(), motDePasse);
    }
}

