/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.util;

import junit.framework.TestCase;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;

public class URLRewriterTest extends TestCase {

    public static final String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/util/URLRewriterTest.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    public URLRewriterTest(String s) { 
        super(s);
    }

    public void setUp() { 
        URLRewriter.addParameterProvider(new SampleParameterProvider1());
        URLRewriter.addParameterProvider(new SampleParameterProvider2());
    }

    public void tearDown() { 
        URLRewriter.clearParameterProviders();
    }

    public void testGetGlobalParams() {
        HttpServletDummyRequest req = new HttpServletDummyRequest();
        HashSet set = new HashSet();
        set.add("param1");
        set.add("param2");
        req.setURL("/foo/bar");
        req.setParameterValues("x", "xvalue");
        req.setParameterValues("y", "32");
        Set rs = URLRewriter.getGlobalParams(req);
        Iterator iter = rs.iterator();
        while (iter.hasNext()) { 
            Map.Entry entry = (Map.Entry)iter.next();
            set.remove(entry.getKey());
        }
        assert(set.isEmpty());
    }

    public void testEncodeURL() {
        HttpServletDummyRequest req = new HttpServletDummyRequest();
        HttpServletDummyResponse resp = new HttpServletDummyResponse();
        req.setURL("/foo/bar");
        req.setParameterValues("x", "xvalue");
        req.setParameterValues("y", "32");
        String encoded = URLRewriter.encodeURL(req, resp, 
                                               "/baz/quux?y=32&z=z%20value");
        assert(encoded.startsWith("/baz/quux?"));
        assert(encoded.indexOf("param2=param2value") > 0);
        assert(encoded.indexOf("param1=param1value") > 0);
        assert(encoded.indexOf("y=32") > 0);
        assert(encoded.indexOf("z=z+value") > 0 || 
               encoded.indexOf("z=z%20value") > 0);

        encoded = URLRewriter.encodeRedirectURL(req, resp, 
                                                "/baz/quux?y=32&z=z%20value");
        assert(encoded.startsWith("/baz/quux?"));
        assert(encoded.indexOf("param2=param2value") > 0);
        assert(encoded.indexOf("param1=param1value") > 0);
        assert(encoded.indexOf("y=32") > 0);
        assert(encoded.indexOf("z=z+value") > 0 || 
               encoded.indexOf("z=z%20value") > 0);
    }
    
    
    private class SampleParameterProvider1 implements ParameterProvider {
        // we can't test models without a dependency on Bebop
        public Set getModels() { 
            return java.util.Collections.EMPTY_SET;
        }

        public Set getParams(HttpServletRequest req) {
            HashSet set = new HashSet();
            set.add(new MapEntry("param1", "param1value"));
            return set;
        }
    }

    private class SampleParameterProvider2 implements ParameterProvider {
        // we can't test models without a dependency on Bebop
        public Set getModels() { 
            return java.util.Collections.EMPTY_SET;
        }

        public Set getParams(HttpServletRequest req) {
            HashSet set = new HashSet();
            set.add(new MapEntry("param2", "param2value"));
            return set;
        }
    }

    private static class MapEntry implements Map.Entry {
        private Object m_key;
        private Object m_value;

        public MapEntry(Object key, Object value) { 
            m_key = key;
            m_value = value;
        }

        public Object getKey() { 
            return m_key;
        }
    
        public Object getValue() { 
            return m_value;
        }
         
        public Object setValue(Object o) { 
            Object old = m_value;
            m_value = o;
            return old;
        }
    }
}
