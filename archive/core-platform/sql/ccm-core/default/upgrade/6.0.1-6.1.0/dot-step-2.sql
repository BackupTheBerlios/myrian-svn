--
-- Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
-- $Id: //core-platform/dev/sql/ccm-core/default/upgrade/6.0.1-6.1.0/dot-step-2.sql#4 $
-- $DateTime: 2004/04/07 16:07:11 $
drop table persistence_dynamic_ot;
drop table persistence_dynamic_assoc;
alter table pdot_backup rename to persistence_dynamic_ot;
alter table pda_backup rename to persistence_dynamic_assoc;
