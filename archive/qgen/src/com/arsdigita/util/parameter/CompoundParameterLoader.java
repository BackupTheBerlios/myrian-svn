package com.arsdigita.util.parameter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @deprecated Use {@link
 * com.arsdigita.util.parameter.CompoundParameterReader} instead.
 **/

public class CompoundParameterLoader implements ParameterLoader {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/arsdigita/util/parameter/CompoundParameterLoader.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    private final List m_loaders;

    public CompoundParameterLoader() {
        m_loaders = new ArrayList();
    }

    public void add(ParameterLoader loader) {
        m_loaders.add(loader);
    }

    public String read(final Parameter param, final ErrorList errors) {
        for (final Iterator it = m_loaders.iterator(); it.hasNext(); ) {
            final ParameterReader reader = (ParameterReader) it.next();

            final String result = reader.read(param, errors);

            if (result != null) {
                return result;
            }
        }

        return null;
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
