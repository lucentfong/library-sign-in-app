package com.lucent.app;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * @author Lucent Fong
 */
public class LibraryUtil {
    private static final int MILLIS_IN_SEC = 1000;
    private static final int MINS_IN_HOUR = 60;
    private static final int SECS_IN_MIN = 60;

    public static String dateFormat = "MM-dd-yy hh:mm";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

    /**
     * miniclass to combine hours and minutes
     */
    public static class HourMinute {
        public int hours, minutes;
        public HourMinute(int hours, int minutes) {
            this.hours = hours;
            this.minutes = minutes;
        }
    }

    /**
     * Converts a list of sign in/out times to date
     * @param times
     * @return
     */
    public static HourMinute convertToHour(List<Long> times){
        int hours = 0, minutes = 0;

        for (int i = 0; i < times.size(); i += 2) {
            Long signInTime = times.get(i);
            if (i + 1 < times.size()) {
                Long signOutTime = times.get(i + 1);
                Long diff = signOutTime - signInTime;
                hours += diff / MILLIS_IN_SEC / (SECS_IN_MIN * MINS_IN_HOUR);
                minutes += diff / MILLIS_IN_SEC / SECS_IN_MIN - (hours * MINS_IN_HOUR);
            }
        }
        return new HourMinute(hours, minutes);
    }

    /**
     * converts milliseconds to date
     */
    public static String ConvertMilliSecondsToFormattedDate(Long milliSeconds){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * Shows an info dialog to the user with message
     * @param msg
     */
    public static void showMessage(String msg) {
        JOptionPane.showMessageDialog(null, msg);
    }

    /**
     * Shows an error dialog to user with message
     * @param msg
     */
    public static void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
