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
-- $Id: //core-platform/dev/sql/default/dmi/dmi-create.sql#3 $
-- $DateTime: 2003/08/15 13:46:34 $


-- data model for DMI: Data Model Initializer 
-- by Bryan Che (bryanche@arsdigita.com)

-- what products are installed: ACS, ECM...
create table dmi_products (
   product_id                      integer
                                   constraint dmi_products_product_id_pk primary key,
   product_name			   varchar(200)
                                   constraint dmi_products_product_name_un unique
                                   constraint dmi_products_product_name_nn not null,
   creation_date		   date
                                   constraint dmi_products_creation_date_nn not null,
   description			   varchar(4000)
);

-- what versions have been installed
create table dmi_product_versions (
   version_id                      integer
                                   constraint dmi_prod_vers_vers_id_pk primary key,
   product_id			   integer
                                   constraint dmi_prod_vers_prd_id_nn not null
                                   constraint dmi_prod_vers_prd_id_fk references dmi_products,
   version_name			   varchar(50)
                                   constraint dmi_product_versions_name_nn not null,
   creation_date		   date
                                   constraint
				   dmi_product_versions_date_nn not null,
   previous_version_id		   integer
                                   constraint dmi_product_versions_prev_v_fk 
				   references dmi_product_versions,
   -- file used to load the data model for this version				   
   install_file			   varchar(300)
                                   constraint dmi_product_versions_file_nn not null, 
   install_errors		   varchar(4000),
   description			   varchar(4000)
);

-- use the same sequence for both tables   		       
create sequence dmi_products_seq;
