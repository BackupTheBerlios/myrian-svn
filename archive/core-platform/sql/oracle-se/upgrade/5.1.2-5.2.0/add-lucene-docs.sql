create table lucene_docs (
    document_id INTEGER not null
        constraint lucen_docs_document_id_p_c2t6I
          primary key,
    content CLOB,
    country CHAR(2),
    creation_date DATE,
    creation_party INTEGER,
    is_deleted CHAR(1) not null,
    language CHAR(2),
    last_modified_date DATE,
    last_modified_party INTEGER,
    summary VARCHAR(4000),
    timestamp DATE not null,
    title VARCHAR(4000) not null,
    type VARCHAR(200) not null
);
