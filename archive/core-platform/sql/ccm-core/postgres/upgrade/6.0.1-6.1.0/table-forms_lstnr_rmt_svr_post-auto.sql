--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/postgres/upgrade/6.0.1-6.1.0/table-forms_lstnr_rmt_svr_post-auto.sql#4 $
-- $DateTime: 2004/08/16 18:10:38 $
create table forms_lstnr_rmt_svr_post (
    listener_id INTEGER not null
        constraint form_lst_rmt_svr_pos_l_p_d2ck9
          primary key,
        -- referential constraint for listener_id deferred due to circular dependencies
    remove_url VARCHAR(700) not null
);
