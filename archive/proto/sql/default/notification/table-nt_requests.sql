-- Table for storing notification requests

create table nt_requests (
    request_id        integer
                      constraint nt_requests_pk
                          primary key
                      constraint nt_requests_fk
                          references acs_objects(object_id),
    digest_id         integer
                      constraint nt_requests_digest_fk
                          references nt_digests(digest_id),
    party_to          integer
                      constraint nt_requests_party_to_fk
                          references parties(party_id),
    message_id        integer                         
                      constraint nt_requests_message_fk
                          references messages(message_id),
    header            varchar(4000),
    signature         varchar(4000),
    expand_group      char(1)
                      default '1'
                      constraint nt_requests_expand_ck
                          check (expand_group in ('0','1')),
    request_date      timestamp
                      default current_timestamp,
    fulfill_date      timestamp,
    status            varchar(20)
                      default 'pending'
                      constraint nt_requests_status_ck
                          check (status in 
                              ('pending',
                               'queued',
                               'sent',
                               'failed_partial',
                               'failed',
                               'cancelled')),
    max_retries       integer
                      default 3
                      constraint nt_requests_retries_nn
                          not null,
    expunge_p         char(1)
                      default '1'
                      constraint nt_requests_expunge_ck
                          check (expunge_p in ('0','1')),
    expunge_msg_p     char(1)
                      default '1'
                      constraint nt_requests_expunge_msg_ck
                         check (expunge_msg_p in ('0','1'))
);
