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

package com.arsdigita.persistence.pdl.ast;

import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Operation;

import com.arsdigita.persistence.Utilities;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Arrays;
import org.apache.log4j.Category;

/**
 * SQLBlockDef
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2002/07/26 $
 */

public class SQLBlockDef extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/SQLBlockDef.java#4 $ by $Author: randyg $, $DateTime: 2002/07/26 15:40:17 $";

    private static Category s_log = 
        Category.getInstance(SQLBlockDef.class);

    private String m_sql;
    private List m_mapStmts = new ArrayList();
    private List m_bindTypes = new ArrayList();
    private boolean m_isCallableStatement = false;

    /**
     * @pre sql != null
     */
    public SQLBlockDef(String sql) {
        if ((sql.indexOf("&gt;") != -1) || (sql.indexOf("&lt;") != -1)) {
            StringBuffer sb = new StringBuffer();
            int nextlt = sql.indexOf("&lt;");
            int nextgt = sql.indexOf("&gt;");
            int lasthit = 0;

            while ((nextlt != -1) || (nextgt != -1)) {
                if (((nextlt < nextgt) && (nextlt != -1)) || (nextgt == -1)) {
                    if ((nextlt - 1) > lasthit) {
                        sb.append(sql.substring(lasthit, nextlt));
                    }

                    sb.append("<");

                    lasthit = nextlt + 4;
                    nextlt = sql.indexOf("&lt;", lasthit);

                    s_log.debug("SO FAR: ");
                    s_log.debug(sb.toString());
                } else {
                    if ((nextgt - 1) > lasthit) {
                        sb.append(sql.substring(lasthit, nextgt));
                    }

                    sb.append(">");

                    lasthit = nextgt + 4;
                    nextgt = sql.indexOf("&gt;", lasthit);

                    s_log.debug("SO FAR: ");
                    s_log.debug(sb.toString());
                }
            }

            if (lasthit < sql.length()) {
                sb.append(sql.substring(lasthit));
            }

            s_log.debug("FINISHED:");
            s_log.debug(sb.toString());

            m_sql = sb.toString();
        } else {
            m_sql = sql;
        }
    }

    public void add(MapStatement m) {
        Iterator maps = m_mapStmts.iterator();

        if (m instanceof MappingDef) {
            MappingDef newMap = (MappingDef)m;

            while (maps.hasNext()) {
                MapStatement stmt = (MapStatement)maps.next();
    
                if (stmt instanceof MappingDef) {
                    MappingDef map = (MappingDef)stmt;

                    if (Arrays.equals(map.getPath(), newMap.getPath())) {
                        error(
                            map.getPrettyPath() + " already mapped in block"
                            );
                    }
                }
            }
        } else {
            m_bindTypes.add(m);
            super.add(m);
            return;
        }

        m_mapStmts.add(m);
        super.add(m);
    }

    void validate() {
        validate(m_mapStmts);
    }

    void validateMappings(ObjectType type) {
        for (int i = 0; i < m_mapStmts.size(); i++) {
            MappingDef map = (MappingDef) m_mapStmts.get(i);
            map.validateMapping(type);
        }

        for (int i = 0; i < m_bindTypes.size(); i++) {
            BindingDef bd = (BindingDef) m_bindTypes.get(i);
            bd.validateMapping(type);
        }
    }

    public String getSQL() {
        return m_sql;
    }

    /**
     * Generate a metadata Operation that this SQLBlockDef represents.
     */
    Operation generateOperation() {
        Operation op = new Operation(m_sql);
        initLineInfo(op);

        op.setCallableStatement(m_isCallableStatement);
        Iterator maps = m_mapStmts.iterator();

        while (maps.hasNext()) {
            MappingDef map = (MappingDef)maps.next();

            op.addMapping(map.generateMapping());
        }

        for (Iterator it = m_bindTypes.iterator(); it.hasNext(); ) {
            BindingDef bd = (BindingDef) it.next();
            if (op.hasBindType(bd.getPath())) {
                bd.error("Bind type specified twice: " + bd);
            } else {
                op.setBindType(bd.getPath(), bd.getType().getTypeCode());
            }
        }

        return op;
    }

    public String toString() {
        StringBuffer result = new StringBuffer("        do {");

        if (m_sql.length() == 0) {
            result.append("}");
        } else if (m_sql.charAt(0) == '\n' || m_sql.charAt(0) == '\r') {
            result.append(m_sql + "}");
        } else {
            result.append(Utilities.LINE_BREAK + "            " + m_sql + 
                          Utilities.LINE_BREAK + "        }");
        }
        int i;
        for (i = 0; i < m_mapStmts.size(); i++) {
            if (i == 0) result.append(" map {");
            result.append(Utilities.LINE_BREAK + "            " + 
                          m_mapStmts.get(i) + ";");
        }

        if (i > 0) {
            result.append(Utilities.LINE_BREAK + "        }");
        }

        return result.toString();
    }


    /**
     *  This returns the map statements for the sql block
     */
    public Collection getMapStatements() {
        return m_mapStmts;
    }


    /**
     *  @param isCallable This dictates whether the statement is 
     *   CallableStatement or a PreparedStatement should be used when
     *   executing the operation
     */ 
    public void setCallableStatement(boolean isCallable) {
        m_isCallableStatement = isCallable;
    }
}
