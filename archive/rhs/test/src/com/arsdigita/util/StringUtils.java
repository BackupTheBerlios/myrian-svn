/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
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


