--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
--
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public License
-- as published by the Free Software Foundation; either version 2.1 of
-- the License, or (at your option) any later version.
--
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--
-- $Id: //core-platform/dev/sql/ccm-core/default/globalization/table-g11n_locales.sql#4 $
-- $DateTime: 2004/08/16 18:10:38 $

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
