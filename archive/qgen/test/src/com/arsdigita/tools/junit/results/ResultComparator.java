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

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Arrays;

/**
 * ResultComparator
 *
 * @author <a href="mailto:jorris@redhat.com">Jon Orris</a>
 *
 */
public class ResultComparator {
    public static void main(String[] args) throws Exception {
        String htmlOutputDir = args[0];
        String previousChangelist = args[1];
        String currentChangelist = args[2];

        diffAll(htmlOutputDir, previousChangelist, currentChangelist);
//          compareFiles("clean.xml", "TEST-com.arsdigita.persistence.PersistenceSuite.xml");
    }

    private static void diffAll(String htmlOutputDir, String previousChangelist, String currentChangelist) throws Exception {
        ResultFileSetLoader loader = new ResultFileSetLoader();
        Map currentTests = loader.loadResultFiles(".");

        FileTestImporter imp = new FileTestImporter();
        Map previousTests = imp.getTestsForChangelist(previousChangelist);

        ArrayList diffs = new ArrayList(currentTests.keySet().size());
        Object[] keys = currentTests.keySet().toArray();
        Arrays.sort(keys);

        for (int i = 0; i < keys.length; i++) {
            String testFile = (String) keys[i];


            XMLResult previous = (XMLResult) previousTests.get(testFile);
            if (null == previous) {
                System.out.println("Null Test file: " + testFile);
                previous = new EmptyXMLResult(testFile);
                previousTests.put(testFile, previous);
            }
            previous.setChangelist(previousChangelist);
            XMLResult current = (XMLResult) currentTests.get(testFile);
            current.setChangelist(currentChangelist);
            ResultDiff diff = new ResultDiff(previous, current);
            diffs.add(diff);
        }

		String databaseType = System.getProperty("database.key");
        ReportIndex index = new ReportIndex(previousChangelist, currentChangelist, databaseType);
        XMLOutputter out = new XMLOutputter("  ", true);
        final String ACS_HOME = System.getProperty("ACS_HOME");
        Transformer tran =  TransformerFactory.newInstance().newTransformer(new StreamSource(ACS_HOME + "/test/xsl/junit.xsl"));
        for (Iterator iterator = diffs.iterator(); iterator.hasNext();) {
            ResultDiff resultDiff = (ResultDiff) iterator.next();
            index.addResult(resultDiff);

            String htmlFile = resultDiff.getAttributeValue("name") + ".html";
            FileWriter file = new FileWriter(htmlOutputDir + "/" + htmlFile);
            JDOMResult html = new JDOMResult();
            tran.transform(new JDOMSource(new Document(resultDiff)), html);
            out.output(html.getDocument(), file);
        }

        JDOMResult indexHtml = new JDOMResult();
        tran =  TransformerFactory.newInstance().newTransformer(new StreamSource(ACS_HOME + "/test/xsl/index.xsl"));
        tran.transform(new JDOMSource(new Document(index)), indexHtml);
        FileWriter indexFile = new FileWriter(htmlOutputDir + "/index.html");
        out.output(indexHtml.getDocument(), indexFile);

        out.output(new Document(index), new FileOutputStream("report_index_" + currentChangelist + ".xml"));
    }

    private static void compareFiles(String canonical, String newFile) throws JDOMException, TransformerException, IOException {
        SAXBuilder builder = new SAXBuilder();
        builder.setFactory(new ResultJDOMFactory());

        XMLResult previous = (XMLResult) builder.build(canonical).getRootElement();
        XMLResult current = (XMLResult) builder.build(newFile).getRootElement();

        ResultDiff diff = new ResultDiff(previous, current);
        Document junitReport = new Document(diff);
        Transformer tran =  TransformerFactory.newInstance().newTransformer(new StreamSource("test/xsl/junit.xsl"));
//        JDOMResult html = new JDOMResult();
//        tran.transform(new JDOMSource(junitReport), html);
        XMLOutputter out = new XMLOutputter("  ", true);
//        out.output(html.getDocument(), System.out);
        out.output(junitReport, System.out);
    }
}
