package com.lucent.app;

import java.io.File;
import java.io.IOException;
import java.time.Month;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Manages the current session (year/season)
 * Attempts to determine the latest session based on what's in the data/ directory, otherwise creates a new
 * file in data/ with the session based on the current time.  Also allows you to change sessions.
 *
 * @author Roland Fong (rfong@princeton.edu)
 */
public class SessionManager {
    private static Session session;

    // file name format: YYYYSEASON.csv, e.g. 2018SPRING.csv
    private static final Pattern NAME_PATTERN = Pattern.compile("[0-9]+[A-Z]+\\.csv");

    private SessionManager() {}

    /**
     * A tutoring session/season, e.g. Spring 2018
     */
    public static class Session implements Comparable<Session> {

        enum Season { // Do not change the order or compareTo will break!
            SPRING, SUMMER, FALL, WINTER;

            static public Season of ( Month month ) {
                switch (month) {
                    // Java quirk: An enum switch case label must be the unqualified name of an enum.
                    // So cannot use `Month.MARCH` here, only `MARCH`.
                    case FEBRUARY: return Season.SPRING;
                    case MARCH: return Season.SPRING;
                    case APRIL: return Season.SPRING;
                    case MAY: return Season.SUMMER;
                    case JUNE: return Season.SUMMER;
                    case JULY: return Season.SUMMER;
                    case AUGUST: return Season.FALL;
                    case SEPTEMBER: return Season.FALL;
                    case OCTOBER: return Season.FALL;
                    case NOVEMBER: return Season.WINTER;
                    case DECEMBER: return Season.WINTER;
                    case JANUARY: return Season.WINTER;
                    default: throw new IllegalArgumentException(month + " not a valid month!");
                }
            }
        }

        public Season season;
        public int year; // e.g. 2018

        public Session(Season season, int year) {
            this.year = year;
            this.season = season;
        }

        @Override
        public int compareTo(Session that) {
            if (this.year < that.year) return -1;
            else if (this.year > that.year) return 1;
            else return this.season.ordinal() - that.season.ordinal(); // this is a hack
        }

        @Override
        public String toString() {
            String s = this.season.toString().toLowerCase();
            return s.substring(0,1).toUpperCase() + s.substring(1) + ", " + this.year;
        }
    }

    /** @return the current session */
    public static Session getSession() {
        if (session == null) {
            session = getLatestSession();
            System.out.println(session);
            if (session == null) {
                session = determineCurrentSession();
                createSession(session);
            }
        }
        return session;
    }

    /**
     * Changes the session, creates a new file if needed
     * @param session the session to change to
     */
    public static void changeSession(Session session) {
        List<Session> sessions = getSessionsFromFile();
        if (sessions == null || sessions.isEmpty())
            createSession(session);

        SessionManager.session = session;
    }

    /**
     * Determines the latest session based on
     * @return the latest session or null if no files
     */
    private static Session getLatestSession() {
        List<Session> sessions = getSessionsFromFile();
        if (!sessions.isEmpty()) {
            Collections.sort(sessions);
            return sessions.get(sessions.size() - 1); // latest session
        }
        return null;
    }

    /**
     * Creates the session by creating a file for it
     * @param session the session to create
     */
    private static void createSession(Session session) {
        File f = new File("data/" + session.year + session.season + ".csv");
        try {
            f.createNewFile();
        } catch (IOException e) {
            System.err.println("Unable to create new session file " + f.getAbsolutePath());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Determines what session it should be given the date
     * @return the session it should be
     */
    private static Session determineCurrentSession() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        Session.Season season = Session.Season.of(Month.of(calendar.get(Calendar.MONTH)));
        return new Session(season, year);
    }

    /** @return the sessions that there is data for based on files in data/ */
    private static List<Session> getSessionsFromFile() {
        File[] files = new File("data/").listFiles();
        List<Session> sessions = null;
        if (files != null) {
            // Scour the directory for files and determine the sessions for each file.
            sessions = Arrays.stream(files)
                    .map(SessionManager::fileToSession)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return sessions;
    }

    /**
     * Converts a file to session
     * @param file the file
     * @return the session
     */
    private static Session fileToSession(File file) {
        String name = file.getName();
        Session sess = null;
        if (NAME_PATTERN.matcher(name).find()) {
            name = name.split("\\.")[0]; // remove ".csv" extension
            Session.Season season = Session.Season.valueOf(name.substring(4));
            int year = Integer.parseInt(name.substring(0, 4));
            sess = new Session(season, year);
        }
        return sess;
    }

}
