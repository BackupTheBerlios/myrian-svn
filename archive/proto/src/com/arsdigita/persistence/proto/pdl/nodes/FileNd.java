package com.arsdigita.persistence.proto.pdl.nodes;

import java.util.*;

/**
 * File
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/01/15 $
 **/

public class FileNd extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/FileNd.java#1 $ by $Author: rhs $, $DateTime: 2003/01/15 10:39:47 $";

    public static final Field MODEL =
        new Field(FileNd.class, "model", ModelNd.class, 1, 1);
    public static final Field IMPORTS =
        new Field(FileNd.class, "imports", ImportNd.class);
    public static final Field OBJECT_TYPES =
        new Field(FileNd.class, "objectTypes", ObjectTypeNd.class);
    public static final Field ASSOCIATIONS =
        new Field(FileNd.class, "associations", AssociationNd.class);

    private String m_name;

    public FileNd(String name) {
        m_name = name;
    }

    public String getName() {
        return m_name;
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onFile(this);
    }

    public FileNd getFile() {
        return this;
    }

    public ModelNd getModel() {
        return (ModelNd) get(MODEL);
    }

    public Collection getImports() {
        return (Collection) get(IMPORTS);
    }

}