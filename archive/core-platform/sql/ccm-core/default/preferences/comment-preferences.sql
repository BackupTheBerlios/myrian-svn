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
-- $Id: //core-platform/dev/sql/ccm-core/default/preferences/comment-preferences.sql#1 $
-- $DateTime: 2003/10/23 15:28:18 $

comment on table preferences is '
    The table stores the preference information. An entry can either be a
    preference node or a key-value pair parameter.
';

comment on column preferences.parent_id is '
    The parent preference id.
';

comment on column preferences.name is '
    Must be unique within the parent node directory.
';

comment on column preferences.is_node is '
    Whether the preference is a node or key-value pair. Note that in case of a
    node, we have no entries in the value_* fields.
';

comment on column preferences.preference_type is '
    Currently we see ''user'' and ''system'' preference types; this might be
    extended in the future.
';

comment on column preferences.value_type is '
    Here go the parameter values in case this is a parameter. null is a
    legitimate value, eg., in case someone specifies an empty string.
';

comment on column preferences.value_string is '
    The value string can hold int, long, float, double, boolean, string and
    byte[].
';
