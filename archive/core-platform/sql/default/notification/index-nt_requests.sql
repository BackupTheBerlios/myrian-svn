-- foreign key indexes
create index nt_requests_digest_id_idx on nt_requests(digest_id);
create index nt_requests_message_id_idx on nt_requests(message_id);
create index nt_requests_party_to_idx on nt_requests(party_to);
