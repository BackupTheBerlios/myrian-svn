--
-- Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.redhat.com/licenses/ccmpl.html
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/test-packaging/sql/default/globalization/table-g11n_locale_charset_map.sql#2 $
-- $DateTime: 2003/08/19 22:28:24 $

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
