package com.lucent.app;

import java.io.*;
import java.util.Calendar;
import java.util.List;

/**
 * @author Lucent Fong (fongl@mcvts.net)
 */
public class DataWriter implements Closeable {
    private Writer writer;

    public DataWriter(String filename) {
        try {
            this.writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filename), "utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a report for a student's sign in/out times and total hours in csv format
     * @param name student name
     * @param times list of sign in/out times
     */
    public void generateStudentReport(String name, List<Long> times) throws IOException {
        writer.write(",,," + name + " Volunteer Report,,,\n,\n");
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < times.size(); i += 2) {
            Long signInTime = times.get(i);
            if (i + 1 < times.size()) {
                Long signOutTime = times.get(i + 1);

                calendar.setTimeInMillis(signInTime);
                writer.write(",," + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.DATE) + ",");
                writer.write(calendar.get(Calendar.HOUR) + ":" +
                        calendar.get(Calendar.MINUTE) +
                        ampmToString(calendar.get(Calendar.AM_PM)) + ",-,");

                calendar.setTimeInMillis(signOutTime);
                writer.write(calendar.get(Calendar.HOUR) + ":" +
                        calendar.get(Calendar.MINUTE) +
                        ampmToString(calendar.get(Calendar.AM_PM)) + ",\n");
            }
        }
        writer.write(",\n");
        LibraryUtil.HourMinute hm = LibraryUtil.convertToHour(times);
        writer.write(",,,Total Time:," + hm.hours + "h," + hm.minutes + "m\n");
        writer.flush();
    }

    /**
     * Converts a Calendar.get(Calendar.AM_PM) int to string
     * @param ampm 0 = am, 1 = pm
     * @return string for AM or PM
     */
    private static String ampmToString(int ampm) {
        switch (ampm) {
            case 0: return "AM";
            case 1: return "PM";
        }
        return "";
    }

    /**
     * Writes all the students in repo to a specified output file
     * @param repo
     */
    public void writeAll(NameRepo repo) throws IOException {
        writer.write(",,,Monroe Library Hours\n\n,,,ID,Hours\n");
        for (String name : repo.getStudents()) {
            writeStudent(name, repo.getStudentTimes(name));
        }
    }

    /**
     * Writes the student name and total time the student has been volunteering to file
     * @param name student name
     * @param times the list of sign in/out times
     */
    private void writeStudent(String name, List<Long> times) throws IOException {
        LibraryUtil.HourMinute hm = LibraryUtil.convertToHour(times);

        writer.write(",,," + name + ", " + hm.hours + " hours, " + hm.minutes + " minutes\n");
        writer.flush();
    }

    /**
     * Required to be called with DataWriter is finished being used.
     */
    @Override
    public void close() throws IOException {
        writer.close();
    }
}
