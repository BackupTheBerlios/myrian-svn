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
-- $Id: //core-platform/proto/sql/default/formbuilder/table-bebop_options.sql#4 $
-- $DateTime: 2003/08/04 16:15:53 $

create table bebop_options (
       option_id                  integer
                                  constraint bebop_options_id_pk
                                  primary key,
       parameter_name             varchar(100),
       label                      varchar(300)          
);
