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
-- $Id: //core-platform/dev/sql/ccm-core/default/kernel/table-ungranted_context_non_leaf_map.sql#1 $
-- $DateTime: 2003/10/23 15:28:18 $

create table ungranted_context_non_leaf_map (
       object_id            integer not null
                            constraint ucnlm_object_id_fk 
                            references acs_objects (object_id),
       implied_context_id   integer constraint ucnlm_implied_context_id_fk
                            references acs_objects(object_id),
       n_generations        integer not null
                            constraint ucnlm_generation_ck
                                check (n_generations >= 0),
       constraint ucnlm_implied_context_pk 
            primary key (object_id, implied_context_id)
);

-- XXX organization index;
