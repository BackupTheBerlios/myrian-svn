/*
 * Copyright (C) 2002 Red Hat Inc. All Rights Reserved.
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.Logger;

/**
 * Collection of miscellaneous utility methods that may occasionally aid
 * debugging.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2002-08-23
 * @version $Id: //core-platform/proto/src/com/arsdigita/util/Debug.java#2 $ $Date: 2002/12/09 $
 **/
public class Debug {
    private static final Logger s_log = Logger.getLogger(Debug.class);

    /**
     * Useful if you want to examine the string <code>text</code> in an
     * editor. Typical use pattern:
     *
     * <pre>
     * Debug.dumpToFile("/tmp/suspect-string.txt", suspectString);
     * </pre>
     *
     * <p>Possible IO exceptions will be logged but not rethrown. </p>
     **/
    public static void dumpToFile(String filename, String text) {
        Writer writer = getWriter(filename);
        if ( writer == null ) {
            return;
        }

        try {
            writer.write(text);
            writer.close();
        } catch (IOException ex) {
            s_log.error("Couldn't dump text to file", ex);
        }
    }


    /**
     * Dump a character array to the file.
     *
     * @see #dumpToFile(String, String)
     **/
    public static void dumpToFile(String filename, char[] chars) {
        Writer writer = getWriter(filename);
        if ( writer == null ) {
            return;
        }

        try {
            writer.write(chars);
            writer.close();
        } catch (IOException ex) {
            s_log.error("Couldn't dump text to file", ex);
        }
    }

    /**
     * Returns a <em>new</em> writer every time you call it.
     **/
    private static Writer getWriter(String filename) {
        return getWriter(filename, false);
    }

    /**
     * Returns a <em>new</em> writer every time you call it.
     **/
    private static Writer getWriter(String filename, boolean append) {
        try {
            return new FileWriter(filename, append);
        } catch (IOException ex) {
            s_log.error("Couldn't create file writer", ex);
        }
        return null;
    }

    public static String readFile(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line=br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();
            return sb.toString();
        } catch (IOException ex) {
            s_log.error("Couldn't read " + filename, ex);
        }
        return null;
    }
}
