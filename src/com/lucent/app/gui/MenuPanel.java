package com.lucent.app.gui;

import com.lucent.app.LibraryUtil;
import com.lucent.app.NameRepo;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created by Lucent on 7/2/2017.
 */
public class MenuPanel extends JPanel implements ActionListener {

    private JButton signInButton;
    private JButton signOutButton;
    private JButton adminButton;
    private JLabel libraryImage;
    private JLabel titleImage;
    private NameRepo repo;

    private static final String PASSWORD = "library123";

    public MenuPanel() {
        this.repo = NameRepo.getInstance();

        titleImage = new JLabel(new ImageIcon("Library Title.png"),JLabel.LEFT);
        signInButton = new JButton("Sign In");
        signOutButton = new JButton("Sign Out");
        adminButton = new JButton("Admin");
        libraryImage = new JLabel(new ImageIcon("library.jpg"),JLabel.CENTER);

        adminButton.addActionListener(this);
        signInButton.addActionListener(this);
        signOutButton.addActionListener(this);

        this.add(titleImage);
        this.add(signInButton);
        this.add(signOutButton);
        this.add(adminButton);
        this.add(libraryImage);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == adminButton) {
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
        } else if (src == signInButton) { //LOGIN
            String s = JOptionPane.showInputDialog(
                    this,
                    "Enter ID: ",
                    "Sign In",
                    JOptionPane.PLAIN_MESSAGE);

            //If a string was returned, say so.
            if ((s != null) && (s.length() > 0)) {
                //System.out.println(s);
                if (repo.contains(s)) {
                    if(repo.isSignTime(s, NameRepo.SIGN_IN)) {
                        long t = System.currentTimeMillis();
                        //System.out.println(t);
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
        } else if (src == signOutButton) { // LOGOUT
            String s = JOptionPane.showInputDialog(
                    this,
                    "Enter ID: ",
                    "Sign Out",
                    JOptionPane.PLAIN_MESSAGE);
            if ((s != null) && (s.length() > 0)) {
                //System.out.println(s);
                if (repo.contains(s)) {
                    if(repo.isSignTime(s, NameRepo.SIGN_OUT)) {
                        long t = System.currentTimeMillis();
                        //System.out.println(t);
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
    }
}