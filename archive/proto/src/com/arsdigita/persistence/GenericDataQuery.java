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

package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.QueryType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.metadata.Operation;
import com.arsdigita.persistence.metadata.Mapping;

/**
 * GenericDataQuery
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 */

public class GenericDataQuery extends DataQueryImpl {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/GenericDataQuery.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    // FIXME: the parameter s is passed in but never used.  -- 2002-11-26
    public GenericDataQuery(Session s, String sql, String[] columns) {
        super(
              new QueryType(
                            "<generic>",
                            new com.arsdigita.persistence.metadata.Event()
                            ),
              new Operation(sql)
              );

        Operation op = getOperation();
        for (int i = 0; i < columns.length; i++) {
            m_type.addProperty(new Property(columns[i],
                                            MetadataRoot.OBJECT));
            op.addMapping(
                          new Mapping(new String[] {columns[i]}, null, columns[i])
                          );
        }
    }

}
