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
