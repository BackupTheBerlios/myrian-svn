/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence.metadata;

import java.io.File;
import java.util.Set;

/**
 * DDLWriter
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #16 $ $Date: 2003/06/29 $
 **/

public class DDLWriter extends com.arsdigita.persistence.proto.pdl.DDLWriter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/DDLWriter.java#16 $ by $Author: dennis $, $DateTime: 2003/06/29 20:13:06 $";

    public DDLWriter(String base,
                     Set files) {
        super(base, files);
    }

    public DDLWriter(String base,
                     Set files,
                     boolean overwrite) {
        super(base, files, overwrite);
    }

    public DDLWriter(File base,
                     Set files,
                     boolean overwrite) {
        super(base, files, overwrite);
    }
}

