package org.myrian.persistence.jdo;

import org.myrian.util.*;

import java.lang.reflect.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * ReflectionHandler
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/10/04 $
 **/

class ReflectionHandler extends DefaultHandler {


    protected boolean invoke(String name, Object[] args) {
        try {
            Method meth = Reflection.dispatch(getClass(), name, args);
            if (meth == null) {
                return false;
            } else {
                meth.invoke(this, args);
                return true;
            }
        } catch (IllegalAccessException e) {
            throw new Error(e);
        } catch (InvocationTargetException e) {
            throw new Error(e);
        }
    }

    protected String studly(String str) {
        StringBuffer result = new StringBuffer();
        boolean start = true;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
            case '-':
                start = true;
                break;
            default:
                if (start) {
                    result.append(Character.toUpperCase(c));
                    start = false;
                } else {
                    result.append(c);
                }
                break;
            }
        }
        return result.toString();
    }

    public void startElement(String uri, String name, String qname,
                             Attributes attributes) {
        if (!invoke("start" + studly(qname), new Object[] {attributes})) {
            start(qname, attributes);
        }
    }

    public void endElement(String uri, String name, String qname) {
        if (!invoke("end" + studly(qname), new Object[0])) {
            end(qname);
        }
    }

    public void start(String name, Attributes attributes) {}

    public void end(String name) {}

}
