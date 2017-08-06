package com.lucent.app.gui;

import com.lucent.app.DataWriter;
import com.lucent.app.LibraryUtil;
import com.lucent.app.NameRepo;
import com.lucent.app.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Admin interface, enabling managing sessions, students within those sessions, viewing sign in and out times,
 * and generating output files
 * @author Roland Fong (rfong@princeton.edu)
 * @author Lucent Fong (fongl@mcvts.net)
 */
public class AdminPanel extends JPanel implements ActionListener, ListSelectionListener, ComponentListener {

    private static final String OUT_FILE = "data/print.csv";

    private static final int PADDING = 20;
    private static final Dimension MAX_DIMEN = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("MMM dd,yyyy HH:mm");

    private DefaultListModel<SessionManager.Session> sessionModel;
    private DefaultListModel<String> studentModel;
    private DefaultListModel<String> timeModel;

    private JList<SessionManager.Session> sessionList;
    private JList<String> studentList;
    private JList<String> timeList;

    private JButton returnButton;
    private JButton listButton;
    private JButton reportButton;
    private JButton addButton;
    private JButton removeButton;
    private JButton newSessionButton;
    private JButton changeSessionButton;
    private JButton refreshButton;

    private JLabel sessionLabel;

    private NameRepo repo;

    public AdminPanel() {
        repo = NameRepo.getInstance();

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));

        sessionLabel = new JLabel();

        addComponentListener(this);

        add(new JLabel("Admin"));
        add(createTablesPanel());
        add(createButtonPanel());
        add(sessionLabel);
    }

    private JPanel createTablesPanel() {
        JPanel tablesPanel = new JPanel();
        tablesPanel.setLayout(new BoxLayout(tablesPanel, BoxLayout.X_AXIS));

        sessionModel = new DefaultListModel<>();
        studentModel = new DefaultListModel<>();
        timeModel = new DefaultListModel<>();

        sessionList = new JList<>(sessionModel);
        studentList = new JList<>(studentModel);
        timeList = new JList<>(timeModel);

        sessionList.setBorder(new TitledBorder("Sessions"));
        studentList.setBorder(new TitledBorder("Students"));
        timeList.setBorder(new TitledBorder("Sign In/Out Times"));

        sessionList.setMaximumSize(MAX_DIMEN);
        studentList.setMaximumSize(MAX_DIMEN);
        timeList.setMaximumSize(MAX_DIMEN);

        sessionList.addListSelectionListener(this);
        studentList.addListSelectionListener(this);

        tablesPanel.add(new JScrollPane(sessionList));
        tablesPanel.add(new JScrollPane(studentList));
        tablesPanel.add(new JScrollPane(timeList));

        return tablesPanel;
    }

    private JPanel createButtonPanel() {
        returnButton = new JButton("<-");
        listButton = new JButton("List Hours");
        reportButton = new JButton("Create Student Report");
        addButton = new JButton ("Add Student");
        removeButton = new JButton("Remove Student");
        newSessionButton = new JButton("New Session");
        changeSessionButton = new JButton("Change Session");
        refreshButton = new JButton("Refresh");

        returnButton.addActionListener(this);
        listButton.addActionListener(this);
        reportButton.addActionListener(this);
        addButton.addActionListener(this);
        removeButton.addActionListener(this);
        newSessionButton.addActionListener(this);
        changeSessionButton.addActionListener(this);
        refreshButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(returnButton);
        buttonPanel.add(listButton);
        buttonPanel.add(reportButton);
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(newSessionButton);
        buttonPanel.add(changeSessionButton);
        buttonPanel.add(refreshButton);

        return buttonPanel;
    }

    private void updateSessionList() {
        sessionModel.clear();
        SessionManager.getAvailableSessions().forEach(sessionModel::addElement);
    }

    private void updateStudentList() {
        studentModel.clear();
        repo.getStudents().forEach(studentModel::addElement);
    }

    private void updateTimeList() {
        timeModel.clear();
        String name = studentList.getSelectedValue();
        if (name != null) {
            repo.getStudentTimes(name).forEach(time -> timeModel.addElement(DATE_FORMATTER.format(new Date(time))));
        }
    }

    private void refresh() {
        updateSessionList();
        updateStudentList();
        updateTimeList();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == listButton) {
            generateStudentListFile();
        } else if (src == reportButton) {
            generateStudentReport();
        } else if (src == addButton) {
            addStudent();
        } else if (src == removeButton) {
            removeStudent();
        } else if (src == returnButton) {
            SignInWindow.showPanel(SignInWindow.MENU_LAYOUT);
        } else if (src == newSessionButton) {
            addNewSession();
        } else if (src == changeSessionButton) {
            changeSession();
        } else if (src == refreshButton) {
            refresh();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        Object src = e.getSource();
        if (src == sessionList) {
            SessionManager.Session newSession = sessionList.getSelectedValue();
            if (sessionList.getSelectedValue() != null) {
                SessionManager.changeSession(newSession);
                repo.loadSession(newSession);
                updateStudentList();
                if (!sessionList.isSelectionEmpty() && !studentModel.isEmpty())
                    studentList.setSelectedIndex(0);
                updateTimeList();
            }
        } else if (src == studentList) {
            String selected = studentList.getSelectedValue();
            if (selected != null) updateTimeList();
        }
    }

    /**
     * Constructs a confirm dialog to ask user to choose a season and a year to create a new session.
     * If user cancels or inputs an invalid year, does nothing.  Otherwise creates a new session in
     * {@link SessionManager} and loads the new session into {@link NameRepo}.
     */
    private void addNewSession() {
        SpinnerModel seasonModel = new SpinnerListModel(SessionManager.Session.Season.values());
        JSpinner seasonSpinner = new JSpinner(seasonModel);
        JTextField yearField = new JTextField();
        yearField.setColumns(4); // assume year only has 4 digits

        Object[] inputs = {
                "Season: ", seasonSpinner,
                "Year: ", yearField
        };

        int res = JOptionPane.showConfirmDialog(this, inputs, "Add a new session", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            SessionManager.Session.Season season = (SessionManager.Session.Season) seasonSpinner.getValue();
            int year;
            try {
                year = Integer.parseInt(yearField.getText());
            } catch (NumberFormatException e) {
                LibraryUtil.showError("Invalid year");
                return;
            }
            SessionManager.Session newSession = new SessionManager.Session(season, year);
            SessionManager.changeSession(newSession);
            repo.loadSession(newSession);

            refresh();

            updateSessionLabel();
            sessionList.setSelectedValue(SessionManager.getSession(), true);

            LibraryUtil.showMessage("Added new session: " + newSession);
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
     * Generates a file with the session and student name for selected student.
     */
    private void generateStudentReport() {
        if (!studentList.isSelectionEmpty()) {
            String student = studentList.getSelectedValue();
            java.util.List<Long> times = repo.getStudentTimes(student);
            SessionManager.Session current = sessionList.getSelectedValue();
            String filename = current.season.toString() + current.year + "-" + student + ".csv";
            try (DataWriter writer = new DataWriter(filename)) {
                writer.generateStudentReport(student, times);
                LibraryUtil.showMessage("Report for " + student + " has been generated");
            } catch (IOException e) {
                LibraryUtil.showError("Error generating report.  Contact the developer.");
                e.printStackTrace();
            }
        } else {
            LibraryUtil.showError("Please select a student.");
        }
    }

    /**
     * Shows dialog to user requesting user to be added, then tries to add that student
     */
    private void addStudent() {
        String name = JOptionPane.showInputDialog(
                this,
                "Enter ID: ",
                "Add Student",
                JOptionPane.PLAIN_MESSAGE);
        if ((name != null) && (name.length() > 0)) {
            if (repo.contains(name)) {
                LibraryUtil.showError("ID already taken");
            } else {
                repo.addName(name);
                updateStudentList();
                updateTimeList();
                LibraryUtil.showMessage(name + " Added");
            }
        }
    }

    /**
     * Shows a dialog to the user to request student to be removed, then tries to remove that student
     */
    private void removeStudent() {
        String name = studentList.getSelectedValue();
        if ((name != null)) {
            int res = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to remove " + name + "?", "Remove student", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.NO_OPTION) return;
            if(repo.contains(name)){
                repo.removeName(name);
                updateStudentList();
                updateTimeList();
                LibraryUtil.showMessage(name + " Removed");
            } else {
                LibraryUtil.showError("ID does not exist");
            }
        } else {
            LibraryUtil.showError("Please select a student to remove");
        }
    }

    /**
     * Changes the session in which the app is configured to receive sign in/out requests from students.
     */
    private void changeSession() {
        SessionManager.Session newSession = sessionList.getSelectedValue();
        SessionManager.changeSession(newSession);
        updateSessionLabel();
        sessionList.setSelectedValue(SessionManager.getSession(), true);
        LibraryUtil.showMessage("Session changed to " + newSession);
    }

    private void updateSessionLabel() {
        sessionLabel.setText("Current Session: " + SessionManager.getSession());
    }

    @Override
    public void componentResized(ComponentEvent componentEvent) { }

    @Override
    public void componentMoved(ComponentEvent componentEvent) { }

    @Override
    public void componentShown(ComponentEvent componentEvent) {
        updateSessionLabel();
        refresh();
        if (!sessionList.isSelectionEmpty() && !studentModel.isEmpty())
            studentList.setSelectedIndex(0);
        sessionList.setSelectedValue(SessionManager.getSession(), true);
    }

    @Override
    public void componentHidden(ComponentEvent componentEvent) { }
}
