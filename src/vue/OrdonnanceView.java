package vue;

import java.awt.*;
import java.sql.Connection;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import controleur.OrdonnanceControleur;
import model.LigneOrd;
import model.Ordonnance;

public class OrdonnanceView extends JFrame {
    private final OrdonnanceControleur controleur;

    private JTable tableOrd, tableLignes;
    private DefaultTableModel modelOrd, modelLignes;

    private JTextField txtNumOrd, txtIdClient, txtDate, txtRechercheClient;
    private JTextField txtLigneIdMed, txtLigneQte;

    private JButton btnAjouterOrd, btnSupprimerOrd;
    private JButton btnAjouterLigne, btnModifierLigne, btnSupprimerLigne;
    private JButton btnFermer;

    private JLabel lblCoutTotal;

    public OrdonnanceView(Connection conn) {
        this.controleur = new OrdonnanceControleur(conn);
        UITheme.applyGlobalDefaults();

        setTitle("PharmaPro — Gestion des ordonnances");
        setSize(1300, 780);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG);
        setLayout(new BorderLayout());

        add(UITheme.createHeader("Gestion des ordonnances", "Créer et gérer les ordonnances et leurs lignes"), BorderLayout.NORTH);
        add(creerCentre(), BorderLayout.CENTER);
        add(creerBas(), BorderLayout.SOUTH);

        chargerOrdonnances(controleur.listerToutes());
        setVisible(true);
    }

    private JPanel creerCentre() {
        JPanel centre = new JPanel(new GridLayout(1, 2, 18, 0));
        centre.setBackground(UITheme.BG);
        centre.setBorder(new EmptyBorder(16, 20, 0, 20));
        centre.add(creerPanelOrdonnances());
        centre.add(creerPanelLignes());
        return centre;
    }

    private JPanel creerPanelOrdonnances() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UITheme.CARD_BG);
        panel.setBorder(UITheme.cardBorder());

        JLabel titre = UITheme.labelGold("Ordonnances");
        titre.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(0, 0, 12, 0)
        ));

        JPanel form = new JPanel(new GridLayout(1, 3, 12, 0));
        form.setOpaque(false);
        txtNumOrd   = UITheme.textField();
        txtIdClient = UITheme.textField();
        txtDate     = UITheme.textField();
        form.add(creerChamp("N° ordonnance", txtNumOrd));
        form.add(creerChamp("ID client", txtIdClient));
        form.add(creerChamp("Date (AAAA-MM-JJ)", txtDate));

        JPanel actionsOrd = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        actionsOrd.setOpaque(false);
        btnAjouterOrd   = UITheme.primaryButton("Ajouter");
        btnSupprimerOrd = UITheme.dangerButton("Supprimer");
        btnAjouterOrd.setPreferredSize(new Dimension(110, 34));
        btnSupprimerOrd.setPreferredSize(new Dimension(110, 34));
        actionsOrd.add(btnAjouterOrd);
        actionsOrd.add(btnSupprimerOrd);

        JPanel topArea = new JPanel(new BorderLayout(0, 6));
        topArea.setOpaque(false);
        topArea.add(form, BorderLayout.CENTER);
        topArea.add(actionsOrd, BorderLayout.SOUTH);

        JPanel rech = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        rech.setOpaque(false);
        txtRechercheClient = UITheme.textField();
        txtRechercheClient.setPreferredSize(new Dimension(160, 34));
        JButton btnRech = UITheme.secondaryButton("Rechercher");
        btnRech.setPreferredSize(new Dimension(110, 34));
        rech.add(UITheme.label("Client :"));
        rech.add(txtRechercheClient);
        rech.add(btnRech);

        modelOrd = new DefaultTableModel(new String[]{"N° Ord", "ID Client", "Date"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableOrd = new JTable(modelOrd);
        UITheme.styleTable(tableOrd);
        JScrollPane scroll = new JScrollPane(tableOrd);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        scroll.getViewport().setBackground(UITheme.CARD_BG);

        JPanel centreArea = new JPanel(new BorderLayout(0, 8));
        centreArea.setOpaque(false);
        centreArea.add(rech, BorderLayout.NORTH);
        centreArea.add(scroll, BorderLayout.CENTER);

        panel.add(titre, BorderLayout.NORTH);
        panel.add(topArea, BorderLayout.NORTH); 
        
        JPanel northWrapper = new JPanel(new BorderLayout(0, 10));
        northWrapper.setOpaque(false);
        northWrapper.add(titre, BorderLayout.NORTH);
        northWrapper.add(topArea, BorderLayout.CENTER);

        panel.removeAll();
        panel.add(northWrapper, BorderLayout.NORTH);
        panel.add(centreArea, BorderLayout.CENTER);

        btnAjouterOrd.addActionListener(e -> ajouterOrdonnance());
        btnSupprimerOrd.addActionListener(e -> supprimerOrdonnance());
        btnRech.addActionListener(e ->
            chargerOrdonnances(controleur.chercherParNomClient(txtRechercheClient.getText())));
        txtRechercheClient.addCaretListener(e ->
            chargerOrdonnances(controleur.chercherParNomClient(txtRechercheClient.getText())));
        tableOrd.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            chargerLignes();
        });

        return panel;
    }

    private JPanel creerPanelLignes() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UITheme.CARD_BG);
        panel.setBorder(UITheme.cardBorder());

        JLabel titre = UITheme.labelGold("Lignes d'ordonnance");
        titre.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(0, 0, 12, 0)
        ));

        JPanel form = new JPanel(new GridLayout(1, 2, 12, 0));
        form.setOpaque(false);
        txtLigneIdMed = UITheme.textField();
        txtLigneQte   = UITheme.textField();
        form.add(creerChamp("ID médicament", txtLigneIdMed));
        form.add(creerChamp("Quantité", txtLigneQte));

        JPanel actionsLigne = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        actionsLigne.setOpaque(false);
        btnAjouterLigne   = UITheme.primaryButton("Ajouter");
        btnModifierLigne  = UITheme.secondaryButton("Modifier");
        btnSupprimerLigne = UITheme.dangerButton("Supprimer");
        btnAjouterLigne.setPreferredSize(new Dimension(100, 34));
        btnModifierLigne.setPreferredSize(new Dimension(100, 34));
        btnSupprimerLigne.setPreferredSize(new Dimension(100, 34));
        actionsLigne.add(btnAjouterLigne);
        actionsLigne.add(btnModifierLigne);
        actionsLigne.add(btnSupprimerLigne);

        JPanel topArea = new JPanel(new BorderLayout(0, 6));
        topArea.setOpaque(false);
        topArea.add(form, BorderLayout.CENTER);
        topArea.add(actionsLigne, BorderLayout.SOUTH);

        modelLignes = new DefaultTableModel(new String[]{"N° Ord", "ID Méd", "Quantité"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableLignes = new JTable(modelLignes);
        UITheme.styleTable(tableLignes);
        JScrollPane scroll = new JScrollPane(tableLignes);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        scroll.getViewport().setBackground(UITheme.CARD_BG);

        lblCoutTotal = new JLabel("Coût total : 0.00 DT");
        lblCoutTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCoutTotal.setForeground(UITheme.PRIMARY);
        lblCoutTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        lblCoutTotal.setBorder(new EmptyBorder(10, 0, 0, 4));

        JPanel centreArea = new JPanel(new BorderLayout(0, 0));
        centreArea.setOpaque(false);
        centreArea.add(scroll, BorderLayout.CENTER);
        centreArea.add(lblCoutTotal, BorderLayout.SOUTH);

        JPanel northWrapper = new JPanel(new BorderLayout(0, 10));
        northWrapper.setOpaque(false);
        northWrapper.add(titre, BorderLayout.NORTH);
        northWrapper.add(topArea, BorderLayout.CENTER);

        panel.add(northWrapper, BorderLayout.NORTH);
        panel.add(centreArea, BorderLayout.CENTER);

        btnAjouterLigne.addActionListener(e -> ajouterLigne());
        btnModifierLigne.addActionListener(e -> modifierLigne());
        btnSupprimerLigne.addActionListener(e -> supprimerLigne());

        tableLignes.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = tableLignes.getSelectedRow();
            if (row < 0) return;
            txtLigneIdMed.setText(String.valueOf(modelLignes.getValueAt(row, 1)));
            txtLigneQte.setText(String.valueOf(modelLignes.getValueAt(row, 2)));
        });

        return panel;
    }

    private JPanel creerBas() {
        JPanel bas = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        bas.setBackground(new Color(14, 16, 22));
        bas.setBorder(UITheme.sectionDividerTop());
        btnFermer = UITheme.neutralButton("Fermer");
        bas.add(btnFermer);
        btnFermer.addActionListener(e -> dispose());
        return bas;
    }

    private JPanel creerChamp(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        p.add(UITheme.label(label), BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void ajouterOrdonnance() {
        try {
            int num     = Integer.parseInt(txtNumOrd.getText().trim());
            int idClt   = Integer.parseInt(txtIdClient.getText().trim());
            String date = txtDate.getText().trim();
            if (date.isEmpty()) { JOptionPane.showMessageDialog(this, "La date est obligatoire."); return; }
            if (controleur.ajouterOrdonnance(num, idClt, date)) {
                chargerOrdonnances(controleur.listerToutes()); viderChampsOrd();
            } else JOptionPane.showMessageDialog(this, "Échec d'ajout.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Formats invalides.");
        }
    }

    private void supprimerOrdonnance() {
        int row = tableOrd.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez une ordonnance."); return; }
        int num = Integer.parseInt(modelOrd.getValueAt(row, 0).toString());
        int ok  = JOptionPane.showConfirmDialog(this,
            "Supprimer l'ordonnance #" + num + " et toutes ses lignes ?",
            "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        if (controleur.supprimerOrdonnance(num)) {
            chargerOrdonnances(controleur.listerToutes());
            modelLignes.setRowCount(0);
            lblCoutTotal.setText("Coût total : 0.00 DT");
        } else JOptionPane.showMessageDialog(this, "Échec de suppression.");
    }

    private void ajouterLigne() {
        int rowOrd = tableOrd.getSelectedRow();
        if (rowOrd < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez une ordonnance d'abord."); return; }
        int num = Integer.parseInt(modelOrd.getValueAt(rowOrd, 0).toString());
        try {
            int idMed = Integer.parseInt(txtLigneIdMed.getText().trim());
            int qte   = Integer.parseInt(txtLigneQte.getText().trim());
            if (!controleur.ajouterLigne(num, idMed, qte)) {
                JOptionPane.showMessageDialog(this, "Stock insuffisant ou médicament introuvable.",
                    "Avertissement", JOptionPane.WARNING_MESSAGE);
                return;
            }
            chargerLignes(); viderChampsLigne();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Formats invalides.");
        }
    }

    private void modifierLigne() {
        int rowOrd = tableOrd.getSelectedRow();
        if (rowOrd < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez une ordonnance d'abord."); return; }
        int num = Integer.parseInt(modelOrd.getValueAt(rowOrd, 0).toString());
        try {
            int idMed = Integer.parseInt(txtLigneIdMed.getText().trim());
            int qte   = Integer.parseInt(txtLigneQte.getText().trim());
            if (!controleur.modifierQuantiteLigne(num, idMed, qte)) {
                JOptionPane.showMessageDialog(this, "Échec de modification."); return;
            }
            chargerLignes(); viderChampsLigne();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Formats invalides.");
        }
    }

    private void supprimerLigne() {
        int rowOrd = tableOrd.getSelectedRow();
        if (rowOrd < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez une ordonnance d'abord."); return; }
        int rowLigne = tableLignes.getSelectedRow();
        if (rowLigne < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez une ligne."); return; }
        int numOrd = Integer.parseInt(modelOrd.getValueAt(rowOrd, 0).toString());
        int idMed  = Integer.parseInt(modelLignes.getValueAt(rowLigne, 1).toString());
        int qte    = Integer.parseInt(modelLignes.getValueAt(rowLigne, 2).toString());
        int ok = JOptionPane.showConfirmDialog(this,
            "Supprimer la ligne médicament #" + idMed + " (qté : " + qte + ") ?",
            "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        if (controleur.supprimerLigne(numOrd, idMed)) { chargerLignes(); viderChampsLigne(); }
        else JOptionPane.showMessageDialog(this, "Échec de suppression.");
    }

    private void chargerOrdonnances(List<Ordonnance> ords) {
        modelOrd.setRowCount(0);
        for (Ordonnance o : ords) {
            int idClt = o.getClt() == null ? 0 : o.getClt().getId_clt();
            modelOrd.addRow(new Object[]{o.getNum_ord(), idClt, o.getDate()});
        }
        if (tableOrd.getRowCount() == 0) {
            modelLignes.setRowCount(0);
            lblCoutTotal.setText("Coût total : 0.00 DT");
        }
    }

    private void chargerLignes() {
        int row = tableOrd.getSelectedRow();
        if (row < 0) {
            modelLignes.setRowCount(0);
            lblCoutTotal.setText("Coût total : 0.00 DT");
            return;
        }
        int num = Integer.parseInt(modelOrd.getValueAt(row, 0).toString());
        modelLignes.setRowCount(0);
        for (LigneOrd l : controleur.lignesDeOrdonnance(num)) {
            int idMed = l.getMed() == null ? 0 : l.getMed().getId_med();
            modelLignes.addRow(new Object[]{num, idMed, l.getQuantite()});
        }
        calculerCoutTotal();
    }

    private void calculerCoutTotal() {
        int row = tableOrd.getSelectedRow();
        if (row < 0) { lblCoutTotal.setText("Coût total : 0.00 DT"); return; }
        int numOrd   = Integer.parseInt(modelOrd.getValueAt(row, 0).toString());
        double total = controleur.calculerCoutTotal(numOrd);
        lblCoutTotal.setText(String.format("Coût total ordonnance #%d : %.2f DT", numOrd, total));
    }

    private void viderChampsOrd()   { txtNumOrd.setText(""); txtIdClient.setText(""); txtDate.setText(""); }
    private void viderChampsLigne() { txtLigneIdMed.setText(""); txtLigneQte.setText(""); }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OrdonnanceView(util.SingletonConnection.getInstance()));
    }
}