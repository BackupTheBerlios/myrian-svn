package com.arsdigita.persistence.pdl;

/**
 * PDLSource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/09/11 $
 **/

public interface PDLSource {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/pdl/PDLSource.java#1 $ by $Author: rhs $, $DateTime: 2003/09/11 14:54:54 $";

    void parse(PDLCompiler compiler);

}
