-- The mime types tables were moved from CMS to WAF between 5.2.1 and 6.0.0.  Therefore, we
-- need to check first whether the tables already exist before creating them.
--------------------------------------------------------------------------------

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
    create table post_convert_html (
        query_id INTEGER not null
            constraint post_conve_htm_quer_id_p_qgdg9
              primary key,
        document CLOB
    )';

    execute immediate '
    create table pre_convert_html (
        id INTEGER not null
            constraint pre_convert_html_id_p_osi1n
              primary key,
        content BLOB
    )';

    execute immediate '
    alter table cms_image_mime_types add
        constraint cms_ima_mim_typ_mim_ty_f_s0zsx foreign key (mime_type)
          references cms_mime_types(mime_type)';

    execute immediate '
    alter table cms_text_mime_types add
        constraint cms_tex_mim_typ_mim_ty_f__tubf foreign key (mime_type)
          references cms_mime_types(mime_type)';

  end if;
end;
/
show errors;

update cms_mime_types
   set java_class = 'com.arsdigita.mimetypes.' || substr(java_class, 1 + length('com.arsdigita.cms.'))
 where instr(java_class, 'com.arsdigita.cms.') = 1;

update cms_image_mime_types
   set sizer_class = 'com.arsdigita.mimetypes.' || substr(sizer_class, 1 + length('com.arsdigita.cms.'))
 where instr(sizer_class, 'com.arsdigita.cms.') = 1;
