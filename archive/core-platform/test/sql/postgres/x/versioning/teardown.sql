-- Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/test/sql/postgres/x/versioning/teardown.sql#1 $
-- 

--
-- Versioning tests.
--
-- Author: Vadim Nasardinov (vadimn@redhat.com)
-- Since:  2003-02-24
-- Version: $Id: //core-platform/dev/test/sql/postgres/x/versioning/teardown.sql#1 $
--          $DateTime: 2003/05/12 18:19:45 $

drop table te_vt1;
drop table te_c1;
drop table te_uvct2;
drop table te_uvct1;
drop table te_vt2;

drop table te_vt3;
drop table te_rt1;
