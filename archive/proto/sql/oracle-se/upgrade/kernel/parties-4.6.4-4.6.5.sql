
-- NOTE TO UPGRADERS:
--
-- We add a non-nullable "default_domain_class" property.  For existing
-- data, there is no way to correctly populate this column.  This upgrade
-- script will just "guess" that the default_domain_class is the same as
-- the "object_type".  This is correct for the common cases, but not for
-- all cases.  The significance of the "default_domain_class" column is
-- explained below.
--
-- ACSObject will save the original domain class name of a domain object in
-- the "default_domain_class" column.  This data is used by the 
-- ACSObjectInstantiator (which is used by DomainObjectFactory).  The
-- instantiator is given a data object and produces a domain object.
-- It will instantiate the default domain class using reflection in the 
-- event that no other instantiator is able to handle the data object.
-- 
-- Although this upgrade script cannot perfectly populate the 
-- default_domain_class column, no existing code should break as a result.
-- The feature this supports is a new feature of ACSObjectInstantiator/
-- DomainObjectFactory.  Only code that relies on this new feature will be
-- affected in the uncommon case that the object_type and default_domain_class
-- should not be the same value.

alter table acs_objects add
    default_domain_class      varchar2(100);

update acs_objects set default_domain_class=object_type;
commit;

