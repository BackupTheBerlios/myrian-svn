--
-- Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.redhat.com/licenses/ccmpl.html
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/test-packaging/sql/ccm-core/oracle-se/upgrade/5.2.1-6.0.0/mime-types.sql#1 $
-- $DateTime: 2003/09/29 17:25:28 $


declare
  v_exists char(1);
begin
  select count(*) into v_exists
    from user_tables
   where lower(table_name) = 'cms_image_mime_types';

  if (v_exists = '0') then
    execute immediate '
    create table cms_image_mime_types (
        mime_type VARCHAR(200) not null
            constraint cms_ima_mim_typ_mim_ty_p_9jrgn
              primary key,
            -- referential constraint for mime_type deferred due to circular dependencies
        sizer_class VARCHAR(4000)
    )';

    execute immediate '
    create table cms_mime_extensions (
        file_extension VARCHAR(200) not null
            constraint cms_mim_ext_fil_extens_p_pnyhk
              primary key,
        mime_type VARCHAR(200) not null
    )';

    execute immediate '
    create table cms_mime_status (
        mime_status_id INTEGER not null
            constraint cms_mim_sta_mim_sta_id_p_m5ygm
              primary key,
        hash_code INTEGER not null,
        inso_filter_works INTEGER not null
    )';

    execute immediate '
    create table cms_mime_types (
        mime_type VARCHAR(200) not null
            constraint cms_mim_type_mime_type_p_kl0ds
              primary key,
        label VARCHAR(200) not null,
        file_extension VARCHAR(200) not null,
        java_class VARCHAR(4000) not null,
        object_type VARCHAR(4000) not null
    )';

    execute immediate '
    create table cms_text_mime_types (
        mime_type VARCHAR(200) not null
            constraint cms_tex_mim_typ_mim_ty_p_3qbec
              primary key,
            -- referential constraint for mime_type deferred due to circular dependencies
        is_inso CHAR(1) not null
    )';

    execute immediate '
    alter table cms_image_mime_types add
        constraint cms_ima_mim_typ_mim_ty_f_s0zsx foreign key (mime_type)
          references cms_mime_types(mime_type)';

    execute immediate '
    alter table cms_text_mime_types add
        constraint cms_tex_mim_typ_mim_ty_f__tubf foreign key (mime_type)
          references cms_mime_types(mime_type)';
  else
    execute immediate 'comment on table cms_image_mime_types is ''''';
    execute immediate 'comment on column cms_image_mime_types.sizer_class is ''''';
    execute immediate 'comment on table cms_mime_types is ''''';
    execute immediate 'comment on column cms_mime_types.file_extension is ''''';
    execute immediate 'comment on column cms_mime_types.java_class is ''''';
    execute immediate 'comment on column cms_mime_types.object_type is ''''';
    execute immediate 'comment on table cms_text_mime_types is ''''';
    execute immediate 'comment on column cms_text_mime_types.is_inso is ''''';

  end if;
end;
/
show errors;

declare
  v_exists char(1);
begin

  select count(*) into v_exists
    from user_tables
   where lower(table_name) = 'pre_convert_html';

  if (v_exists = '1') then
    execute immediate 'drop index convert_to_html_index';
    execute immediate 'drop table pre_convert_html';
  end if;

  select count(*) into v_exists
    from user_tables
   where lower(table_name) = 'post_convert_html';

  if (v_exists = '1') then
    execute immediate 'drop table post_convert_html';
  end if;

end;
/
show errors;

create table pre_convert_html (
    id INTEGER not null
        constraint pre_convert_html_id_p_osi1n
          primary key,
    content BLOB
);
create index convert_to_html_index on pre_convert_html(content) indextype is
ctxsys.context parameters('filter ctxsys.inso_filter');

create table post_convert_html (
    query_id INTEGER not null
        constraint post_conve_htm_quer_id_p_qgdg9
          primary key,
    document CLOB
);

update cms_mime_types
   set java_class = 'com.arsdigita.mimetypes.' || substr(java_class, 1 + length('com.arsdigita.cms.'))
 where instr(java_class, 'com.arsdigita.cms.') = 1;

update cms_image_mime_types
   set sizer_class = 'com.arsdigita.mimetypes.' || substr(sizer_class, 1 + length('com.arsdigita.cms.'))
 where instr(sizer_class, 'com.arsdigita.cms.') = 1;
