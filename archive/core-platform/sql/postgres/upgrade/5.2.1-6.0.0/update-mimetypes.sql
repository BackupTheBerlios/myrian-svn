--
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
-- $Id: //core-platform/dev/sql/postgres/upgrade/5.2.1-6.0.0/update-mimetypes.sql#2 $
-- $DateTime: 2003/08/15 13:46:34 $

update cms_mime_types
set java_class = 'com.arsdigita.mimetypes.' ||
                  substring(java_class
                            from 1 + length('com.arsdigita.cms.'))
where position('com.arsdigita.cms.' in java_class) = 1;

update cms_image_mime_types
set sizer_class = 'com.arsdigita.mimetypes.' ||
                  substring(sizer_class
                            from 1 + length('com.arsdigita.cms.'))
where position('com.arsdigita.cms.' in sizer_class) = 1;
