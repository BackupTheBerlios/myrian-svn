alter table portals add 
    constraint portals_portal_id_f_kXxME foreign key (portal_id)
      references applications(application_id) on delete cascade;

alter table portlets add 
    constraint portlets_portal_id_f_bo8Mb foreign key (portal_id)
      references portals(portal_id);

alter table portlets add 
    constraint portlets_portlet_id_f_pr1ez foreign key (portlet_id)
      references applications(application_id) on delete cascade;
