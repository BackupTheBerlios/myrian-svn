package com.arsdigita.persistence.pdl;

import java.util.Collection;

/**
 * The PDLFilter interface can be used to filter the contents of a
 * {@link PDLSource} based on filename extension and database suffix.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/01/16 $
 **/

public interface PDLFilter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/PDLFilter.java#3 $ by $Author: ashah $, $DateTime: 2004/01/16 13:01:04 $";

    /**
     * Tests the names for inclusion in the filtered results of a
     * PDLSource.
     *
     * @param names a collection of strings to test
     * @return the collection of accepted names
     **/

    Collection accept(Collection names);

}
