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
-- $Id: //core-platform/test-qgen/sql/ccm-core/default/versioning/comment-vc_generic_operations.sql#1 $
-- $DateTime: 2003/12/10 16:59:20 $

comment on column vc_generic_operations.old_value is '
  The old value of the attribute (could be null). Non-varchar values 
  (such as integers, dates, etc.) are coerced to a string format.
';
