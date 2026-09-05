package view;

import controller.MenuCommand;
import controller.MenuController;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Level-selection panel.
 *
 * Shows one clickable card per level. Selecting a card highlights it, updates
 * the info box and fires the matching SELECT_LVL command so the controller
 * pre-loads that level into the engine. The PLAY button then starts a run on
 * whichever level is currently selected.
 */
public class LevelMenu extends JPanel implements Menu {

    private static final long serialVersionUID = 1L;

    /** Display name of each level, in card order. */
    private static final String[] LEVEL_NAMES = { "LEVEL 1", "LEVEL 2" };

    /** Command fired when the card at the same index is selected. */
    private static final MenuCommand[] LEVEL_CMDS = {
        MenuCommand.SELECT_LVL1, MenuCommand.SELECT_LVL2
    };

    /** Short description shown in the info box for each level. */
    private static final String[] LEVEL_DESCS = { "Beginner difficulty", "Advanced pattern" };

    private final MenuController controller;
    private final JLabel         levelNameLabel;
    private final JLabel         levelDescLabel;

    private LevelCard[] cards;
    private int         selectedIndex = 0;

    /**
     * Builds the panel and wires every interaction to the controller.
     *
     * @param controller the menu controller that handles cards and buttons
     */
    public LevelMenu(MenuController controller) {
        this.controller = controller;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setFocusable(true);
        setPreferredSize(new Dimension(RenderConstants.WIDTH, RenderConstants.HEIGHT));

        JLabel title = MenuStyle.makeLabel("SELECT LEVEL",
                                           MenuStyle.SUBTITLE_FONT, new Color(200, 220, 255));

        levelNameLabel = MenuStyle.makeLabel(" ", MenuStyle.SUBTITLE_FONT, MenuStyle.ACCENT);
        levelDescLabel = MenuStyle.makeLabel(" ", MenuStyle.SMALL_FONT, new Color(180, 180, 200));

        JPanel infoPanel = buildInfoPanel();
        JPanel cardsRow  = buildCardsRow();

        JButton playBtn = MenuStyle.makeButton("PLAY");
        playBtn.setActionCommand(MenuCommand.START_SELECTED.name());
        playBtn.addActionListener(controller);

        JButton backBtn = MenuStyle.makeButton("BACK TO MENU");
        backBtn.setActionCommand(MenuCommand.BACK_TO_MENU.name());
        backBtn.addActionListener(controller);

        add(MenuStyle.vGlue());
        add(title);
        add(MenuStyle.vSpace(22));
        add(cardsRow);
        add(MenuStyle.vSpace(16));
        add(infoPanel);
        add(MenuStyle.vSpace(28));
        add(playBtn);
        add(MenuStyle.vSpace(12));
        add(backBtn);
        add(MenuStyle.vGlue());

        updateInfo(selectedIndex);
    }

    // -------------------------------------------------------------------------
    // Selection logic
    // -------------------------------------------------------------------------

    /**
     * Selects the card at the given index and asks the controller to pre-load
     * the matching level.
     *
     * @param idx zero-based index of the card to select
     */
    private void selectCard(int idx) {
        selectedIndex = idx;
        for (int i = 0; i < cards.length; i++) {
            cards[i].setSelected(i == idx);
        }
        updateInfo(idx);
        controller.actionPerformed(new ActionEvent(
                this, ActionEvent.ACTION_PERFORMED, LEVEL_CMDS[idx].name()));
    }

    /**
     * Updates the info box to describe the selected level.
     *
     * @param idx zero-based index of the selected level
     */
    private void updateInfo(int idx) {
        levelNameLabel.setText(LEVEL_NAMES[idx]);
        levelDescLabel.setText(LEVEL_DESCS[idx]);
    }

    // -------------------------------------------------------------------------
    // Panel builders
    // -------------------------------------------------------------------------

    /**
     * Builds the horizontal row of level cards, each firing
     * selectCard(int) when clicked.
     *
     * @return the configured row panel
     */
    private JPanel buildCardsRow() {
        cards = new LevelCard[LEVEL_NAMES.length];

        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        row.setMaximumSize(new Dimension(RenderConstants.WIDTH, 110));

        row.add(Box.createHorizontalGlue());
        for (int i = 0; i < LEVEL_NAMES.length; i++) {
            final int idx = i;
            cards[i] = new LevelCard(LEVEL_NAMES[i], LEVEL_DESCS[i], i == selectedIndex);
            cards[i].addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { selectCard(idx); }
            });
            row.add(cards[i]);
            if (i < LEVEL_NAMES.length - 1) {
                row.add(Box.createHorizontalStrut(18));
            }
        }
        row.add(Box.createHorizontalGlue());
        return row;
    }

    /**
     * Builds the rounded box showing name and description of the selected level.
     *
     * @return the configured info panel
     */
    private JPanel buildInfoPanel() {
        JPanel panel = new JPanel() {

            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 28, 60, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(255, 165, 0, 80));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override public boolean isOpaque() { return false; }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(360, 80));
        panel.setPreferredSize(new Dimension(360, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        panel.add(levelNameLabel);
        panel.add(MenuStyle.vSpace(4));
        panel.add(levelDescLabel);
        return panel;
    }

    // -------------------------------------------------------------------------
    // Inner class: LevelCard
    // -------------------------------------------------------------------------

    /**
     * A single clickable tile in the level row. It tracks hover and selection
     * state and paints itself entirely, with no Swing decoration.
     */
    private static class LevelCard extends JPanel {

        private static final long serialVersionUID = 1L;

        private static final int CARD_WIDTH  = 200;
        private static final int CARD_HEIGHT = 100;

        private final String name;
        private final String description;

        private boolean selected;
        private boolean hovered;

        /**
         * Creates a level card.
         *
         * @param name        level display name
         * @param description short description
         * @param selected    whether the card starts selected
         */
        LevelCard(String name, String description, boolean selected) {
            this.name        = name;
            this.description = description;
            this.selected    = selected;
            setOpaque(false);
            setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
            setMaximumSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
            });
        }

        /**
         * Updates the selection state and repaints.
         *
         * @param selected true to highlight this card
         */
        void setSelected(boolean selected) {
            this.selected = selected;
            repaint();
        }

        /**
         * Paints background, border and text according to the current state, plus
         * a small accent dot on the selected card.
         *
         * @param g the graphics context provided by Swing
         */
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            Color background = selected ? new Color(60, 45, 10)
                             : hovered  ? new Color(45, 40, 75)
                                        : new Color(30, 28, 55);
            g2.setColor(background);
            g2.fillRoundRect(0, 0, w, h, 16, 16);

            Color border = selected ? MenuStyle.ACCENT
                         : hovered  ? new Color(150, 120, 60)
                                    : new Color(60, 58, 100);
            g2.setColor(border);
            g2.setStroke(new BasicStroke(selected ? 2.5f : 1.5f));
            g2.drawRoundRect(1, 1, w - 3, h - 3, 16, 16);

            g2.setFont(MenuStyle.BTN_FONT);
            g2.setColor(selected ? MenuStyle.ACCENT : Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(name, (w - fm.stringWidth(name)) / 2, 36);

            g2.setFont(MenuStyle.SMALL_FONT);
            g2.setColor(new Color(160, 160, 190));
            fm = g2.getFontMetrics();
            g2.drawString(description, (w - fm.stringWidth(description)) / 2, 66);

            if (selected) {
                g2.setColor(MenuStyle.ACCENT);
                g2.fillOval(w / 2 - 4, h - 14, 8, 8);
            }
            g2.dispose();
        }
    }

    // -------------------------------------------------------------------------
    // Panel background and lifecycle
    // -------------------------------------------------------------------------

    /**
     * Paints the dark gradient background of this panel.
     *
     * @param g the graphics context provided by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        MenuStyle.paintStandardBg(g, getWidth(), getHeight(),
                new Color(12, 12, 40), new Color(22, 10, 50));
        super.paintComponent(g);
    }

    /**
     * Re-applies the last selection, which also re-loads that level into the
     * engine, and takes keyboard focus.
     */
    @Override
    public void onShow() {
        selectCard(selectedIndex);
        requestFocusInWindow();
    }
}
