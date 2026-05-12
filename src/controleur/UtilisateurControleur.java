package controleur;

import dao.UtilisateurDao;
import model.Utilisateur;

import java.sql.Connection;
import java.util.List;

/**
 * Contrôleur (C) — intermédiaire entre UtilisateurView et UtilisateurDao.
 * Aucune logique SQL ici, uniquement de la validation métier légère.
 */
public class UtilisateurControleur {

    private final UtilisateurDao utilisateurDao;

    public UtilisateurControleur(Connection connection) {
        this.utilisateurDao = new UtilisateurDao(connection);
    }

    // =========================================================================
    // CRUD
    // =========================================================================

    /** Crée un utilisateur après validation des champs obligatoires. */
    public boolean creer(Utilisateur u) {
        if (u == null) return false;
        if (u.getLogin() == null || u.getLogin().trim().isEmpty()) return false;
        if (u.getPasswd() == null || u.getPasswd().trim().isEmpty()) return false;
        if (u.getRole() == null || u.getRole().trim().isEmpty()) return false;
        return utilisateurDao.create(u);
    }

    /** Retourne la liste complète des utilisateurs. */
    public List<Utilisateur> getTous() {
        return utilisateurDao.getTous();
    }

    /** Retourne un utilisateur par son CIN, ou null s'il n'existe pas. */
    public Utilisateur getParId(int cin) {
        return utilisateurDao.getParId(cin);
    }

    /** Met à jour les informations d'un utilisateur. */
    public boolean modifier(Utilisateur u) {
        if (u == null) return false;
        return utilisateurDao.update(u);
    }

    /** Supprime un utilisateur (et sa ligne de spécialisation) par CIN. */
    public boolean supprimer(int cin) {
        return utilisateurDao.delete(cin);
    }

    // =========================================================================
    // RECHERCHE
    // =========================================================================

    /** Recherche des utilisateurs dont le nom contient la chaîne donnée. */
    public List<Utilisateur> chercherParNom(String nom) {
        if (nom == null) nom = "";
        return utilisateurDao.rechercherParNom(nom.trim());
    }
}