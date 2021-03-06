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
-- $Id: //core-platform/test-qgen/sql/ccm-core/oracle-se/upgrade/5.1.2-5.2.0/drop-notes.sql#1 $
-- $DateTime: 2003/12/10 16:59:20 $


-- Data model to support sample notes application

-- Copyright (C) 2001 ArsDigita Corporation
-- Authors: Scott Seago (scott@arsdigita.com)
--	    Tzu-Mainn Chen (tzumainn@arsdigita.com)

drop table theme_stylesheet_map;
drop table notes;
drop table note_themes;
