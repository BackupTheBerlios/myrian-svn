--
-- Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
--
-- The contents of this file are subject to the ArsDigita Public 
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.arsdigita.com/ADPL.txt
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--

--
-- This file contains the data model for the party test cases.
--
-- @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
-- @version $Revision: #1 $ $Date: 2002/07/22 $
--

create table t_articles (
    article_id    integer primary key,
    text          clob
);

create table t_images (
    image_id    integer primary key,
    bytes       blob
);

create table t_article_image_map (
    article_id    integer references t_articles,
    image_id      integer references t_images,
    caption       varchar(4000),
    primary key (article_id, image_id)
);

