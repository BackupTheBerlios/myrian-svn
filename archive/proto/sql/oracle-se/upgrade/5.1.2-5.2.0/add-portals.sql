--
-- Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.redhat.com/licenses/ccmpl.html
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/proto/sql/oracle-se/upgrade/5.1.2-5.2.0/add-portals.sql#2 $
-- $DateTime: 2003/04/09 16:35:55 $

create table portals (
    portal_id INTEGER not null
        constraint portals_portal_id_p_6p_a1
          primary key,
        -- referential constraint for portal_id deferred due to circular dependencies
    template_p CHAR(1),
    title VARCHAR(200)
);
