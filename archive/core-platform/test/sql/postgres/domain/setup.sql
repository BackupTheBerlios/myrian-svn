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


