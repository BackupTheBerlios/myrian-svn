--
-- Copyright (C) 2001-2003 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/default/x/versioning/insert-vcx_events.sql#1 $
-- $DateTime: 2003/05/12 18:19:45 $

-- NOTE: this needs to be kept in sync with
-- com.arsdigita.x.versioning.Event

insert into vcx_events (event_id, name) 
values (1, 'create');

insert into vcx_events (event_id, name)
values (2, 'delete');

insert into vcx_events (event_id, name)
values (3, 'add');

insert into vcx_events (event_id, name)
values (4, 'remove');

insert into vcx_events (event_id, name)
values (5, 'set');
