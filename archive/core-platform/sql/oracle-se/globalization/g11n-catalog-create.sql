--
-- Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
--
-- The contents of this file are subject to the ArsDigita Public 
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.arsdigita.com/ADPL.txt
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--

--
-- Create message catalog.
--
-- @author <a href="mailto:yon@arsdigita.com">yon@arsdigita.com</a>
-- @version $Id: //core-platform/dev/sql/oracle-se/globalization/g11n-catalog-create.sql#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $
--

create table g11n_catalogs (
    catalog_id                  integer
                                constraint g11n_catalogs_pk
                                primary key,
    catalog_name                varchar2(400)
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

create unique index g11n_catalogs_name_locale_un on
    g11n_catalogs (catalog_name, locale_id);
