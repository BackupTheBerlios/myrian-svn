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
-- Create character sets.
--
-- @author <a href="mailto:yon@arsdigita.com">yon@arsdigita.com</a>
-- @version $Id: //core-platform/dev/sql/oracle-se/globalization/g11n-charsets-create.sql#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $
--

create table g11n_charsets (
    charset_id                  integer
                                constraint g11n_charsets_charset_id_pk
                                primary key,
    charset                     varchar2(30)
                                constraint g11n_charsets_charset_nn
                                not null
);

create unique index g11n_charsets_charset_idx on g11n_charsets (charset);
