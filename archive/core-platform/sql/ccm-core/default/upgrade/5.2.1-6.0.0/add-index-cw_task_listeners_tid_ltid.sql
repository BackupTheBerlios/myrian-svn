--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
--
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public License
-- as published by the Free Software Foundation; either version 2.1 of
-- the License, or (at your option) any later version.
--
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--
-- $Id: //core-platform/dev/sql/ccm-core/default/upgrade/5.2.1-6.0.0/add-index-cw_task_listeners_tid_ltid.sql#4 $
-- $DateTime: 2004/08/16 18:10:38 $


alter table cw_task_listeners drop constraint task_listeners_pk;
alter table cw_task_listeners add
    constraint cw_tas_lis_lis_tas_id__p_cl43z
        primary key(listener_task_id, task_id);

create index cw_task_listeners_tid_ltid_idx on cw_task_listeners(task_id, listener_task_id);
