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
-- $Id: //core-platform/test-packaging/sql/default/versioning/insert-vc_actions.sql#2 $
-- $DateTime: 2003/08/19 22:28:24 $

  insert into vc_actions (action, description) 
  values (
    'create', 'Create a new value for the attribute'
  );

 insert into vc_actions (action, description)
  values (
    'update', 'Update the attribute''s value (for single-valued
attributes)'
  );

  insert into vc_actions (action, description)
  values (
    'add', 'Add a value for a multi-valued attribute'
  );

  insert into vc_actions (action, description)
  values (
    'remove', 'Remove a value from a multi-valued attribute'
  );

  insert into vc_actions (action, description)
  values (
    'create_content', 'Create new content for the object'
  );

  insert into vc_actions (action, description)
  values (
    'update_content', 'Modify existing content for the object'
  );
