package com.arsdigita.util.parameter;

import java.util.*;

/**
 * CompoundParameterLoader
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/09/23 $
 **/

public class CompoundParameterLoader implements ParameterLoader {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/CompoundParameterLoader.java#1 $ by $Author: rhs $, $DateTime: 2003/09/23 15:22:43 $";

    private final List m_loaders;

    public CompoundParameterLoader() {
        m_loaders = new ArrayList();
    }

    public void add(ParameterLoader loader) {
        m_loaders.add(loader);
    }

    public ParameterValue load(Parameter param) {
        for (Iterator it = m_loaders.iterator(); it.hasNext(); ) {
            ParameterLoader loader = (ParameterLoader) it.next();
            ParameterValue value = loader.load(param);
            if (value != null) { return value; }
        }

        return null;
    }

}
