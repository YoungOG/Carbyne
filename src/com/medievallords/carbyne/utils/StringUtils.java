package com.medievallords.carbyne.utils;

import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class StringUtils {

    private static final long serialVersionUID = 1L;
    private static final Pattern INVALIDFILECHARS = Pattern.compile("[^a-z0-9-]");
    private static final Pattern STRICTINVALIDCHARS = Pattern.compile("[^a-z0-9]");
    private static final Pattern INVALIDCHARS = Pattern.compile("[^\t\n\r\u0020-\u007E\u0085\u00A0-\uD7FF\uE000-\uFFFC]");

    /**
     * Current justification for formatting
     */
    private Alignment currentAlignment;

    /**
     * Current max length in a line
     */
    private int maxChars;

    //Used to clean file names before saving to disk
    public static String sanitizeFileName(final String name) {
        return INVALIDFILECHARS.matcher(name.toLowerCase(Locale.ENGLISH)).replaceAll("_");
    }

    //Used to clean strings/names before saving as filenames/permissions
    public static String safeString(final String string) {
        return STRICTINVALIDCHARS.matcher(string.toLowerCase(Locale.ENGLISH)).replaceAll("_");
    }

    //Less restrictive string sanitizing, when not used as perm or filename
    public static String sanitizeString(final String string) {
        return INVALIDCHARS.matcher(string).replaceAll("");
    }

    public static String joinList(Object... list) {
        return joinList(", ", list);
    }

    public static String joinList(String seperator, Object... list) {
        StringBuilder buf = new StringBuilder();
        for (Object each : list) {
            if (buf.length() > 0) {
                buf.append(seperator);
            }

            if (each instanceof Collection) {
                buf.append(joinList(seperator, ((Collection) each).toArray()));
            } else {
                try {
                    buf.append(each.toString());
                } catch (Exception e) {
                    buf.append(each.toString());
                }
            }
        }
        return buf.toString();
    }

    public static String joinListSkip(String seperator, String skip, Object... list) {
        StringBuilder buf = new StringBuilder();
        for (Object each : list) {
            if (each.toString().equalsIgnoreCase(skip)) {
                continue;
            }

            if (buf.length() > 0) {
                buf.append(seperator);
            }

            if (each instanceof Collection) {
                buf.append(joinListSkip(seperator, skip, ((Collection) each).toArray()));
            } else {
                try {
                    buf.append(each.toString());
                } catch (Exception e) {
                    buf.append(each.toString());
                }
            }
        }
        return buf.toString();
    }

    public void StringUtils(int maxChars, Alignment align) {
        switch (align) {
            case LEFT:
            case CENTER:
            case RIGHT:
                this.currentAlignment = align;
                break;
            default:
                throw new IllegalArgumentException("invalid justification arg.");
        }
        if (maxChars < 0) {
            throw new IllegalArgumentException("maxChars must be positive.");
        }
        this.maxChars = maxChars;
    }

    public StringBuffer format(Object input, StringBuffer where, FieldPosition ignore) {
        String s = input.toString();
        List<String> strings = splitInputString(s);

        for (String wanted : strings) {
            //Get the spaces in the right place.
            switch (currentAlignment) {
                case RIGHT:
                    pad(where, maxChars - wanted.length());
                    where.append(wanted);
                    break;
                case CENTER:
                    int toAdd = maxChars - wanted.length();
                    pad(where, toAdd / 2);
                    where.append(wanted);
                    pad(where, toAdd - toAdd / 2);
                    break;
                case LEFT:
                    where.append(wanted);
                    pad(where, maxChars - wanted.length());
                    break;
            }

            where.append("\n");
        }

        return where;
    }

    protected final void pad(StringBuffer to, int howMany) {
        for (int i = 0; i < howMany; i++)
            to.append(' ');
    }

    String format(String s) {
        return format(s, new StringBuffer(), null).toString();
    }

    /**
     * ParseObject is required, but not useful here.
     */
    public Object parseObject(String source, ParsePosition pos) {
        return source;
    }

    private List<String> splitInputString(String str) {
        List<String> list = new ArrayList<>();
        if (str == null)
            return list;
        for (int i = 0; i < str.length(); i = i + maxChars) {
            int endindex = Math.min(i + maxChars, str.length());
            list.add(str.substring(i, endindex));
        }
        return list;
    }

    public enum Alignment {
        LEFT, CENTER, RIGHT,
    }
}