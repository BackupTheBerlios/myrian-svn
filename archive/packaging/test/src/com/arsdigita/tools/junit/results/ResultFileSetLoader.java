/*
 * Copyright (C) 2001, 2002, 2003, 2003 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.tools.junit.results;

import com.arsdigita.util.UncheckedWrapperException;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

/**
 *  ResultFileSetLoader
 *
 *  @author <a href="mailto:jorris@redhat.com">Jon Orris</a>
 *  @version $Revision: #2 $ $Date Nov 6, 2002 $
 */
public class ResultFileSetLoader {

    private static final FilenameFilter s_testFilter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
            final boolean isTestFile = name.startsWith("TEST") && name.endsWith(".xml");
            return isTestFile;
        }
    };

    private SAXBuilder m_builder;

    public ResultFileSetLoader() {
        m_builder = new SAXBuilder();
        m_builder.setFactory(new ResultJDOMFactory());
    }

    Map loadResultFiles(String path) {
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        String[] testFiles = new File(path).list(s_testFilter);
        Map tests = new HashMap();
        for (int i = 0; i < testFiles.length; i++) {
            String testFile = testFiles[i];
            XMLResult result = loadResult(path + testFile);
            if (null != result) {
                tests.put(testFile, result);
            }
        }
        return tests;

    }

    /**
     * Loads the results into memory.
     * @param filename
     * @return
     */
    private XMLResult loadResult(String filename) {
        try {
            Document doc = m_builder.build(filename);
            XMLResult res = (XMLResult) doc.getRootElement();
            return res;
        } catch(JDOMException e) {
            // This is likely due to an empty document
            EmptyXMLResult res = new EmptyXMLResult(filename);
            return res;
        }
    }

}
