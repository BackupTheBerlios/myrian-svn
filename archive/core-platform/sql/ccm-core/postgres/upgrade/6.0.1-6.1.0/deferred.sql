alter table agentportlets add 
    constraint agentport_superport_id_f_9z3yn foreign key (superportlet_id)
      references portlets(portlet_id);
alter table agentportlets add 
    constraint agentportlet_portle_id_f_vi1h4 foreign key (portlet_id)
      references portlets(portlet_id);
alter table init_requirements add 
    constraint init_require_requ_init_f_i6rgg foreign key (required_init)
      references inits(class_name);
alter table init_requirements add 
    constraint init_requirements_init_f_cmmdn foreign key (init)
      references inits(class_name);
alter table lucene_ids add 
    constraint lucene_ids_host_id_f_fcxb8 foreign key (host_id)
      references web_hosts(host_id);