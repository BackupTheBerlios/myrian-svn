--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of the
-- License at http://www.redhat.com/licenses/ccmpl.html.
--
-- Software distributed under the License is distributed on an
-- "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
-- or implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/postgres/upgrade/6.0.1-6.1.0/deferred.sql#5 $
-- $DateTime: 2004/04/07 16:07:11 $
alter table admin_app add
    constraint admi_app_applicatio_id_f_e0r3b foreign key (application_id)
      references applications(application_id);
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
alter table forms_lstnr_rmt_svr_post add
    constraint form_lst_rmt_svr_pos_l_f_59l2x foreign key (listener_id)
      references bebop_process_listeners(listener_id);
alter table sitemap_app add
    constraint sitem_app_applicati_id_f_echq6 foreign key (application_id)
      references applications(application_id);
