package com.arsdigita.persistence.proto.metadata;

import java.util.*;

/**
 * Model
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class Model {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/Model.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    private static final HashMap MODELS = new HashMap();

    public static final Model getInstance(String model) {
        if (model == null) {
            return null;
        }

        Model result;
        
        if (MODELS.containsKey(model)) {
            result = (Model) MODELS.get(model);
        } else {
            synchronized (MODELS) {
                if (MODELS.containsKey(model)) {
                    result = (Model) MODELS.get(model);
                } else {
                    int dot = model.lastIndexOf('.');
                    Model parent;
                    String name;
                    if (dot > -1) {
                        parent = getInstance(model.substring(0, dot));
                        name = model.substring(dot + 1);
                    } else {
                        parent = null;
                        name = model;
                    }

                    result = new Model(parent, name);
                    MODELS.put(model, result);
                }
            }
        }

        return result;
    }

    private Model m_parent;
    private String m_name;

    private Model(Model parent, String name) {
        m_parent = parent;
        m_name = name;
    }

    public Model getParent() {
        return m_parent;
    }

    public String getName() {
        return m_name;
    }

    public String getQualifiedName() {
        if (m_parent == null) {
            return m_name;
        } else {
            return m_parent.getQualifiedName() + "." + m_name;
        }
    }

}
