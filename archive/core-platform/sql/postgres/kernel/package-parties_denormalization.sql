create or replace function parties_add_group (
    integer
  )
  returns integer as '
  declare
    v_group_id alias for $1;
  begin
      -- every group is a subgroup of itself.
      insert into group_subgroup_trans_index
      (group_id, subgroup_id, n_paths)
      values
      (v_group_id, v_group_id, 0);
  return null;
  end' language 'plpgsql';

create or replace function parties_add_subgroup (
     integer, integer
  )
  returns integer as '
  declare
    v_group_id alias for $1;
    v_subgroup_id alias for $2;
    v_path_increment integer;
    new_entry record;
    v_exists_p integer;
  begin

      perform parties_add_subgroup_members(v_group_id, v_subgroup_id);

      for new_entry in 
          select ancestors.group_id, descendants.subgroup_id,
                 (ancestors.n_paths * descendants.n_paths) as n_paths
          from group_subgroup_trans_index ancestors,
               group_subgroup_trans_index descendants
          where ancestors.subgroup_id = v_group_id
            and descendants.group_id = v_subgroup_id
      loop

          if ((v_group_id = new_entry.group_id) or
              (v_subgroup_id = new_entry.subgroup_id)) then
            v_path_increment := 1;
          else 
            v_path_increment := new_entry.n_paths;
          end if;

          select count(*) into v_exists_p
          from group_subgroup_trans_index
          where group_id = new_entry.group_id
            and subgroup_id = new_entry.subgroup_id;

          IF (v_exists_p > 0) THEN          
             update group_subgroup_trans_index
              set n_paths = n_paths + v_path_increment
              where group_id = new_entry.group_id
               and subgroup_id = new_entry.subgroup_id;
          else 
              insert into group_subgroup_trans_index
              (group_id, subgroup_id, n_paths)
              values
              (new_entry.group_id, new_entry.subgroup_id, v_path_increment);
          end if;
      end loop;

  return null;
  end;' language 'plpgsql';
  
create or replace function parties_remove_subgroup (
        integer, integer
  )
  returns integer as '
  declare
    v_group_id alias for $1;
    v_subgroup_id alias for $2;
    v_path_decrement integer;
    remove_entry record;
  begin

      for remove_entry in 
          select ancestors.group_id, descendants.subgroup_id,
                 (ancestors.n_paths * descendants.n_paths) as n_paths
          from group_subgroup_trans_index ancestors,
               group_subgroup_trans_index descendants
          where ancestors.subgroup_id = v_group_id
            and descendants.group_id = v_subgroup_id
      loop

        if ((remove_entry.group_id = v_group_id) or
            (remove_entry.subgroup_id = v_subgroup_id)) then
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
        if (NOT FOUND) then

           update group_subgroup_trans_index
              set n_paths = n_paths - v_path_decrement
            where group_id = remove_entry.group_id
              and subgroup_id = remove_entry.subgroup_id;

        end if;

      end loop;

      perform parties_remove_subgroup_members(v_group_id, v_subgroup_id);

  return null;
  end;' language 'plpgsql';

create or replace function parties_add_member (
     integer, integer
  )
  returns integer as ' 
   declare
    v_group_id alias for $1;
    v_member_id alias for $2;
    v_path_increment integer;
    new_entry record;
    v_exists_p integer;
  begin

      for new_entry in 
          select ancestors.group_id, ancestors.n_paths
          from group_subgroup_trans_index ancestors
          where ancestors.subgroup_id = v_group_id
      loop

          if (v_group_id = new_entry.group_id) then
            v_path_increment := 1;
          else 
            v_path_increment := new_entry.n_paths;
          end if;

          select count(*) into v_exists_p
          from group_subgroup_trans_index
          where group_id = new_entry.group_id
            and subgroup_id = new_entry.subgroup_id;

          IF (v_exists_p > 0) THEN          
              update group_member_trans_index
              set n_paths = n_paths + v_path_increment
              where group_id = new_entry.group_id
                and member_id = v_member_id;
          else 
              insert into group_member_trans_index
              (group_id, member_id, n_paths)
              values
              (new_entry.group_id, v_member_id, v_path_increment);
          end if;
      end loop;

  return null;
  end;' language 'plpgsql';

create or replace function parties_remove_member (
         integer, integer
  )
  returns integer as '
   declare
     v_group_id alias for $1;
     v_member_id alias for $2;
     v_path_decrement integer;
     remove_entry record;
  begin

      for remove_entry in 
          select ancestors.group_id, ancestors.n_paths
          from group_subgroup_trans_index ancestors
          where ancestors.subgroup_id = v_group_id
      loop

        if (remove_entry.group_id = v_group_id) then
            v_path_decrement := 1;
        else
            v_path_decrement := remove_entry.n_paths;
        end if;

        -- delete this entry if n_path would become 0 if we were
        -- to decrement n_paths
        delete from group_member_trans_index
        where group_id = remove_entry.group_id
          and member_id = v_member_id
          and n_paths <= v_path_decrement;

        -- if nothing got deleted, then decrement n_paths
        if (NOT FOUND) then

           update group_member_trans_index
              set n_paths = n_paths - v_path_decrement
            where group_id = remove_entry.group_id
              and member_id = v_path_decrement;

        end if;

      end loop;

  return null;
  end;' language 'plpgsql';

create or replace function parties_add_subgroup_members (
      integer, integer
  )
  returns integer as '
  declare
    v_group_id alias for $1;
    v_subgroup_id alias for $2;
    v_path_increment integer;
    new_entry record;
  begin

      for new_entry in 
          select ancestors.group_id, members.member_id,
                 (ancestors.n_paths * members.n_paths) as n_paths
          from group_subgroup_trans_index ancestors,
               group_member_trans_index members
          where ancestors.subgroup_id = v_group_id
            and members.group_id = v_subgroup_id
      loop

          if (v_group_id = new_entry.group_id) then
            v_path_increment := 1;
          else 
            v_path_increment := new_entry.n_paths;
          end if;

          select count(*) into v_exists_p
          from group_subgroup_trans_index
          where group_id = new_entry.group_id
            and subgroup_id = new_entry.subgroup_id;

          IF (v_exists_p > 0) THEN          
              update group_member_trans_index
              set n_paths = n_paths + v_path_increment
              where group_id = new_entry.group_id
                and member_id = new_entry.member_id;
          else 
              insert into group_member_trans_index
              (group_id, member_id, n_paths)
              values
              (new_entry.group_id, new_entry.member_id, v_path_increment);
          end if;
      end loop;

  return null;
  end;' language 'plpgsql';

create or replace function parties_remove_subgroup_members (
      integer, integer
  )
  returns integer as '
  declare
    v_group_id alias for $1;
    v_subgroup_id alias for $2;
    v_path_decrement integer;
    remove_entry record;
  begin

      for remove_entry in
          select ancestors.group_id, members.member_id,
                 (ancestors.n_paths * members.n_paths) as n_paths
          from group_subgroup_trans_index ancestors,
               group_member_trans_index members
          where ancestors.subgroup_id = v_group_id
            and members.group_id = v_subgroup_id
      loop

          if (v_group_id = remove_entry.group_id) then
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
        if (NOT FOUND) then

           update group_member_trans_index
              set n_paths = n_paths - v_path_decrement
            where group_id = remove_entry.group_id
              and member_id = remove_entry.member_id;

        end if;

      end loop;

  return null;
  end;' language 'plpgsql';
