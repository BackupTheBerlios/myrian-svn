--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of the
-- License at http://www.redhat.com/licenses/ccmpl.html.
--
-- Software distributed under the License is distributed on an
-- "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
-- or implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/default/categorization/comment-cat_categories.sql#4 $
-- $DateTime: 2004/04/07 16:07:11 $

comment on table cat_categories is '
  Defines a category.
';
comment on column cat_categories.enabled_p is '
 To let content producers suggest categories that can later be approved
 by admins. This is a form of collaborative categorization.
';
