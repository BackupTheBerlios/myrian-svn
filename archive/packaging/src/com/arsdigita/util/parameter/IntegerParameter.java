package com.arsdigita.util.parameter;

import com.arsdigita.util.*;
import java.util.*;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/IntegerParameter.java#1 $
 */
public class IntegerParameter extends StringParameter {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/IntegerParameter.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/08/26 11:56:51 $";

    public IntegerParameter(final String name) {
        super(name);
    }

    public List validate(final ParameterStore store) {
        final String value = store.read(this);
        final List errors = super.validate(store);

        if (value != null) {
            try {
                Integer.valueOf(value);
            } catch (NumberFormatException nfe) {
                addError(errors, nfe.getMessage());
            }
        }

        return errors;
    }

    protected Object unmarshal(final String value) {
        return Integer.valueOf(value);
    }
}
