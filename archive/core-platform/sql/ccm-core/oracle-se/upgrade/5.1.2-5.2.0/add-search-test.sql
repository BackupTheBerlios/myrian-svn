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
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/upgrade/5.1.2-5.2.0/add-search-test.sql#1 $
-- $DateTime: 2003/10/23 15:28:18 $

create table search_test_author (
    author_id INTEGER not null
        constraint sear_tes_auth_autho_id_p_i_7fz
          primary key,
        -- referential constraint for author_id deferred due to circular dependencies
    name VARCHAR(100)
);

create table search_test_book (
    book_id INTEGER not null
        constraint searc_tes_book_book_id_p_vylnb
          primary key,
        -- referential constraint for book_id deferred due to circular dependencies
    title VARCHAR(100)
);

create table search_test_book_chapter (
    chapter_id INTEGER not null
        constraint sear_tes_boo_cha_cha_i_p_qchkk
          primary key,
        -- referential constraint for chapter_id deferred due to circular dependencies
    chapter_num INTEGER,
    content CLOB
);

alter table search_test_author add 
    constraint sear_tes_auth_autho_id_f_klil2 foreign key (author_id)
      references acs_objects(object_id) on delete cascade;
alter table search_test_book add 
    constraint searc_tes_book_book_id_f_eqgc0 foreign key (book_id)
      references acs_objects(object_id) on delete cascade;
alter table search_test_book_chapter add 
    constraint sear_tes_boo_cha_cha_i_f_fonpi foreign key (chapter_id)
      references acs_objects(object_id) on delete cascade;
