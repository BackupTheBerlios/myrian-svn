--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the Open Software License v2.1
-- (the "License"); you may not use this file except in compliance with the
-- License. You may obtain a copy of the License at
-- http://rhea.redhat.com/licenses/osl2.1.html.
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/postgres/upgrade/6.0.1-6.1.0/table-forms_lstnr_rmt_svr_post-auto.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $
create table forms_lstnr_rmt_svr_post (
    listener_id INTEGER not null
        constraint form_lst_rmt_svr_pos_l_p_d2ck9
          primary key,
        -- referential constraint for listener_id deferred due to circular dependencies
    remove_url VARCHAR(700) not null
);
