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
-- $Id: //core-platform/proto/sql/default/kernel/table-group_subgroup_trans_index.sql#4 $
-- $DateTime: 2003/08/04 16:15:53 $


create table group_subgroup_trans_index (
	group_id	integer 
                constraint gsti_group_id_nn
                not null
			    constraint gsti_group_id_fk
			    references groups(group_id) on delete cascade,
	subgroup_id	integer
                constraint gsti_subgroup_id_nn
                not null
			    constraint gsti_subgroup_id_fk
			    references groups(group_id) on delete cascade,
    n_paths     integer not null,
	constraint gsti_group_party_pk primary key(group_id, subgroup_id),
    -- This prevents circularity in the group-subgroup graph.
    -- If group_id=subgroup_id then n_paths=0.
	constraint gsti_circularity_ck 
                check ( group_id!=subgroup_id or n_paths=0 ),
    -- This constraint makes sure that we never forget to delete rows when
    -- we decrement n_paths.  n_paths should never reach 0 except for
    -- mappings where group_id=subgroup_id (in which case n_paths should
    -- always be 0 due to above constraint).
    constraint gsti_n_paths_ck
                check (n_paths>0 or group_id=subgroup_id)
);

-- XXX organization index;
