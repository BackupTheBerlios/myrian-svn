--
-- Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the Open Software License v2.1
-- (the "License"); you may not use this file except in compliance with the
-- License. You may obtain a copy of the License at
-- http://rhea.redhat.com/licenses/osl2.1.html.
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/test/sql/oracle-se/domain/setup.sql#7 $
-- $DateTime: 2004/03/30 17:47:27 $


@@ ../../default/domain/setup.sql

create or replace function package_abstractness(v_id integer) return number
is
abs_count number;
total number;
begin
	select count(*) into abs_count from t_class  where package_id = v_id and is_abstract = 1;
	select count(*) into total from t_class  where package_id = v_id;
	return abs_count / total;
end;
/
