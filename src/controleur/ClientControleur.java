package controleur;

import dao.ClientDao;
import model.Client;
import java.sql.Connection;
import java.util.List;

public class ClientControleur {
    private ClientDao clientDao;

    public ClientControleur(Connection connection) {
        this.clientDao = new ClientDao(connection);
    }

    public boolean ajouterClient(Client c) {
        return clientDao.create(c);
    }

    public boolean modifierClient(Client c) {
        return clientDao.update(c);
    }

    public boolean supprimerClient(int id) {
        return clientDao.delete(id);
    }

    public List<Client> recupererTous() {
        return clientDao.getTous();
    }

    public List<Client> chercherParNom(String nom) {
        return clientDao.rechercherParNom(nom);
    }
}