--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/default/upgrade/5.2.1-6.0.0/insert-vcx_event_types.sql#3 $
-- $DateTime: 2004/04/07 16:07:11 $

-- NOTE: this needs to be kept in sync with
-- com.arsdigita.x.versioning.Event

insert into vcx_event_types (id, name) 
values (1, 'create');

insert into vcx_event_types (id, name)
values (2, 'delete');

insert into vcx_event_types (id, name)
values (3, 'add');

insert into vcx_event_types (id, name)
values (4, 'remove');

insert into vcx_event_types (id, name)
values (5, 'set');
