--
-- Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of the
-- License at http://www.redhat.com/licenses/ccmpl.html.
--
-- Software distributed under the License is distributed on an
-- "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
-- or implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/dev/sql/ccm-core/oracle-se/mimetypes/function-convert_to_html.sql#3 $
-- $DateTime: 2004/04/07 16:07:11 $


create or replace function convert_to_html (
        v_doc_id           in integer
) return varchar
  is
  result   varchar(512);
  err_num  number;
  err_msg  varchar(600);
begin
   -- Make sure nothing in destination table
   delete from post_convert_html where query_id = v_doc_id;
   result := ''; -- assume success
   -- Do conversion
   begin
      ctx_doc.filter('convert_to_html_index', v_doc_id, 
                     'post_convert_html', v_doc_id, FALSE);
   exception
   when others then
	err_num := SQLCODE;
	err_msg := SQLERRM;
        result := 'Error code=' || err_num || ' Error msg=' || err_msg;
   end;
   return result;
end convert_to_html;
/
show errors;
