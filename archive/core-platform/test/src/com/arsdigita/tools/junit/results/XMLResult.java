/**
 * Created by IntelliJ IDEA.
 * User: jorris
 * Date: Nov 4, 2002
 * Time: 2:46:19 PM
 * To change this template use Options | File Templates.
 */
package com.arsdigita.tools.junit.results;
import org.jdom.Element;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
public class XMLResult extends Element  {
    protected XMLResult() {
        super("testsuite");
    }


    public String getSuiteName() {
        final String name = getAttributeValue("name");
        return name;
    }

    public int getTestCount() {
        final String count = getAttributeValue("tests");
        return Integer.parseInt(count);
    }

    public boolean hasTest(String name) {
        return getIndex().containsKey(name);
    }

    public XMLTestCase getTestCase(String name) {
        return (XMLTestCase) m_index.get(name);
    }

    private Map getIndex() {
        if (null == m_index) {
            m_index = new HashMap();
            List testcases = getChildren("testcase");
            for (Iterator iterator = testcases.iterator(); iterator.hasNext();) {
                XMLTestCase test = (XMLTestCase) iterator.next();
                final String name = test.getAttributeValue("name");
                m_index.put(name, test);

            }
        }

        return m_index;
    }

    private Map m_index = null;
}

