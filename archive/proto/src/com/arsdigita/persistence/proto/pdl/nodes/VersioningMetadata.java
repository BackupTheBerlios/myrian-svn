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

package com.arsdigita.persistence.proto.pdl.nodes;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Versioning metadata.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-02-18
 * @version $Revision: #1 $ $Date: 2003/02/18 $
 */
public class VersioningMetadata {
    private final static Logger LOG =
        Logger.getLogger(VersioningMetadata.class);

    private final Node.Switch m_switch;
    private final Set m_versionedTypes;

    private final static VersioningMetadata ROOT = new VersioningMetadata();

    private VersioningMetadata() {
        m_versionedTypes = new HashSet();
        m_switch = new Node.Switch() {
                public void onObjectType(ObjectTypeNd ot) {
                    if ( ot.getVersioned() != null ) {
                        LOG.info("emitVersioned: " + ot.getName() +
                                 " is versioned.", new Throwable());
                        // FIXME: should probably add the corresponding instance
                        // of ObjectType instead. -- vadimn@redhat.com,
                        // 2003-02-18
                        m_versionedTypes.add(ot);
                    }
                }
            };
    }

    public static VersioningMetadata getVersioningMetadata() {
        return ROOT;
    }

    public Node.Switch nodeSwitch() {
        return m_switch;
    }
}

