package com.arsdigita.persistence.pdl;

/**
 * PDLFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/09/26 $
 **/

public interface PDLFilter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/PDLFilter.java#1 $ by $Author: justin $, $DateTime: 2003/09/26 16:16:05 $";

    boolean accept(String name);

}
