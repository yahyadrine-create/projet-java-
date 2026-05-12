package vue;

import controleur.AdministrateurControle;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.geom.*;
import java.sql.Connection;
import java.util.List;

public class AdministrateurView extends JFrame {

    private AdministrateurControle controle;

    public AdministrateurView(Connection connection) {
        this.controle = new AdministrateurControle(connection);
        UITheme.applyGlobalDefaults();

        setTitle("PharmaPro — Tableau de bord Administrateur");
        setSize(1200, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG);
        setLayout(new BorderLayout());

        add(UITheme.createHeader("Tableau de bord", "Administrateur — Gestion complète du système"), BorderLayout.NORTH);
        add(creerOnglets(), BorderLayout.CENTER);
        add(creerFooter(), BorderLayout.SOUTH);

        setVisible(true);
    }

    // =========================================================================
    // ONGLETS
    // =========================================================================
    private JTabbedPane creerOnglets() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(UITheme.BG);
        tabs.setForeground(UITheme.TEXT);
        tabs.setFont(UITheme.H3);
        tabs.addTab("   Gestion   ", creerMenu());
        tabs.addTab("   Analyses  ", creerDashboard());
        return tabs;
    }

    // =========================================================================
    // MENU — grille 2×3 (6 tuiles)
    // =========================================================================
    private JPanel creerMenu() {
        // 2 lignes × 3 colonnes pour loger la nouvelle tuile Utilisateurs
        JPanel menuPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        menuPanel.setBackground(UITheme.BG);
        menuPanel.setBorder(new EmptyBorder(32, 42, 32, 42));

        JButton btnClients      = creerTuile("Gestion des clients",
                "Ajouter, modifier et consulter les clients",        "👥", UITheme.PRIMARY);
        JButton btnMeds         = creerTuile("Gestion des médicaments",
                "Catalogue et informations produits",                 "💊", UITheme.ACCENT);
        JButton btnStock        = creerTuile("Gestion des stocks",
                "Niveaux de stock et alertes critiques",              "📦", UITheme.WARNING);
        JButton btnOrdonnances  = creerTuile("Gestion des ordonnances",
                "Créer et gérer les ordonnances",                     "🧾", new Color(160, 90, 200));
        JButton btnUtilisateurs = creerTuile("Gestion des utilisateurs",
                "Comptes pharmaciens et administrateurs",             "🔑", new Color(80, 160, 220));
        JButton btnLogout       = creerTuile("Déconnexion",
                "Quitter la session en cours",                        "🔒", UITheme.DANGER);

        menuPanel.add(btnClients);
        menuPanel.add(btnMeds);
        menuPanel.add(btnStock);
        menuPanel.add(btnOrdonnances);
        menuPanel.add(btnUtilisateurs);
        menuPanel.add(btnLogout);

        btnClients.addActionListener(e      -> new ClientView(controle.getConnection()).setVisible(true));
        btnMeds.addActionListener(e         -> new MedicamentView(controle.getConnection()).setVisible(true));
        btnStock.addActionListener(e        -> new MedicamentView(controle.getConnection()).setVisible(true));
        btnOrdonnances.addActionListener(e  -> new OrdonnanceView(controle.getConnection()).setVisible(true));
        btnUtilisateurs.addActionListener(e -> new UtilisateurView(controle.getConnection()).setVisible(true));
        btnLogout.addActionListener(e       -> confirmerDeconnexion());

        return menuPanel;
    }

    // =========================================================================
    // DASHBOARD
    // =========================================================================
    private JPanel creerDashboard() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UITheme.BG);
        root.setBorder(new EmptyBorder(18, 22, 18, 22));

        int    nbMeds  = controle.getNombreMedicaments();
        int    nbClt   = controle.getNombreClients();
        int    nbOrd   = controle.getNombreOrdonnances();
        double caTotal = controle.getCATotal();

        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 16, 0));
        kpiRow.setOpaque(false);
        kpiRow.setBorder(new EmptyBorder(0, 0, 18, 0));
        kpiRow.add(creerKpiCard("Médicaments",  String.valueOf(nbMeds),
                "références en catalogue",   UITheme.ACCENT));
        kpiRow.add(creerKpiCard("Clients",      String.valueOf(nbClt),
                "clients enregistrés",       UITheme.PRIMARY));
        kpiRow.add(creerKpiCard("Ordonnances",  String.valueOf(nbOrd),
                "ordonnances émises",        new Color(160, 90, 200)));
        kpiRow.add(creerKpiCard("CA Total",     String.format("%.2f DT", caTotal),
                "chiffre d'affaires cumulé", UITheme.SUCCESS));

        List<String[]> topVentes = controle.getTopVentes(5);
        List<String[]> caParMois = controle.getCAParMois();

        JPanel chartsRow = new JPanel(new GridLayout(1, 2, 18, 0));
        chartsRow.setOpaque(false);
        chartsRow.add(creerCardGraphique(
                "Top 5 Médicaments les plus vendus",
                creerGraphiqueTopVentes(topVentes)));
        chartsRow.add(creerCardGraphique(
                "Chiffre d'affaires par mois",
                creerGraphiqueCA(caParMois)));

        root.add(kpiRow,    BorderLayout.NORTH);
        root.add(chartsRow, BorderLayout.CENTER);
        return root;
    }

    // =========================================================================
    // FOOTER — bouton Déconnexion stylisé (rouge, arrondi, bord droit)
    // =========================================================================
    private Component creerFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(14, 16, 22));
        footer.setBorder(BorderFactory.createCompoundBorder(
            UITheme.sectionDividerTop(),
            new EmptyBorder(10, 20, 10, 20)
        ));

        // Libellé version à gauche
        JLabel version = new JLabel("PharmaPro v1.0  •  Administrateur");
        version.setFont(UITheme.SMALL);
        version.setForeground(UITheme.TEXT_DIM);
        footer.add(version, BorderLayout.WEST);
        return footer;
    }

    // =========================================================================
    // DÉCONNEXION
    // =========================================================================
    private void confirmerDeconnexion() {
        int ok = JOptionPane.showConfirmDialog(this,
                "Voulez-vous vous déconnecter ?", "Déconnexion",
                JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            dispose();
            new LoginView(controle.getConnection()).setVisible(true);
        }
    }

    // =========================================================================
    // CARTE KPI
    // =========================================================================
    private JPanel creerKpiCard(String titre, String valeur, String sous, Color couleur) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                new EmptyBorder(18, 20, 18, 20)));

        JPanel wrap = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(couleur);
                g.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
            }
        };
        wrap.setOpaque(false);

        JLabel lTitre = new JLabel(titre);
        lTitre.setFont(UITheme.SMALL);
        lTitre.setForeground(UITheme.TEXT_MUTED);

        JLabel lVal = new JLabel(valeur);
        lVal.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lVal.setForeground(couleur);

        JLabel lSous = new JLabel(sous);
        lSous.setFont(UITheme.SMALL);
        lSous.setForeground(UITheme.TEXT_DIM);

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        inner.setBorder(new EmptyBorder(0, 10, 0, 0));
        inner.add(lTitre);
        inner.add(Box.createVerticalStrut(6));
        inner.add(lVal);
        inner.add(Box.createVerticalStrut(3));
        inner.add(lSous);

        wrap.add(inner, BorderLayout.CENTER);
        card.add(wrap, BorderLayout.CENTER);
        return card;
    }

    // =========================================================================
    // WRAPPER CARTE GRAPHIQUE
    // =========================================================================
    private JPanel creerCardGraphique(String titre, JComponent graphique) {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(UITheme.cardBorder());

        JLabel lTitre = UITheme.labelGold(titre);
        lTitre.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, UITheme.BORDER),
                new EmptyBorder(0, 0, 12, 0)));

        card.add(lTitre,    BorderLayout.NORTH);
        card.add(graphique, BorderLayout.CENTER);
        return card;
    }

    // =========================================================================
    // GRAPHIQUE 1 : BARRES HORIZONTALES — TOP 5 VENTES
    // =========================================================================
    private JComponent creerGraphiqueTopVentes(final List<String[]> data) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int W = getWidth(), H = getHeight();
                int padL = 160, padR = 70, padT = 20, padB = 30;
                int chartW = W - padL - padR;
                int chartH = H - padT - padB;

                if (data == null || data.isEmpty()) {
                    g2.setColor(UITheme.TEXT_MUTED);
                    g2.setFont(UITheme.BODY);
                    g2.drawString("Aucune vente enregistrée.", padL, H / 2);
                    return;
                }

                double maxVal = 1;
                for (String[] row : data) {
                    try { maxVal = Math.max(maxVal, Double.parseDouble(row[1])); }
                    catch (Exception ignored) {}
                }

                Color[] palette = {
                    UITheme.PRIMARY, new Color(180, 140, 65),
                    UITheme.ACCENT,  new Color(65, 170, 155), UITheme.WARNING
                };

                int n = data.size();
                int barSpacing = 12;
                int barH = (chartH - barSpacing * (n - 1)) / Math.max(n, 1);

                for (int i = 0; i < n; i++) {
                    String nom = data.get(i)[0];
                    double val;
                    try { val = Double.parseDouble(data.get(i)[1]); } catch (Exception e) { val = 0; }

                    int y    = padT + i * (barH + barSpacing);
                    int barW = (int) (val / maxVal * chartW);
                    Color c  = palette[i % palette.length];

                    g2.setColor(new Color(40, 45, 60));
                    g2.fillRoundRect(padL, y + barH / 4, chartW, barH / 2, 6, 6);

                    if (barW > 0) {
                        g2.setPaint(new GradientPaint(padL, y, c.darker(), padL + barW, y, c));
                        g2.fillRoundRect(padL, y + barH / 4, barW, barH / 2, 6, 6);
                        g2.setColor(new Color(255, 255, 255, 30));
                        g2.fillRoundRect(padL + barW - 6, y + barH / 4, 6, barH / 2, 6, 6);
                    }

                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    g2.setColor(UITheme.TEXT_MUTED);
                    String shortNom = nom.length() > 18 ? nom.substring(0, 16) + "…" : nom;
                    FontMetrics fm  = g2.getFontMetrics();
                    g2.drawString(shortNom,
                            padL - fm.stringWidth(shortNom) - 10,
                            y + barH / 2 + fm.getAscent() / 2);

                    g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    g2.setColor(c);
                    String valStr = String.valueOf((long) val);
                    g2.drawString(valStr,
                            padL + barW + 8,
                            y + barH / 2 + g2.getFontMetrics().getAscent() / 2);
                }

                g2.setColor(UITheme.BORDER);
                g2.setStroke(new BasicStroke(1));
                g2.drawLine(padL, padT + chartH, padL + chartW, padT + chartH);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(UITheme.TEXT_DIM);
                g2.drawString("Quantité vendue", padL + chartW / 2 - 35, H - 4);
            }
        };
        panel.setBackground(UITheme.CARD_BG);
        panel.setOpaque(true);
        return panel;
    }

    // =========================================================================
    // GRAPHIQUE 2 : COURBE LISSÉE — CA PAR MOIS
    // =========================================================================
    private JComponent creerGraphiqueCA(final List<String[]> data) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int W = getWidth(), H = getHeight();
                int padL = 68, padR = 24, padT = 22, padB = 42;
                int chartW = W - padL - padR;
                int chartH = H - padT - padB;

                if (data == null || data.isEmpty()) {
                    g2.setColor(UITheme.TEXT_MUTED);
                    g2.setFont(UITheme.BODY);
                    g2.drawString("Aucune donnée de vente disponible.", padL + 10, H / 2);
                    return;
                }

                double maxVal = 1;
                for (String[] row : data) {
                    try { maxVal = Math.max(maxVal, Double.parseDouble(row[1])); }
                    catch (Exception ignored) {}
                }

                int n = data.size();

                for (int i = 0; i <= 4; i++) {
                    int y = padT + chartH - (int) ((double) i / 4 * chartH);
                    g2.setColor(UITheme.SEPARATOR);
                    g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                            0, new float[]{4, 6}, 0));
                    g2.drawLine(padL, y, padL + chartW, y);
                    double lv = (double) i / 4 * maxVal;
                    String lbl = lv >= 1000
                            ? String.format("%.0fk", lv / 1000)
                            : String.format("%.0f", lv);
                    g2.setStroke(new BasicStroke(1));
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    g2.setColor(UITheme.TEXT_DIM);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(lbl, padL - fm.stringWidth(lbl) - 6, y + fm.getAscent() / 2);
                }
                g2.setStroke(new BasicStroke(1));

                int[] xs = new int[n];
                int[] ys = new int[n];
                for (int i = 0; i < n; i++) {
                    double val;
                    try { val = Double.parseDouble(data.get(i)[1]); } catch (Exception e) { val = 0; }
                    xs[i] = padL + (n == 1 ? chartW / 2 : (int) ((double) i / (n - 1) * chartW));
                    ys[i] = padT + chartH - (int) (val / maxVal * chartH);
                }

                if (n >= 2) {
                    GeneralPath area = new GeneralPath();
                    area.moveTo(xs[0], padT + chartH);
                    area.lineTo(xs[0], ys[0]);
                    for (int i = 1; i < n; i++) {
                        double cpX = (xs[i - 1] + xs[i]) / 2.0;
                        area.curveTo(cpX, ys[i - 1], cpX, ys[i], xs[i], ys[i]);
                    }
                    area.lineTo(xs[n - 1], padT + chartH);
                    area.closePath();
                    g2.setPaint(new GradientPaint(
                            0, padT,          new Color(196, 160, 80, 70),
                            0, padT + chartH, new Color(196, 160, 80, 0)));
                    g2.fill(area);

                    GeneralPath curve = new GeneralPath();
                    curve.moveTo(xs[0], ys[0]);
                    for (int i = 1; i < n; i++) {
                        double cpX = (xs[i - 1] + xs[i]) / 2.0;
                        curve.curveTo(cpX, ys[i - 1], cpX, ys[i], xs[i], ys[i]);
                    }
                    g2.setColor(UITheme.PRIMARY);
                    g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.draw(curve);
                    g2.setStroke(new BasicStroke(1));
                }

                for (int i = 0; i < n; i++) {
                    g2.setColor(new Color(196, 160, 80, 40));
                    g2.fillOval(xs[i] - 7, ys[i] - 7, 14, 14);
                    g2.setColor(UITheme.PRIMARY);
                    g2.fillOval(xs[i] - 4, ys[i] - 4, 8, 8);
                    g2.setColor(UITheme.CARD_BG);
                    g2.fillOval(xs[i] - 2, ys[i] - 2, 4, 4);

                    String mois = data.get(i)[0];
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    g2.setColor(UITheme.TEXT_MUTED);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(mois,
                            xs[i] - fm.stringWidth(mois) / 2,
                            padT + chartH + 16);
                }

                g2.setColor(UITheme.BORDER_BRIGHT);
                g2.drawLine(padL, padT, padL, padT + chartH);
                g2.drawLine(padL, padT + chartH, padL + chartW, padT + chartH);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(UITheme.TEXT_DIM);
                g2.drawString("DT", 4, padT + 12);
            }
        };
        panel.setBackground(UITheme.CARD_BG);
        panel.setOpaque(true);
        return panel;
    }

    // =========================================================================
    // TUILE MENU
    // =========================================================================
    private JButton creerTuile(String titre, String description, String icone, Color couleur) {
        JButton b = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(couleur);
                g2.fillRoundRect(0, 0, 5, getHeight(), 4, 4);
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

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(22, 24, 22, 20));

        JLabel iconeLabel = new JLabel(icone);
        iconeLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titreLabel = new JLabel(titre);
        titreLabel.setFont(UITheme.H3);
        titreLabel.setForeground(UITheme.TEXT);
        titreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(UITheme.SMALL);
        descLabel.setForeground(UITheme.TEXT_MUTED);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel badge = new JLabel("  \u25CF  ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 8));
        badge.setForeground(couleur);
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(iconeLabel);
        content.add(Box.createVerticalStrut(12));
        content.add(titreLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(descLabel);
        content.add(Box.createVerticalStrut(8));
        content.add(badge);
        b.add(content, BorderLayout.CENTER);

        JLabel arrow = new JLabel("\u203A");
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
        SwingUtilities.invokeLater(() ->
                new AdministrateurView(util.SingletonConnection.getInstance()));
    }
}