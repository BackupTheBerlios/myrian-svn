package com.arsdigita.util.parameter;

import org.apache.commons.beanutils.converters.IntegerConverter;

/**
 * Subject to change.
 *
 * A parameter representing a Java <code>Integer</code>.
 *
 * @see java.lang.Integer
 * @see Parameter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/IntegerParameter.java#4 $
 */
public class IntegerParameter extends AbstractParameter {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/IntegerParameter.java#4 $" +
        "$Author: justin $" +
        "$DateTime: 2003/11/10 12:56:08 $";

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
