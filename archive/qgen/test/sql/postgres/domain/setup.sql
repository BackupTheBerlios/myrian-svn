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
-- $Id: //core-platform/test-qgen/test/sql/postgres/domain/setup.sql#1 $
-- $DateTime: 2003/12/10 16:59:20 $


@@ ../../default/domain/setup.sql

create or replace function package_abstractness(integer)
  returns numeric as '
  declare
    v_id alias for $1;
    abs_count numeric;
    total numeric;
  begin
	select count(*) into abs_count from t_class  where package_id = v_id and is_abstract = 1;
	select count(*) into total from t_class  where package_id = v_id;
	return abs_count / total;
  end' language 'plpgsql';
