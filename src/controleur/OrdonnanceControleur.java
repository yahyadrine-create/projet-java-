package controleur;

import java.sql.Connection;
import java.util.List;

import dao.LigneOrdDao;
import dao.OrdonnanceDao;
import model.Client;
import model.LigneOrd;
import model.Medicament;
import model.Ordonnance;

public class OrdonnanceControleur {
    private final OrdonnanceDao ordonnanceDao;
    private final LigneOrdDao ligneOrdDao;

    public OrdonnanceControleur(Connection connection) {
        this.ordonnanceDao = new OrdonnanceDao(connection);
        this.ligneOrdDao = new LigneOrdDao(connection);
    }

    public List<Ordonnance> listerToutes() {
        return ordonnanceDao.getTous();
    }

    public List<Ordonnance> chercherParNomClient(String nomClient) {
        return ordonnanceDao.rechercherParNom(nomClient == null ? "" : nomClient.trim());
    }

    public boolean ajouterOrdonnance(int numOrd, int idClient, String date) {
        Ordonnance o = new Ordonnance();
        o.setNum_ord(numOrd);
        o.setDate(date);
        o.setClt(new Client(idClient));
        return ordonnanceDao.create(o);
    }

    public boolean supprimerOrdonnance(int numOrd) {
        return ordonnanceDao.delete(numOrd);
    }

    public List<LigneOrd> lignesDeOrdonnance(int numOrd) {
        return ligneOrdDao.getParOrdonnance(numOrd);
    }

    public boolean ajouterLigne(int numOrd, int idMedicament, int quantite) {
        if (quantite <= 0) return false;
        Ordonnance o = new Ordonnance(numOrd);
        Medicament m = new Medicament();
        m.setId_med(idMedicament);
        return ligneOrdDao.create(new LigneOrd(o, m, quantite));
    }

    public boolean modifierQuantiteLigne(int numOrd, int idMedicament, int quantite) {
        if (quantite <= 0) return false;
        Ordonnance o = new Ordonnance(numOrd);
        Medicament m = new Medicament();
        m.setId_med(idMedicament);
        return ligneOrdDao.update(new LigneOrd(o, m, quantite));
    }
    public boolean supprimerLigne(int numOrd, int idMedicament) {
        return ligneOrdDao.deleteLigne(numOrd, idMedicament);
    }
    public double calculerCoutTotal(int numOrd) {
        return ligneOrdDao.calculerCoutTotal(numOrd);
    }
}

