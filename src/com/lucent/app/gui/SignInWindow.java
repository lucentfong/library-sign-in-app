package com.lucent.app.gui;

import javax.swing.*;
import java.awt.*;

import static javax.swing.SwingUtilities.invokeLater;

/**
 * @author Lucent Fong (fongl@mcvts.net)
 */
public class SignInWindow {
    public static final String MENU_LAYOUT = "menu-layout";
    public static final String ADMIN_LAYOUT = "admin-layout";

    private static JPanel cardLayoutPanel;

    private SignInWindow() {}

    public static void showPanel(String panel) {
        ((CardLayout) cardLayoutPanel.getLayout()).show(cardLayoutPanel, panel);
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {

        // Initialize the cardlayout
        cardLayoutPanel = new JPanel(new CardLayout());
        cardLayoutPanel.add(new MenuPanel(), MENU_LAYOUT);
        cardLayoutPanel.add(new AdminPanel(), ADMIN_LAYOUT);

        showPanel(MENU_LAYOUT);

        // Create and set up the window.
        JFrame frame = new JFrame("Sign In - Admin");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.getContentPane().add(cardLayoutPanel, BorderLayout.CENTER);
        frame.setPreferredSize(new Dimension(800, 600));

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        invokeLater(SignInWindow::createAndShowGUI);
    }
}
