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
-- //enterprise/kernel/dev/kernel/sql/parties-create.sql 
--
-- @author oumi@arsdigita.com
-- @creation-date 2001-05-10
-- @version $Id: //core-platform/dev/sql/oracle-se/kernel/parties-create.sql#1 $
--

---------------------
-- EMAIL_ADDRESSES --
---------------------

create table email_addresses (
	email_address   varchar2(100) not null
                    constraint email_addresses_pk primary key
                    constraint email_address_lower_ck
                        check (lower(email_address) = email_address),
	-- TO DO: these should be non-nullable and default to whatever
	-- JDBC's version of false is.
	bouncing_p	char(1),
	verified_p	char(1)
);

comment on table email_addresses is '
 The email table is used for the Email data object, which is a general
 purpose object for reprsenting email addresses and related information.
';

-------------
-- PARTIES --
-------------

create table parties (
	party_id	    integer
                    constraint parties_party_id_nn
                    not null
			        constraint parties_party_id_fk references
			        acs_objects (object_id)
			        constraint parties_pk primary key,
    primary_email   varchar2(100),
	uri		        varchar(200)
);

comment on table parties is '
 Party is the supertype of user and group. It exists because
 many other types of object can have relationships to parties.
';

comment on column parties.primary_email is '
 Stores a reference to the party''s primary email address.
';

comment on column parties.uri is '
 This URI is a user-specified URI for the party.  E.g., a personal web page,
 a company web site, etc.
';

create table party_email_map (
	party_id	    integer not null
    			    constraint pem_party_id_fk
	        		    references parties(party_id) on delete cascade,
    email_address   varchar2(100),
    constraint pem_party_email_uq
                    unique(party_id, email_address)
);

comment on table party_email_map is '
  Supports the association between parties and their email addresses.
';

--     In order to work around some optimization issues with MDSQL,
--     we won't bother introducing this foreign key constraint
--     as it introduces circular dependencies between party and 
--     party_email_map.
--
-- alter table parties add constraint parties_primary_email_fk 
--   foreign key (party_id, primary_email) 
--       references party_email_map (party_id, email_address);


------------------
-- PERSON_NAMES --
------------------

create table person_names (
	name_id	        integer not null
                        constraint person_names_pk primary key,
	given_name	varchar2(60) not null,
	family_name	varchar2(60) not null,
	middle_names	varchar2(80)
);

comment on table person_names is '
 The person_names table is used for the PersonName data object, which is a 
 general purpose object for reprsenting names of people.
';


-----------
-- USERS --
-----------

create table users (
	user_id	    integer
                constraint users_user_id_nn
                not null
				constraint users_user_id_fk
				references parties (party_id)
				constraint users_pk primary key,
    name_id     integer
                constraint users_person_name_id_nn
                not null
				constraint users_person_name_id_fk
				references person_names(name_id)
				constraint users_person_name_id_un unique,
	screen_name		varchar2(100)
				constraint users_screen_name_un
				unique
);


comment on table users is '
 A user is a type of party.  In the data object model and domain object layer,
 we need to make sure that a user has a unique primary email address that is
 not null.  The primary email address referenced in the parties table is
 non unique and nullable.
';


----------------------------
-- GROUP TYPES AND GROUPS --
----------------------------

create table groups (
    group_id    integer
                constraint groups_group_id_nn
                not null
                constraint groups_group_id_fk
                references parties (party_id)
                constraint groups_pk primary key,
	name		varchar(200) not null
);

create table group_member_map (
        id              integer not null
                        constraint gmm_membership_id_pk
                        primary key,
	group_id            integer
                        constraint gmm_group_id_nn
                        not null
                        constraint gmm_group_id_fk
                       	references groups(group_id)
                        on delete cascade,
	member_id           integer 
                        constraint gmm_member_id_nn
                        not null
                       	constraint gmm_member_id_fk
                        references users(user_id),
	constraint gmm_group_member_un unique(group_id, member_id)
);

-- TO DO: add a constraint that prevents circularity in the subgroup graph.
-- This would probably be enforced by trigger.

create table group_subgroup_map (
        id              integer not null
                        constraint gsm_id_pk
                        primary key,
	group_id            integer
                        constraint gsm_group_id_nn
                        not null
                        constraint gsm_group_id_fk
                        references groups(group_id),
	subgroup_id         integer
                        constraint gsm_subgroup_id_nn
                        not null
                        constraint gsm_subgroup_id_fk
                        references groups(group_id),
	constraint gsm_group_party_un unique(group_id, subgroup_id),
	constraint gsm_circularity_ck check (group_id!=subgroup_id)
);


-----------------
-- GROUP ROLES --
-----------------

create table roles (
        role_id           integer 
                          constraint roles_role_id_pk primary key,
        group_id          integer 
                          constraint group_roles_group_id_nn not null
                          constraint group_roles_group_id_fk references groups(group_id)
                          on delete cascade,
        name              varchar(200) 
                          constraint roles_name_nn not null,
        description       varchar(4000),
        implicit_group_id integer 
                          constraint group_roles_impl_group_id_nn not null
                          constraint group_roles_impl_group_id_fk references groups(group_id)
                          on delete cascade,
        constraint roles_different_groups check(implicit_group_id <> group_id),
        constraint roles_group_id_name_un unique(group_id, name)
);

create index roles_implicit_group_id_idx on roles(implicit_group_id);

comment on table roles is '
        This table is used to store metadata about the roles in the
        system. Each role is represented by the Role object type.
';

comment on column roles.group_id is '
        This column refers to the group for which the role was
        created.
';

comment on column roles.implicit_group_id is '
        Temporary hack. Implementation currently creates a subgroup
        for each row. The created subgroup is references by
        implicit_group_id.  
';


----------------------
-- DENORMALIZATIONS --
----------------------

create table group_subgroup_trans_index (
	group_id	integer 
                constraint gsti_group_id_nn
                not null
			    constraint gsti_group_id_fk
			    references groups(group_id) on delete cascade,
	subgroup_id	integer
                constraint gsti_subgroup_id_nn
                not null
			    constraint gsti_subgroup_id_fk
			    references groups(group_id) on delete cascade,
    n_paths     integer not null,
	constraint gsti_group_party_pk primary key(group_id, subgroup_id),
    -- This prevents circularity in the group-subgroup graph.
    -- If group_id=subgroup_id then n_paths=0.
	constraint gsti_circularity_ck 
                check ( group_id!=subgroup_id or n_paths=0 ),
    -- This constraint makes sure that we never forget to delete rows when
    -- we decrement n_paths.  n_paths should never reach 0 except for
    -- mappings where group_id=subgroup_id (in which case n_paths should
    -- always be 0 due to above constraint).
    constraint gsti_n_paths_ck
                check (n_paths>0 or group_id=subgroup_id)
) organization index;

create table group_member_trans_index (
	group_id	integer 
                constraint gmti_group_id_nn
                not null
			    constraint gmti_group_id_fk
			    references groups(group_id) on delete cascade,
	member_id	integer
                constraint gmti_subgroup_id_nn
                not null
			    constraint gmti_subgroup_id_fk
			    references users(user_id) on delete cascade,
    n_paths     integer not null,
	constraint gmti_group_party_pk primary key(group_id, member_id),
    -- This constraint makes sure that we never forget to delete rows when
    -- we decrement n_paths.  n_paths should never reach 0 except for
    -- mappings where group_id=subgroup_id (in which case n_paths should
    -- always be 0 due to above constraint).
    constraint gmti_n_paths_ck
                check (n_paths>0)
) organization index;

create or replace package parties_denormalization
as
  procedure add_group (
      group_id    in groups.group_id%TYPE
  );
  procedure add_subgroup (
      group_id    in groups.group_id%TYPE,
      subgroup_id in groups.group_id%TYPE
  );
  procedure remove_subgroup (
      group_id    in groups.group_id%TYPE,
      subgroup_id in groups.group_id%TYPE
  );
  procedure add_member (
      group_id    in groups.group_id%TYPE,
      member_id   in users.user_id%TYPE
  );
  procedure remove_member (
      group_id    in groups.group_id%TYPE,
      member_id   in users.user_id%TYPE
  );
  procedure add_subgroup_members (
      group_id    in groups.group_id%TYPE,
      subgroup_id in groups.group_id%TYPE
  );
  procedure remove_subgroup_members (
      group_id    in groups.group_id%TYPE,
      subgroup_id in groups.group_id%TYPE
  );
end parties_denormalization;
/
show errors

create or replace package body parties_denormalization
as
  procedure add_group (
      group_id    in groups.group_id%TYPE
  )
  as begin
      -- every group is a subgroup of itself.
      insert into group_subgroup_trans_index
      (group_id, subgroup_id, n_paths)
      values
      (add_group.group_id, add_group.group_id, 0);
  end add_group;

  procedure add_subgroup (
      group_id    in groups.group_id%TYPE,
      subgroup_id in groups.group_id%TYPE
  )
  as
    v_path_increment integer;
  begin

      add_subgroup_members(group_id, subgroup_id);

      for new_entry in (
          select ancestors.group_id, descendants.subgroup_id,
                 (ancestors.n_paths * descendants.n_paths) as n_paths
          from group_subgroup_trans_index ancestors,
               group_subgroup_trans_index descendants
          where ancestors.subgroup_id = add_subgroup.group_id
            and descendants.group_id = add_subgroup.subgroup_id
      ) loop

          if ((add_subgroup.group_id = new_entry.group_id) or
              (add_subgroup.subgroup_id = new_entry.subgroup_id)) then
            v_path_increment := 1;
          else 
            v_path_increment := new_entry.n_paths;
          end if;

          update group_subgroup_trans_index
          set n_paths = n_paths + v_path_increment
          where group_id = new_entry.group_id
            and subgroup_id = new_entry.subgroup_id;

          if (SQL%NOTFOUND) then

              insert into group_subgroup_trans_index
              (group_id, subgroup_id, n_paths)
              values
              (new_entry.group_id, new_entry.subgroup_id, v_path_increment);
          end if;
      end loop;

  end add_subgroup;

  procedure remove_subgroup (
      group_id    in groups.group_id%TYPE,
      subgroup_id in groups.group_id%TYPE
  )
  as
    v_path_decrement integer;
  begin

      for remove_entry in (
          select ancestors.group_id, descendants.subgroup_id,
                 (ancestors.n_paths * descendants.n_paths) as n_paths
          from group_subgroup_trans_index ancestors,
               group_subgroup_trans_index descendants
          where ancestors.subgroup_id = remove_subgroup.group_id
            and descendants.group_id = remove_subgroup.subgroup_id
      ) loop

        if ((remove_entry.group_id = remove_subgroup.group_id) or
            (remove_entry.subgroup_id = remove_subgroup.subgroup_id)) then
            v_path_decrement := 1;
        else
            v_path_decrement := remove_entry.n_paths;
        end if;

        -- delete this entry if n_path would become 0 if we were
        -- to decrement n_paths
        delete from group_subgroup_trans_index
        where group_id = remove_entry.group_id
          and subgroup_id = remove_entry.subgroup_id
          and n_paths <= v_path_decrement;

        -- if nothing got deleted, then decrement n_paths
        if (SQL%NOTFOUND) then

           update group_subgroup_trans_index
              set n_paths = n_paths - v_path_decrement
            where group_id = remove_entry.group_id
              and subgroup_id = remove_entry.subgroup_id;

        end if;

      end loop;

      remove_subgroup_members(group_id, subgroup_id);

  end remove_subgroup;

  procedure add_member (
      group_id    in groups.group_id%TYPE,
      member_id   in users.user_id%TYPE
  )
  as
    v_path_increment integer;
  begin

      for new_entry in (
          select ancestors.group_id, ancestors.n_paths
          from group_subgroup_trans_index ancestors
          where ancestors.subgroup_id = add_member.group_id
      ) loop

          if (add_member.group_id = new_entry.group_id) then
            v_path_increment := 1;
          else 
            v_path_increment := new_entry.n_paths;
          end if;

          update group_member_trans_index
          set n_paths = n_paths + v_path_increment
          where group_id = new_entry.group_id
            and member_id = add_member.member_id;

          if (SQL%NOTFOUND) then

              insert into group_member_trans_index
              (group_id, member_id, n_paths)
              values
              (new_entry.group_id, add_member.member_id, v_path_increment);
          end if;
      end loop;

  end add_member;

  procedure remove_member (
      group_id    in groups.group_id%TYPE,
      member_id   in users.user_id%TYPE
  )
  as
    v_path_decrement integer;
  begin

      for remove_entry in (
          select ancestors.group_id, ancestors.n_paths
          from group_subgroup_trans_index ancestors
          where ancestors.subgroup_id = remove_member.group_id
      ) loop

        if (remove_entry.group_id = remove_member.group_id) then
            v_path_decrement := 1;
        else
            v_path_decrement := remove_entry.n_paths;
        end if;

        -- delete this entry if n_path would become 0 if we were
        -- to decrement n_paths
        delete from group_member_trans_index
        where group_id = remove_entry.group_id
          and member_id = remove_member.member_id
          and n_paths <= v_path_decrement;

        -- if nothing got deleted, then decrement n_paths
        if (SQL%NOTFOUND) then

           update group_member_trans_index
              set n_paths = n_paths - v_path_decrement
            where group_id = remove_entry.group_id
              and member_id = v_path_decrement;

        end if;

      end loop;

  end remove_member;

  procedure add_subgroup_members (
      group_id    in groups.group_id%TYPE,
      subgroup_id in groups.group_id%TYPE
  )
  as
    v_path_increment integer;
  begin

      for new_entry in (
          select ancestors.group_id, members.member_id,
                 (ancestors.n_paths * members.n_paths) as n_paths
          from group_subgroup_trans_index ancestors,
               group_member_trans_index members
          where ancestors.subgroup_id = add_subgroup_members.group_id
            and members.group_id = add_subgroup_members.subgroup_id
      ) loop

          if (add_subgroup_members.group_id = new_entry.group_id) then
            v_path_increment := 1;
          else 
            v_path_increment := new_entry.n_paths;
          end if;

          update group_member_trans_index
          set n_paths = n_paths + v_path_increment
          where group_id = new_entry.group_id
            and member_id = new_entry.member_id;

          if (SQL%NOTFOUND) then

              insert into group_member_trans_index
              (group_id, member_id, n_paths)
              values
              (new_entry.group_id, new_entry.member_id, v_path_increment);
          end if;
      end loop;

  end add_subgroup_members;

  procedure remove_subgroup_members (
      group_id    in groups.group_id%TYPE,
      subgroup_id in groups.group_id%TYPE
  )
  as
    v_path_decrement integer;
  begin

      for remove_entry in (
          select ancestors.group_id, members.member_id,
                 (ancestors.n_paths * members.n_paths) as n_paths
          from group_subgroup_trans_index ancestors,
               group_member_trans_index members
          where ancestors.subgroup_id = remove_subgroup_members.group_id
            and members.group_id = remove_subgroup_members.subgroup_id
      ) loop

          if (remove_subgroup_members.group_id = remove_entry.group_id) then
            v_path_decrement := 1;
          else 
            v_path_decrement := remove_entry.n_paths;
          end if;

        -- delete this entry if n_path would become 0 if we were
        -- to decrement n_paths
        delete from group_member_trans_index
        where group_id = remove_entry.group_id
          and member_id = remove_entry.member_id
          and n_paths <= v_path_decrement;

        -- if nothing got deleted, then decrement n_paths
        if (SQL%NOTFOUND) then

           update group_member_trans_index
              set n_paths = n_paths - v_path_decrement
            where group_id = remove_entry.group_id
              and member_id = remove_entry.member_id;

        end if;

      end loop;

  end remove_subgroup_members;

end parties_denormalization;
/
show errors

--
-- Triggers to maintain denormalizations
--

create or replace trigger groups_in_tr
after insert on groups
for each row
begin
    parties_denormalization.add_group(:new.group_id);
end;
/
show errors

-- Subgroup triggers

create or replace trigger group_subgroup_in_tr
after insert on group_subgroup_map
for each row
begin
    parties_denormalization.add_subgroup(:new.group_id, :new.subgroup_id);
end;
/
show errors

create or replace trigger group_subgroup_del_tr
after delete on group_subgroup_map
for each row
begin
    parties_denormalization.remove_subgroup(:old.group_id, :old.subgroup_id);
end;
/
show errors

create or replace trigger group_subgroup_up_tr
after update on group_subgroup_map
for each row
begin
    parties_denormalization.remove_subgroup(:old.group_id, :old.subgroup_id);
    parties_denormalization.add_subgroup(:new.group_id, :new.subgroup_id);
end;
/
show errors

-- Membership triggers

create or replace trigger group_member_in_tr
after insert on group_member_map
for each row
begin
    parties_denormalization.add_member(:new.group_id, :new.member_id);
end;
/
show errors

create or replace trigger group_member_del_tr
after delete on group_member_map
for each row
begin
    parties_denormalization.remove_member(:old.group_id, :old.member_id);
end;
/
show errors

create or replace trigger group_member_up_tr
after update on group_member_map
for each row
begin
    parties_denormalization.remove_member(:old.group_id, :old.member_id);
    parties_denormalization.add_member(:new.group_id, :new.member_id);
end;
/
show errors


-----------
-- VIEWS --
-----------

-- This view's implementation will change when we implement denormalizations.
--
--       If there is a path from group A to group B through the subgroup 
--       graph, then this view will contain (A,B).  Note that this implies
--       it would contain (A,A)
--
create or replace view group_subgroup_trans_map
as select group_id, subgroup_id
   from group_subgroup_trans_index;

--       IF user u is a direct member of group B  
--          AND
--          (A,B) is in group_subgroup_trans_map
--       THEN 
--          this view will contain (A,u).  
create or replace view group_member_trans_map
as select group_id, member_id from group_member_trans_index;


create or replace view party_member_trans_map
as select user_id as party_id, user_id as member_id 
   from users
   UNION ALL
   select group_id as party_id, member_id
   from group_member_trans_index;

--------------------------------
-- KERNEL USER AUTHENTICATION --
--------------------------------

create table user_authentication (
	auth_id			integer not null
				constraint user_auth_pk primary key,
	user_id			not null
				constraint user_auth_user_id_fk
				references users (user_id)
				constraint user_auth_user_un unique,
	password		varchar2(100) not null,
	salt			varchar2(100),
	password_question	varchar2(1000),
	password_answer		varchar2(1000)
);

-------------
-- INDEXES --
-------------

create unique index gmti_member_group_idx 
    on group_member_trans_index (member_id, group_id);

create unique index gsti_subgroup_group_idx
    on group_subgroup_trans_index (subgroup_id, group_id);

create index parties_primary_email_idx
    on parties (primary_email);
