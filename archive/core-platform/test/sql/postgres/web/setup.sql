create table test_application (
   	id               integer 
                           constraint test_application_id_pk primary key
                           references applications(application_id)
);

