begin;

create view dual as select 1;

\i ddl/postgres/create.sql
\i default/globalization/table-g11n_charsets.sql
\i default/globalization/index-g11n_charsets.sql
\i default/globalization/table-g11n_locales.sql
\i default/globalization/index-g11n_locales.sql
\i default/globalization/table-g11n_locale_charset_map.sql
\i default/globalization/index-g11n_locale_charset_map.sql
\i postgres/globalization/table-g11n_catalogs.sql
\i default/globalization/index-g11n_catalogs.sql



\i default/preferences/table-preferences.sql
\i default/preferences/index-preferences.sql
\i default/preferences/comment-preferences.sql



\i default/kernel/sequence-acs_object_id_seq.sql

\i default/kernel/table-group_subgroup_trans_index.sql
\i default/kernel/index-group_subgroup_trans_index.sql
\i default/kernel/table-group_member_trans_index.sql
\i default/kernel/index-group_member_trans_index.sql
\i postgres/kernel/package-parties_denormalization.sql
\i postgres/kernel/trigger-acs_parties.sql
\i default/kernel/view-group_subgroup_trans_map.sql
\i default/kernel/view-group_member_trans_map.sql
\i default/kernel/view-party_member_trans_map.sql


\i default/kernel/insert-privileges.sql

\i default/kernel/insert-object_zero.sql

\i default/kernel/table-granted_context_non_leaf_map.sql
\i default/kernel/index-granted_context_non_leaf_map.sql
\i default/kernel/table-ungranted_context_non_leaf_map.sql
\i default/kernel/index-ungranted_context_non_leaf_map.sql
\i default/kernel/table-object_grants.sql
\i default/kernel/table-context_child_counts.sql
\i default/kernel/table-object_context_map.sql
\i default/kernel/index-object_context_map.sql
\i default/kernel/view-all_context_non_leaf_map.sql

\i postgres/kernel/package-permission_denormalization.sql
\i postgres/kernel/trigger-acs_permissions.sql
\i default/kernel/index-acs_permissions.sql

\i default/kernel/insert-users.sql
\i postgres/kernel/insert-permissions.sql
\i default/kernel/view-granted_trans_context_index.sql
\i default/kernel/view-granted_trans_context_map.sql
\i default/kernel/view-ungranted_trans_context_index.sql
\i default/kernel/view-ungranted_trans_context_map.sql
\i default/kernel/view-object_context_trans_map.sql
\i postgres/kernel/function-package_id_for_object_id.sql

-- XXX
--\i default/kernel/view-object_package_map.sql

\i default/categorization/table-cat_categories.sql
\i default/categorization/comment-cat_categories.sql
\i default/categorization/table-cat_category_category_map.sql
\i default/categorization/comment-cat_category_category_map.sql
\i default/categorization/table-cat_object_category_map.sql
\i default/categorization/index-cat_object_category_map.sql
\i default/categorization/comment-cat_object_category_map.sql
\i default/categorization/table-cat_object_root_category_map.sql
\i default/categorization/index-cat_object_root_category_map.sql
\i default/categorization/table-cat_purposes.sql
\i default/categorization/comment-cat_purposes.sql
\i default/categorization/table-cat_category_purpose_map.sql
\i default/categorization/index-cat_category_purpose_map.sql

\i default/auditing/table-acs_auditing.sql
\i default/auditing/index-acs_auditing.sql

\i default/messaging/table-messages.sql
\i default/messaging/index-messages.sql
\i default/messaging/comment-messages.sql
\i postgres/messaging/table-message_parts.sql
\i default/messaging/index-message_parts.sql
\i default/messaging/comment-message_parts.sql
\i default/messaging/table-message_threads.sql

\i default/notification/table-nt_digests.sql
\i default/notification/index-nt_digests.sql
\i default/notification/table-nt_requests.sql
\i default/notification/index-nt_requests.sql
\i default/notification/table-nt_queue.sql

-- Not a hope in hell of intermedia working with PG ;-)
--\i default/search/table-search_content.sql
--\i default/search/block-autogroup.sql
--\i default/search/index-xml_content_index.sql
--\i default/search/index-raw_content_index.sql
--\i default/search/table-content_change_time.sql
--\i default/search/table-search_indexing_jobs.sql
--\i default/search/insert-dummy.sql
--\i default/search/package-search_indexing.sql

\i default/places/table-places.sql
\i default/places/table-place_hierarchy.sql
\i default/places/index-place_hierarchy.sql
\i default/places/table-place_hierarchy_tc.sql
\i default/places/index-place_hierarchy_tc.sql
\i default/places/trigger-place_hierarchy.sql
\i default/places/table-pl_countries.sql
\i default/places/table-pl_regions.sql
\i default/places/table-pl_municipalities.sql
\i default/places/table-pl_postal_codes.sql
\i default/places/table-pl_us_states.sql
\i default/places/table-pl_us_counties.sql
\i default/places/index-pl_us_counties.sql

\i default/versioning/table-vc_objects.sql
\i default/versioning/comment-vc_objects.sql
\i default/versioning/table-vc_transactions.sql
\i default/versioning/index-vc_transactions.sql
\i default/versioning/comment-vc_transactions.sql
\i default/versioning/table-vc_actions.sql
\i default/versioning/comment-vc_actions.sql
\i default/versioning/insert-vc_actions.sql
\i default/versioning/table-vc_operations.sql
\i default/versioning/index-vc_operations.sql
\i default/versioning/comment-vc_operations.sql
\i default/versioning/table-vc_generic_operations.sql
\i default/versioning/comment-vc_generic_operations.sql
\i postgres/versioning/table-vc_clob_operations.sql
\i postgres/versioning/table-vc_blob_operations.sql
\i postgres/versioning/function-last_attr_value.sql

\i postgres/workflow/sequence-cw_sequences.sql
\i default/workflow/table-cw_tasks.sql
\i default/workflow/index-cw_tasks.sql
\i default/workflow/table-cw_user_tasks.sql
\i default/workflow/table-cw_task_dependencies.sql
\i default/workflow/table-cw_task_comments.sql
\i default/workflow/table-cw_system_tasks.sql
\i default/workflow/table-cw_task_listeners.sql
\i default/workflow/table-cw_task_user_assignees.sql
\i default/workflow/table-cw_task_group_assignees.sql
\i default/workflow/table-cw_processes.sql
\i default/workflow/index-cw_processes.sql
\i default/workflow/table-cw_process_task_map.sql
\i default/workflow/table-cw_process_definitions.sql

\i default/formbuilder/table-bebop_components.sql
\i default/formbuilder/comment-bebop_components.sql
\i default/formbuilder/table-bebop_widgets.sql
\i default/formbuilder/comment-bebop_widgets.sql
\i default/formbuilder/table-bebop_options.sql
\i default/formbuilder/comment-bebop_options.sql
\i default/formbuilder/table-bebop_form_sections.sql
\i default/formbuilder/comment-bebop_form_sections.sql
\i default/formbuilder/table-bebop_process_listeners.sql
\i default/formbuilder/comment-bebop_process_listeners.sql
\i default/formbuilder/table-bebop_form_process_listeners.sql
\i default/formbuilder/comment-bebop_form_process_listeners.sql
\i default/formbuilder/table-bebop_component_hierarchy.sql
\i default/formbuilder/comment-bebop_component_hierarchy.sql
\i default/formbuilder/table-bebop_listeners.sql
\i default/formbuilder/comment-bebop_listeners.sql
\i default/formbuilder/table-bebop_listener_map.sql
\i default/formbuilder/comment-bebop_listener_map.sql
\i default/formbuilder/table-bebop_object_type.sql
\i default/formbuilder/comment-bebop_object_type.sql
\i default/formbuilder/table-bebop_meta_object.sql
\i default/formbuilder/comment-bebop_meta_object.sql
\i default/formbuilder/table-forms_widget_label.sql
\i default/formbuilder/comment-forms_widget_label.sql
\i default/formbuilder/table-forms_lstnr_conf_email.sql
\i default/formbuilder/comment-forms_lstnr_conf_email.sql
\i default/formbuilder/table-forms_lstnr_conf_redirect.sql
\i default/formbuilder/comment-forms_lstnr_conf_redirect.sql
\i default/formbuilder/table-forms_lstnr_simple_email.sql
\i default/formbuilder/comment-forms_lstnr_simple_email.sql
\i default/formbuilder/table-forms_lstnr_tmpl_email.sql
\i default/formbuilder/comment-forms_lstnr_tmpl_email.sql
\i default/formbuilder/table-forms_lstnr_xml_email.sql
\i default/formbuilder/comment-forms_lstnr_xml_email.sql
\i default/formbuilder/table-forms_dataquery.sql
\i default/formbuilder/comment-forms_dataquery.sql
\i default/formbuilder/table-forms_dd_select.sql
\i default/formbuilder/comment-forms_dd_select.sql
\i default/formbuilder/sequence-forms_unique_id_seq.sql
\i default/formbuilder/comment-forms_unique_id_seq.sql

\i default/addresses/table-us_addresses.sql

\i postgres/persistence/table-persistence_dynamic_ot.sql
\i postgres/persistence/table-persistence_dynamic_assoc.sql

\i default/kernel/index-foreign_keys.sql 

\i ddl/postgres/deferred.sql

commit;
