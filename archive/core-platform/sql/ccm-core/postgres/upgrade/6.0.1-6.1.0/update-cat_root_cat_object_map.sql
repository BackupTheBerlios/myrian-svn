--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/postgres/upgrade/6.0.1-6.1.0/update-cat_root_cat_object_map.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $
create table cat_root_cat_object_map_temp (
    id NUMERIC not null
        constraint temp_cat_1
          primary key,
    category_id INTEGER not null,
    object_id INTEGER not null,
    use_context VARCHAR(700),
    constraint temp_cat_2
      unique(object_id, use_context)
);

insert into cat_root_cat_object_map_temp
  (id, category_id, object_id, use_context)
select nextval('acs_object_id_seq'), category_id, object_id, null
  from cat_root_cat_object_map;

drop table cat_root_cat_object_map;
alter table cat_root_cat_object_map_temp rename to cat_root_cat_object_map;

alter table cat_root_cat_object_map drop constraint temp_cat_1;
alter table cat_root_cat_object_map add
    constraint cat_roo_cat_obj_map_id_p_qw9kr
      primary key (id);

alter table cat_root_cat_object_map drop constraint temp_cat_2;
alter table cat_root_cat_object_map add
    constraint cat_roo_cat_obj_map_ob_u_gqgrh
      unique (object_id, use_context);

alter table cat_root_cat_object_map add 
    constraint cat_roo_cat_obj_map_ca_f_jqvmd foreign key (category_id)
      references cat_categories(category_id);
alter table cat_root_cat_object_map add 
    constraint cat_roo_cat_obj_map_ob_f_anfmx foreign key (object_id)
      references acs_objects(object_id);

create index cat_roo_cat_obj_map_cat_id_idx on cat_root_cat_object_map(category_id);
