package controleur;

import model.Medicament;
import model.Client;
import dao.MedicamentDao;
import dao.ClientDao;
import java.sql.Connection;
import java.util.List;

public class AdministrateurControle {
    private Connection connection;
    private MedicamentDao medDao;
    private ClientDao clientDao;

    public AdministrateurControle(Connection connection) {
        this.connection = connection;
        this.medDao = new MedicamentDao(connection);
        this.clientDao = new ClientDao(connection);
    }

    /**
     * Calcule le nombre total de médicaments en rupture de stock
     * Utile pour les alertes sur le tableau de bord de l'administrateur
     */
    public int verifierAlertesStock(int seuilCritique) {
        List<Medicament> tous = medDao.getTous();
        int alertes = 0;
        for (Medicament m : tous) {
            if (m.getQuantite() <= seuilCritique) {
                alertes++;
            }
        }
        return alertes;
    }

    /**
     * Calcule la somme totale des crédits (dettes) de tous les clients
     */
    public double calculerTotalCredits() {
        List<Client> clients = clientDao.getTous();
        double total = 0;
        for (Client c : clients) {
            total += c.getCredit();
        }
        return total;
    }

    /**
     * Méthode pour obtenir le nombre total d'entrées dans le système
     */
    public int getNombreTotalMeds() {
        return medDao.getTous().size();
    }

    public int getNombreTotalClients() {
        return clientDao.getTous().size();
    }
}