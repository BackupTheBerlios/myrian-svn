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

-- tests-create.sql

-- Data model for a searchable_note object, an object type that
-- is used to test searching.

-- Author: Jeff Teeters (teeters@arsdigita.com)

-- $Id: //core-platform/dev/test/sql/oracle-se/search/setup.sql#4 $

create table searchable_notes (
    note_id integer 
			constraint searchable_notes_id_fk references
			acs_objects (object_id)
			constraint searchable_notes_pk primary key,
    creation_date date not null,
    last_modified date not null,
    title varchar(255) not null,
    body varchar(4000)
);


-- For testing getting attributes of related objects.

create table search_test_book (
    book_id     integer
			constraint search_test_book_fk references
			acs_objects (object_id)
			constraint search_test_book_pk primary key,
    title       varchar(100)
);


-- Chapters will be child objects of books.

create table search_test_book_chapter (
    chapter_id  integer
			constraint search_test_book_chap_fk references
			acs_objects (object_id)
			constraint search_test_book_chap_pk primary key,
    chapter_num integer,
    content    clob     -- to test clobs.
);

-- authors are associated with books or chapters but are not child objects
create table search_test_author (
    author_id   integer
			constraint search_test_author_fk references
			acs_objects (object_id)
			constraint search_test_author_pk primary key,
    name        varchar(100)
);

create table search_test_book_chap_map (
    book_id     integer 
			constraint search_book_chap_map_b_fk references
			search_test_book (book_id),
    chapter_id  integer 
			constraint search_book_chap_map_c_fk references
			search_test_book_chapter (chapter_id),
    constraint search_book_chapter_map_un
    unique (book_id, chapter_id)
);


-- mapping table between chapters and authors
create table search_test_chap_auth_map (
    author_id      integer 
			constraint search_chap_auth_map_a_fk references
			search_test_author (author_id),
    chapter_id     integer 
			constraint search_chap_auth_map_c_fk references
			search_test_book_chapter (chapter_id),
    constraint search_chap_auth_map_un
    unique (author_id, chapter_id)
);


