--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the Open Software License v2.1
-- (the "License"); you may not use this file except in compliance with the
-- License. You may obtain a copy of the License at
-- http://rhea.redhat.com/licenses/osl2.1.html.
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/upgrade/5.1.2-5.2.0/add-portlet-portals-constraints.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

alter table portals add 
    constraint portals_portal_id_f_kbx1t foreign key (portal_id)
      references applications(application_id) on delete cascade;
alter table portlets add 
    constraint portlets_portal_id_f_bombq foreign key (portal_id)
      references portals(portal_id);
alter table portlets add 
    constraint portlets_portlet_id_f_erf4o foreign key (portlet_id)
      references applications(application_id) on delete cascade;
