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
-- $Id: //core-platform/dev/test/sql/oracle-se/search/teardown.sql#5 $
-- $DateTime: 2002/10/16 14:12:35 $


-- tests-drop.sql

-- Drop data model used for search tests.

drop table search_test_book_chap_map;
drop table search_test_chap_auth_map;
drop table search_test_book;
drop table search_test_book_chapter;
drop table search_test_author;
