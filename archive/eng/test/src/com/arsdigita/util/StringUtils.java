/*
 * Copyright (C) 2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.arsdigita.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * A (static) class of generally-useful string utilities.
 *
 * @author Bill Schneider
 */
public class StringUtils {

    private static final Logger s_log = Logger.getLogger(StringUtils.class);

    private StringUtils() {
        // can't instantiate me!
    }

    public static final String NEW_LINE = System.getProperty("line.separator");

    /**
     * Tests if a string is empty.
     * @param s A string to test
     * @return <code>true</code> if <code>s</code> is null or empty;
     * otherwise <code>false</code>
     */
    public final static boolean emptyString(String s) {
        boolean expr = (s == null || s.length() == 0);
        return expr;
    }

    /**
     * Tests if a string is empty.
     * @param o A string to test
     * @return <code>true</code> if <code>o</code> is null or empty;
     * otherwise <code>false</code>
     */
    public final static boolean emptyString(Object o) {
        boolean expr =
            (o == null || (o instanceof String && ((String)o).length() ==0));
        return expr;
    }

    /**
     * Convert a string of items separated by a separator
     * character to an array of the items.  sep is the separator
     * character.  Example: Input - s == "cat,house,dog" sep==','
     * Output - {"cat", "house", "dog"}
     * @param s string contains items separated by a separator character.
     * @param sep separator character.
     * @return Array of items.
     **/
    public static String [] split(String s, char sep) {
        ArrayList al = new ArrayList();
        int start_pos, end_pos;
        start_pos = 0;
        while (start_pos < s.length()) {
            end_pos = s.indexOf(sep, start_pos);
            if (end_pos == -1) {
                end_pos = s.length();
            }
            String found_item = s.substring(start_pos, end_pos);
            al.add(found_item);
            start_pos = end_pos + 1;
        }
        if (s.length() > 0 && s.charAt(s.length()-1) == sep) {
            al.add("");  // In case last character is separator
        }
        String [] returned_array = new String[al.size()];
        al.toArray(returned_array);
        return returned_array;
    }

    /**
     * Converts an array of Strings into a single String separated by
     * a given character.
     * Example Input: {"cat", "house", "dog"}, ','
     * Output -  "cat,house,dog"
     *
     * @param strings The string array too join.
     * @param joinChar The character to join the array members together.
     *
     * @pre strings != null
     *
     * @return Joined String
     **/
    public static String join(String[] strings, char joinChar) {
        StringBuffer result = new StringBuffer();
        final int lastIdx = strings.length - 1;
        for (int idx = 0; idx < strings.length; idx++) {
            result.append(strings[idx]);
            if (idx < lastIdx) {
                result.append(joinChar);
            }
        }

        return result.toString();
    }
    /**
     * Converts an array of Strings into a single String separated by
     * a given string.
     * Example Input: {"cat", "house", "dog"}, ", "
     * Output -  "cat, house, dog"
     *
     * @param strings The string array too join.
     * @param joinStr The string to join the array members together.
     *
     * @pre strings != null
     *
     * @return Joined String
     **/
    public static String join(String[] strings, String joinStr) {
        StringBuffer result = new StringBuffer();
        final int lastIdx = strings.length - 1;
        for (int idx = 0; idx < strings.length; idx++) {
            result.append(strings[idx]);
            if (idx < lastIdx) {
                result.append(joinStr);
            }
        }

        return result.toString();
    }

    /**
     * Strip extra white space from a string.  This replaces any white space
     * character or consecutive white space characters with a single space.
     * It is useful when comparing strings that should be equal except for
     * possible differences in white space.  Example:  input = "I  \ndo\tsee".
     * Output = "I do see".
     * @param s string that may contain extra white space
     * @return string the same as the input, but with extra white space
     * removed and replaced by a single space.
     */
    static public String stripWhiteSpace(String s) {
        StringBuffer to = new StringBuffer();
        boolean inSpace = true;
        boolean isSpace;
        char c;
        for (int i=0; i<s.length(); i++) {
            c = s.charAt(i);
            isSpace = Character.isWhitespace(c);
            if (!isSpace) {
                to.append(c);
                inSpace = false;
            } else if (!inSpace) {
                to.append(' ');
                inSpace = true;
            }
        }
        return to.toString().trim();
    }

}


