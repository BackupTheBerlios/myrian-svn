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
-- $Id: //core-platform/proto/sql/default/globalization/table-g11n_charsets.sql#2 $
-- $DateTime: 2003/04/09 16:35:55 $

create table g11n_charsets (
    charset_id                  integer
                                constraint g11n_charsets_charset_id_pk
                                primary key,
    charset                     varchar(30)
                                constraint g11n_charsets_charset_nn
                                not null
);
