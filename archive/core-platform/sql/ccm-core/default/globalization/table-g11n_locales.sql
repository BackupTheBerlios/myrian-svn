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
-- $Id: //core-platform/dev/sql/ccm-core/default/globalization/table-g11n_locales.sql#3 $
-- $DateTime: 2004/04/07 16:07:11 $

create table g11n_locales (
    locale_id                   integer
                                constraint g11n_locales_locale_id_pk
                                primary key,
    language                    char(2)
                                constraint g11n_locales_language_nn
                                not null,
    country                     char(2),
    variant                     varchar(30),
    default_charset_id          integer
                                constraint g11n_locales_def_charset_id_fk
                                references g11n_charsets (charset_id)
);
