/*
 * Copyright (C) 2002-2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.arsdigita.persistence.metadata;

import java.io.File;
import java.util.Set;

/**
 * DDLWriter
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2004/10/01 $
 **/

public class DDLWriter extends org.myrian.persistence.pdl.DDLWriter {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/metadata/DDLWriter.java#4 $ by $Author: vadim $, $DateTime: 2004/10/01 18:41:18 $";

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
