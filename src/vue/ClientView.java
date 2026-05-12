package vue;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.sql.Connection;
import controleur.ClientControleur;
import model.Client;

public class ClientView extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtNom, txtPrenom, txtAge, txtTel, txtCredit, txtRecherche;
    private JButton btnAjouter, btnModifier, btnSupprimer, btnFermer;
    private ClientControleur controle;

    public ClientView(Connection conn) {
        this.controle = new ClientControleur(conn);
        UITheme.applyGlobalDefaults();

        setTitle("PharmaPro — Gestion des clients");
        setSize(1140, 740);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG);
        setLayout(new BorderLayout());

        add(UITheme.createHeader("Gestion des clients", "Ajouter, modifier et supprimer des clients"), BorderLayout.NORTH);
        add(creerCorps(), BorderLayout.CENTER);
        add(creerBas(), BorderLayout.SOUTH);

        chargerDonnees();
        setVisible(true);
    }

    private JPanel creerCorps() {
        JPanel corps = new JPanel(new BorderLayout(0, 16));
        corps.setBackground(UITheme.BG);
        corps.setBorder(new EmptyBorder(18, 22, 0, 22));

        JPanel formCard = new JPanel(new BorderLayout(0, 14));
        formCard.setBackground(UITheme.CARD_BG);
        formCard.setBorder(UITheme.cardBorder());

        JLabel formTitle = UITheme.labelGold("Informations client");
        formTitle.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(0, 0, 12, 0)
        ));

        JPanel fields = new JPanel(new GridLayout(2, 6, 14, 10));
        fields.setOpaque(false);

        txtId     = UITheme.textField();
        txtNom    = UITheme.textField();
        txtPrenom = UITheme.textField();
        txtAge    = UITheme.textField();
        txtTel    = UITheme.textField();
        txtCredit = UITheme.textField();

        fields.add(creerChamp("ID Client", txtId));
        fields.add(creerChamp("Nom", txtNom));
        fields.add(creerChamp("Prénom", txtPrenom));
        fields.add(creerChamp("Âge", txtAge));
        fields.add(creerChamp("Téléphone", txtTel));
        fields.add(creerChamp("Crédit (DT)", txtCredit));

        formCard.add(formTitle, BorderLayout.NORTH);
        formCard.add(fields, BorderLayout.CENTER);
        corps.add(formCard, BorderLayout.NORTH);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(UITheme.CARD_BG);
        tableCard.setBorder(UITheme.cardBorder());

        JLabel tableTitle = UITheme.labelGold("Liste des clients");
        tableTitle.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(0, 0, 12, 0)
        ));

        tableModel = new DefaultTableModel(
            new String[]{"ID", "Nom", "Prénom", "Âge", "Crédit (DT)", "Téléphone"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        scroll.getViewport().setBackground(UITheme.CARD_BG);
        scroll.setBackground(UITheme.CARD_BG);
        styleScrollBar(scroll);

        tableCard.add(tableTitle, BorderLayout.NORTH);
        tableCard.add(scroll, BorderLayout.CENTER);
        corps.add(tableCard, BorderLayout.CENTER);

        return corps;
    }

    private JPanel creerBas() {
        JPanel bas = new JPanel(new BorderLayout(0, 0));
        bas.setBackground(new Color(14, 16, 22));
        bas.setBorder(BorderFactory.createCompoundBorder(
            UITheme.sectionDividerTop(),
            new EmptyBorder(12, 22, 12, 22)
        ));

        JPanel recherche = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        recherche.setOpaque(false);
        txtRecherche = UITheme.textField();
        txtRecherche.setPreferredSize(new Dimension(240, 36));
        recherche.add(UITheme.label("🔍  Rechercher par nom :"));
        recherche.add(txtRecherche);

        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        boutons.setOpaque(false);
        btnAjouter   = UITheme.primaryButton("Ajouter");
        btnModifier  = UITheme.secondaryButton("Modifier");
        btnSupprimer = UITheme.dangerButton("Supprimer");
        btnFermer    = UITheme.neutralButton("Fermer");

        boutons.add(btnAjouter);
        boutons.add(btnModifier);
        boutons.add(btnSupprimer);
        boutons.add(btnFermer);

        bas.add(recherche, BorderLayout.WEST);
        bas.add(boutons, BorderLayout.EAST);

        configurerEvents();
        return bas;
    }

    private JPanel creerChamp(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        p.add(UITheme.label(label), BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void styleScrollBar(JScrollPane scroll) {
        scroll.getVerticalScrollBar().setBackground(UITheme.CARD_BG);
        scroll.getHorizontalScrollBar().setBackground(UITheme.CARD_BG);
    }

    private void configurerEvents() {
        btnAjouter.addActionListener(e   -> ajouter());
        btnModifier.addActionListener(e  -> modifier());
        btnSupprimer.addActionListener(e -> supprimer());
        btnFermer.addActionListener(e    -> dispose());

        txtRecherche.addCaretListener(e -> rechercher());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            remplirChamps();
        });
    }

    private void chargerDonnees() {
        tableModel.setRowCount(0);
        for (Client c : controle.recupererTous()) {
            tableModel.addRow(new Object[]{
                c.getId_clt(), c.getNom(), c.getPrenom(),
                c.getAge(), c.getCredit(), c.getNum_tel()
            });
        }
    }

    private void rechercher() {
        String q = txtRecherche.getText() == null ? "" : txtRecherche.getText().trim();
        tableModel.setRowCount(0);
        for (Client c : controle.chercherParNom(q)) {
            tableModel.addRow(new Object[]{
                c.getId_clt(), c.getNom(), c.getPrenom(),
                c.getAge(), c.getCredit(), c.getNum_tel()
            });
        }
    }

    private void remplirChamps() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtId.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        txtNom.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        txtPrenom.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        txtAge.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        txtCredit.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        txtTel.setText(String.valueOf(tableModel.getValueAt(row, 5)));
    }

    private void viderChamps() {
        txtId.setText(""); txtNom.setText(""); txtPrenom.setText("");
        txtAge.setText(""); txtTel.setText(""); txtCredit.setText("");
    }

    private Client lireFormulaire() {
        int id        = Integer.parseInt(txtId.getText().trim());
        String nom    = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        int age       = Integer.parseInt(txtAge.getText().trim());
        int tel       = Integer.parseInt(txtTel.getText().trim());
        double credit = Double.parseDouble(txtCredit.getText().trim());
        return new Client(id, nom, prenom, age, credit, tel);
    }

    private void ajouter() {
        try {
            Client c = lireFormulaire();
            if (controle.ajouterClient(c)) { chargerDonnees(); viderChamps(); }
            else showErreur("Échec d'ajout du client.");
        } catch (Exception ex) {
            showErreur("Vérifiez les formats des champs.");
        }
    }

    private void modifier() {
        if (table.getSelectedRow() < 0) { showErreur("Sélectionnez un client à modifier."); return; }
        try {
            Client c = lireFormulaire();
            if (controle.modifierClient(c)) { chargerDonnees(); viderChamps(); }
            else showErreur("Échec de modification.");
        } catch (Exception ex) {
            showErreur("Vérifiez les formats des champs.");
        }
    }

    private void supprimer() {
        int row = table.getSelectedRow();
        if (row < 0) { showErreur("Sélectionnez un client à supprimer."); return; }
        int id = Integer.parseInt(String.valueOf(tableModel.getValueAt(row, 0)));
        int ok = JOptionPane.showConfirmDialog(this,
            "Supprimer le client #" + id + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        if (controle.supprimerClient(id)) { chargerDonnees(); viderChamps(); }
        else showErreur("Échec de suppression.");
    }

    private void showErreur(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientView(null));
    }
}