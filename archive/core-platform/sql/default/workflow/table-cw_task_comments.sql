create table cw_task_comments (
  comment_id		  integer
			  constraint task_comments_comment_id_nn not null,
  task_id                 integer
			  constraint task_comments_task_id_nn not null
			  constraint task_comments_task_id_fk 
			  references cw_tasks(task_id) on delete cascade,
  task_comment		  varchar(4000),
  comment_date		  date default sysdate,
  party_id		  integer,     
  --
  -- allow same comment on many tasks, many comments on same task
  constraint task_comments_pk
  primary key (comment_id, task_id)
);
