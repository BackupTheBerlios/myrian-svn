package com.arsdigita.persistence.pdl;

/**
 * The PDLFilter interface can be used to filter the contents of a
 * {@link PDLSource} based on filename extension and database suffix.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/11/06 $
 **/

public interface PDLFilter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/PDLFilter.java#2 $ by $Author: rhs $, $DateTime: 2003/11/06 00:02:45 $";

    /**
     * Tests the name for inclusion in the filtered results of a
     * PDLSource.
     *
     * @param name the name to test
     * @return true iff the name is to be included in the filtered result
     **/

    boolean accept(String name);

}
