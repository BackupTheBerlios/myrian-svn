package com.arsdigita.persistence.proto.pdl.nodes;

import java.util.*;

/**
 * File
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class File extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/File.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    public static final Field MODEL =
        new Field(File.class, "model", Model.class, 1, 1);
    public static final Field IMPORTS =
        new Field(File.class, "imports", Import.class);
    public static final Field OBJECT_TYPES =
        new Field(File.class, "objectTypes", ObjectType.class);
    public static final Field ASSOCIATIONS =
        new Field(File.class, "associations", Association.class);

    private String m_name;

    public File(String name) {
        m_name = name;
    }

    public String getName() {
        return m_name;
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onFile(this);
    }

    public File getFile() {
        return this;
    }

    public Model getModel() {
        return (Model) get(MODEL);
    }

    public Collection getImports() {
        return (Collection) get(IMPORTS);
    }

}
