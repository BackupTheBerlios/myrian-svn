create table g11n_locale_charset_map (
    locale_id                   integer
                                constraint g11n_lcm_locale_id_nn
                                not null
                                constraint g11n_lcm_locale_id_fk
                                references g11n_locales (locale_id),
    charset_id                  integer
                                constraint g11n_lcm_charset_id_nn
                                not null
                                constraint g11n_lcm_charset_id_fk
                                references g11n_charsets (charset_id)
);
