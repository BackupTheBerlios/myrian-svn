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
-- $Id: //core-platform/test-qgen/sql/ccm-core/default/persistence/table-persistence_dynamic_ot.sql#1 $
-- $DateTime: 2003/12/10 16:59:20 $

create table persistence_dynamic_ot (
    pdl_id                 integer
                           constraint persist_dynamic_ot_pdl_id_fk
                           references acs_objects
                           constraint persist_dynamic_ot_pdl_id_pk
                           primary key,
    pdl_file               clob not null,
    dynamic_object_type    varchar(700) 
                           constraint persist_dynamic_ot_dot_un
                           unique                            
);
