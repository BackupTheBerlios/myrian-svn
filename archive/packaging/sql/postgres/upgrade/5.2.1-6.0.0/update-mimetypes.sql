update cms_mime_types
set java_class = 'com.arsdigita.mimetypes.' ||
                  substring(java_class
                            from 1 + length('com.arsdigita.cms.'))
where position('com.arsdigita.cms.' in java_class) = 1;

update cms_image_mime_types
set sizer_class = 'com.arsdigita.mimetypes.' ||
                  substring(sizer_class
                            from 1 + length('com.arsdigita.cms.'))
where position('com.arsdigita.cms.' in sizer_class) = 1;
