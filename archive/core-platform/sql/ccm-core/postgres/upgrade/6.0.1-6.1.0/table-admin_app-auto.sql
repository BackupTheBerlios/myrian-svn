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
-- $Id: //core-platform/dev/sql/ccm-core/postgres/upgrade/6.0.1-6.1.0/table-admin_app-auto.sql#3 $
-- $DateTime: 2004/04/07 16:07:11 $
create table admin_app (
    application_id INTEGER not null
        constraint admi_app_applicatio_id_p_lonsa
          primary key
        -- referential constraint for application_id deferred due to circular dependencies
);
