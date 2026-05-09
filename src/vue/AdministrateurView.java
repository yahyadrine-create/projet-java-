package vue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.sql.Connection;

public class AdministrateurView extends JFrame {
    private Connection connection;

    public AdministrateurView(Connection connection) {
        this.connection = connection;
        UITheme.applyGlobalDefaults();

        setTitle("PharmaPro — Tableau de bord Administrateur");
        setSize(1040, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG);
        setLayout(new BorderLayout());

        add(UITheme.createHeader("Tableau de bord", "Administrateur — Gestion complète du système"), BorderLayout.NORTH);
        add(creerMenu(), BorderLayout.CENTER);
        add(creerFooter(), BorderLayout.SOUTH);

        setVisible(true);
    }

    private JPanel creerMenu() {
        JPanel menuPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        menuPanel.setBackground(UITheme.BG);
        menuPanel.setBorder(new EmptyBorder(32, 42, 32, 42));

        JButton btnClients  = creerTuile("Gestion des clients",
            "Ajouter, modifier et consulter les clients", "👥", UITheme.PRIMARY);
        JButton btnMeds     = creerTuile("Gestion des médicaments",
            "Catalogue et informations produits", "💊", UITheme.ACCENT);
        JButton btnStock    = creerTuile("Gestion des stocks",
            "Niveaux de stock et alertes critiques", "📦", UITheme.WARNING);
        JButton btnLogout   = creerTuile("Déconnexion",
            "Quitter la session en cours", "🔒", UITheme.DANGER);

        menuPanel.add(btnClients);
        menuPanel.add(btnMeds);
        menuPanel.add(btnStock);
        menuPanel.add(btnLogout);

        btnClients.addActionListener(e -> new ClientView(connection).setVisible(true));
        btnMeds.addActionListener(e    -> new MedicamentView(connection).setVisible(true));
        btnStock.addActionListener(e   -> new MedicamentView(connection).setVisible(true));
        btnLogout.addActionListener(e  -> {
            int ok = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vous déconnecter ?", "Déconnexion", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                dispose();
                new LoginView(connection).setVisible(true);
            }
        });

        return menuPanel;
    }

    private JPanel creerFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 12));
        footer.setBackground(new Color(14, 16, 22));
        footer.setBorder(UITheme.sectionDividerTop());
        JLabel version = new JLabel("PharmaPro v1.0  •  Administrateur");
        version.setFont(UITheme.SMALL);
        version.setForeground(UITheme.TEXT_DIM);
        footer.add(version);
        return footer;
    }

    private JButton creerTuile(String titre, String description, String icone, Color couleur) {
        JButton b = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                // Bande colorée gauche
                g2.setColor(couleur);
                g2.fillRoundRect(0, 0, 5, getHeight(), 4, 4);
                // Lueur subtile en haut
                g2.setColor(new Color(couleur.getRed(), couleur.getGreen(), couleur.getBlue(), 15));
                g2.fillRect(5, 0, getWidth() - 5, getHeight() / 3);
                super.paintComponent(g);
            }
        };

        b.setLayout(new BorderLayout(0, 0));
        b.setBackground(UITheme.CARD_BG);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1, true));

        // Contenu
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(26, 24, 26, 20));

        JLabel iconeLabel = new JLabel(icone);
        iconeLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        iconeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titreLabel = new JLabel(titre);
        titreLabel.setFont(UITheme.H3);
        titreLabel.setForeground(UITheme.TEXT);
        titreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(UITheme.SMALL);
        descLabel.setForeground(UITheme.TEXT_MUTED);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Badge couleur
        JLabel badge = new JLabel("  ●  ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 8));
        badge.setForeground(couleur);
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(iconeLabel);
        content.add(Box.createVerticalStrut(14));
        content.add(titreLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(descLabel);
        content.add(Box.createVerticalStrut(10));
        content.add(badge);
        b.add(content, BorderLayout.CENTER);

        // Flèche droite
        JLabel arrow = new JLabel("›");
        arrow.setFont(new Font("Segoe UI", Font.BOLD, 28));
        arrow.setForeground(UITheme.TEXT_DIM);
        arrow.setBorder(new EmptyBorder(0, 0, 0, 20));
        b.add(arrow, BorderLayout.EAST);

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(UITheme.CARD_HOVER);
                b.setBorder(BorderFactory.createLineBorder(couleur, 1, true));
                titreLabel.setForeground(couleur);
                arrow.setForeground(couleur);
                b.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(UITheme.CARD_BG);
                b.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1, true));
                titreLabel.setForeground(UITheme.TEXT);
                arrow.setForeground(UITheme.TEXT_DIM);
                b.repaint();
            }
        });

        return b;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdministrateurView(null));
    }
}