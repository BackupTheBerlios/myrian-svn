package com.redhat.persistence.pdl.nodes;

/**
 * Import
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/04 $
 **/

public class ImportNd extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/pdl/nodes/ImportNd.java#1 $ by $Author: dennis $, $DateTime: 2003/08/04 15:56:00 $";

    public static final Field PATH =
        new Field(ImportNd.class, "path", IdentifierNd.class, 1);

    private boolean m_isWildcard = false;

    public void setWildcard(boolean value) {
        m_isWildcard = value;
    }

    public boolean isWildcard() {
        return m_isWildcard;
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onImport(this);
    }

    private String getPath() {
        final StringBuffer result = new StringBuffer();

        traverse(new Switch() {
                public void onIdentifier(IdentifierNd id) {
                    if (result.length() > 0) {
                        result.append('.');
                    }
                    result.append(id.getName());
                }
            });

        return result.toString();
    }

    public String qualify(TypeNd type) {
        if (type.isQualified()) {
            throw new IllegalArgumentException
                ("Type is already qualified.");
        }

        if (isWildcard()) {
            return getPath() + "." + type.getName();
        } else if (getPath().endsWith("." + type.getName())) {
            return getPath();
        } else {
            return null;
        }
    }

}
