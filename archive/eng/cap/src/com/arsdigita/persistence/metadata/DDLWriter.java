/*
 * Copyright (C) 2002-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.arsdigita.persistence.metadata;

import java.io.File;
import java.util.Set;

/**
 * DDLWriter
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class DDLWriter extends com.redhat.persistence.pdl.DDLWriter {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/metadata/DDLWriter.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
