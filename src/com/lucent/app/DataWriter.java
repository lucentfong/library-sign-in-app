package com.lucent.app;

import java.io.*;
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
