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
-- $Id: //core-platform/test-qgen/sql/ccm-core/default/globalization/index-g11n_locales.sql#1 $
-- $DateTime: 2003/12/10 16:59:20 $

create unique index g11n_locales_locale_idx on
    g11n_locales (language, country, variant);

create index g11n_locales_default_char_idx on 
    g11n_locales(default_charset_id);
