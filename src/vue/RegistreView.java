package vue;

import java.awt.*;
import java.sql.Connection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import controleur.RegistreControleur;
import model.Utilisateur;
import util.SingletonConnection;

public class RegistreView extends JFrame {
    private final Connection connection;
    private final RegistreControleur controleur;

    private JTextField txtCin, txtNom, txtPrenom, txtTel, txtLogin;
    private JPasswordField txtMotDePasse;
    private JComboBox<String> cbRole;
    private JButton btnCreer, btnAnnuler;

    public RegistreView(Connection connection) {
        this.connection  = connection;
        this.controleur  = new RegistreControleur(connection);
        UITheme.applyGlobalDefaults();

        setTitle("PharmaPro — Créer un compte");
        setSize(700, 580);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG);
        setLayout(new BorderLayout());

        add(UITheme.createHeader("Créer un compte", "Enregistrer un nouvel utilisateur dans le système"), BorderLayout.NORTH);
        add(creerCorps(), BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel creerCorps() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UITheme.BG);
        wrapper.setBorder(new EmptyBorder(24, 36, 24, 36));

        JPanel card = new JPanel(new BorderLayout(0, 22));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(UITheme.cardBorder());

        JLabel sectionTitle = UITheme.labelGold("Informations du compte");
        sectionTitle.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(0, 0, 14, 0)
        ));

        JPanel form = new JPanel(new GridLayout(4, 2, 16, 12));
        form.setOpaque(false);

        txtCin        = UITheme.textField();
        txtNom        = UITheme.textField();
        txtPrenom     = UITheme.textField();
        txtTel        = UITheme.textField();
        txtLogin      = UITheme.textField();
        txtMotDePasse = new JPasswordField();
        txtMotDePasse.setFont(UITheme.BODY);
        txtMotDePasse.setForeground(UITheme.TEXT);
        txtMotDePasse.setBackground(new Color(22, 25, 34));
        txtMotDePasse.setCaretColor(UITheme.PRIMARY);
        txtMotDePasse.setPreferredSize(new Dimension(0, 38));
        txtMotDePasse.setBorder(UITheme.fieldBorder());

        cbRole = new JComboBox<>(new String[]{"pharmacien", "administrateur"});
        cbRole.setFont(UITheme.BODY);
        cbRole.setBackground(new Color(22, 25, 34));
        cbRole.setForeground(UITheme.TEXT);

        form.add(creerChampLabel("CIN *", txtCin));
        form.add(creerChampLabel("Téléphone *", txtTel));
        form.add(creerChampLabel("Nom *", txtNom));
        form.add(creerChampLabel("Prénom *", txtPrenom));
        form.add(creerChampLabel("Login *", txtLogin));
        form.add(creerChampLabel("Mot de passe *", txtMotDePasse));
        form.add(creerChampLabel("Rôle *", cbRole));
        form.add(new JLabel());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actions.setOpaque(false);
        btnAnnuler = UITheme.neutralButton("Annuler");
        btnCreer   = UITheme.primaryButton("CRÉER LE COMPTE");
        btnCreer.setPreferredSize(new Dimension(160, 38));
        actions.add(btnAnnuler);
        actions.add(btnCreer);

        JPanel top = new JPanel(new BorderLayout(0, 14));
        top.setOpaque(false);
        top.add(sectionTitle, BorderLayout.NORTH);
        top.add(form, BorderLayout.CENTER);

        card.add(top, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);

        btnCreer.addActionListener(e -> creerCompte());
        btnAnnuler.addActionListener(e -> {
            dispose();
            new LoginView(connection).setVisible(true);
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1; gbc.weighty = 1;
        wrapper.add(card, gbc);
        return wrapper;
    }

    private JPanel creerChampLabel(String labelText, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setOpaque(false);
        p.add(UITheme.label(labelText), BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void creerCompte() {
        try {
            int cin       = Integer.parseInt(txtCin.getText().trim());
            int tel       = Integer.parseInt(txtTel.getText().trim());
            String nom    = txtNom.getText().trim();
            String prenom = txtPrenom.getText().trim();
            String login  = txtLogin.getText().trim();
            String mdp    = new String(txtMotDePasse.getPassword()).trim();
            String role   = String.valueOf(cbRole.getSelectedItem());

            if (nom.isEmpty() || prenom.isEmpty() || login.isEmpty() || mdp.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tous les champs marqués * sont obligatoires.");
                return;
            }
            Utilisateur u = new Utilisateur(cin, nom, prenom, mdp, tel, login, role);
            if (!controleur.inscrire(u)) {
                JOptionPane.showMessageDialog(this, "Échec de création (login déjà utilisé ?).");
                return;
            }
            JOptionPane.showMessageDialog(this, "Compte créé avec succès.");
            dispose();
            new LoginView(connection).setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Vérifiez les formats (CIN / téléphone).");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistreView(SingletonConnection.getInstance()));
    }
}