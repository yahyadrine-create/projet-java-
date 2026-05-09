package vue;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public final class UITheme {
    private UITheme() {}

    // ─── PALETTE SOMBRE PROFESSIONNELLE (Anthracite & Or) ─────────────────────
    public static final Color BG            = new Color(18, 20, 26);      // Fond général ultra-sombre
    public static final Color BG_SECONDARY  = new Color(24, 27, 35);      // Fond légèrement plus clair
    public static final Color CARD_BG       = new Color(30, 34, 44);      // Fond cartes
    public static final Color CARD_HOVER    = new Color(36, 41, 54);      // Carte survolée
    public static final Color PANEL_BG      = new Color(22, 25, 33);      // Fond panneaux

    public static final Color PRIMARY       = new Color(196, 160, 80);    // Or chaud principal
    public static final Color PRIMARY_DARK  = new Color(160, 125, 55);    // Or foncé (hover)
    public static final Color PRIMARY_LIGHT = new Color(196, 160, 80, 30);// Or transparent (sélection)
    public static final Color ACCENT        = new Color(82, 196, 175);    // Teal médical lumineux
    public static final Color ACCENT_DARK   = new Color(55, 160, 140);    // Teal foncé

    public static final Color SUCCESS       = new Color(72, 199, 116);    // Vert succès
    public static final Color DANGER        = new Color(220, 80, 80);     // Rouge danger
    public static final Color DANGER_DARK   = new Color(180, 55, 55);     // Rouge foncé
    public static final Color WARNING       = new Color(240, 160, 40);    // Ambre warning

    public static final Color TEXT          = new Color(230, 232, 240);   // Texte principal clair
    public static final Color TEXT_MUTED    = new Color(130, 140, 165);   // Texte secondaire
    public static final Color TEXT_DIM      = new Color(80, 90, 115);     // Texte tertiaire
    public static final Color BORDER        = new Color(48, 54, 72);      // Bordure subtile
    public static final Color BORDER_BRIGHT = new Color(70, 80, 105);     // Bordure active
    public static final Color BORDER_FOCUS  = new Color(196, 160, 80);    // Bordure focus (or)
    public static final Color SEPARATOR     = new Color(38, 43, 58);      // Séparateur

    // Aliases compatibilité
    public static final Color GREEN = ACCENT;
    public static final Color BLUE  = new Color(80, 130, 220);
    public static final Color RED   = DANGER;

    // ─── TYPOGRAPHIE ──────────────────────────────────────────────────────────
    public static final Font H1     = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font H2     = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font H3     = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font LABEL  = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font BUTTON = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font MONO   = new Font("Consolas", Font.PLAIN, 13);
    public static final Font TITLE  = new Font("Georgia", Font.BOLD, 24);

    // ─── BORDURES ─────────────────────────────────────────────────────────────
    public static Border cardBorder() {
        return new CompoundBorder(
            new LineBorder(BORDER, 1, true),
            new EmptyBorder(18, 20, 18, 20)
        );
    }

    public static Border cardBorderElevated() {
        return new CompoundBorder(
            new LineBorder(BORDER_BRIGHT, 1, true),
            new EmptyBorder(20, 22, 20, 22)
        );
    }

    public static Border sectionDividerTop() {
        return new MatteBorder(1, 0, 0, 0, SEPARATOR);
    }

    public static Border fieldBorder() {
        return BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
    }

    public static Border fieldBorderFocus() {
        return BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_FOCUS, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
    }

    // ─── LABELS ───────────────────────────────────────────────────────────────
    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(LABEL);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    public static JLabel labelBold(String text) {
        JLabel l = new JLabel(text);
        l.setFont(H3);
        l.setForeground(TEXT);
        return l;
    }

    public static JLabel labelGold(String text) {
        JLabel l = new JLabel(text);
        l.setFont(H3);
        l.setForeground(PRIMARY);
        return l;
    }

    // ─── CHAMPS TEXTE ─────────────────────────────────────────────────────────
    public static JTextField textField() {
        JTextField tf = new JTextField();
        tf.setFont(BODY);
        tf.setForeground(TEXT);
        tf.setBackground(new Color(22, 25, 34));
        tf.setCaretColor(PRIMARY);
        tf.setPreferredSize(new Dimension(0, 38));
        tf.setBorder(fieldBorder());
        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                tf.setBorder(fieldBorderFocus());
                tf.setBackground(new Color(26, 30, 42));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                tf.setBorder(fieldBorder());
                tf.setBackground(new Color(22, 25, 34));
            }
        });
        return tf;
    }

    // ─── STYLE TABLE ──────────────────────────────────────────────────────────
    public static void styleTable(JTable t) {
        t.setRowHeight(40);
        t.setFont(BODY);
        t.setBackground(CARD_BG);
        t.setForeground(TEXT);
        t.setSelectionBackground(new Color(196, 160, 80, 40));
        t.setSelectionForeground(PRIMARY);
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(true);
        t.setGridColor(SEPARATOR);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setFillsViewportHeight(true);

        // Alternance de lignes sombres
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(BODY);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                if (isSelected) {
                    setBackground(new Color(196, 160, 80, 35));
                    setForeground(PRIMARY);
                } else if (row % 2 == 0) {
                    setBackground(CARD_BG);
                    setForeground(TEXT);
                } else {
                    setBackground(new Color(26, 30, 40));
                    setForeground(TEXT);
                }
                return this;
            }
        });

        JTableHeader h = t.getTableHeader();
        h.setBackground(new Color(22, 26, 36));
        h.setForeground(PRIMARY);
        h.setFont(new Font("Segoe UI", Font.BOLD, 11));
        h.setPreferredSize(new Dimension(0, 44));
        h.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_BRIGHT));
        ((DefaultTableCellRenderer) h.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
    }

    // ─── BOUTONS ──────────────────────────────────────────────────────────────
    public static JButton primaryButton(String text) {
        return buildButton(text, PRIMARY, PRIMARY_DARK, new Color(18, 20, 26));
    }

    public static JButton secondaryButton(String text) {
        return buildButton(text, ACCENT, ACCENT_DARK, new Color(18, 20, 26));
    }

    public static JButton dangerButton(String text) {
        return buildButton(text, DANGER, DANGER_DARK, Color.WHITE);
    }

    public static JButton neutralButton(String text) {
        JButton b = new JButton(text);
        b.setFont(BUTTON);
        b.setBackground(new Color(38, 43, 58));
        b.setForeground(TEXT);
        b.setPreferredSize(new Dimension(130, 38));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_BRIGHT, 1, true),
            new EmptyBorder(0, 16, 0, 16)
        ));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(50, 57, 78));
                b.setForeground(PRIMARY);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(38, 43, 58));
                b.setForeground(TEXT);
            }
        });
        return b;
    }

    private static JButton buildButton(String text, Color base, Color hover, Color fg) {
        JButton b = new JButton(text);
        b.setBackground(base);
        b.setForeground(fg);
        b.setFont(BUTTON);
        b.setPreferredSize(new Dimension(130, 38));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent e)  { b.setBackground(base); }
            public void mousePressed(java.awt.event.MouseEvent e) {
                b.setBackground(hover.darker());
            }
        });
        return b;
    }

    // Alias compatibilité
    public static JButton button(String text, Color base, Color hover) {
        return buildButton(text, base, hover, Color.WHITE);
    }

    public static JButton button(String text, Color base) {
        return button(text, base, base.darker());
    }

    // ─── HEADER ───────────────────────────────────────────────────────────────
    public static JPanel createHeader(String title, String subtitle) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(14, 16, 22));
        header.setPreferredSize(new Dimension(0, subtitle == null ? 72 : 88));
        header.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER),
            new EmptyBorder(0, 30, 0, 30)
        ));

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);
        inner.setBorder(new EmptyBorder(subtitle == null ? 20 : 14, 0, 0, 0));

        // Barre dorée latérale gauche (simulée via titre avec décoration)
        JLabel titleLabel = new JLabel("  " + title);
        titleLabel.setFont(TITLE);
        titleLabel.setForeground(PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inner.add(titleLabel);

        if (subtitle != null) {
            JLabel subLabel = new JLabel("  " + subtitle);
            subLabel.setFont(SMALL);
            subLabel.setForeground(TEXT_MUTED);
            subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            inner.add(Box.createVerticalStrut(4));
            inner.add(subLabel);
        }

        // Ligne accent or en bas
        JPanel accent = new JPanel();
        accent.setBackground(PRIMARY);
        accent.setPreferredSize(new Dimension(0, 2));

        // Logo / badge à droite
        JLabel badge = new JLabel("PharmaPro");
        badge.setFont(new Font("Georgia", Font.ITALIC, 13));
        badge.setForeground(TEXT_DIM);
        badge.setBorder(new EmptyBorder(0, 0, 0, 8));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(badge);

        header.add(inner, BorderLayout.CENTER);
        header.add(right, BorderLayout.EAST);
        header.add(accent, BorderLayout.SOUTH);
        return header;
    }

    // ─── BADGE ────────────────────────────────────────────────────────────────
    public static JLabel badge(String text, Color bg) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(Color.WHITE);
        lbl.setBackground(bg);
        lbl.setOpaque(true);
        lbl.setBorder(new EmptyBorder(3, 10, 3, 10));
        return lbl;
    }

    // ─── UTILITAIRES ──────────────────────────────────────────────────────────
    public static void soften(JComponent c) {
        c.setOpaque(true);
        c.setBackground(CARD_BG);
        c.setBorder(cardBorder());
    }

    /**
     * Applique le look sombre global (LookAndFeel par défaut de Swing reste,
     * mais on force les couleurs système pour les composants natifs).
     */
    public static void applyGlobalDefaults() {
        UIManager.put("Panel.background", BG);
        UIManager.put("OptionPane.background", CARD_BG);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("Button.background", new Color(38, 43, 58));
        UIManager.put("Button.foreground", TEXT);
        UIManager.put("Button.font", BUTTON);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Label.font", BODY);
        UIManager.put("TextField.background", new Color(22, 25, 34));
        UIManager.put("TextField.foreground", TEXT);
        UIManager.put("TextField.caretForeground", PRIMARY);
        UIManager.put("ComboBox.background", new Color(22, 25, 34));
        UIManager.put("ComboBox.foreground", TEXT);
        UIManager.put("ComboBox.selectionBackground", new Color(196, 160, 80, 60));
        UIManager.put("ComboBox.selectionForeground", PRIMARY);
        UIManager.put("ScrollPane.background", CARD_BG);
        UIManager.put("ScrollBar.background", new Color(24, 27, 36));
        UIManager.put("ScrollBar.thumb", new Color(55, 62, 82));
        UIManager.put("ScrollBar.thumbHighlight", new Color(70, 80, 105));
        UIManager.put("ScrollBar.width", 8);
        UIManager.put("PasswordField.background", new Color(22, 25, 34));
        UIManager.put("PasswordField.foreground", TEXT);
        UIManager.put("PasswordField.caretForeground", PRIMARY);
        UIManager.put("Table.background", CARD_BG);
        UIManager.put("Table.foreground", TEXT);
        UIManager.put("Table.selectionBackground", new Color(196, 160, 80, 40));
        UIManager.put("Table.selectionForeground", PRIMARY);
        UIManager.put("TableHeader.background", new Color(22, 26, 36));
        UIManager.put("TableHeader.foreground", PRIMARY);
    }
}