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

create sequence ad_template_sample_users_seq start with 5 increment by 1;

create table ad_template_sample_users (
       user_id         integer,
       first_name      varchar(20),
       last_name       varchar(20),
       address1        varchar(40),
       address2        varchar(40),
       city            varchar(40),
       state           varchar(2)
);


--work around APM loading everything alphabetically
delete from ad_template_sample_users;

insert into ad_template_sample_users values 
 (1, 'Fred', 'Jones', '101 Main St.', NULL, 'Orange', 'CA');
                
insert into ad_template_sample_users values 
 (2, 'Frieda', 'Mae', 'Lexington Hospital', '102 Central St.', 
      'Orange', 'CA');

insert into ad_template_sample_users values 
 (3, 'Sally', 'Saxberg', 'Board of Supervisors', '1933 Fruitvale St.', 
      'Woodstock', 'CA');

insert into ad_template_sample_users values 
 (4, 'Yoruba', 'Diaz', '12 Magic Ave.', NULL, 'Lariot', 'WY');
