create table g11n_charsets (
    charset_id                  integer
                                constraint g11n_charsets_charset_id_pk
                                primary key,
    charset                     varchar(30)
                                constraint g11n_charsets_charset_nn
                                not null
);
