-- We have to drop and recreate the old primary key before we can add the new index.

alter table cw_task_listeners drop constraint task_listeners_pk;
alter table cw_task_listeners add
    constraint cw_tas_lis_lis_tas_id__p_cl43z
        primary key(listener_task_id, task_id);

create index cw_task_listeners_tid_ltid_idx on cw_task_listeners(task_id, listener_task_id);