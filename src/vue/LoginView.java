package vue;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import controleur.LoginControleur;
import model.Utilisateur;
import util.SingletonConnection;

public class LoginView extends JFrame {
    private Connection connection;
    private final LoginControleur controleur;

    private JTextField txtLogin;
    private JPasswordField txtMotDePasse;
    private JLabel lblErreur;
    private JButton btnConnexion;
    private JButton btnRegistre;

    public LoginView(Connection connection) {
        this.connection = connection;
        this.controleur = new LoginControleur(connection);
        UITheme.applyGlobalDefaults();

        setTitle("PharmaPro — Connexion");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG);
        setLayout(new BorderLayout());

        add(creerPanneau(), BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel creerPanneau() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(UITheme.BG);
        // Fond avec texture subtile (grille de points)
        root.setBackground(UITheme.BG);

        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(UITheme.CARD_BG);
        card.setPreferredSize(new Dimension(400, 500));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_BRIGHT, 1, true),
            new EmptyBorder(0, 0, 0, 0)
        ));

        // ── En-tête de la carte ──────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Fond foncé premium
                g2.setColor(new Color(14, 16, 22));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Grille de points décorative
                g2.setColor(new Color(196, 160, 80, 18));
                for (int x = 0; x < getWidth(); x += 18) {
                    for (int y = 0; y < getHeight(); y += 18) {
                        g2.fillOval(x, y, 2, 2);
                    }
                }
            }
        };
        topBar.setPreferredSize(new Dimension(0, 130));
        topBar.setBorder(new EmptyBorder(28, 32, 22, 32));
        topBar.setOpaque(false);

        JPanel logoArea = new JPanel();
        logoArea.setLayout(new BoxLayout(logoArea, BoxLayout.Y_AXIS));
        logoArea.setOpaque(false);

        // Carré décoratif Or
        JLabel cross = new JLabel("✚");
        cross.setFont(new Font("Segoe UI", Font.BOLD, 22));
        cross.setForeground(UITheme.PRIMARY);
        cross.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel appName = new JLabel("PharmaPro");
        appName.setFont(new Font("Georgia", Font.BOLD, 26));
        appName.setForeground(UITheme.PRIMARY);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("Système de gestion de pharmacie");
        tagline.setFont(UITheme.SMALL);
        tagline.setForeground(UITheme.TEXT_MUTED);
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoArea.add(cross);
        logoArea.add(Box.createVerticalStrut(6));
        logoArea.add(appName);
        logoArea.add(Box.createVerticalStrut(4));
        logoArea.add(tagline);
        topBar.add(logoArea, BorderLayout.CENTER);

        JPanel accentLine = new JPanel();
        accentLine.setBackground(UITheme.PRIMARY);
        accentLine.setPreferredSize(new Dimension(0, 2));
        topBar.add(accentLine, BorderLayout.SOUTH);

        // ── Corps du formulaire ──────────────────────────────────────────────
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(UITheme.CARD_BG);
        form.setBorder(new EmptyBorder(28, 32, 28, 32));

        JLabel loginTitle = new JLabel("Connexion");
        loginTitle.setFont(UITheme.H2);
        loginTitle.setForeground(UITheme.TEXT);
        loginTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel loginSub = new JLabel("Entrez vos identifiants pour accéder au système");
        loginSub.setFont(UITheme.SMALL);
        loginSub.setForeground(UITheme.TEXT_MUTED);
        loginSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtLogin = UITheme.textField();
        txtMotDePasse = new JPasswordField();
        txtMotDePasse.setFont(UITheme.BODY);
        txtMotDePasse.setForeground(UITheme.TEXT);
        txtMotDePasse.setBackground(new Color(22, 25, 34));
        txtMotDePasse.setCaretColor(UITheme.PRIMARY);
        txtMotDePasse.setPreferredSize(new Dimension(0, 38));
        txtMotDePasse.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtMotDePasse.setBorder(UITheme.fieldBorder());
        txtMotDePasse.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                txtMotDePasse.setBorder(UITheme.fieldBorderFocus());
                txtMotDePasse.setBackground(new Color(26, 30, 42));
            }
            public void focusLost(FocusEvent e) {
                txtMotDePasse.setBorder(UITheme.fieldBorder());
                txtMotDePasse.setBackground(new Color(22, 25, 34));
            }
        });

        lblErreur = new JLabel(" ");
        lblErreur.setFont(UITheme.SMALL);
        lblErreur.setForeground(UITheme.DANGER);
        lblErreur.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnConnexion = UITheme.primaryButton("SE CONNECTER");
        btnConnexion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnConnexion.setPreferredSize(new Dimension(Integer.MAX_VALUE, 42));
        btnConnexion.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnConnexion.setForeground(new Color(18, 20, 26));

        btnRegistre = UITheme.neutralButton("Créer un compte");
        btnRegistre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnRegistre.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(loginTitle);
        form.add(Box.createVerticalStrut(4));
        form.add(loginSub);
        form.add(Box.createVerticalStrut(24));
        form.add(creerChampLabel("Identifiant", txtLogin));
        form.add(Box.createVerticalStrut(14));
        form.add(creerChampLabel("Mot de passe", txtMotDePasse));
        form.add(Box.createVerticalStrut(8));
        form.add(lblErreur);
        form.add(Box.createVerticalStrut(16));
        form.add(btnConnexion);
        form.add(Box.createVerticalStrut(10));
        form.add(btnRegistre);

        card.add(topBar, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);

        root.add(card);
        return root;
    }

    private JPanel creerChampLabel(String labelText, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        JLabel lbl = UITheme.label(labelText);
        lbl.setForeground(UITheme.TEXT_MUTED);
        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void tenterConnexion() {
        lblErreur.setText(" ");
        String login = txtLogin.getText() == null ? "" : txtLogin.getText().trim();
        String mdp   = new String(txtMotDePasse.getPassword());

        if (login.isEmpty()) { lblErreur.setText("L'identifiant est obligatoire."); return; }
        if (mdp.trim().isEmpty()) { lblErreur.setText("Le mot de passe est obligatoire."); return; }

        Utilisateur u = controleur.authentifier(login, mdp);
        if (u == null) { lblErreur.setText("Identifiant ou mot de passe incorrect."); return; }

        String role = u.getRole() == null ? "" : u.getRole().trim().toLowerCase();
        dispose();

        if ("administrateur".equals(role)) { new AdministrateurView(connection).setVisible(true); return; }
        if ("pharmacien".equals(role))     { new PharmacienView(connection).setVisible(true); return; }

        JOptionPane.showMessageDialog(null,
            "Connexion réussie (" + role + "), interface non disponible.",
            "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    {
        SwingUtilities.invokeLater(() -> {
            btnConnexion.addActionListener(e -> tenterConnexion());
            btnRegistre.addActionListener(e -> {
                dispose();
                new RegistreView(connection).setVisible(true);
            });
            KeyAdapter enterKey = new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) tenterConnexion();
                }
            };
            txtLogin.addKeyListener(enterKey);
            txtMotDePasse.addKeyListener(enterKey);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginView(SingletonConnection.getInstance()));
    }
}