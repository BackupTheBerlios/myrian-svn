create index acs_perm_grantee_priv_idx
    on acs_permissions (grantee_id, privilege);
create index acs_perm_creation_user_idx on acs_permissions(creation_user);
