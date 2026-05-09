package vue;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.sql.Connection;
import controleur.MedicamentControler;
import model.Medicament;

public class MedicamentView extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtNom, txtPrix, txtQuantite, txtDateExp, txtCategorie, txtRecherche;
    private JButton btnAjouter, btnModifier, btnSupprimer, btnFermer;
    private MedicamentControler controle;

    public MedicamentView(Connection conn) {
        this.controle = new MedicamentControler(conn);
        UITheme.applyGlobalDefaults();

        setTitle("PharmaPro — Gestion des médicaments");
        setSize(1140, 740);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG);
        setLayout(new BorderLayout());

        add(UITheme.createHeader("Médicaments & Stock", "Gérer le catalogue et les niveaux de stock"), BorderLayout.NORTH);
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

        JLabel formTitle = UITheme.labelGold("Informations médicament");
        formTitle.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(0, 0, 12, 0)
        ));

        JPanel fields = new JPanel(new GridLayout(2, 6, 14, 10));
        fields.setOpaque(false);

        txtId        = UITheme.textField();
        txtNom       = UITheme.textField();
        txtPrix      = UITheme.textField();
        txtQuantite  = UITheme.textField();
        txtDateExp   = UITheme.textField();
        txtCategorie = UITheme.textField();

        fields.add(creerChamp("Code médicament", txtId));
        fields.add(creerChamp("Désignation", txtNom));
        fields.add(creerChamp("Prix unitaire (DT)", txtPrix));
        fields.add(creerChamp("Quantité en stock", txtQuantite));
        fields.add(creerChamp("Date expiration (AAAA-MM-JJ)", txtDateExp));
        fields.add(creerChamp("Catégorie", txtCategorie));

        formCard.add(formTitle, BorderLayout.NORTH);
        formCard.add(fields, BorderLayout.CENTER);
        corps.add(formCard, BorderLayout.NORTH);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(UITheme.CARD_BG);
        tableCard.setBorder(UITheme.cardBorder());

        JLabel tableTitle = UITheme.labelGold("Catalogue des médicaments");
        tableTitle.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(0, 0, 12, 0)
        ));

        tableModel = new DefaultTableModel(
            new String[]{"Code", "Désignation", "Prix (DT)", "Quantité", "Expiration", "Catégorie"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        scroll.getViewport().setBackground(UITheme.CARD_BG);

        tableCard.add(tableTitle, BorderLayout.NORTH);
        tableCard.add(scroll, BorderLayout.CENTER);
        corps.add(tableCard, BorderLayout.CENTER);

        return corps;
    }

    private JPanel creerBas() {
        JPanel bas = new JPanel(new BorderLayout());
        bas.setBackground(new Color(14, 16, 22));
        bas.setBorder(BorderFactory.createCompoundBorder(
            UITheme.sectionDividerTop(),
            new EmptyBorder(12, 22, 12, 22)
        ));

        JPanel recherche = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        recherche.setOpaque(false);
        txtRecherche = UITheme.textField();
        txtRecherche.setPreferredSize(new Dimension(240, 36));
        recherche.add(UITheme.label("🔍  Rechercher :"));
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

    private void configurerEvents() {
        btnAjouter.addActionListener(e   -> ajouter());
        btnModifier.addActionListener(e  -> modifier());
        btnSupprimer.addActionListener(e -> supprimer());
        btnFermer.addActionListener(e    -> dispose());

        txtRecherche.addCaretListener(e -> {
            tableModel.setRowCount(0);
            for (Medicament m : controle.chercher(txtRecherche.getText())) {
                tableModel.addRow(new Object[]{
                    m.getId_med(), m.getNom(), m.getPrix(),
                    m.getQuantite(), m.getDateExpiratino(), m.getCategorie()
                });
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            remplirChamps();
        });
    }

    private void chargerDonnees() {
        tableModel.setRowCount(0);
        for (Medicament m : controle.listerTout()) {
            tableModel.addRow(new Object[]{
                m.getId_med(), m.getNom(), m.getPrix(),
                m.getQuantite(), m.getDateExpiratino(), m.getCategorie()
            });
        }
    }

    private void remplirChamps() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtId.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        txtNom.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        txtPrix.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        txtQuantite.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        txtDateExp.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        txtCategorie.setText(String.valueOf(tableModel.getValueAt(row, 5)));
    }

    private void viderChamps() {
        txtId.setText(""); txtNom.setText(""); txtPrix.setText("");
        txtQuantite.setText(""); txtDateExp.setText(""); txtCategorie.setText("");
    }

    private void ajouter() {
        try {
            int id      = Integer.parseInt(txtId.getText().trim());
            String nom  = txtNom.getText().trim();
            int qte     = Integer.parseInt(txtQuantite.getText().trim());
            String date = txtDateExp.getText().trim();
            double prix = Double.parseDouble(txtPrix.getText().trim());
            String cat  = txtCategorie.getText().trim();
            if (nom.isEmpty() || date.isEmpty() || cat.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Remplissez tous les champs."); return;
            }
            if (controle.trouverParId(id) != null) {
                JOptionPane.showMessageDialog(this,
                    "Ce code existe déjà. Utilisez « Modifier ».",
                    "Code déjà utilisé", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (controle.ajouter(new Medicament(id, nom, qte, date, prix, cat))) {
                chargerDonnees(); viderChamps();
            } else JOptionPane.showMessageDialog(this, "Échec d'insertion.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vérifiez les formats (code, quantité, prix).");
        }
    }

    private void modifier() {
        if (table.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un médicament à modifier."); return;
        }
        try {
            Medicament m = new Medicament(
                Integer.parseInt(txtId.getText().trim()),
                txtNom.getText().trim(),
                Integer.parseInt(txtQuantite.getText().trim()),
                txtDateExp.getText().trim(),
                Double.parseDouble(txtPrix.getText().trim()),
                txtCategorie.getText().trim()
            );
            if (controle.modifier(m)) { chargerDonnees(); viderChamps(); }
            else JOptionPane.showMessageDialog(this, "Échec de modification.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Vérifiez les formats des champs.");
        }
    }

    private void supprimer() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un médicament."); return;
        }
        int id = Integer.parseInt(String.valueOf(tableModel.getValueAt(row, 0)));
        int ok = JOptionPane.showConfirmDialog(this,
            "Supprimer le médicament #" + id + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        if (controle.supprimer(id)) { chargerDonnees(); viderChamps(); }
        else JOptionPane.showMessageDialog(this, "Échec de suppression.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MedicamentView(null));
    }
}