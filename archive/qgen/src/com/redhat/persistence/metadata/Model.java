/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.redhat.persistence.metadata;

import java.util.HashMap;

/**
 * Model
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 **/

public class Model {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/metadata/Model.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

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
