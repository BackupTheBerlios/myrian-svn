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
-- $Id: //core-platform/dev/sql/oracle-se/workflow/table-cw_task_comments.sql#3 $
-- $DateTime: 2003/05/21 12:25:09 $

create table cw_task_comments (
  comment_id		  integer
			  constraint task_comments_comment_id_nn not null,
  task_id                 integer
			  constraint task_comments_task_id_nn not null
			  constraint task_comments_task_id_fk 
			  references cw_tasks(task_id),
  task_comment		  varchar(4000),
  comment_date		  date default sysdate,
  party_id		  integer,     
  --
  -- allow same comment on many tasks, many comments on same task
  constraint task_comments_pk
  primary key (comment_id, task_id)
);
