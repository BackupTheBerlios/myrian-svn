-- recreate this primary key as the order of columns has changed
alter table acs_permissions drop constraint acs_premissions_pk;
alter table acs_permissions add 
    constraint acs_per_gra_id_pri_obj_p_r_g7i 
    primary key(grantee_id, object_id, privilege);

-- primary key of cat_category_purpose_map has changed
alter table cat_category_purpose_map drop constraint cat_cat_pur_map_pk;
alter table cat_category_purpose_map add 
  constraint cat_cat_pur_map_pk
  primary key(category_id, purpose_id)


--drop unused constraints
alter table apm_package_type_listener_map drop constraint apm_listener_map_id_class_un;
alter table group_member_map drop constraint gmm_group_member_un;
alter table group_subgroup_map drop constraint gsm_group_party_un;
alter table parameterized_privileges drop constraint param_priv_un;
alter table party_email_map drop constraint pem_party_email_uq;
alter table site_nodes drop constraint site_nodes_un;
alter table user_authentication drop constraint user_auth_user_un;
alter table cat_category_purpose_map drop constraint cat_obj_map_purpose_id_fk;
alter table site_nodes drop constraint site_nodes_object_id_fk;

-- add new constraints
alter table acs_stylesheet_node_map add 
    constraint acs_sty_nod_map_nod_id_p_xfcfh
    primary key(node_id, stylesheet_id);

alter table acs_stylesheet_type_map add 
    constraint acs_sty_typ_map_sty_id_p_s85gu
    primary key(package_type_id, stylesheet_id);

alter table apm_package_type_listener_map add
    constraint apm_pac_typ_lis_map_pa_p_rmmpq
    primary key(listener_id, package_type_id);

alter table group_member_map add
    constraint grou_mem_map_mem_id_gr_p__iseb
    primary key(group_id, member_id);

alter table group_subgroup_map add
    constraint grou_sub_map_gro_id_su_p_iylla
    primary key(group_id, subgroup_id);

alter table parameterized_privileges add
    constraint para_pri_par_key_bas_p_p_4uo4r
    primary key(base_privilege, param_key);

alter table party_email_map add
    constraint part_ema_map_ema_add_p_p_aihue
    primary key(email_address, party_id);

alter table site_nodes add 
    constraint site_nod_paren_id_name_u__hhqn
    unique(name, parent_id);

alter table cat_category_purpose_map add
    constraint cat_obj_map_purpose_id_fk
    foreign key (purpose_id) references cat_purposes(purpose_id) on delete cascade;

alter table site_nodes add 
    constraint site_nodes_node_id_f_YBmNJ foreign key (node_id)
    references acs_objects(object_id) on delete cascade;

alter table site_nodes add 
    constraint site_nodes_object_id_f_veZHP foreign key (object_id)
    references apm_packages(package_id);

alter table user_authentication add 
    constraint user_authentica_aut_id_f_LbgaU foreign key (auth_id)
      references parties(party_id) on delete cascade;

---- rename constraints
---- (primary key)
--alter table acs_objects rename constraint acs_objects_pk to acs_objects_object_id_p_shkbb;
--alter table acs_privileges rename constraint acs_privileges_pk to acs_privileg_privilege_p_hopvj;
--alter table acs_stylesheets rename constraint acs_stylesheets_pk to acs_stylesh_stylesh_id_p_ojuvh;
--alter table apm_listeners rename constraint apm_listeners_pk to apm_listene_listene_id_p_9pmtt;
--alter table apm_package_types rename constraint apm_package_types_pk to apm_pac_typ_pac_typ_id_p_qhlyv;
--alter table apm_packages rename constraint apm_packages_pack_id_pk to apm_package_package_id_p_g21sh;
--alter table email_addresses rename constraint email_addresses_pk to emai_addre_ema_address_p_i3q3w;
--alter table groups rename constraint groups_pk to groups_group_id_p_cgkh2;
--alter table object_container_map rename constraint aocm_object_id_pk to obje_contai_map_obj_id_p_ym6mf;
--alter table object_context rename constraint object_context_pk to objec_contex_object_id_p_dcamk;
--alter table parties rename constraint parties_pk to parties_party_id_p_jz589;
--alter table person_names rename constraint person_names_pk to person_names_name_id_p_6_goq;
--alter table roles rename constraint roles_role_id_pk to roles_role_id_p_bw14v;
--alter table site_nodes rename constraint site_nodes_node_id_pk to site_nodes_node_id_p_zm_6r;
--alter table user_authentication rename constraint user_auth_pk to user_authentica_aut_id_p_l_bj_;
--alter table users rename constraint users_pk to users_user_id_p_caf0x;
--alter table vc_blob_operations rename constraint vc_blob_operations_pk to vc_blo_operat_opera_id_p_zdnl0;
--alter table vc_clob_operations rename constraint vc_clob_operations_pk to vc_clo_operat_opera_id_p_eo5ss;
---- (unique)
--alter table apm_listeners rename constraint apm_listeners_class_un to apm_listen_liste_class_u_ccfqm;
--alter table apm_package_types rename constraint apm_packages_types_p_uri_un to apm_pack_typ_packa_uri_u_4s3gd;
--alter table apm_package_types rename constraint apm_package_types_key_un to apm_pack_typ_packa_key_u_85xqm;
--alter table apm_package_types rename constraint apm_package_types_pretty_n_un to apm_pack_typ_pret_name_u_i8_6v;
--alter table apm_package_types rename constraint apm_package_types_pretty_pl_un to apm_pac_typ_pre_plural_u_kbg7r;
--alter table roles rename constraint roles_group_id_name_un to roles_group_id_name_u_gfgin;
--alter table users rename constraint users_screen_name_un to users_screen_name_u_vnqun;
---- (foreign key)
--alter table acs_permissions rename constraint acs_perm_creation_user_fk to acs_permiss_creat_user_f_stjyj;
--alter table acs_permissions rename constraint acs_permissions_grantee_id_fk to acs_permissio_grant_id_f_gxzae;
--alter table acs_permissions rename constraint acs_permissions_on_what_id_fk to acs_permissio_objec_id_f_f37tx;
--alter table acs_permissions rename constraint acs_permissions_priv_fk to acs_permissi_privilege_f_0hg0g;
--alter table acs_stylesheet_node_map rename constraint acs_stylesheet_node_node_fk to acs_sty_nod_map_nod_id_f_bqq1o;
--alter table acs_stylesheet_node_map rename constraint acs_stylesheet_node_sheet_fk to acs_sty_nod_map_sty_id_f_gf05q;
--alter table acs_stylesheet_type_map rename constraint acs_stylesheet_type_sheet_fk to acs_sty_typ_map_sty_id_f_otxia;
--alter table acs_stylesheet_type_map rename constraint acs_stylesheet_type_type_fk to acs_sty_typ_map_pac_ty_f_pmkfa;
--alter table acs_stylesheets rename constraint acs_stylesheet_id_fk to acs_stylesh_stylesh_id_f_cfiz6;
--alter table acs_stylesheets rename constraint acs_stylesheets_locale_fk to acs_styleshee_local_id_f_hu1r2;
--alter table apm_package_type_listener_map rename constraint apm_listener_map_list_id_fk to apm_pac_typ_lis_map_li_f_thi2h;
--alter table apm_package_type_listener_map rename constraint apm_listener_map_pt_id_fk to apm_pac_typ_lis_map_pa_f_avb1h;
--alter table apm_packages rename constraint apm_packages_locale_id_fk to apm_packages_locale_id_f_bw0dp;
--alter table apm_packages rename constraint apm_packages_package_id_fk to apm_package_package_id_f_er8a9;
--alter table apm_packages rename constraint apm_packages_type_id_fk to apm_packa_packa_typ_id_f_aoceh;
--alter table group_member_map rename constraint gmm_group_id_fk to grou_membe_map_grou_id_f_osws8;
--alter table group_member_map rename constraint gmm_member_id_fk to grou_memb_map_membe_id_f_x3ofv;
--alter table group_subgroup_map rename constraint gsm_group_id_fk to grou_subgro_map_gro_id_f_4zz92;
--alter table group_subgroup_map rename constraint gsm_subgroup_id_fk to grou_subg_map_subgr_id_f_muop0;
--alter table groups rename constraint groups_group_id_fk to groups_group_id_f_7pegr;
--alter table object_container_map rename constraint aocm_container_id_fk to obje_cont_map_conta_id_f_vrgxb;
--alter table object_container_map rename constraint aocm_object_id_fk to obje_contai_map_obj_id_f_guaos;
--alter table object_context rename constraint object_context_context_id_fk to objec_contex_contex_id_f_yrosb;
--alter table object_context rename constraint object_context_object_id_fk to objec_contex_object_id_f_xmu8p;
--alter table parameterized_privileges rename constraint param_priv_base_privilege_fk to para_pri_bas_privilege_f_pwbge;
--alter table parties rename constraint parties_party_id_fk to parties_party_id_f_ue6mt;
--alter table party_email_map rename constraint pem_party_id_fk to part_emai_map_party_id_f_svalk;
--alter table roles rename constraint group_roles_group_id_fk to roles_group_id_f_o_9pf;
--alter table roles rename constraint group_roles_impl_group_id_fk to role_implicit_group_id_f_ogrlp;
--alter table site_nodes rename constraint site_nodes_parent_id_fk to site_nodes_parent_id_f_dlnlg;
--alter table user_authentication rename constraint user_auth_user_id_fk to user_authentica_use_id_f_zbugu;
--alter table users rename constraint users_person_name_id_fk to users_name_id_f_axxb8;
--alter table users rename constraint users_user_id_fk to users_user_id_f_evw3z;
--alter table vc_blob_operations rename constraint vc_blob_operations_fk to vc_blo_operat_opera_id_f_bnaum;
--alter table vc_clob_operations rename constraint vc_clob_operations_fk to vc_clo_operat_opera_id_f_vhfnz;

