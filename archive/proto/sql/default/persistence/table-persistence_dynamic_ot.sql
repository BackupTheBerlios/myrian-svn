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
-- $Id: //core-platform/proto/sql/default/persistence/table-persistence_dynamic_ot.sql#2 $
-- $DateTime: 2003/04/09 16:35:55 $

create table persistence_dynamic_ot (
    pdl_id                 integer
                           constraint persist_dynamic_ot_pdl_id_fk
                           references acs_objects on delete cascade
                           constraint persist_dynamic_ot_pdl_id_pk
                           primary key,
    pdl_file               clob not null,
    dynamic_object_type    varchar(700) 
                           constraint persist_dynamic_ot_dot_un
                           unique                            
);
