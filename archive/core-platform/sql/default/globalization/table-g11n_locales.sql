create table g11n_locales (
    locale_id                   integer
                                constraint g11n_locales_locale_id_pk
                                primary key,
    language                    char(2)
                                constraint g11n_locales_language_nn
                                not null,
    country                     char(2),
    variant                     varchar(30),
    default_charset_id          integer
                                constraint g11n_locales_def_charset_id_fk
                                references g11n_charsets (charset_id)
);
