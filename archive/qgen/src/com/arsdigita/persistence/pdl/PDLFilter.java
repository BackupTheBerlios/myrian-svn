package com.arsdigita.persistence.pdl;

import java.util.Collection;

/**
 * The PDLFilter interface can be used to filter the contents of a
 * {@link PDLSource} based on filename extension and database suffix.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/01/29 $
 **/

public interface PDLFilter {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/arsdigita/persistence/pdl/PDLFilter.java#2 $ by $Author: ashah $, $DateTime: 2004/01/29 12:35:08 $";

    /**
     * Tests the names for inclusion in the filtered results of a
     * PDLSource.
     *
     * @param names a collection of strings to test
     * @return the collection of accepted names
     **/

    Collection accept(Collection names);

}
