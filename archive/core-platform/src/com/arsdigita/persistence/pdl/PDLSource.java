package com.arsdigita.persistence.pdl;

/**
 * The PDLSource interface provides a means for loading PDL from a
 * variety of different locations.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/11/06 $
 **/

public interface PDLSource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/PDLSource.java#2 $ by $Author: rhs $, $DateTime: 2003/11/06 00:02:45 $";

    /**
     * Parses the contents of this PDLSource using the given
     * PDLCompiler.
     *
     * @param compiler the PDLCompiler used to parse the contents of
     * this PDLSource
     **/

    void parse(PDLCompiler compiler);

}
