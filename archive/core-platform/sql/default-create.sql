include default/function-currentDate.sql

include default/globalization/table-g11n_charsets.sql
include default/globalization/index-g11n_charsets.sql
include default/globalization/table-g11n_locales.sql
include default/globalization/index-g11n_locales.sql
include default/globalization/table-g11n_locale_charset_map.sql
include default/globalization/index-g11n_locale_charset_map.sql
include default/globalization/table-g11n_catalogs.sql
include default/globalization/index-g11n_catalogs.sql



include default/preferences/table-preferences.sql
include default/preferences/index-preferences.sql
include default/preferences/comment-preferences.sql



include default/kernel/sequence-acs_object_id_seq.sql
include default/kernel/table-acs_objects.sql
include default/kernel/index-acs_objects.sql
include default/kernel/table-object_container_map.sql
include default/kernel/index-object_container_map.sql

include default/kernel/table-email_addresses.sql
include default/kernel/comment-email_addresses.sql
include default/kernel/table-parties.sql
include default/kernel/index-parties.sql
include default/kernel/comment-parties.sql
include default/kernel/table-party_email_map.sql
include default/kernel/comment-party_email_map.sql
include default/kernel/constraint-parties.sql
include default/kernel/table-person_names.sql
include default/kernel/comment-person_names.sql
include default/kernel/table-users.sql
include default/kernel/comment-users.sql

include default/kernel/table-groups.sql
include default/kernel/table-group_member_map.sql
include default/kernel/table-group_subgroup_map.sql
include default/kernel/table-roles.sql
include default/kernel/index-roles.sql
include default/kernel/comment-roles.sql

include default/kernel/table-group_subgroup_trans_index.sql
include default/kernel/index-group_subgroup_trans_index.sql
include default/kernel/table-group_member_trans_index.sql
include default/kernel/index-group_member_trans_index.sql
include default/kernel/package-parties_denormalization.sql
include default/kernel/trigger-acs_parties.sql
include default/kernel/view-group_subgroup_trans_map.sql
include default/kernel/view-group_member_trans_map.sql
include default/kernel/view-party_member_trans_map.sql

include default/kernel/table-user_authentication.sql

include default/kernel/table-acs_privileges.sql
include default/kernel/comment-acs_privileges.sql
include default/kernel/table-parameterized_privileges.sql
include default/kernel/insert-privileges.sql
include default/kernel/table-acs_permissions.sql
include default/kernel/comment-acs_permissions.sql

include default/kernel/table-object_context.sql
include default/kernel/comment-object_context.sql
include default/kernel/insert-object_zero.sql

include default/kernel/table-granted_context_non_leaf_map.sql
include default/kernel/index-granted_context_non_leaf_map.sql
include default/kernel/table-ungranted_context_non_leaf_map.sql
include default/kernel/index-ungranted_context_non_leaf_map.sql
include default/kernel/table-object_grants.sql
include default/kernel/table-context_child_counts.sql
include default/kernel/table-object_context_map.sql
include default/kernel/index-object_context_map.sql
include default/kernel/view-all_context_non_leaf_map.sql

include default/kernel/package-permission_denormalization.sql
include default/kernel/trigger-acs_permissions.sql
include default/kernel/index-acs_permissions.sql

include default/kernel/insert-users.sql
include default/kernel/insert-permissions.sql
include default/kernel/view-granted_trans_context_index.sql
include default/kernel/view-granted_trans_context_map.sql
include default/kernel/view-ungranted_trans_context_index.sql
include default/kernel/view-ungranted_trans_context_map.sql
include default/kernel/view-object_context_trans_map.sql

include default/kernel/table-site_nodes.sql
include default/kernel/index-site_nodes.sql
include default/kernel/table-apm_package_types.sql
include default/kernel/table-apm_listeners.sql
include default/kernel/table-apm_package_type_listener_map.sql
include default/kernel/table-apm_packages.sql
include default/kernel/index-apm_packages.sql
include default/kernel/view-object_package_map.sql

include default/kernel/table-acs_stylesheets.sql
include default/kernel/index-acs_stylesheets.sql
include default/kernel/table-acs_stylesheet_type_map.sql
include default/kernel/index-acs_stylesheet_type_map.sql
include default/kernel/table-acs_stylesheet_node_map.sql
include default/kernel/index-acs_stylesheet_node_map.sql

include default/kernel/function-package_id_for_object_id.sql

include default/categorization/table-cat_categories.sql
include default/categorization/comment-cat_categories.sql
include default/categorization/table-cat_category_category_map.sql
include default/categorization/comment-cat_category_category_map.sql
include default/categorization/table-cat_object_category_map.sql
include default/categorization/index-cat_object_category_map.sql
include default/categorization/comment-cat_object_category_map.sql
include default/categorization/table-cat_object_root_category_map.sql
include default/categorization/index-cat_object_root_category_map.sql
include default/categorization/table-cat_purposes.sql
include default/categorization/comment-cat_purposes.sql
include default/categorization/table-cat_category_purpose_map.sql
include default/categorization/index-cat_category_purpose_map.sql

include default/auditing/table-acs_auditing.sql
include default/auditing/index-acs_auditing.sql

include default/messaging/table-messages.sql
include default/messaging/index-messages.sql
include default/messaging/comment-messages.sql
include default/messaging/table-message_parts.sql
include default/messaging/index-message_parts.sql
include default/messaging/comment-message_parts.sql
include default/messaging/table-message_threads.sql

include default/notification/table-nt_digests.sql
include default/notification/index-nt_digests.sql
include default/notification/table-nt_requests.sql
include default/notification/index-nt_requests.sql
include default/notification/table-nt_queue.sql

include default/search/table-search_content.sql
include default/search/block-autogroup.sql
include default/search/index-xml_content_index.sql
include default/search/index-raw_content_index.sql
include default/search/table-content_change_time.sql
include default/search/table-search_indexing_jobs.sql
include default/search/insert-dummy.sql
include default/search/package-search_indexing.sql

include default/places/table-places.sql
include default/places/table-place_hierarchy.sql
include default/places/index-place_hierarchy.sql
include default/places/table-place_hierarchy_tc.sql
include default/places/index-place_hierarchy_tc.sql
include default/places/trigger-place_hierarchy.sql
include default/places/table-pl_countries.sql
include default/places/table-pl_regions.sql
include default/places/table-pl_municipalities.sql
include default/places/table-pl_postal_codes.sql
include default/places/table-pl_us_states.sql
include default/places/table-pl_us_counties.sql
include default/places/index-pl_us_counties.sql

include default/versioning/table-vc_objects.sql
include default/versioning/comment-vc_objects.sql
include default/versioning/table-vc_transactions.sql
include default/versioning/index-vc_transactions.sql
include default/versioning/comment-vc_transactions.sql
include default/versioning/table-vc_actions.sql
include default/versioning/comment-vc_actions.sql
include default/versioning/insert-vc_actions.sql
include default/versioning/table-vc_operations.sql
include default/versioning/index-vc_operations.sql
include default/versioning/comment-vc_operations.sql
include default/versioning/table-vc_generic_operations.sql
include default/versioning/comment-vc_generic_operations.sql
include default/versioning/table-vc_clob_operations.sql
include default/versioning/table-vc_blob_operations.sql
include default/versioning/function-last_attr_value.sql

include default/workflow/sequence-cw_sequences.sql
include default/workflow/table-cw_tasks.sql
include default/workflow/index-cw_tasks.sql
include default/workflow/table-cw_user_tasks.sql
include default/workflow/table-cw_task_dependencies.sql
include default/workflow/table-cw_task_comments.sql
include default/workflow/table-cw_system_tasks.sql
include default/workflow/table-cw_task_listeners.sql
include default/workflow/table-cw_task_user_assignees.sql
include default/workflow/table-cw_task_group_assignees.sql
include default/workflow/table-cw_processes.sql
include default/workflow/index-cw_processes.sql
include default/workflow/table-cw_process_task_map.sql
include default/workflow/table-cw_process_definitions.sql

include default/formbuilder/table-bebop_components.sql
include default/formbuilder/comment-bebop_components.sql
include default/formbuilder/table-bebop_widgets.sql
include default/formbuilder/comment-bebop_widgets.sql
include default/formbuilder/table-bebop_options.sql
include default/formbuilder/comment-bebop_options.sql
include default/formbuilder/table-bebop_form_sections.sql
include default/formbuilder/comment-bebop_form_sections.sql
include default/formbuilder/table-bebop_process_listeners.sql
include default/formbuilder/comment-bebop_process_listeners.sql
include default/formbuilder/table-bebop_form_process_listeners.sql
include default/formbuilder/comment-bebop_form_process_listeners.sql
include default/formbuilder/table-bebop_component_hierarchy.sql
include default/formbuilder/comment-bebop_component_hierarchy.sql
include default/formbuilder/table-bebop_listeners.sql
include default/formbuilder/comment-bebop_listeners.sql
include default/formbuilder/table-bebop_listener_map.sql
include default/formbuilder/comment-bebop_listener_map.sql
include default/formbuilder/table-bebop_object_type.sql
include default/formbuilder/comment-bebop_object_type.sql
include default/formbuilder/table-bebop_meta_object.sql
include default/formbuilder/comment-bebop_meta_object.sql
include default/formbuilder/table-forms_widget_label.sql
include default/formbuilder/comment-forms_widget_label.sql
include default/formbuilder/table-forms_lstnr_conf_email.sql
include default/formbuilder/comment-forms_lstnr_conf_email.sql
include default/formbuilder/table-forms_lstnr_conf_redirect.sql
include default/formbuilder/comment-forms_lstnr_conf_redirect.sql
include default/formbuilder/table-forms_lstnr_simple_email.sql
include default/formbuilder/comment-forms_lstnr_simple_email.sql
include default/formbuilder/table-forms_lstnr_tmpl_email.sql
include default/formbuilder/comment-forms_lstnr_tmpl_email.sql
include default/formbuilder/table-forms_lstnr_xml_email.sql
include default/formbuilder/comment-forms_lstnr_xml_email.sql
include default/formbuilder/table-forms_dataquery.sql
include default/formbuilder/comment-forms_dataquery.sql
include default/formbuilder/table-forms_dd_select.sql
include default/formbuilder/comment-forms_dd_select.sql
include default/formbuilder/sequence-forms_unique_id_seq.sql
include default/formbuilder/comment-forms_unique_id_seq.sql

include default/addresses/table-us_addresses.sql

include default/persistence/table-persistence_dynamic_ot.sql
include default/persistence/table-persistence_dynamic_assoc.sql

include default/kernel/index-foreign_keys.sql 

