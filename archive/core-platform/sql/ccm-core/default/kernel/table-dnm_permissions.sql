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
-- $Id: //core-platform/dev/sql/ccm-core/default/kernel/table-dnm_permissions.sql#1 $
-- $DateTime: 2004/01/15 10:03:14 $
-- autor: Aram Kananov <aram@kananov.com>

create table dnm_permissions (
       pd_object_id integer 
           constraint dnm_permissions_oid_nn not null,
       pd_grantee_id integer 
           constraint dnm_permissions_gid_nn not null,
       pd_priv_01 	   varchar(1),
       pd_priv_02	   varchar(1),
       pd_priv_03	   varchar(1),
       pd_priv_04	   varchar(1),
       pd_priv_05	   varchar(1),
       pd_priv_06	   varchar(1),
       pd_priv_07	   varchar(1),
       pd_priv_08	   varchar(1),
       pd_priv_09	   varchar(1),
       pd_priv_10	   varchar(1),
       pd_priv_11	   varchar(1),
       pd_priv_12          varchar(1),
       pd_priv_13	   varchar(1),
       pd_priv_14	   varchar(1),
       pd_priv_15	   varchar(1),
       pd_priv_16          varchar(1),
       pd_priv_17	   varchar(1),
       pd_priv_18	   varchar(1),
       pd_priv_19	   varchar(1),
       pd_priv_20	   varchar(1),
       pd_priv_21	   varchar(1),
       pd_priv_22	   varchar(1),
       pd_priv_23	   varchar(1),
       pd_priv_24	   varchar(1),
       pd_priv_25	   varchar(1),
       pd_priv_26	   varchar(1),
       pd_priv_27	   varchar(1),
       pd_priv_28	   varchar(1),
       pd_priv_29	   varchar(1),
       pd_priv_30          varchar(1),
       pd_priv_31	   varchar(1),
       pd_priv_32	   varchar(1),
       pd_priv_33	   varchar(1),
       pd_priv_34	   varchar(1),
       pd_priv_35	   varchar(1),
       pd_priv_36	   varchar(1),
       pd_priv_37	   varchar(1),
       pd_priv_38	   varchar(1),
       pd_priv_39	   varchar(1),
       pd_priv_40	   varchar(1),
       pd_priv_41	   varchar(1),
       pd_priv_42	   varchar(1),
       pd_priv_43	   varchar(1),
       pd_priv_44	   varchar(1),
       pd_priv_45	   varchar(1),
       pd_priv_46	   varchar(1),
       pd_priv_47	   varchar(1),
       pd_priv_48	   varchar(1),
       pd_priv_49	   varchar(1),
       pd_priv_50	   varchar(1),
       pd_priv_51	   varchar(1),       
       pd_priv_52	   varchar(1),
       pd_priv_53	   varchar(1),
       pd_priv_54	   varchar(1),
       pd_priv_55	   varchar(1),
       pd_priv_56	   varchar(1),
       pd_priv_57	   varchar(1),
       pd_priv_58	   varchar(1),
       pd_priv_59	   varchar(1),
       pd_priv_60	   varchar(1),
       pd_priv_61	   varchar(1),
       pd_priv_62          varchar(1),
       pd_priv_63	   varchar(1),
       pd_priv_64	   varchar(1),
       pd_priv_65	   varchar(1),
       pd_priv_66          varchar(1),
       pd_priv_67	   varchar(1),
       pd_priv_68	   varchar(1),
       pd_priv_69	   varchar(1),
       pd_priv_70	   varchar(1),
       pd_priv_71	   varchar(1),
       pd_priv_72	   varchar(1),
       pd_priv_73	   varchar(1),
       pd_priv_74	   varchar(1),
       pd_priv_75	   varchar(1),
       pd_priv_76	   varchar(1),
       pd_priv_77	   varchar(1),
       pd_priv_78	   varchar(1),
       pd_priv_79	   varchar(1),
       pd_priv_80          varchar(1),
       pd_priv_81	   varchar(1),
       pd_priv_82	   varchar(1),
       pd_priv_83	   varchar(1),
       pd_priv_84	   varchar(1),
       pd_priv_85	   varchar(1),
       pd_priv_86	   varchar(1),
       pd_priv_87	   varchar(1),
       pd_priv_88	   varchar(1),
       pd_priv_89	   varchar(1),
       pd_priv_90	   varchar(1),
       pd_priv_91	   varchar(1),
       pd_priv_92	   varchar(1),
       pd_priv_93	   varchar(1),
       pd_priv_94	   varchar(1),
       pd_priv_95	   varchar(1),
       pd_priv_96	   varchar(1),
       pd_priv_97	   varchar(1),
       pd_priv_98	   varchar(1),
       pd_priv_99	   varchar(1),
       pd_n_grants integer not null,
       pd_priv_list   varchar(4000),
       constraint dnm_permissions_ck check (pd_n_grants > 0),
       constraint dnm_permissions_pk primary key(pd_object_id, pd_grantee_id)
) ;

--TODO: convert it to IOT? 
-- organization index including pd_priv_50 overflow;
