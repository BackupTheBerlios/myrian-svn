package com.redhat.persistence.metadata;

import java.util.*;

/**
 * Model
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class Model {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/metadata/Model.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

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
