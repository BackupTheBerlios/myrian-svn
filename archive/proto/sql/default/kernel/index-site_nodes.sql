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
-- $Id: //core-platform/proto/sql/default/kernel/index-site_nodes.sql#4 $
-- $DateTime: 2003/08/04 16:15:53 $

create index site_nodes_object_id_idx on site_nodes (object_id);
create index site_nodes_parent_id_idx on site_nodes (parent_id);

create unique index site_nodes_url_idx on site_nodes (url);
