create or replace procedure upgrade_stylesheets (key IN varchar, 
                                                 path IN varchar)
                                                 
as 
  x integer;
  pt integer;
begin
  -- SELECT INTO is very unforgiving
  -- only upgrade package if we've actually installed it!
  select count(1) into x from apm_package_types
      where package_key=key;
  if (x = 0) then
    return;
  end if;

  select acs_object_id_seq.nextval into x from dual;

  insert into acs_objects (object_id, object_type, display_name)
     values (x, 'com.arsdigita.kernel.Stylesheet', path);

  insert into acs_stylesheets
      (stylesheet_id, locale_id, output_type, pathname) 
      values (x, null, 'text/html', path);

  select package_type_id into pt from apm_package_types
      where package_key=key;

  delete from acs_stylesheet_type_map
   where package_type_id = pt;

  insert into acs_stylesheet_type_map (stylesheet_id, package_type_id)
      values (x, pt);
end;
/ 
show errors

begin
  upgrade_stylesheets('bebop', '/packages/bebop/xsl/bebop.xsl');
  upgrade_stylesheets('cms', '/packages/cms/xsl/cms.xsl');
  upgrade_stylesheets('cms-workspace', '/packages/cms/xsl/cms.xsl');
end;
/
show errors

drop procedure upgrade_stylesheets;

