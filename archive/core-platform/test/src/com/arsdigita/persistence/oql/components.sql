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
-- $Id: //core-platform/dev/test/src/com/arsdigita/persistence/oql/components.sql#10 $
-- $DateTime: 2004/04/07 16:07:11 $

create table components (
    component_id INTEGER not null
        constraint component_component_id_p_6gckk
          primary key,
    name VARCHAR(200),
    test_id INTEGER not null
        constraint components_test_id_f_9042c
          references tests(test_id)
)
