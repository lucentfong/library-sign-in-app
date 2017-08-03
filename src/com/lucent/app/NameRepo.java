package com.lucent.app;

import java.io.*;
import java.util.*;

/**
 * Singleton class holding the name and hours for each student.  Interfaces with a file.
 *
 * @author Lucent Fong
 */
public class NameRepo {
    public static final int SIGN_IN = 0;
    public static final int SIGN_OUT = 1;
    private static final String DATE_FILE = "data/dates.csv";
    private static String dataFile;
    private Map<String, List<Long>> studentToTimes;

    private static NameRepo instance;

    static {
        loadSession(SessionManager.getSession());
    }

    /** Prevent instantiation of NameRepo outside this class */
    private NameRepo() {}

    /** @return the singleton instance of namerepo */
    public static NameRepo getInstance() {
        return instance;
    }

    /**
     * Loads the specified session in memory, throws an exception if invalid session
     * @param session the session to load
     */
    public static void loadSession(SessionManager.Session session) {
        instance = new NameRepo();
        instance.studentToTimes = new HashMap<>();
        try {
            dataFile = "data/" + session.year + session.season.toString() + ".csv";
            BufferedReader reader = new BufferedReader(new FileReader(dataFile));
            String line;
            String raw[];
            while ((line = reader.readLine()) != null) {
                raw = line.split(",");
                String name = raw[0];
                List<Long> times = new ArrayList<>();
                for (int i = 1; i < raw.length; i++)
                    times.add(Long.parseLong(raw[i]));

                instance.studentToTimes.put(name, times);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return a set of student names
     */
    public Set<String> getStudents() {
        return studentToTimes.keySet();
    }

    /**
     * Adds a name to the record if it's not already there.
     * @param name the student name
     * @return true if name already in record, false otherwise
     */
    public void addName(String name){
        if (!studentToTimes.containsKey(name)) {
            studentToTimes.put(name, new ArrayList<>());
            rewrite();
        }
    }

    /**
     * Remove student from the repo
     * @param name the student identifier
     */
    public void removeName(String name){
        if(studentToTimes.containsKey(name)){
            studentToTimes.remove(name);
            rewrite();
        }
    }

    /**
     * rewrites the name file
     */
    public void rewrite(){
        try (Writer rewriter = new PrintWriter(new FileOutputStream(dataFile, false))) {
            // Add time to the list for the student
            for (String student : studentToTimes.keySet()) {
                rewriter.write(student + ",");
                for(Long studentTime : studentToTimes.get(student)){
                    rewriter.write(studentTime + ",");
                }
                rewriter.write("\n");
            }
        } catch (FileNotFoundException e) {
            System.out.print("ERROR: File not found");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("ERROR: Error reading from file");
            e.printStackTrace();
        }
        writeDates();
    }

    /**
     *
     */
    public void writeDates(){
        try (Writer rewriter = new PrintWriter(new FileOutputStream(DATE_FILE, false))) {
            // Add time to the list for the student
            for (String student : studentToTimes.keySet()) {
                rewriter.write(student + ",");
                for(Long studentTime : studentToTimes.get(student)){
                    String date = LibraryUtil.ConvertMilliSecondsToFormattedDate(studentTime);
                    rewriter.write(date + ",");
                }
                rewriter.write("\n");
            }
        } catch (FileNotFoundException e) {
            System.out.print("ERROR: File not found");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("ERROR: Error reading from file");
            e.printStackTrace();
        }
    }

    /**
     * Returns list of times for given student name
     * @param name the student name
     * @return list of times
     */
    public List<Long> searchName(String name){
        return studentToTimes.get(name);
    }

    /**
     * Returns true if ready for sign in/out (pass in SIGN_IN or SIGN_OUT)
     * @param name the student name
     * @param sign sign in or sign out
     * @return list of times
     */
    public boolean isSignTime(String name, int sign){
        return (studentToTimes.get(name).size() % 2 == sign);
    }

    /**
     * Adds the time to the student's list of sign in/out times
     * @param time student name
     * @param name the timestamp
     */
    public void record(long time, String name) {
        if (!contains(name)) throw new IllegalArgumentException("Student doesn't exist!");
        studentToTimes.get(name).add(time);
        rewrite();
    }

    /**
     * Returns true if name repository contains name
     * @param name the student name
     * @return true if name in repository otherwise false
     */
    public boolean contains(String name) {
        return studentToTimes.containsKey(name);
    }
}
