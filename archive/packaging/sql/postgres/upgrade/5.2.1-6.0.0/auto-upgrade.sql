--------------------------------------------------------------------------------
-- Constraints Dropped
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- These columns went from 'not nullable' to 'nullable'.
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- These columns went from 'nullable' to 'not nullable'.
--------------------------------------------------------------------------------
alter table CAT_CATEGORIES alter ABSTRACT_P set not null;
alter table CAT_CATEGORIES alter ENABLED_P set not null;
alter table PORTLETS alter PORTAL_ID set not null;

--------------------------------------------------------------------------------
-- These default values for these columns changed.
--------------------------------------------------------------------------------
alter table CAT_CATEGORIES alter ABSTRACT_P drop default;

--------------------------------------------------------------------------------
-- These char(1) boolean check constraints have been added.
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- These 'not null' check constraints have been dropped.
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- These foreign key constraints have change their action for 'on delete'.
-- Their names may have changed as well.
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- These constraints have changed their name.
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- These indexes have changed their name.
--------------------------------------------------------------------------------
update pg_class set relname = 'cat_categori_catego_id_p_yeprq' where UPPER(relname) = UPPER('cat_categories_pk');


