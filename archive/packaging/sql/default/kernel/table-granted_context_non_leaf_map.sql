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
-- $Id: //core-platform/test-packaging/sql/default/kernel/table-granted_context_non_leaf_map.sql#2 $
-- $DateTime: 2003/08/19 22:28:24 $


-- context hierarchy is denormalized into 2 tables.
-- The union of these 2 tables contains mapping from objects to their
-- implied contexts.  Every object has itself as an implied context, BUT
-- this implicit mapping is only entered into these denormalizations when
-- the object in question has a permission granted on it.
-- The structure of these denormalizaitons is primarily geared towards
-- optimization of permissions checks.  A secondary objective is to
-- minimize the cost of inserting objects, setting their context, and
-- granting permissions on them.  Finally, these denormalizations may
-- prove useful for permissions UI, e.g., "display all objects that
-- inherit permissions from X".

-- This table holds the mappings between object and implied contexts
-- where the implied contexts have direct grant(s).
create table granted_context_non_leaf_map (
       object_id            integer not null
                            constraint gcnlm_object_id_fk 
                            references acs_objects (object_id),
       implied_context_id   integer constraint gcnlm_implied_context_id_fk
                            references acs_objects(object_id),
       n_generations        integer not null
                            constraint gcnlm_generation_ck
                                check (n_generations >= 0),
       constraint gcnlm_implied_context_pk 
            primary key (object_id, implied_context_id)
);

-- XXX organization index;
