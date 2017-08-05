package com.lucent.app.gui;

import com.lucent.app.LibraryUtil;
import com.lucent.app.NameRepo;
import com.lucent.app.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Date;

/**
 * @author Lucent Fong (Fongl@mcvts.net)
 */
public class MenuPanel extends JPanel implements ActionListener, ComponentListener {
    private static final int PADDING = 20;

    private JButton signInButton;
    private JButton signOutButton;
    private JButton adminButton;
    private JLabel sessionLabel;
    private NameRepo repo;

    private static final String PASSWORD = "library123";

    public MenuPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));

        repo = NameRepo.getInstance();

        JLabel libraryImage = new JLabel(new ImageIcon("img/library.jpg"));
        libraryImage.setAlignmentX(CENTER_ALIGNMENT);

        sessionLabel = new JLabel("Current Session: " + SessionManager.getSession());
        sessionLabel.setAlignmentX(CENTER_ALIGNMENT);

        add(createTopPanel());
        add(libraryImage);
        add(sessionLabel);
    }

    private JPanel createTopPanel() {
        JLabel titleImage = new JLabel(new ImageIcon("img/Library Title.png"), JLabel.LEFT);

        signInButton = new JButton("Sign In");
        signOutButton = new JButton("Sign Out");
        adminButton = new JButton("Admin");

        adminButton.addActionListener(this);
        signInButton.addActionListener(this);
        signOutButton.addActionListener(this);

        addComponentListener(this);

        JPanel topBar = new JPanel();
        topBar.add(titleImage);
        topBar.add(signInButton);
        topBar.add(signOutButton);
        topBar.add(adminButton);

        return topBar;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == adminButton) admin();
        else if (src == signInButton) signIn();
        else if (src == signOutButton) signOut();
    }

    /**
     * Prompt for username and sign the student in if exists
     */
    private void signIn() {
        String s = JOptionPane.showInputDialog(
                this,
                "Enter ID: ",
                "Sign In",
                JOptionPane.PLAIN_MESSAGE);

        //If a string was returned, say so.
        if ((s != null) && (s.length() > 0)) {
            if (repo.contains(s)) {
                if(repo.isSignTime(s, NameRepo.SIGN_IN)) {
                    long t = System.currentTimeMillis();
                    repo.record(t, s);
                    Date date = new Date(t);
                    LibraryUtil.showMessage(s + " Logged In @ " + date);
                } else {
                    LibraryUtil.showError("Already Logged in");
                }
            } else {
                LibraryUtil.showError("Invalid ID");
            }
        }
    }

    /**
     * Prompt user for username and sign user out if exists
     */
    private void signOut() {
        String s = JOptionPane.showInputDialog(
                this,
                "Enter ID: ",
                "Sign Out",
                JOptionPane.PLAIN_MESSAGE);
        if ((s != null) && (s.length() > 0)) {
            if (repo.contains(s)) {
                if(repo.isSignTime(s, NameRepo.SIGN_OUT)) {
                    long t = System.currentTimeMillis();
                    repo.record(t, s);
                    Date date = new Date(t);
                    LibraryUtil.showMessage(s + " Logged Out @ " + date);
                }
                else {
                    LibraryUtil.showError("Not Logged In");
                }
            } else {
                LibraryUtil.showError("Invalid ID");
            }
        }
    }

    /**
     * Prompt for admin password and sign in if correct
     */
    private void admin() {
        String s = JOptionPane.showInputDialog(
                this,
                "Enter Password: ",
                "Admin",
                JOptionPane.PLAIN_MESSAGE);
        if (s.equals(PASSWORD)) {
            SignInWindow.showPanel(SignInWindow.ADMIN_LAYOUT);
        } else {
            LibraryUtil.showError("Invalid Password");
        }
    }

    @Override
    public void componentResized(ComponentEvent componentEvent) { }

    @Override
    public void componentMoved(ComponentEvent componentEvent) { }

    @Override
    public void componentShown(ComponentEvent componentEvent) {
        sessionLabel.setText("Current Session: " + SessionManager.getSession());
    }

    @Override
    public void componentHidden(ComponentEvent componentEvent) { }
}
