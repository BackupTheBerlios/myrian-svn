create table bebop_options (
       option_id                  integer
                                  constraint bebop_options_id_pk
                                  primary key,
       parameter_name             varchar(100),
       label                      varchar(300)          
);
