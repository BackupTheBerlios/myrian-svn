@@ default/globalization/table-g11n_charsets.sql
@@ default/globalization/index-g11n_charsets.sql
@@ default/globalization/table-g11n_locales.sql
@@ default/globalization/index-g11n_locales.sql
@@ default/globalization/table-g11n_locale_charset_map.sql
@@ default/globalization/index-g11n_locale_charset_map.sql
@@ default/globalization/table-g11n_catalogs.sql
@@ default/globalization/index-g11n_catalogs.sql



@@ oracle-se/preferences/table-preferences.sql
@@ default/preferences/index-preferences.sql
@@ default/preferences/comment-preferences.sql



@@ default/kernel/sequence-acs_object_id_seq.sql
@@ default/kernel/table-acs_objects.sql
@@ default/kernel/index-acs_objects.sql
@@ default/kernel/table-object_container_map.sql
@@ default/kernel/index-object_container_map.sql

@@ default/kernel/table-email_addresses.sql
@@ default/kernel/comment-email_addresses.sql
@@ default/kernel/table-parties.sql
@@ default/kernel/index-parties.sql
@@ default/kernel/comment-parties.sql
@@ default/kernel/table-party_email_map.sql
@@ default/kernel/comment-party_email_map.sql
@@ default/kernel/constraint-parties.sql
@@ default/kernel/table-person_names.sql
@@ default/kernel/comment-person_names.sql
@@ default/kernel/table-users.sql
@@ default/kernel/comment-users.sql

@@ default/kernel/table-groups.sql
@@ default/kernel/table-group_member_map.sql
@@ default/kernel/table-group_subgroup_map.sql
@@ default/kernel/table-roles.sql
@@ default/kernel/index-roles.sql
@@ default/kernel/comment-roles.sql

@@ default/kernel/table-group_subgroup_trans_index.sql
@@ default/kernel/index-group_subgroup_trans_index.sql
@@ default/kernel/table-group_member_trans_index.sql
@@ default/kernel/index-group_member_trans_index.sql
@@ default/kernel/package-parties_denormalization.sql
@@ default/kernel/trigger-acs_parties.sql
@@ default/kernel/view-group_subgroup_trans_map.sql
@@ default/kernel/view-group_member_trans_map.sql
@@ default/kernel/view-party_member_trans_map.sql

@@ default/kernel/table-user_authentication.sql

@@ default/kernel/table-acs_privileges.sql
@@ default/kernel/comment-acs_privileges.sql
@@ default/kernel/table-parameterized_privileges.sql
@@ default/kernel/insert-privileges.sql
@@ oracle-se/kernel/table-acs_permissions.sql
@@ default/kernel/comment-acs_permissions.sql

@@ default/kernel/table-object_context.sql
@@ default/kernel/comment-object_context.sql
@@ default/kernel/insert-object_zero.sql

@@ default/kernel/table-granted_context_non_leaf_map.sql
@@ default/kernel/index-granted_context_non_leaf_map.sql
@@ default/kernel/table-ungranted_context_non_leaf_map.sql
@@ default/kernel/index-ungranted_context_non_leaf_map.sql
@@ default/kernel/table-object_grants.sql
@@ default/kernel/table-context_child_counts.sql
@@ default/kernel/table-object_context_map.sql
@@ default/kernel/index-object_context_map.sql
@@ default/kernel/view-all_context_non_leaf_map.sql

@@ default/kernel/package-permission_denormalization.sql
@@ default/kernel/trigger-acs_permissions.sql
@@ default/kernel/index-acs_permissions.sql

@@ default/kernel/insert-users.sql
@@ default/kernel/insert-permissions.sql
@@ default/kernel/view-granted_trans_context_index.sql
@@ default/kernel/view-granted_trans_context_map.sql
@@ oracle-se/kernel/view-ungranted_trans_context_index.sql
@@ oracle-se/kernel/view-ungranted_trans_context_map.sql
@@ default/kernel/view-object_context_trans_map.sql

@@ default/kernel/table-site_nodes.sql
@@ default/kernel/index-site_nodes.sql
@@ default/kernel/table-apm_package_types.sql
@@ default/kernel/table-apm_listeners.sql
@@ default/kernel/table-apm_package_type_listener_map.sql
@@ default/kernel/table-apm_packages.sql
@@ default/kernel/index-apm_packages.sql
@@ default/kernel/view-object_package_map.sql

@@ default/kernel/table-acs_stylesheets.sql
@@ default/kernel/index-acs_stylesheets.sql
@@ default/kernel/table-acs_stylesheet_type_map.sql
@@ default/kernel/index-acs_stylesheet_type_map.sql
@@ default/kernel/table-acs_stylesheet_node_map.sql
@@ default/kernel/index-acs_stylesheet_node_map.sql

@@ default/kernel/table-note_themes.sql
@@ default/kernel/table-notes.sql
@@ default/kernel/table-theme_stylesheet_map.sql

@@ default/categorization/table-cat_categories.sql
@@ default/categorization/comment-cat_categories.sql
@@ default/categorization/table-cat_category_category_map.sql
@@ default/categorization/comment-cat_category_category_map.sql
@@ default/categorization/table-cat_object_category_map.sql
@@ default/categorization/index-cat_object_category_map.sql
@@ default/categorization/comment-cat_object_category_map.sql
@@ default/categorization/table-cat_object_root_category_map.sql
@@ default/categorization/index-cat_object_root_category_map.sql
@@ default/categorization/table-cat_purposes.sql
@@ default/categorization/comment-cat_purposes.sql
@@ default/categorization/table-cat_category_purpose_map.sql
@@ default/categorization/index-cat_category_purpose_map.sql

@@ oracle-se/auditing/table-acs_auditing.sql
@@ default/auditing/index-acs_auditing.sql

@@ oracle-se/messaging/table-messages.sql
@@ default/messaging/index-messages.sql
@@ default/messaging/comment-messages.sql
@@ default/messaging/table-message_parts.sql
@@ default/messaging/index-message_parts.sql
@@ default/messaging/comment-message_parts.sql
@@ oracle-se/messaging/table-message_threads.sql

@@ oracle-se/notification/table-nt_digests.sql
@@ default/notification/index-nt_digests.sql
@@ oracle-se/notification/table-nt_requests.sql
@@ default/notification/index-nt_requests.sql
@@ default/notification/table-nt_queue.sql

@@ default/search/table-search_content.sql
@@ default/search/block-autogroup.sql
@@ default/search/index-xml_content_index.sql
@@ default/search/index-raw_content_index.sql
@@ default/search/table-content_change_time.sql
@@ default/search/table-search_indexing_jobs.sql
@@ default/search/insert-dummy.sql
@@ default/search/package-search_indexing.sql

@@ default/places/table-places.sql
@@ default/places/table-place_hierarchy.sql
@@ default/places/index-place_hierarchy.sql
@@ default/places/table-place_hierarchy_tc.sql
@@ default/places/index-place_hierarchy_tc.sql
@@ default/places/trigger-place_hierarchy.sql
@@ default/places/table-pl_countries.sql
@@ default/places/table-pl_regions.sql
@@ default/places/table-pl_municipalities.sql
@@ default/places/table-pl_postal_codes.sql
@@ default/places/table-pl_us_states.sql
@@ default/places/table-pl_us_counties.sql
@@ default/places/index-pl_us_counties.sql

@@ default/versioning/table-vc_objects.sql
@@ default/versioning/comment-vc_objects.sql
@@ default/versioning/table-vc_transactions.sql
@@ default/versioning/index-vc_transactions.sql
@@ default/versioning/comment-vc_transactions.sql
@@ default/versioning/table-vc_actions.sql
@@ default/versioning/comment-vc_actions.sql
@@ default/versioning/insert-vc_actions.sql
@@ default/versioning/table-vc_operations.sql
@@ default/versioning/index-vc_operations.sql
@@ default/versioning/comment-vc_operations.sql
@@ default/versioning/table-vc_generic_operations.sql
@@ default/versioning/comment-vc_generic_operations.sql
@@ default/versioning/table-vc_clob_operations.sql
@@ default/versioning/table-vc_blob_operations.sql
@@ default/versioning/function-last_attr_value.sql

@@ default/workflow/sequence-cw_sequences.sql
@@ default/workflow/table-cw_tasks.sql
@@ default/workflow/index-cw_tasks.sql
@@ default/workflow/table-cw_user_tasks.sql
@@ default/workflow/table-cw_task_dependencies.sql
@@ default/workflow/table-cw_task_comments.sql
@@ default/workflow/table-cw_system_tasks.sql
@@ default/workflow/table-cw_task_listeners.sql
@@ default/workflow/table-cw_task_user_assignees.sql
@@ default/workflow/table-cw_task_group_assignees.sql
@@ default/workflow/table-cw_processes.sql
@@ default/workflow/index-cw_processes.sql
@@ default/workflow/table-cw_process_task_map.sql
@@ default/workflow/table-cw_process_definitions.sql

@@ default/formbuilder/table-bebop_components.sql
@@ default/formbuilder/comment-bebop_components.sql
@@ default/formbuilder/table-bebop_widgets.sql
@@ default/formbuilder/comment-bebop_widgets.sql
@@ default/formbuilder/table-bebop_options.sql
@@ default/formbuilder/comment-bebop_options.sql
@@ default/formbuilder/table-bebop_form_sections.sql
@@ default/formbuilder/comment-bebop_form_sections.sql
@@ default/formbuilder/table-bebop_process_listeners.sql
@@ default/formbuilder/comment-bebop_process_listeners.sql
@@ default/formbuilder/table-bebop_form_process_listeners.sql
@@ default/formbuilder/comment-bebop_form_process_listeners.sql
@@ default/formbuilder/table-bebop_component_hierarchy.sql
@@ default/formbuilder/comment-bebop_component_hierarchy.sql
@@ default/formbuilder/table-bebop_listeners.sql
@@ default/formbuilder/comment-bebop_listeners.sql
@@ default/formbuilder/table-bebop_listener_map.sql
@@ default/formbuilder/comment-bebop_listener_map.sql
@@ default/formbuilder/table-bebop_object_type.sql
@@ default/formbuilder/comment-bebop_object_type.sql
@@ default/formbuilder/table-bebop_meta_object.sql
@@ default/formbuilder/comment-bebop_meta_object.sql
@@ default/formbuilder/table-forms_widget_label.sql
@@ default/formbuilder/comment-forms_widget_label.sql
@@ default/formbuilder/table-forms_lstnr_conf_email.sql
@@ default/formbuilder/comment-forms_lstnr_conf_email.sql
@@ default/formbuilder/table-forms_lstnr_conf_redirect.sql
@@ default/formbuilder/comment-forms_lstnr_conf_redirect.sql
@@ default/formbuilder/table-forms_lstnr_simple_email.sql
@@ default/formbuilder/comment-forms_lstnr_simple_email.sql
@@ default/formbuilder/table-forms_lstnr_tmpl_email.sql
@@ default/formbuilder/comment-forms_lstnr_tmpl_email.sql
@@ default/formbuilder/table-forms_lstnr_xml_email.sql
@@ default/formbuilder/comment-forms_lstnr_xml_email.sql
@@ default/formbuilder/table-forms_dataquery.sql
@@ default/formbuilder/comment-forms_dataquery.sql
@@ default/formbuilder/table-forms_dd_select.sql
@@ default/formbuilder/comment-forms_dd_select.sql
@@ default/formbuilder/sequence-forms_unique_id_seq.sql
@@ default/formbuilder/comment-forms_unique_id_seq.sql

@@ default/addresses/table-us_addresses.sql

@@ default/persistence/table-persistence_dynamic_ot.sql
@@ default/persistence/table-persistence_dynamic_assoc.sql

@@ default/kernel/index-foreign_keys.sql 
