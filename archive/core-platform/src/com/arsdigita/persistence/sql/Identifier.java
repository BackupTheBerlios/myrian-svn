package com.arsdigita.persistence.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Identifier
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 **/

public class Identifier extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/sql/Identifier.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    private String[] m_path;
    private boolean m_isBindVar = false;
    private List m_leafElements;

    private static Map s_hashMap = new HashMap();

    // Cache the result of makeString
    private String m_pathString;

    private Identifier(String[] path) {
        m_path = path;
        if (m_path[0].charAt(0) == ':') {
            m_isBindVar = true;
            m_path[0] = m_path[0].substring(1);
        }
    }

    public boolean isLeaf() {
        return true;
    }

    public List getLeafElements() {
        if (m_leafElements == null) {
            m_leafElements = new ArrayList();
            addLeafElements(m_leafElements);
        }
        return m_leafElements;
    }

    public void addLeafElements(List l) {
        l.add(this);
    }

    public String[] getPath() {
        return m_path;
    }

    public boolean isBindVar() {
        return m_isBindVar;
    }

    String makeString() {
        if (m_pathString == null) {
            StringBuffer result = new StringBuffer();

            if (isBindVar()) {
                result.append(':');
            }

            for (int i = 0; i < m_path.length; i++) {
                result.append(m_path[i]);
                if (i < m_path.length - 1) {
                    result.append('.');
                }
            }
            m_pathString = result.toString();
        }
        return m_pathString;
    }

    public static Identifier getInstance(String[] path) {
        String key = pathToKey(path);
        Identifier returnValue = (Identifier) s_hashMap.get(key);
        if (returnValue == null) {
            returnValue = new Identifier(path);
            s_hashMap.put(key, returnValue);
        }
        return returnValue;
    }

    private static String pathToKey(String[] path) {
        StringBuffer sb = new StringBuffer(7 * path.length);
        for (int i = 0; i < path.length; i++) {
            sb.append(path[i]);
        }
        return sb.toString();
    }
}
