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
-- Create locales.
--
-- @author <a href="mailto:yon@arsdigita.com">yon@arsdigita.com</a>
-- @version $Id: //core-platform/dev/sql/oracle-se/globalization/g11n-locales-create.sql#3 $ by $Author: dan $, $DateTime: 2002/07/31 10:49:40 $
--

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

-- foreign key index
create index g11n_locales_default_char_idx on g11n_locales(default_charset_id);

create unique index g11n_locales_locale_idx on
    g11n_locales (language, country, variant);

create table g11n_locale_charset_map (
    locale_id                   integer
                                constraint g11n_lcm_locale_id_nn
                                not null
                                constraint g11n_lcm_locale_id_fk
                                references g11n_locales (locale_id),
    charset_id                  integer
                                constraint g11n_lcm_charset_id_nn
                                not null
                                constraint g11n_lcm_charset_id_fk
                                references g11n_charsets (charset_id)
);

create unique index g11n_locale_charset_map_pk on
    g11n_locale_charset_map (locale_id, charset_id);
