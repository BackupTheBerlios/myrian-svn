--
-- Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
--
-- The contents of this file are subject to the CCM Public
-- License (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of
-- the License at http://www.redhat.com/licenses/ccmpl.html
--
-- Software distributed under the License is distributed on an "AS
-- IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
-- implied. See the License for the specific language governing
-- rights and limitations under the License.
--
-- $Id: //core-platform/test-packaging/sql/oracle-se/upgrade/5.1.2-5.2.0/package-parties_denormalization.sql#2 $
-- $DateTime: 2003/08/19 22:28:24 $

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
      hierarchy_add_item(add_group.group_id, 'group_subgroup_trans_index', 
                         'group_id', 'subgroup_id');
  end add_group;

  procedure add_subgroup (
      group_id    in groups.group_id%TYPE,
      subgroup_id in groups.group_id%TYPE
  )
  as
    v_path_increment integer;
  begin

      add_subgroup_members(group_id, subgroup_id);

      hierarchy_add_subitem(add_subgroup.group_id, 
                            add_subgroup.subgroup_id,
                           'group_subgroup_trans_index',
                           'group_id', 'subgroup_id');
  end add_subgroup;

  procedure remove_subgroup (
      group_id    in groups.group_id%TYPE,
      subgroup_id in groups.group_id%TYPE
  )
  as
    v_path_decrement integer;
  begin
      hierarchy_remove_subitem(group_id, subgroup_id,
                              'group_subgroup_trans_index',      
                              'group_id', 'subgroup_id');

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
