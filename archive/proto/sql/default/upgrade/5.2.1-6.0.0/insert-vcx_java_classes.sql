--
-- Copyright (C) 2001-2003 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/proto/sql/default/upgrade/5.2.1-6.0.0/insert-vcx_java_classes.sql#2 $
-- $DateTime: 2003/08/04 16:15:53 $

-- NOTE: this needs to be kept in sync with
-- com.arsdigita.x.versioning.serialization.Types

insert into vcx_java_classes (id, name) 
values (0, 'java.lang.Void');

insert into vcx_java_classes (id, name) 
values (1, 'java.math.BigDecimal');

insert into vcx_java_classes (id, name) 
values (2, 'java.math.BigInteger');

insert into vcx_java_classes (id, name) 
values (3, 'not.implemented.Blob');

insert into vcx_java_classes (id, name) 
values (4, 'java.lang.Boolean');

insert into vcx_java_classes (id, name) 
values (5, 'java.lang.Byte');

insert into vcx_java_classes (id, name) 
values (6, 'java.lang.Character');

insert into vcx_java_classes (id, name) 
values (7, 'java.util.Date');

insert into vcx_java_classes (id, name) 
values (8, 'java.lang.Double');

insert into vcx_java_classes (id, name) 
values (9, 'java.lang.Float');

insert into vcx_java_classes (id, name) 
values (10, 'java.lang.Integer');

insert into vcx_java_classes (id, name) 
values (11, 'java.lang.Long');

insert into vcx_java_classes (id, name) 
values (12, 'com.arsdigita.persistence.OID');

insert into vcx_java_classes (id, name) 
values (13, 'java.lang.Short');

insert into vcx_java_classes (id, name) 
values (14, 'java.lang.String');

insert into vcx_java_classes (id, name) 
values (15, 'java.sql.Timestamp');

