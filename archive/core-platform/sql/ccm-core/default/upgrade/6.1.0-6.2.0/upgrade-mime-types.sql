-- this adds a bunch of mime types for Open Office.

insert into cms_mime_types (mime_type, label, file_extension, java_class, object_type) values ('application/vnd.sun.xml.writer', 'OpenOffice Writer', 'sxw', 'com.arsdigita.mimetypes.TextMimeType', 'com.arsdigita.cms.TextMimeType');
insert into cms_mime_types (mime_type, label, file_extension, java_class, object_type) values ('application/vnd.sun.xml.writer.template', 'OpenOffice Writer Template', 'stw', 'com.arsdigita.mimetypes.TextMimeType', 'com.arsdigita.cms.TextMimeType');
insert into cms_mime_types (mime_type, label, file_extension, java_class, object_type) values ('application/vnd.sun.xml.calc', 'OpenOffice SpreadSheets', 'sxc', 'com.arsdigita.mimetypes.TextMimeType', 'com.arsdigita.cms.TextMimeType');
insert into cms_mime_types (mime_type, label, file_extension, java_class, object_type) values ('application/vnd.sun.xml.calc.template', 'OpenOffice SpreadSheets Template', 'stc', 'com.arsdigita.mimetypes.TextMimeType', 'com.arsdigita.cms.TextMimeType');
insert into cms_mime_types (mime_type, label, file_extension, java_class, object_type) values ('application/vnd.sun.xml.draw', 'OpenOffice Draw', 'sxd', 'com.arsdigita.mimetypes.MimeType', 'com.arsdigita.cms.MimeType');
insert into cms_mime_types (mime_type, label, file_extension, java_class, object_type) values ('application/vnd.sun.xml.draw.template', 'OpenOffice Draw Template', 'std', 'com.arsdigita.mimetypes.MimeType', 'com.arsdigita.cms.MimeType');
insert into cms_mime_types (mime_type, label, file_extension, java_class, object_type) values ('application/vnd.sun.xml.impress', 'OpenOffice Impress', 'sxi', 'com.arsdigita.mimetypes.MimeType', 'com.arsdigita.cms.MimeType');
insert into cms_mime_types (mime_type, label, file_extension, java_class, object_type) values ('application/vnd.sun.xml.impress.template', 'OpenOffice Impress Template', 'sti', 'com.arsdigita.mimetypes.MimeType', 'com.arsdigita.cms.MimeType');
insert into cms_mime_types (mime_type, label, file_extension, java_class, object_type) values ('application/vnd.sun.xml.writer.global', 'OpenOffice Writer Global', 'sxg', 'com.arsdigita.mimetypes.TextMimeType', 'com.arsdigita.cms.TextMimeType');
insert into cms_mime_types (mime_type, label, file_extension, java_class, object_type) values ('application/vnd.sun.xml.math', 'OpenOffice Math', 'sxm', 'com.arsdigita.mimetypes.TextMimeType', 'com.arsdigita.cms.TextMimeType');


insert into cms_mime_extensions (mime_type, file_extension) values ('application/vnd.sun.xml.writer', 'sxw');
insert into cms_mime_extensions (mime_type, file_extension) values ('application/vnd.sun.xml.writer.template', 'stw');
insert into cms_mime_extensions (mime_type, file_extension) values ('application/vnd.sun.xml.calc', 'sxc');
insert into cms_mime_extensions (mime_type, file_extension) values ('application/vnd.sun.xml.calc.template', 'stc');
insert into cms_mime_extensions (mime_type, file_extension) values ('application/vnd.sun.xml.draw', 'sxd');
insert into cms_mime_extensions (mime_type, file_extension) values ('application/vnd.sun.xml.draw.template', 'std');
insert into cms_mime_extensions (mime_type, file_extension) values ('application/vnd.sun.xml.impress', 'sxi');
insert into cms_mime_extensions (mime_type, file_extension) values ('application/vnd.sun.xml.impress.template', 'sti');
insert into cms_mime_extensions (mime_type, file_extension) values ('application/vnd.sun.xml.writer.global', 'sxg');
insert into cms_mime_extensions (mime_type, file_extension) values ('application/vnd.sun.xml.math', 'sxm');
