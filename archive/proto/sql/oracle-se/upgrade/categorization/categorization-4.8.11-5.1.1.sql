-- adding the concept of an abstract category
alter table cat_categories add (abstract_p char(1) default '0' constraint cat_categories_abstract_p_ck check(abstract_p in ('0','1')));
