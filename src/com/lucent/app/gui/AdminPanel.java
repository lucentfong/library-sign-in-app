package com.lucent.app.gui;

import com.lucent.app.DataWriter;
import com.lucent.app.LibraryUtil;
import com.lucent.app.NameRepo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by Lucent on 7/2/2017.
 */
public class AdminPanel extends JPanel implements ActionListener {
    public static final int PADDING = 20;
    private static final String OUT_FILE = "print.csv";

    private StudentTimeTableModel model;

    private JButton returnButton;
    private JButton listButton;
    private JButton addButton;
    private JButton removeButton;

    private NameRepo repo;

    public AdminPanel() {
        repo = NameRepo.getInstance();

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING)); // padding

        model = new StudentTimeTableModel();

        JLabel label = new JLabel("Admin");
        JTable dateTable = new JTable(model);
        dateTable.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        returnButton = new JButton("Back");
        listButton = new JButton("List Hours");
        addButton = new JButton ("Add Student");
        removeButton = new JButton("Remove Student");

        returnButton.addActionListener(this);
        listButton.addActionListener(this);
        addButton.addActionListener(this);
        removeButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(returnButton);
        buttonPanel.add(listButton);
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

        add(label);
        add(new JScrollPane(dateTable));
        add(buttonPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == listButton) {
            generateStudentListFile();
        } else if (src == addButton) {
            addStudent();
        } else if (src == removeButton) {
            removeStudent();
        } else if (src == returnButton) {
            SignInWindow.showPanel(SignInWindow.MENU_LAYOUT);
        }
    }

    /**
     * Creates a file that lists the students and time each student volunteered
     */
    private void generateStudentListFile() {
        try (DataWriter writer = new DataWriter(OUT_FILE)) {
            //Outputs file with name and number of hours for given session
            writer.writeAll(repo);
            LibraryUtil.showMessage("Created new file " + OUT_FILE + " of names");
        } catch (IOException ex) {
            LibraryUtil.showError("There was an error creating " + OUT_FILE);
            ex.printStackTrace();
        }
    }

    /**
     * LibraryUtil.shows dialog to user requesting user to be added, then tries to add that student
     */
    private void addStudent() {
        String s = JOptionPane.showInputDialog(
                this,
                "Enter ID: ",
                "Add Student",
                JOptionPane.PLAIN_MESSAGE);
        if ((s != null) && (s.length() > 0)) {
            if (repo.contains(s)) {
                LibraryUtil.showError("ID already taken");
            } else {
                model.addStudent(s);
                LibraryUtil.showMessage(s + " Added");
            }
        }
    }

    /**
     * LibraryUtil.shows a dialog to the user to request student to be removed, then tries to remove that student
     */
    private void removeStudent() {
        String s = JOptionPane.showInputDialog(
                this,
                "Enter ID: ",
                "Remove Student",
                JOptionPane.PLAIN_MESSAGE);
        if ((s != null) && (s.length() > 0)) {
            if(repo.contains(s)){
                model.removeStudent(s);
                LibraryUtil.showMessage(s + " Removed");
            } else {
                LibraryUtil.showError("ID does not exist");
            }
        }
    }

}
