--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of the
-- License at http://www.redhat.com/licenses/ccmpl.html.
--
-- Software distributed under the License is distributed on an
-- "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
-- or implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/upgrade/5.1.2-5.2.0/add-portlets.sql#3 $
-- $DateTime: 2004/04/07 16:07:11 $

create table portlets (
    portlet_id INTEGER not null
        constraint portlets_portlet_id_p_8vw6u
          primary key,
        -- referential constraint for portlet_id deferred due to circular dependencies
    portal_id INTEGER
        -- referential constraint for portal_id deferred due to circular dependencies
);
