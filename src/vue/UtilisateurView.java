package vue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.util.List;

import controleur.UtilisateurControleur;
import model.Utilisateur;


public class UtilisateurView extends JFrame {

    private final UtilisateurControleur controleur;

    private JTextField txtCin, txtNom, txtPrenom, txtTel, txtLogin;
    private JPasswordField txtMotDePasse;
    private JComboBox<String> cbRole;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtRecherche;

    private JButton btnAjouter, btnModifier, btnSupprimer, btnVider, btnFermer;

    
    public UtilisateurView(Connection connection) {
        this.controleur = new UtilisateurControleur(connection);
        UITheme.applyGlobalDefaults();

        setTitle("PharmaPro — Gestion des utilisateurs");
        setSize(1180, 760);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG);
        setLayout(new BorderLayout());

        add(UITheme.createHeader(
                "Gestion des utilisateurs",
                "Créer, modifier et supprimer les comptes pharmaciens et administrateurs"),
            BorderLayout.NORTH);
        add(creerCorps(), BorderLayout.CENTER);
        add(creerBas(),   BorderLayout.SOUTH);

        chargerDonnees();
        setVisible(true);
    }

    private JPanel creerCorps() {
        JPanel corps = new JPanel(new BorderLayout(0, 16));
        corps.setBackground(UITheme.BG);
        corps.setBorder(new EmptyBorder(18, 22, 0, 22));

        corps.add(creerFormulaireCard(), BorderLayout.NORTH);
        corps.add(creerTableCard(),      BorderLayout.CENTER);

        return corps;
    }

    private JPanel creerFormulaireCard() {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(UITheme.cardBorder());

        JLabel titre = UITheme.labelGold("Informations utilisateur");
        titre.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(0, 0, 12, 0)
        ));

        JPanel ligne1 = new JPanel(new GridLayout(1, 4, 14, 0));
        ligne1.setOpaque(false);

        txtCin    = UITheme.textField();
        txtNom    = UITheme.textField();
        txtPrenom = UITheme.textField();
        txtTel    = UITheme.textField();

        ligne1.add(creerChamp("CIN *", txtCin));
        ligne1.add(creerChamp("Nom *", txtNom));
        ligne1.add(creerChamp("Prénom *", txtPrenom));
        ligne1.add(creerChamp("Téléphone *", txtTel));

        JPanel ligne2 = new JPanel(new GridLayout(1, 4, 14, 0));
        ligne2.setOpaque(false);

        txtLogin      = UITheme.textField();
        txtMotDePasse = creerPasswordField();
        cbRole        = creerComboRole();

        ligne2.add(creerChamp("Login *", txtLogin));
        ligne2.add(creerChamp("Mot de passe *", txtMotDePasse));
        ligne2.add(creerChamp("Rôle *", cbRole));
        ligne2.add(new JPanel() {{ setOpaque(false); }}); 

        JPanel fields = new JPanel(new GridLayout(2, 1, 0, 10));
        fields.setOpaque(false);
        fields.add(ligne1);
        fields.add(ligne2);

        card.add(titre,  BorderLayout.NORTH);
        card.add(fields, BorderLayout.CENTER);
        return card;
    }

    private JPanel creerTableCard() {
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(UITheme.cardBorder());

        JLabel titre = UITheme.labelGold("Liste des utilisateurs");
        titre.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(0, 0, 12, 0)
        ));

        tableModel = new DefaultTableModel(
            new String[]{"CIN", "Nom", "Prénom", "Téléphone", "Login", "Rôle"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);

        table.getColumnModel().getColumn(5).setCellRenderer(
            new javax.swing.table.DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(
                        JTable t, Object value, boolean isSelected,
                        boolean hasFocus, int row, int col) {
                    super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                    setBorder(new EmptyBorder(0, 14, 0, 14));
                    String role = value == null ? "" : value.toString().toLowerCase();
                    if (isSelected) {
                        setForeground(UITheme.PRIMARY);
                        setBackground(new Color(196, 160, 80, 35));
                    } else if ("administrateur".equals(role)) {
                        setForeground(UITheme.WARNING);
                        setBackground(row % 2 == 0 ? UITheme.CARD_BG : new Color(26, 30, 40));
                    } else {
                        setForeground(UITheme.ACCENT);
                        setBackground(row % 2 == 0 ? UITheme.CARD_BG : new Color(26, 30, 40));
                    }
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    return this;
                }
            }
        );

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        scroll.getViewport().setBackground(UITheme.CARD_BG);
        scroll.setBackground(UITheme.CARD_BG);

        card.add(titre,  BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
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
        recherche.add(UITheme.label("🔍  Rechercher par nom :"));
        recherche.add(txtRecherche);

        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        boutons.setOpaque(false);

        btnAjouter   = UITheme.primaryButton("Ajouter");
        btnModifier  = UITheme.secondaryButton("Modifier");
        btnSupprimer = UITheme.dangerButton("Supprimer");
        btnVider     = UITheme.neutralButton("Vider");
        btnFermer    = UITheme.neutralButton("Fermer");

        boutons.add(btnAjouter);
        boutons.add(btnModifier);
        boutons.add(btnSupprimer);
        boutons.add(btnVider);
        boutons.add(btnFermer);

        bas.add(recherche, BorderLayout.WEST);
        bas.add(boutons,   BorderLayout.EAST);

        configurerEvenements();
        return bas;
    }

    private void configurerEvenements() {
        btnAjouter.addActionListener(e   -> ajouter());
        btnModifier.addActionListener(e  -> modifier());
        btnSupprimer.addActionListener(e -> supprimer());
        btnVider.addActionListener(e     -> viderChamps());
        btnFermer.addActionListener(e    -> dispose());

        txtRecherche.addCaretListener(e -> rechercher());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            remplirChamps();
        });
    }

    private void ajouter() {
        try {
            Utilisateur u = lireFormulaire();
            if (u == null) return;

            if (controleur.getParId(u.getCin()) != null) {
                showErreur("Ce CIN est déjà utilisé.");
                return;
            }
            if (controleur.creer(u)) {
                chargerDonnees();
                viderChamps();
                showInfo("Utilisateur créé avec succès.");
            } else {
                showErreur("Échec de création (login déjà utilisé ?).");
            }
        } catch (NumberFormatException ex) {
            showErreur("CIN et téléphone doivent être des nombres entiers.");
        }
    }

    private void modifier() {
        int row = table.getSelectedRow();
        if (row < 0) { showErreur("Sélectionnez un utilisateur à modifier."); return; }
        try {
            Utilisateur u = lireFormulaire();
            if (u == null) return;
            if (controleur.modifier(u)) {
                chargerDonnees();
                viderChamps();
                showInfo("Utilisateur modifié avec succès.");
            } else {
                showErreur("Échec de modification.");
            }
        } catch (NumberFormatException ex) {
            showErreur("CIN et téléphone doivent être des nombres entiers.");
        }
    }

    private void supprimer() {
        int row = table.getSelectedRow();
        if (row < 0) { showErreur("Sélectionnez un utilisateur à supprimer."); return; }

        int cin    = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        String nom = tableModel.getValueAt(row, 1).toString();

        int ok = JOptionPane.showConfirmDialog(this,
            "Supprimer l'utilisateur « " + nom + " » (CIN : " + cin + ") ?",
            "Confirmation de suppression", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;

        if (controleur.supprimer(cin)) {
            chargerDonnees();
            viderChamps();
        } else {
            showErreur("Échec de suppression.");
        }
    }


    private void chargerDonnees() {
        tableModel.setRowCount(0);
        for (Utilisateur u : controleur.getTous()) {
            tableModel.addRow(new Object[]{
                u.getCin(), u.getNom(), u.getPrenom(),
                u.getNum_tel(), u.getLogin(), u.getRole()
            });
        }
    }

    private void rechercher() {
        String q = txtRecherche.getText() == null ? "" : txtRecherche.getText().trim();
        tableModel.setRowCount(0);
        for (Utilisateur u : controleur.chercherParNom(q)) {
            tableModel.addRow(new Object[]{
                u.getCin(), u.getNom(), u.getPrenom(),
                u.getNum_tel(), u.getLogin(), u.getRole()
            });
        }
    }

    private void remplirChamps() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtCin.setText(tableModel.getValueAt(row, 0).toString());
        txtNom.setText(tableModel.getValueAt(row, 1).toString());
        txtPrenom.setText(tableModel.getValueAt(row, 2).toString());
        txtTel.setText(tableModel.getValueAt(row, 3).toString());
        txtLogin.setText(tableModel.getValueAt(row, 4).toString());
        txtMotDePasse.setText(""); 
        String role = tableModel.getValueAt(row, 5).toString().toLowerCase();
        cbRole.setSelectedItem("administrateur".equals(role) ? "administrateur" : "pharmacien");
    }

    private void viderChamps() {
        txtCin.setText(""); txtNom.setText(""); txtPrenom.setText("");
        txtTel.setText(""); txtLogin.setText(""); txtMotDePasse.setText("");
        cbRole.setSelectedIndex(0);
        table.clearSelection();
    }

   
    private Utilisateur lireFormulaire() {
        String cinStr  = txtCin.getText().trim();
        String nom     = txtNom.getText().trim();
        String prenom  = txtPrenom.getText().trim();
        String telStr  = txtTel.getText().trim();
        String login   = txtLogin.getText().trim();
        String mdp     = new String(txtMotDePasse.getPassword()).trim();
        String role    = (String) cbRole.getSelectedItem();

        if (cinStr.isEmpty() || nom.isEmpty() || prenom.isEmpty()
                || telStr.isEmpty() || login.isEmpty()) {
            showErreur("Tous les champs marqués * sont obligatoires (sauf mot de passe en modification).");
            return null;
        }

        int cin = Integer.parseInt(cinStr);   
        int tel = Integer.parseInt(telStr);

        
        if (mdp.isEmpty()) {
            int row = table.getSelectedRow();
            if (row < 0) {
                showErreur("Le mot de passe est obligatoire pour la création.");
                return null;
            }
            Utilisateur existant = controleur.getParId(cin);
            if (existant != null) mdp = existant.getPasswd() != null ? existant.getPasswd() : "";
        }

        return new Utilisateur(cin, nom, prenom, mdp, tel, login, role);
    }


    private JPanel creerChamp(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        p.add(UITheme.label(label), BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JPasswordField creerPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(UITheme.BODY);
        pf.setForeground(UITheme.TEXT);
        pf.setBackground(new Color(22, 25, 34));
        pf.setCaretColor(UITheme.PRIMARY);
        pf.setPreferredSize(new Dimension(0, 38));
        pf.setBorder(UITheme.fieldBorder());
        pf.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                pf.setBorder(UITheme.fieldBorderFocus());
                pf.setBackground(new Color(26, 30, 42));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                pf.setBorder(UITheme.fieldBorder());
                pf.setBackground(new Color(22, 25, 34));
            }
        });
        return pf;
    }

    private JComboBox<String> creerComboRole() {
        JComboBox<String> cb = new JComboBox<>(new String[]{"pharmacien", "administrateur"});
        cb.setFont(UITheme.BODY);
        cb.setBackground(new Color(22, 25, 34));
        cb.setForeground(UITheme.TEXT);
        cb.setPreferredSize(new Dimension(0, 38));
        return cb;
    }

    private void showErreur(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }
}