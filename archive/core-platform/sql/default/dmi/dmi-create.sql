
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
