package com.medievallords.carbyne.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {

    private static Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?"
                    + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?"
                    + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?",
            Pattern.CASE_INSENSITIVE);

    public static String removeTimePattern(String input) {
        return timePattern.matcher(input).replaceFirst("").trim();
    }

    public static long parseDateDiff(String time, boolean future) throws Exception {
        Matcher m = timePattern.matcher(time);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        boolean found = false;
        while (m.find()) {
            if (m.group() == null || m.group().isEmpty())
                continue;
            for (int i = 0; i < m.groupCount(); i++)
                if (m.group(i) != null && !m.group(i).isEmpty()) {
                    found = true;
                    break;
                }
            if (found) {
                if (m.group(1) != null && !m.group(1).isEmpty())
                    years = Integer.parseInt(m.group(1));
                if (m.group(2) != null && !m.group(2).isEmpty())
                    months = Integer.parseInt(m.group(2));
                if (m.group(3) != null && !m.group(3).isEmpty())
                    weeks = Integer.parseInt(m.group(3));
                if (m.group(4) != null && !m.group(4).isEmpty())
                    days = Integer.parseInt(m.group(4));
                if (m.group(5) != null && !m.group(5).isEmpty())
                    hours = Integer.parseInt(m.group(5));
                if (m.group(6) != null && !m.group(6).isEmpty())
                    minutes = Integer.parseInt(m.group(6));
                if (m.group(7) != null && !m.group(7).isEmpty())
                    seconds = Integer.parseInt(m.group(7));
                break;
            }
        }
        if (!found)
            throw new Exception("Illegal Date");
        Calendar c = new GregorianCalendar();
        if (years > 0)
            c.add(Calendar.YEAR, years * (future ? 1 : -1));
        if (months > 0)
            c.add(Calendar.MONTH, months * (future ? 1 : -1));
        if (weeks > 0)
            c.add(Calendar.WEEK_OF_YEAR, weeks * (future ? 1 : -1));
        if (days > 0)
            c.add(Calendar.DAY_OF_MONTH, days * (future ? 1 : -1));
        if (hours > 0)
            c.add(Calendar.HOUR_OF_DAY, hours * (future ? 1 : -1));
        if (minutes > 0)
            c.add(Calendar.MINUTE, minutes * (future ? 1 : -1));
        if (seconds > 0)
            c.add(Calendar.SECOND, seconds * (future ? 1 : -1));
        Calendar max = new GregorianCalendar();
        max.add(Calendar.YEAR, 10);
        if (c.after(max))
            return max.getTimeInMillis();
        return c.getTimeInMillis();
    }

    static int dateDiff(int type, Calendar fromDate, Calendar toDate, boolean future) {
        int diff = 0;
        long savedDate = fromDate.getTimeInMillis();
        while (future && !fromDate.after(toDate) || !future && !fromDate.before(toDate)) {
            savedDate = fromDate.getTimeInMillis();
            fromDate.add(type, future ? 1 : -1);
            diff++;
        }
        diff--;
        fromDate.setTimeInMillis(savedDate);
        return diff;
    }

    public static String formatDateDiff(long date) {
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(date);
        Calendar now = new GregorianCalendar();
        return DateUtil.formatDateDiff(now, c);
    }

    public static String formatDateDiff(Calendar fromDate, Calendar toDate) {
        boolean future = false;
        if (toDate.equals(fromDate))
            return "now";
        if (toDate.after(fromDate))
            future = true;
        StringBuilder sb = new StringBuilder();
        int[] types = new int[]{Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY,
                Calendar.MINUTE, Calendar.SECOND};
        String[] names = new String[]{"year", "years", "month", "months", "day", "days", "hour", "hours", "minute",
                "minutes", "second", "seconds"};
        int accuracy = 0;
        for (int i = 0; i < types.length; i++) {
            if (accuracy > 2)
                break;
            int diff = dateDiff(types[i], fromDate, toDate, future);
            if (diff > 0) {
                accuracy++;
                sb.append(" ").append(diff).append(" ").append(names[i << 2 + (diff > 1 ? 1 : 0)]);
            }
        }
        if (sb.length() == 0)
            return "now";
        return sb.toString().trim();
    }

    public static String readableTime(long time, boolean abbreviate) {
        long SECOND = 1000;
        long MINUTE = 60 * SECOND;
        long HOUR = 60 * MINUTE;
        long DAY = 24 * HOUR;
        long MONTH = 30 * DAY;

        long ms = time;
        StringBuilder text = new StringBuilder();
        if (ms > MONTH) {
            text.append(ms / MONTH).append((!abbreviate ? " months" : "m "));
            ms %= MONTH;
        }
        if (ms > DAY) {
            text.append(ms / DAY).append((!abbreviate ? " days " : "d "));
            ms %= DAY;
        }
        if (ms > HOUR) {
            text.append(ms / HOUR).append((!abbreviate ? " hours " : "h "));
            ms %= HOUR;
        }
        if (ms > MINUTE) {
            text.append(ms / MINUTE).append((!abbreviate ? " minutes " : "m "));
            ms %= MINUTE;
        }
        if (ms > SECOND)
            text.append(ms / SECOND).append((!abbreviate ? " seconds" : "s"));

        return text.toString();
    }

    public static String getProperDate(Date date) {
        DateFormat f1 = new SimpleDateFormat("MM/dd/yy");
        DateFormat f2 = new SimpleDateFormat("h:mma");
        return f1.format(date) + " " + f2.format(date) + " EST";
    }
}