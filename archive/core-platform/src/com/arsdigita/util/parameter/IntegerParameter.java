package com.arsdigita.util.parameter;

import org.apache.commons.beanutils.converters.IntegerConverter;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/IntegerParameter.java#3 $
 */
public class IntegerParameter extends AbstractParameter {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/IntegerParameter.java#3 $" +
        "$Author: jorris $" +
        "$DateTime: 2003/10/28 18:36:21 $";

    static {
        Converters.set(Integer.class, new IntegerConverter());
    }

    public IntegerParameter(final String name) {
        super(name, Integer.class);
    }

    public IntegerParameter(final String name,
                            final int multiplicity,
                            final Object defaalt) {
        super(name, multiplicity, defaalt, Integer.class);
    }
}
