--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the Open Software License v2.1
-- (the "License"); you may not use this file except in compliance with the
-- License. You may obtain a copy of the License at
-- http://rhea.redhat.com/licenses/osl2.1.html.
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/postgres/globalization/table-g11n_catalogs.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

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
    catalog                     bytea,
    last_modified               date
                                constraint g11n_catalogs_last_modified_nn
                                not null
);
