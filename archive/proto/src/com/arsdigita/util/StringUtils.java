/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;
import org.apache.oro.text.regex.Substitution;
import org.apache.oro.text.regex.Util;


/**
 * A (static) class of generally-useful string utilities.
 * @author Bill Schneider
 */

public class StringUtils {

    private static Perl5Util s_re = new Perl5Util();

    private StringUtils() {
        // can't instantiate me!
    }

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
     * If the String is null, returns an empty string.  Otherwise,
     * returns the string unaltered
     */
    public final static String nullToEmptyString(String s) {
        return (s == null) ? "" : s;
    }

    /**
     * Escapes some "special" characters in HTML text (ampersand, angle
     * brackets, quote).
     * @param s The plain-text string to quote
     * @return The string with special characters escpaed.
     */
    public final static String quoteHtml(String s) {
        if (s != null) {
            StringBuffer result = new StringBuffer(s.length() + 10);
            for (int i = 0; i < s.length(); i++) {
                char ch = s.charAt(i);
                switch (ch) {
                case '&':
                    result.append("&amp;");
                    break;
                case '"':
                    result.append("&quot;");
                    break;
                case '<':
                    result.append("&lt;");
                    break;
                case '>':
                    result.append("&gt;");
                    break;
                default:
                    result.append(ch);
                }
            }
            return result.toString();
        } else {
            return "";
        }
    }

    /**
     * Takes a plaintext string, and returns an HTML string that, when
     * rendered by a web browser, will appear as the original input string
     *
     * @param s The input plaintext string
     * @return A HTML string with blank lines coverted to <pre>&lt;p></pre>
     * and ampersands/angle brackets escaped.
     */
    public final static String textToHtml(String s) {
        s = quoteHtml(s);
        s = s_re.substitute("s/\r\n\r\n/<p>/g", s);
        s = s_re.substitute("s/\n\n/<p>/g", s);
        s = s_re.substitute("s/\r\r/<p>/g", s);
        s = s_re.substitute("s/\r\n/<br>/g", s);
        s = s_re.substitute("s/\n/<br>/g", s);
        s = s_re.substitute("s/\r/<br>/g", s);
        return s;
    }

    /**
     * Removes tags and substitutes P tags with newlines.  For much
     * more extensive conversion of HTML fragments to plain text
     * equivalents, see {@link HtmlToText}.
     */
    public final static String htmlToText(String s) {
        if (s != null) {
            // first take out new-lines
            s = s_re.substitute("s/\n//g", s);
            s = s_re.substitute("s/\r//g", s);
            s = s_re.substitute("s/<[Pp]>/\n\n/g", s);
            s = s_re.substitute("s/<br>/\n/ig", s);
            // take out other tags
            s = s_re.substitute("s/<([^>]*)>//g", s);
            return s;
        } else {
            return "";
        }
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
     * Extract a parameter value from a packed list of parameter values.
     * Example: input: key="age", sep=',', plist="cost=23,age=27,name=Thom"
     * output = "27".  This is a simple implementation that is meant
     * for controlled use in which the key and values are known to
     * be safe.  Specifically, the equals character must be used to indicate
     * parameter assignments.  There is no escape character.  Thus the
     * parameter names and values cannot contain the equals character or the
     * separator character.
     *
     * @param key the key indicating which parameter value to extract.
     * @param plist packed list of key=value assignments.  The character '='
     *   must be used to indicate the assignment.
     * @param sep separator character.
     * @return the value corresponding to the key, or null if the key is not
     *         present.  If the key appears in the list more than once,
     *         the first value is returned.
     **/
    public static String getParameter(String key, String plist, char sep) {
        int key_end;
        int key_start = 0;
        String found_value;
        while (key_start < plist.length()) {
            key_start = plist.indexOf(key, key_start);
            if (key_start == -1) {
                return null;   // Did not find key
            }
            key_end = key_start + key.length();
            if (plist.charAt(key_end) == '=' &&
                (key_start == 0 || plist.charAt(key_start - 1) == sep)) {
                // Found isolated parameter value, this is the match
                int value_end = plist.indexOf(sep, key_end);
                if (value_end == -1) {
                    // did not find another separator, return value
                    found_value = plist.substring(key_end + 1);
                } else {
                    // found another separator, return value
                    found_value = plist.substring(key_end + 1, value_end);
                }
                return found_value;
            } else {
                key_start++;   // did not find.  Advance past current position
            }
        }
        return null;
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

    /**
     * Get a String representation for an Object.  If it has an
     * asString method, use that; otherwise fall back on toString
     */

    public static String toString(Object o) {
        try {
            return (String) o.getClass().getMethod("asString", null)
                .invoke(o, new Object[0]);
        } catch (NoSuchMethodException e) {
            return o.toString();
        } catch (Exception e) {
            throw new UncheckedWrapperException
                ("Invoking asString() on an " + o.getClass(), e);
        }
    }

    /**
     * create a String representation of a map.  This method is not
     * too necessary, because Map.toString() does almost the same.
     */

    public static String toString(Map m) {
        StringBuffer to = new StringBuffer();
        if (m == null) {
            to.append("null");
        } else {
            to.append(m.getClass().getName());
            Set entrySet = m.entrySet();
            if (entrySet == null) {
                to.append("[null entrySet]");
            } else {
                Iterator entries = entrySet.iterator();
                if (entries == null) {
                    to.append("[null iterator]");
                } else {
                    String newLine = System.getProperty("line.separator");
                    to.append("{");
                    String comma = newLine;

                    while (entries.hasNext()) {
                        to.append(comma);
                        comma = "," + newLine;
                        Map.Entry e = (Map.Entry)entries.next();
                        to  .append(toString(e.getKey()))
                            .append(" => ")
                            .append(toString(e.getValue()));
                    }
                    to.append(newLine).append("}");
                }
            }
        }
        String result = to.toString();
        return result;
    }

    /**
     * Strips all new-line characters from the input string.
     * @param str a string to strip
     * @return the input string with all new-line characters
     * removed.
     * @post result.indexOf('\r') == 0
     * @post result.indexOf('\n') == 0
     */
    public static String stripNewLines(String str) {
        int len = str.length();
        StringBuffer sb = new StringBuffer(len);
        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i);
            if (ch != '\r' && ch != '\n') {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    /**
     * <p>Add a possible newline for proper wrapping.</p>
     *
     * <p>Checks the given String to see if it ends with whitspace.
     * If so, it assumes this whitespace is intentional formatting and
     * returns a reference to the original string.  If not, a new
     * <code>String</code> object is created containing the original
     * plus a platform-dependent newline character obtained from
     * {@link System#getProperties}.</p>
     *
     */

    public static String addNewline(String s) {
        int n = s.length()-1;
        if (n == -1) {
            return s;
        } else if (Character.isWhitespace(s.charAt(n))) {
            return s;
        } else {
            return s.concat(System.getProperty("line.separator"));
        }
    }


    /**
     *  This takes the passed in string and truncates it.
     *  It cuts the string off at the length specified and then
     *  goes back to the most recent space and truncates any
     *  word that may have been cut off.  It also takes the
     *  string and converts it to plain text so that no HTML
     *  will be shown.
     */
    public static String truncateString(String s, int length) {
        return truncateString(s, length, true);
    }


    /**
     *  This takes the passed in string and truncates it.
     *  It cuts the string off at the length specified and then
     *  goes back to the most recent space and truncates any
     *  word that may have been cut off.  The htmlToText dictates
     *  whehter or not the string should be converted from HTML to
     *  text before being truncated
     *
     *  @param s The string to be truncated
     *  @param length The length which to truncate the string
     *  @param removeHTML Whether or not to convert the HTML to text
     */
    public static String truncateString(String s, int length,
                                        boolean removeHTML) {
        if (s == null) {
            return "";
        }

        String string = s;
        if (removeHTML) {
            string = htmlToText(string);
        }

        if (string.length() <= length) {
            return string;
        }

        return string.substring(0, string.lastIndexOf(" ", length));
    }


    /**
     * "join" a List of Strings into a single string, with each string
     * separated by a defined separator string.
     *
     * @param elements the strings to join together
     * @param sep the separator string
     * @return the strings joined together
     */
    public static String join(List elements, String sep) {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        Iterator iter = elements.iterator();

        while (iter.hasNext()) {
            String element = (String)iter.next();

            if (!first) {
                sb.append(sep);
            } else {
                first = false;
            }

            sb.append(element);
        }

        return sb.toString();
    }

    /**
     * Removes whitespace from the beginning of a string. If the
     * string consists of nothing but whitespace characters, an empty
     * string is returned.
     */

    public final static String trimleft(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return s.substring(i);
            }
        }
        return "";
    }

    /**
     * Returns a String containing the specified repeat count of a
     * given pattern String.
     *
     * @param pattern the pattern String
     * @param repeatCount the number of time to repeat it
     */

    public final static String repeat(String pattern, int repeatCount) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < repeatCount; i++) {
            sb.append(pattern);
        }
        return sb.toString();
    }

    /**
     * Returns a String containing the specified repeat count of a
     * given pattern character.
     *
     * @param pattern the pattern character
     * @param repeatCount the number of time to repeat it
     */

    public final static String repeat(char pattern, int repeatCount) {
        return repeat(String.valueOf(pattern), repeatCount);
    }

    /**
     * Wrap a string to be no wider than 80 characters.  This is just
     * a convenience method for calling the more general method with a
     * default string width.
     *
     * @param input the String to wrap
     *
     * @since  5.1.2
     */

    public final static String wrap(String input) {
        return wrap(input,80);
    }

    /**
     * Wrap a string to be no wider than a specified number of
     * characters by inserting line breaks.  If the input is null or
     * the empty string, a string consisting of only the newline
     * character will be returned. Otherwise the input string will be
     * wrapped to the specified line length.  In all cases the last
     * character of the return value will be a single newline.
     *
     * <p>Notes:
     *
     * <ol>
     * <li>line breaks in the input string are preserved
     * <li>wrapping is "soft" in that lines in the output string may
     *     be longer than maxLength if they consist of contiguous
     *     non-whitespace characters.
     * </ol>
     *
     * @param input the String to wrap
     * @param maxLength the maximum number of characters between line
     * breaks
     *
     * @since  5.1.2
     */
    public final static String wrap(String input, int maxLength) {

        final char SPACE = ' ';
        final char ENDL  = '\n';

        // Make sure that we start with a string terminated by a
        // newline character.  Some of the index calculations below
        // depend on this.

        if (emptyString(input)) {
            return String.valueOf(ENDL);
        } else {
            input = input.trim() + String.valueOf(ENDL);
        }

        StringBuffer output = new StringBuffer();

        int startOfLine = 0;

        while (startOfLine < input.length()) {

            String line = input.substring
                (startOfLine, Math.min(input.length(),
                                       startOfLine + maxLength));

            if (line.equals("")) {
                break;
            }

            int firstNewLine = line.indexOf(ENDL);
            if (firstNewLine != -1) {

                // there is a newline
                output.append
                    (input.substring(startOfLine,
                                     startOfLine + firstNewLine));
                output.append(ENDL);
                startOfLine += firstNewLine + 1;
                continue;
            }

            if (startOfLine + maxLength > input.length()) {

                // we're on the last line and it is < maxLength so
                // just return it

                output.append(line);
                break;
            }

            int lastSpace = line.lastIndexOf(SPACE);
            if (lastSpace == -1) {

                // no space found!  Try the first space in the whole
                // rest of the string

                int nextSpace = input.indexOf
                    (SPACE, startOfLine);
                int nextNewLine = input.indexOf
                    (ENDL, startOfLine);

                if (nextSpace == -1) {
                    lastSpace = nextNewLine;
                } else {
                    lastSpace = Math.min
                        (nextSpace,nextNewLine);
                }

                if (lastSpace == -1) {
                    // didn't find any more whitespace, append the
                    // whole thing as a line
                    output.append(input.substring(startOfLine));
                    break;
                }

                // code below will add this to the start of the line

                lastSpace -= startOfLine;
            }

            // append up to the last space

            output.append(input.substring(startOfLine,
                                          startOfLine + lastSpace));
            output.append(ENDL);

            startOfLine += lastSpace + 1;
        }

        return output.toString();
    }

    /**
     * Returns true if the String is AlphaNumeric. Obviously, this is not at all globalized and should
     * only be used with English text.
     *
     * @param value String to check
     * @return true if value is alphanumeric, false otherwise.
     */
    public static boolean isAlphaNumeric(String value) {
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (!(('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') ||
                  ('0' <= c && c <= '9'))) {
                return false;
            }
        }

        return true;
    }

    /**
     * This method performs interpolation on multiple variables.
     * The keys in the hash table correspond directly to the placeholders
     * in the string. The values in the hash table can either be
     * plain strings, or an instance of the PlaceholderValueGenerator
     * interface
     *
     * Variable placeholders are indicated in text by surrounding
     * a key word with a pair of colons. The keys in the hash
     * table correspond to the names
     *
     * eg. "::forename:: has the email address ::email::"
     *
     * @param text the text to interpolate
     * @param vars a hash table containing key -> value mappings
     *
     */
    public static String interpolate(String text, Map vars) {
        HashSubstitution subst = new HashSubstitution(vars);
        Perl5Matcher matcher = new Perl5Matcher();
        Perl5Compiler compiler = new Perl5Compiler();
        StringBuffer result = new StringBuffer();
        PatternMatcherInput input = new PatternMatcherInput(text);

        try {
            Util.substitute(result,
                            matcher,
                            compiler.compile("(::(?:\\w+(?:\\.\\w+)*)::)"),
                            subst,
                            input,
                            Util.SUBSTITUTE_ALL);
        } catch (MalformedPatternException e) {
            throw new UncheckedWrapperException("cannot perform substitution", e);
        }
        return result.toString();
    }


    /**
     * THis method performs a single variable substitution
     * on a string. The placeholder takes the form of
     * ::key:: within the sample text.
     *
     * @param text the text to process for substitutions
     * @param key the name of the placeholder
     * @param value the value to insert upon encountering a placeholder
     */
    public static String interpolate(String text, String key, String value) {
        String pattern = "s/::" + key + "::/" + value + "/";

        return s_re.substitute(pattern, text);
    }



    /**
     * Finds all occurrences of <code>find</code> in <code>str</code> and
     * replaces them with them with <code>replace</code>.
     *
     * @pre find != null
     * @pre replace != null
     **/
    public static String replace(final String str,
                                 final String find,
                                 final String replace) {

        Assert.assertNotNull(find, "find");
        Assert.assertNotNull(replace, "replace");

        if ( str == null ) return null;

        int cur = str.indexOf(find);
        if ( cur < 0 ) return str;

        final int findLength = find.length();
        // If replace is longer than find, assume the result is going to be
        // slightly longer than the original string.
        final int bufferLength =
            replace.length() > findLength ? (int) (str.length() * 1.1) : str.length();
        StringBuffer sb = new StringBuffer(bufferLength);
        int last = 0;

        if ( cur == 0 ) {
            sb.append(replace);
            cur = str.indexOf(find, cur+findLength);
            last = findLength;
        }

        while ( cur > 0 ) {
            sb.append(str.substring(last, cur));
            sb.append(replace);
            last = cur + findLength;
            cur = str.indexOf(find, cur+findLength);
        }
        if ( last < str.length()-1) {
            sb.append(str.substring(last));
        }

        return sb.toString();
    }


    /**
     * An interface allowing the value for a placeholder to be
     * dynamically generated.
     */
    public interface PlaceholderValueGenerator {
        /**
         * Returns the value corresponding to the supplied key
         * placeholder.
         *
         * @param the key being substituted
         */
        public String generate(String key);
    }



    private static class HashSubstitution implements Substitution {
        private Map m_hash;

        public HashSubstitution(Map hash) {
            m_hash = hash;
        }

        public void appendSubstitution(StringBuffer appendBuffer,
                                       MatchResult match,
                                       int substitutionCount,
                                       PatternMatcherInput originalInput,
                                       PatternMatcher matcher,
                                       Pattern pattern) {
            String placeholder = match.toString();
            String key = placeholder.substring(2, placeholder.length()-2);

            Object value = (m_hash.containsKey(key) ?
                            m_hash.get(key) :
                            placeholder);
            String val;
            try {
                PlaceholderValueGenerator gen = (PlaceholderValueGenerator)value;
                val = gen.generate(key);
            } catch (ClassCastException ex) {
                val = (String)value;
            }

            appendBuffer.append(val);
        }
    }
}
