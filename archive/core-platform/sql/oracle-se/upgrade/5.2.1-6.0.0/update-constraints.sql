-- Remove explicit not null constraints
--------------------------------------------------------------------------------
alter table cw_task_dependencies drop constraint task_dep_id_nn;
alter table cw_task_dependencies drop constraint task_dep_task_id_nn;
alter table cw_task_group_assignees drop constraint task_group_task_id_nn;
alter table cw_task_group_assignees drop constraint group_task_id_nn;
alter table cw_task_listeners drop constraint listen_task_id_nn;
alter table cw_task_listeners drop constraint task_listen_task_id_nn;
alter table cw_task_user_assignees drop constraint task_user_task_id_nn;
alter table cw_task_user_assignees drop constraint user_task_id_nn;

-- Remove 'on delete cascade'
--------------------------------------------------------------------------------
alter table acs_permissions drop constraint acs_permiss_creat_user_f_hiyn9;
alter table acs_permissions add
    constraint acs_permiss_creat_user_f_hiyn9 foreign key (creation_user)
      references users(user_id);

alter table acs_permissions drop constraint acs_permissi_privilege_f_p76ev;
alter table acs_permissions add
    constraint acs_permissi_privilege_f_p76ev foreign key (privilege)
      references acs_privileges(privilege);

alter table acs_permissions drop constraint acs_permissio_grant_id_f_vmo0e;
alter table acs_permissions add
    constraint acs_permissio_grant_id_f_vmo0e foreign key (grantee_id)
      references parties(party_id);

alter table acs_permissions drop constraint acs_permissio_objec_id_f_5swtm;
alter table acs_permissions add
    constraint acs_permissio_objec_id_f_5swtm foreign key (object_id)
      references acs_objects(object_id);

alter table acs_stylesheet_node_map drop constraint acs_sty_nod_map_nod_id_f_q55q3;
alter table acs_stylesheet_node_map add
    constraint acs_sty_nod_map_nod_id_f_q55q3 foreign key (node_id)
      references site_nodes(node_id);

alter table acs_stylesheet_node_map drop constraint acs_sty_nod_map_sty_id_f_guej5;
alter table acs_stylesheet_node_map add
    constraint acs_sty_nod_map_sty_id_f_guej5 foreign key (stylesheet_id)
      references acs_stylesheets(stylesheet_id);

alter table acs_stylesheet_type_map drop constraint acs_sty_typ_map_pac_ty_f_emkua;
alter table acs_stylesheet_type_map add
    constraint acs_sty_typ_map_pac_ty_f_emkua foreign key (package_type_id)
      references apm_package_types(package_type_id);

alter table acs_stylesheet_type_map drop constraint acs_sty_typ_map_sty_id_f_38x8p;
alter table acs_stylesheet_type_map add
    constraint acs_sty_typ_map_sty_id_f_38x8p foreign key (stylesheet_id)
      references acs_stylesheets(stylesheet_id);

alter table acs_stylesheets drop constraint acs_stylesh_stylesh_id_f_2fiok;
alter table acs_stylesheets add
    constraint acs_stylesh_stylesh_id_f_2fiok foreign key (stylesheet_id)
      references acs_objects(object_id);

alter table acs_stylesheets drop constraint acs_styleshee_local_id_f_wjfrg;
alter table acs_stylesheets add
    constraint acs_styleshee_local_id_f_wjfrg foreign key (locale_id)
      references g11n_locales(locale_id);

alter table apm_package_type_listener_map drop constraint apm_pac_typ_lis_map_li_f_i78gw;
alter table apm_package_type_listener_map add
    constraint apm_pac_typ_lis_map_li_f_i78gw foreign key (listener_id)
      references apm_listeners(listener_id);

alter table apm_package_type_listener_map drop constraint apm_pac_typ_lis_map_pa_f_0_qfw;
alter table apm_package_type_listener_map add
    constraint apm_pac_typ_lis_map_pa_f_0_qfw foreign key (package_type_id)
      references apm_package_types(package_type_id);

alter table apm_packages drop constraint apm_packa_packa_typ_id_f_adr4w;
alter table apm_packages add
    constraint apm_packa_packa_typ_id_f_adr4w foreign key (package_type_id)
      references apm_package_types(package_type_id);

alter table apm_packages drop constraint apm_package_package_id_f_46may;
alter table apm_packages add
    constraint apm_package_package_id_f_46may foreign key (package_id)
      references acs_objects(object_id);

alter table apm_packages drop constraint apm_packages_locale_id_f_qlps4;
alter table apm_packages add
    constraint apm_packages_locale_id_f_qlps4 foreign key (locale_id)
      references g11n_locales(locale_id);

alter table application_type_privilege_map drop constraint appl_typ_pri_map_app_t_f_kgrfj;
alter table application_type_privilege_map add
    constraint appl_typ_pri_map_app_t_f_kgrfj foreign key (application_type_id)
      references application_types(application_type_id);

alter table application_type_privilege_map drop constraint appl_typ_pri_map_privi_f_s3pwb;
alter table application_type_privilege_map add
    constraint appl_typ_pri_map_privi_f_s3pwb foreign key (privilege)
      references acs_privileges(privilege);

alter table application_types drop constraint applica_typ_pac_typ_id_f_v80ma;
alter table application_types add
    constraint applica_typ_pac_typ_id_f_v80ma foreign key (package_type_id)
      references apm_package_types(package_type_id);

alter table application_types drop constraint applicat_typ_provid_id_f_bm274;
alter table application_types add
    constraint applicat_typ_provid_id_f_bm274 foreign key (provider_id)
      references application_types(application_type_id);

alter table applications drop constraint applica_applica_typ_id_f_k2bi3;
alter table applications add
    constraint applica_applica_typ_id_f_k2bi3 foreign key (application_type_id)
      references application_types(application_type_id);

alter table applications drop constraint applica_par_applica_id_f_hvxh7;
alter table applications add
    constraint applica_par_applica_id_f_hvxh7 foreign key (parent_application_id)
      references applications(application_id);

alter table applications drop constraint applicati_applicati_id_f_a35g2;
alter table applications add
    constraint applicati_applicati_id_f_a35g2 foreign key (application_id)
      references acs_objects(object_id);

alter table applications drop constraint application_package_id_f_cdaho;
alter table applications add
    constraint application_package_id_f_cdaho foreign key (package_id)
      references apm_packages(package_id);

alter table bebop_form_process_listeners drop constraint bebop_form_process_lstnr_fs_fk;
alter table bebop_form_process_listeners add
    constraint bebop_form_process_lstnr_fs_fk foreign key (form_section_id)
      references bebop_form_sections;

alter table bebop_form_process_listeners drop constraint bebop_form_process_lstnr_li_fk;
alter table bebop_form_process_listeners add
    constraint bebop_form_process_lstnr_li_fk foreign key (listener_id)
      references bebop_process_listeners;

alter table bebop_meta_object drop constraint bebop_meta_obj_object_id_fk;
alter table bebop_meta_object add
    constraint bebop_meta_obj_object_id_fk foreign key (object_id)
      references acs_objects;

alter table bebop_object_type drop constraint bebop_object_type_type_id_fk;
alter table bebop_object_type add
    constraint bebop_object_type_type_id_fk foreign key (type_id)
      references acs_objects;

alter table bebop_process_listeners drop constraint bebop_process_listeners_fk;
alter table bebop_process_listeners add
    constraint bebop_process_listeners_fk foreign key (listener_id)
      references acs_objects(object_id);

alter table cat_categories drop constraint cat_categories_fk;
alter table cat_categories add
    constraint cat_categori_catego_id_f__xtwr foreign key (category_id)
      references acs_objects(object_id);

alter table cat_category_category_map drop constraint cat_cat_map_category_id_fk;
alter table cat_category_category_map add
    constraint cat_cat_map_category_id_fk foreign key (related_category_id)
      references cat_categories(category_id);

alter table cat_category_category_map drop constraint cat_cat_map_parent_id_fk;
alter table cat_category_category_map add
    constraint cat_cat_map_parent_id_fk foreign key (category_id)
      references cat_categories(category_id);

alter table cat_category_purpose_map drop constraint cat_cat_pur_map_cat_id_fk;
alter table cat_category_purpose_map add
    constraint cat_cat_pur_map_cat_id_fk foreign key (category_id)
      references cat_categories(category_id);

alter table cat_category_purpose_map drop constraint cat_obj_map_purpose_id_fk;
alter table cat_category_purpose_map add
    constraint cat_obj_map_purpose_id_fk foreign key (purpose_id)
      references cat_purposes(purpose_id);

alter table cat_object_category_map drop constraint cat_obj_cat_map_cat_id_fk;
alter table cat_object_category_map add
    constraint cat_obj_cat_map_cat_id_fk foreign key (category_id)
      references cat_categories(category_id);

alter table cat_object_category_map drop constraint cat_obj_map_object_id_fk;
alter table cat_object_category_map add
    constraint cat_obj_map_object_id_fk foreign key (object_id)
      references acs_objects(object_id);

alter table cat_purposes drop constraint cat_purposes_purpose_id_fk;
alter table cat_purposes add
    constraint cat_purposes_purpose_id_fk foreign key (purpose_id)
      references acs_objects(object_id);

alter table cat_root_cat_object_map drop constraint cat_roo_cat_obj_map_ca_f_jqvmd;
alter table cat_root_cat_object_map add
    constraint cat_roo_cat_obj_map_ca_f_jqvmd foreign key (category_id)
      references cat_categories(category_id);

alter table cat_root_cat_object_map drop constraint cat_roo_cat_obj_map_ob_f_anfmx;
alter table cat_root_cat_object_map add
    constraint cat_roo_cat_obj_map_ob_f_anfmx foreign key (object_id)
      references acs_objects(object_id);

alter table cw_processes drop constraint processes_object_fk;
alter table cw_processes add
    constraint processes_object_fk foreign key (object_id)
      references acs_objects(object_id);

alter table cw_task_comments drop constraint task_comments_task_id_fk;
alter table cw_task_comments add
    constraint task_comments_task_id_fk foreign key (task_id)
      references cw_tasks(task_id);

alter table cw_task_dependencies drop constraint task_def_id_fk;
alter table cw_task_dependencies add
    constraint cw_tas_depe_dep_tas_id_f_bn0m5 foreign key (dependent_task_id)
      references cw_tasks(task_id);

alter table cw_task_dependencies drop constraint task_dep_task_id_fk;
alter table cw_task_dependencies add
    constraint cw_tas_dependen_tas_id_f_b1uoz foreign key (task_id)
      references cw_tasks(task_id);

alter table cw_task_group_assignees drop constraint group_task_id_fk;
alter table cw_task_group_assignees add
    constraint cw_tas_gro_assi_gro_id_f_or5kj foreign key (group_id)
      references groups(group_id);

alter table cw_task_group_assignees drop constraint task_group_task_id_fk;
alter table cw_task_group_assignees add
    constraint cw_tas_gro_assi_tas_id_f_mhi2k foreign key (task_id)
      references cw_user_tasks(task_id);

alter table cw_task_listeners drop constraint listen_task_id_fk;
alter table cw_task_listeners add
    constraint cw_tas_list_lis_tas_id_f_x1n02 foreign key (listener_task_id)
      references cw_tasks(task_id);

alter table cw_task_listeners drop constraint task_listen_task_id_fk;
alter table cw_task_listeners add
    constraint cw_tas_listener_tas_id_f_s2fj9 foreign key (task_id)
      references cw_tasks(task_id);

alter table cw_task_user_assignees drop constraint user_task_id_fk;
alter table cw_task_user_assignees add
    constraint cw_tas_use_assi_use_id_f_w856_ foreign key (user_id)
      references users(user_id);

alter table cw_user_tasks drop constraint user_tasks_task_id_fk;
alter table cw_user_tasks add
    constraint user_tasks_task_id_fk foreign key (task_id)
      references cw_tasks(task_id);

alter table forms_dataquery drop constraint forms_dq_query_id_fk;
alter table forms_dataquery add
    constraint forms_dq_query_id_fk foreign key (query_id)
      references acs_objects(object_id);

alter table forms_dd_select drop constraint forms_dds_widget_id_fk;
alter table forms_dd_select add
    constraint forms_dds_widget_id_fk foreign key (widget_id)
      references bebop_widgets(widget_id);

alter table forms_lstnr_conf_email drop constraint forms_lstnr_conf_email_fk;
alter table forms_lstnr_conf_email add
    constraint forms_lstnr_conf_email_fk foreign key (listener_id)
      references bebop_process_listeners(listener_id);

alter table forms_lstnr_conf_redirect drop constraint forms_lstnr_conf_redirect_fk;
alter table forms_lstnr_conf_redirect add
    constraint forms_lstnr_conf_redirect_fk foreign key (listener_id)
      references bebop_process_listeners(listener_id);

alter table forms_lstnr_simple_email drop constraint forms_lstnr_simple_email_fk;
alter table forms_lstnr_simple_email add
    constraint forms_lstnr_simple_email_fk foreign key (listener_id)
      references bebop_process_listeners(listener_id);

alter table forms_lstnr_tmpl_email drop constraint forms_lstnr_tmpl_email_fk;
alter table forms_lstnr_tmpl_email add
    constraint forms_lstnr_tmpl_email_fk foreign key (listener_id)
      references bebop_process_listeners(listener_id);

alter table forms_lstnr_xml_email drop constraint forms_lstnr_xml_email_fk;
alter table forms_lstnr_xml_email add
    constraint forms_lstnr_xml_email_fk foreign key (listener_id)
      references bebop_process_listeners(listener_id);

alter table forms_widget_label drop constraint forms_wgt_label_label_id_fk;
alter table forms_widget_label add
    constraint forms_wgt_label_label_id_fk foreign key (label_id)
      references bebop_widgets(widget_id);

alter table group_member_map drop constraint grou_memb_map_membe_id_f_bs3u_;
alter table group_member_map add
    constraint grou_memb_map_membe_id_f_bs3u_ foreign key (member_id)
      references users(user_id);

alter table group_member_map drop constraint grou_membe_map_grou_id_f_d7lhm;
alter table group_member_map add
    constraint grou_membe_map_grou_id_f_d7lhm foreign key (group_id)
      references groups(group_id);

alter table group_subgroup_map drop constraint grou_subg_map_subgr_id_f_1jo4e;
alter table group_subgroup_map add
    constraint grou_subg_map_subgr_id_f_1jo4e foreign key (subgroup_id)
      references groups(group_id);

alter table group_subgroup_map drop constraint grou_subgro_map_gro_id_f_todnr;
alter table group_subgroup_map add
    constraint grou_subgro_map_gro_id_f_todnr foreign key (group_id)
      references groups(group_id);

alter table groups drop constraint groups_group_id_f_l4tvr;
alter table groups add
    constraint groups_group_id_f_l4tvr foreign key (group_id)
      references parties(party_id);

alter table message_parts drop constraint message_parts_message_id_fk;
alter table message_parts add
    constraint message_parts_message_id_fk foreign key (message_id)
      references messages(message_id);

alter table messages drop constraint messages_message_id_fk;
alter table messages add
    constraint messages_message_id_fk foreign key (message_id)
      references acs_objects(object_id);

alter table object_container_map drop constraint obje_cont_map_conta_id_f_v66b1;
alter table object_container_map add
    constraint obje_cont_map_conta_id_f_v66b1 foreign key (container_id)
      references acs_objects(object_id);

alter table object_container_map drop constraint obje_contai_map_obj_id_f_guads;
alter table object_container_map add
    constraint obje_contai_map_obj_id_f_guads foreign key (object_id)
      references acs_objects(object_id);

alter table object_context drop constraint objec_contex_contex_id_f_crdh1;
alter table object_context add
    constraint objec_contex_contex_id_f_crdh1 foreign key (context_id)
      references acs_objects(object_id);

alter table object_context drop constraint objec_contex_object_id_f_mbuxe;
alter table object_context add
    constraint objec_contex_object_id_f_mbuxe foreign key (object_id)
      references acs_objects(object_id);

alter table parameterized_privileges drop constraint para_pri_bas_privilege_f_elb6t;
alter table parameterized_privileges add
    constraint para_pri_bas_privilege_f_elb6t foreign key (base_privilege)
      references acs_privileges(privilege);

alter table parties drop constraint parties_party_id_f_j4k1i;
alter table parties add
    constraint parties_party_id_f_j4k1i foreign key (party_id)
      references acs_objects(object_id);

alter table party_email_map drop constraint part_emai_map_party_id_f_7_00_;
alter table party_email_map add
    constraint part_emai_map_party_id_f_7_00_ foreign key (party_id)
      references parties(party_id);

alter table persistence_dynamic_assoc drop constraint pers_dyn_assoc_pdl_id_fk;
alter table persistence_dynamic_assoc add
    constraint pers_dyn_assoc_pdl_id_fk foreign key (pdl_id)
      references acs_objects(object_id);

alter table persistence_dynamic_ot drop constraint persist_dynamic_ot_pdl_id_fk;
alter table persistence_dynamic_ot add
    constraint persist_dynamic_ot_pdl_id_fk foreign key (pdl_id)
      references acs_objects(object_id);

alter table portals drop constraint portals_portal_id_f_kbx1t;
alter table portals add
    constraint portals_portal_id_f_kbx1t foreign key (portal_id)
      references applications(application_id);

alter table portlets drop constraint portlets_portal_id_f_bombq;
alter table portlets add
    constraint portlets_portal_id_f_bombq foreign key (portal_id)
      references portals(portal_id);

alter table portlets drop constraint portlets_portlet_id_f_erf4o;
alter table portlets add
    constraint portlets_portlet_id_f_erf4o foreign key (portlet_id)
      references applications(application_id);

alter table preferences drop constraint preferences_parent_fk;
alter table preferences add
    constraint preferences_parent_fk foreign key (parent_id)
      references preferences(preference_id);

alter table roles drop constraint role_implicit_group_id_f_o6g0p;
alter table roles add
    constraint role_implicit_group_id_f_o6g0p foreign key (implicit_group_id)
      references groups(group_id);

alter table roles drop constraint roles_group_id_f_doyeu;
alter table roles add
    constraint roles_group_id_f_doyeu foreign key (group_id)
      references groups(group_id);

alter table site_nodes drop constraint site_nodes_node_id_f_n1m2y;
alter table site_nodes add
    constraint site_nodes_node_id_f_n1m2y foreign key (node_id)
      references acs_objects(object_id);

alter table site_nodes drop constraint site_nodes_object_id_f_ked74;
alter table site_nodes add
    constraint site_nodes_object_id_f_ked74 foreign key (object_id)
      references apm_packages(package_id);

alter table site_nodes drop constraint site_nodes_parent_id_f_sacav;
alter table site_nodes add
    constraint site_nodes_parent_id_f_sacav foreign key (parent_id)
      references site_nodes(node_id);

alter table user_authentication drop constraint user_authentica_use_id_f_z1jvj;
alter table user_authentication add
    constraint user_authentica_use_id_f_z1jvj foreign key (user_id)
      references users(user_id);

alter table user_authentication drop constraint user_authentica_aut_id_f_0bgpj;
alter table user_authentication add
    constraint user_authentica_aut_id_f_0bgpj foreign key (auth_id)
      references parties(party_id);

alter table users drop constraint users_name_id_f_0xbbm;
alter table users add
    constraint users_name_id_f_0xbbm foreign key (name_id)
      references person_names(name_id);

alter table users drop constraint users_user_id_f_t_lso;
alter table users add
    constraint users_user_id_f_t_lso foreign key (user_id)
      references parties(party_id);


-- Fix Constraint Ordering
-- NOTE: The following ddl assumes that no tables have a referential constraint against one
--       of these tables.  This holds for core and *should* hold in general as these tables
--       are unlikely cadidates for foreign keys.
--------------------------------------------------------------------------------
alter table acs_permissions drop constraint acs_per_gra_id_obj_id__p_lrweb;
alter table acs_permissions add
    constraint acs_per_gra_id_obj_id__p_lrweb
      primary key(object_id, grantee_id, privilege);

alter table acs_stylesheet_type_map drop constraint acs_sty_typ_map_pac_ty_p_afjeo;
alter table acs_stylesheet_type_map add
    constraint acs_sty_typ_map_pac_ty_p_afjeo
      primary key(stylesheet_id, package_type_id);

alter table apm_package_type_listener_map drop constraint apm_pac_typ_lis_map_li_p_6_z6o;
alter table apm_package_type_listener_map add
    constraint apm_pac_typ_lis_map_li_p_6_z6o
      primary key(package_type_id, listener_id);

alter table cw_task_group_assignees drop constraint task_group_assignees_pk;
alter table cw_task_group_assignees add
    constraint cw_tas_gro_ass_gro_id__p_0bqv_
        primary key(group_id, task_id);

alter table cw_task_listeners drop constraint task_listeners_pk;
alter table cw_task_listeners add
    constraint cw_tas_lis_lis_tas_id__p_cl43z
        primary key(listener_task_id, task_id);

alter table group_member_map drop constraint grou_mem_map_gro_id_me_p_9zo_i;
alter table group_member_map add
    constraint grou_mem_map_gro_id_me_p_9zo_i
      primary key(member_id, group_id);

alter table group_subgroup_map drop constraint grou_sub_map_gro_id_su_p_8caa0;
alter table group_subgroup_map add
    constraint grou_sub_map_gro_id_su_p_8caa0
      primary key(subgroup_id, group_id);

alter table object_container_map drop constraint obje_contai_map_obj_id_p_ymkb5;
alter table object_container_map add
    constraint obje_con_map_con_id_ob_p_ul6se
      primary key(object_id, container_id);

alter table parameterized_privileges drop constraint para_pri_bas_pri_par_k_p_a1rpb;
alter table parameterized_privileges add
    constraint para_pri_bas_pri_par_k_p_a1rpb
      primary key(param_key, base_privilege);

alter table party_email_map drop constraint part_ema_map_ema_add_p_p_px7u4;
alter table party_email_map add
    constraint part_ema_map_ema_add_p_p_px7u4
      primary key(party_id, email_address);

alter table site_nodes drop constraint site_node_nam_paren_id_u_a3b4a;
alter table site_nodes add
    constraint site_node_nam_paren_id_u_a3b4a
      unique(parent_id, name);

-- Actions that require PL/SQL
----------------------------------------------------------------------------------
declare
  version varchar2(4000);
  compatibility varchar2(4000);
  v_constraint_name varchar2(4000);
begin

  -- Find and fix cw_task_user_assignees fk constraint
  --------------------------------------------------------------------------------
  select constraint_name into v_constraint_name
    from user_constraints uc
   where lower(table_name) = 'cw_task_user_assignees'
     and constraint_type = 'R'
     and exists (select 1
                   from user_cons_columns ucc
                  where ucc.constraint_name = uc.constraint_name
                    and lower(column_name) = 'task_id'
                    and position = 1);

  if (v_constraint_name is not null) then
    execute immediate 'alter table cw_task_user_assignees drop constraint ' ||  v_constraint_name;
    execute immediate 'alter table cw_task_user_assignees add constraint cw_tas_use_assi_tas_id_f_feri7 foreign key(task_id) references cw_user_tasks(task_id)';
  end if;

  -- Rename constraints
  --------------------------------------------------------------------------------
  DBMS_UTILITY.DB_VERSION (version, compatibility);
  if (compatibility >= '9.2.0.0.0') then
    -- The following ddl will only work on Oracle 9.2 or greater
    execute immediate 'alter table cat_categories rename constraint cat_categories_pk to cat_categori_catego_id_p_yeprq';
    execute immediate 'alter table cw_task_dependencies rename constraint task_dependencies_pk to cw_tas_dep_dep_tas_id__p_hdzws';
    execute immediate 'alter table cw_task_user_assignees rename constraint task_user_assignees_pk to cw_tas_use_ass_tas_id__p_vsdyq';
  end if;
end;
/
show errors;


