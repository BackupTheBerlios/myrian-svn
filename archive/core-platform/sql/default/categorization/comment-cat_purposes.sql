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
-- $Id: //core-platform/dev/sql/default/categorization/comment-cat_purposes.sql#2 $
-- $DateTime: 2003/01/07 14:51:38 $

comment on table cat_purposes is '
   Used to define what the various top-level branches 
   (i.e. direct children of the "/" category) are to be used for
';
comment on column cat_purposes.key is '
   Unique integer defined via static final ints in the Java domain class
';
comment on column cat_purposes.name is '
   Name is used to display the category purpose in select lists, etc.
';
