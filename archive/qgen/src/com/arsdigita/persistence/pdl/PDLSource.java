package com.arsdigita.persistence.pdl;

/**
 * The PDLSource interface provides a means for loading PDL from a
 * variety of different locations.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 **/

public interface PDLSource {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/arsdigita/persistence/pdl/PDLSource.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    /**
     * Parses the contents of this PDLSource using the given
     * PDLCompiler.
     *
     * @param compiler the PDLCompiler used to parse the contents of
     * this PDLSource
     **/

    void parse(PDLCompiler compiler);

}
