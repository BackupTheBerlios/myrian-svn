package com.arsdigita.persistence.pdl;

import com.arsdigita.persistence.metadata.*;

import java.io.Reader;

/**
 * PDLCompiler
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/09/10 $
 **/

public class PDLCompiler {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/pdl/PDLCompiler.java#1 $ by $Author: rhs $, $DateTime: 2003/09/10 10:46:29 $";

    private final com.redhat.persistence.pdl.PDL m_pdl;

    public PDLCompiler() {
        m_pdl = new com.redhat.persistence.pdl.PDL();
    }

    public void parse(String name, Reader contents) {
        m_pdl.load(contents, name);
    }

    public void emit(MetadataRoot root) {
        m_pdl.emit(root.getRoot());
        m_pdl.emitVersioned();
        MetadataRoot.loadPrimitives();
    }

}
