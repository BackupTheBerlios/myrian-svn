package com.arsdigita.persistence.pdl;

/**
 * PDLFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/09/12 $
 **/

public interface PDLFilter {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/pdl/PDLFilter.java#1 $ by $Author: rhs $, $DateTime: 2003/09/12 10:06:13 $";

    boolean accept(String name);

}
