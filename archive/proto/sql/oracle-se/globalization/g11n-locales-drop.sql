--
-- Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/proto/sql/oracle-se/globalization/g11n-locales-drop.sql#1 $
-- $DateTime: 2002/11/27 19:51:05 $


--
-- Drop locales.
--
-- @author <a href="mailto:yon@arsdigita.com">yon@arsdigita.com</a>
-- @version $Id: //core-platform/proto/sql/oracle-se/globalization/g11n-locales-drop.sql#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $
--

drop table g11n_locale_charset_map;
drop table g11n_locales;
