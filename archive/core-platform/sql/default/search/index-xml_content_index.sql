-- Indices created on the content.

CREATE INDEX xml_content_index ON search_content(xml_content) INDEXTYPE IS ctxsys.context
   parameters('filter ctxsys.null_filter section group autogroup');
