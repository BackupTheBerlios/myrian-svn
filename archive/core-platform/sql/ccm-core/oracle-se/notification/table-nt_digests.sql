--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the Open Software License v2.1
-- (the "License"); you may not use this file except in compliance with the
-- License. You may obtain a copy of the License at
-- http://rhea.redhat.com/licenses/osl2.1.html.
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/notification/table-nt_digests.sql#2 $
-- $DateTime: 2004/03/30 17:47:27 $

create table nt_digests (
    digest_id         integer
                      constraint nt_digest_pk
                          primary key
                      constraint nt_digest_fk
                          references acs_objects(object_id),
    party_from        integer
                      constraint nt_digest_party_from_fk
                          references parties(party_id),
    subject           varchar(250)
                      constraint nt_digest_subject_nn 
                          not null,
    header            varchar(4000)
                      constraint nt_digest_header_nn
                          not null,
    separator         varchar(100)
                      constraint nt_digest_separator_nn
                          not null,
    signature         varchar(4000)
                      constraint nt_digest_signature_nn
                          not null,
    frequency         integer
                      default 15
                      constraint nt_digest_frequence_nn
                          not null,
    next_run          date
                      constraint nt_digest_next_run_nn
                          not null
);
