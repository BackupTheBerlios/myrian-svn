create unique index g11n_locales_locale_idx on
    g11n_locales (language, country, variant);

create index g11n_locales_default_char_idx on 
    g11n_locales(default_charset_id);
