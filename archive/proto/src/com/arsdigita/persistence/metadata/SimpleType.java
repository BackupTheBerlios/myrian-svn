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

import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Collection;

/**
 * The SimpleType class is the base class for all the primative DataTypes
 * that the persistence layer knows how to store. These simple types serve as
 * the atoms from which compound types are built.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2003/04/23 $
 */

public class SimpleType extends DataType {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/metadata/SimpleType.java#3 $ by $Author: vadim $, $DateTime: 2003/04/23 09:49:19 $";

    static final SimpleType
	wrap(com.arsdigita.persistence.proto.metadata.ObjectType obj) {
	if (obj == null) {
	    return null;
	} else {
	    return new SimpleType(obj);
	}
    }

    private com.arsdigita.persistence.proto.metadata.ObjectType m_type;

    /**
     * Constructs a new SimpleType with the given name.
     **/

    private SimpleType
	(com.arsdigita.persistence.proto.metadata.ObjectType obj) {
        super(obj);
	m_type = obj;
    }

    public Class getJavaClass() {
	return m_type.getJavaClass();
    }

}
