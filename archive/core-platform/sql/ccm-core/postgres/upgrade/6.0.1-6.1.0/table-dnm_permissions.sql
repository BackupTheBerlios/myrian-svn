--
-- Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
--
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public License
-- as published by the Free Software Foundation; either version 2.1 of
-- the License, or (at your option) any later version.
--
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
--
-- $Id: //core-platform/dev/sql/ccm-core/postgres/upgrade/6.0.1-6.1.0/table-dnm_permissions.sql#4 $
-- $DateTime: 2004/08/16 18:10:38 $

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
