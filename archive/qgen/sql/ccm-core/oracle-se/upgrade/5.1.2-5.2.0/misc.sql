--
-- Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/test-qgen/sql/ccm-core/oracle-se/upgrade/5.1.2-5.2.0/misc.sql#1 $
-- $DateTime: 2003/12/10 16:59:20 $

alter table acs_permissions modify ( 
    creation_date default null
);

alter table acs_stylesheet_node_map modify (
    stylesheet_id not null,
    node_id not null
);

alter table acs_stylesheets modify (
    output_type default null
);

alter table acs_stylesheet_type_map modify (
    stylesheet_id not null,
    package_type_id not null
);

alter table apm_package_types modify (
    dispatcher_class default null
);

alter table email_addresses modify (
    bouncing_p not null,
    verified_p not null
);

alter table party_email_map modify (
    email_address not null
);

alter table preferences modify (
    is_node number
);

alter table roles modify (
    group_id null
);

alter table site_nodes modify (
    directory_p default null,
    pattern_p default null
);

alter table vc_transactions modify (
    timestamp date default sysdate
);

alter table group_member_map drop column id;
alter table group_subgroup_map drop column id;
