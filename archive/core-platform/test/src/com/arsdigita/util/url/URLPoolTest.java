/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
 *
 */
package com.arsdigita.util.url;

import junit.framework.TestCase;

import java.io.*;

import com.arsdigita.util.StringUtils;

/*
* Copyright (C) 2003, 2003, 2003 Red Hat Inc. All Rights Reserved.
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


/**
 * URLPoolTest
 *
 */
public class URLPoolTest extends TestCase {
    URLPool m_pool = new URLPool();

    public void testFetchURL() throws Exception {

        final String file = System.getProperty("test.initscript");
        final String url = "file://" + file;
        final String urlData = m_pool.fetchURL(url);

        String fileData = readFile(file);
        assertEquals("File not loaded by Pool!",  fileData, urlData);

        try {
            m_pool.fetchURL(null);
            fail("Shouldn't accept null URL");
        } catch(IllegalArgumentException e) {
        }

        try {
            m_pool.fetchURL("");
            fail("Shouldn't accept blank URL");
        } catch(IllegalArgumentException e) {
        }

        try {
            m_pool.fetchURL("snert://gribble.com/");
            fail("Shouldn't accept an invalid URL");
        } catch(IllegalArgumentException e) {
        }


    }

    public void testHTTPFetch() {
        runFetchTest("small");
        runFetchTest("large");
        runFetchTest("empty");
        runFetchTest("lock");
    }


    private void runFetchTest(String site) {
    // TODO: Add test servet
//        String serverURL = "http://localhost:9999/";
//        String data = m_pool.fetchURL(serverURL);
    }

    private String readFile(String filename) throws Exception {

        Reader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(filename));

            char[] input = new char[1024];
            int numRead;
            StringBuffer buffer = new StringBuffer();
            while (( numRead = reader.read(input)) > 0) {
                buffer.append(input, 0, numRead);
            }
            return buffer.toString();

        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch(IOException e) {

                }

            }
        }

    }
}
