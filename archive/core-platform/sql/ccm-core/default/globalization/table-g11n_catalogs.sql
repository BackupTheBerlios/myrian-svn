--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/default/globalization/table-g11n_catalogs.sql#3 $
-- $DateTime: 2004/04/07 16:07:11 $

create table g11n_catalogs (
    catalog_id                  integer
                                constraint g11n_catalogs_pk
                                primary key,
    catalog_name                varchar(400)
                                constraint g11n_catalogs_catalog_name_nn
                                not null,
    locale_id                   integer
                                constraint g11n_catalogs_locale_id_fk
                                references g11n_locales (locale_id),
    catalog                     blob,
    last_modified               date
                                constraint g11n_catalogs_last_modified_nn
                                not null
);
