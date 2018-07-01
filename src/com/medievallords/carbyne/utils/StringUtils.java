package com.medievallords.carbyne.utils;

import org.bukkit.ChatColor;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static final Pattern pattern = Pattern.compile("(&.)");
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

    public static String fixNameTag(String tag) {
        Matcher matcher = pattern.matcher(tag.substring(0, 16));
        List<String> chars = new ArrayList<>();
        while (matcher.find()) {
            chars.add(matcher.group(1).substring(1));
        }
        Collections.reverse(chars);
        for (String character : chars) {
            ChatColor c = ChatColor.getByChar(character);
            if (c != null) {
                return (tag.substring(0, 16) + "&" + character + tag.substring(17));
            }
        }
        return tag;
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

    public static String formatDouble(double d) {
        if (d == (long) d)
            return String.format("%d", (long) d);
        else
            return String.format("%s", d);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static String formatHealth(double health) {
        double hearts = health / 5;
        DecimalFormat format = new DecimalFormat("#");

        if (hearts <= 10 && hearts >= 7.5)
            return String.format(" &a%s \u2764", format.format(hearts));
        else if (hearts <= 7.5 && hearts >= 5)
            return String.format(" &e%s \u2764", format.format(hearts));
        else if (hearts <= 5 && hearts >= 2.5)
            return String.format(" &6%s \u2764", format.format(hearts));
        else
            return String.format(" &c%s \u2764", format.format(hearts));
    }

    public static String formatHealthBar(int health) {
        StringBuilder s = new StringBuilder();

        if (health >= 13)
            s.append("§a");
        else if (health >= 7)
            s.append("§e");
        else
            s.append("§c");

        health /= 2;

        int req = (10 - health);
        int oReq = health;

        for (int i = 0; i < oReq; i++)
            s.append("\u2758");

        s.append("§7");

        for (int i = 0; i < req; i++)
            s.append("\u2758");

        return s.toString();
    }

    public enum Alignment {
        LEFT, CENTER, RIGHT,
    }
}