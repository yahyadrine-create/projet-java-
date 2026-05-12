package controleur;

import dao.UtilisateurDao;
import model.Utilisateur;

import java.sql.Connection;
import java.util.List;



public class UtilisateurControleur {

    private final UtilisateurDao utilisateurDao;

    public UtilisateurControleur(Connection connection) {
        this.utilisateurDao = new UtilisateurDao(connection);
    }

        public boolean creer(Utilisateur u) {
        if (u == null) return false;
        if (u.getLogin() == null || u.getLogin().trim().isEmpty()) return false;
        if (u.getPasswd() == null || u.getPasswd().trim().isEmpty()) return false;
        if (u.getRole() == null || u.getRole().trim().isEmpty()) return false;
        return utilisateurDao.create(u);
    }

    public List<Utilisateur> getTous() {
        return utilisateurDao.getTous();
    }

    public Utilisateur getParId(int cin) {
        return utilisateurDao.getParId(cin);
    }

    public boolean modifier(Utilisateur u) {
        if (u == null) return false;
        return utilisateurDao.update(u);
    }

    public boolean supprimer(int cin) {
        return utilisateurDao.delete(cin);
    }

    
    public List<Utilisateur> chercherParNom(String nom) {
        if (nom == null) nom = "";
        return utilisateurDao.rechercherParNom(nom.trim());
    }
}