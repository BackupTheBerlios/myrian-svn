comment on table object_context is '
 The context_id column points to an object that provides a context for
 this object. Often this will reflect an observed hierarchy in a site,
 for example a bboard message would probably list a bboard topic as
 it''s context, and a bboard topic might list a sub-site as it''s
 context. Whenever we ask a question of the form "can user X perform
 action Y on object Z", the acs security model will defer to an
 object''s context if there is no information about user X''s
 permission to perform action Y on object Z. 
';
