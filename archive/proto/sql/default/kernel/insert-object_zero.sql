-------------------------
-- Add the Root Context -
-------------------------

-- developers should never access this object directly.
-- The only way to access this object is by checking/granting/revoking
-- UniversalPermissonDescriptors instead of regular PermissionDescriptors.
-- In the future, it is likely that this object will go away or not be
-- an ACSObject.
insert into acs_objects 
(object_id, object_type, display_name, 
 default_domain_class)
values 
(0, 'com.arsdigita.kernel.ACSObject', 'Universal Permission Context', 
 'com.arsdigita.kernel.ACSObject');
