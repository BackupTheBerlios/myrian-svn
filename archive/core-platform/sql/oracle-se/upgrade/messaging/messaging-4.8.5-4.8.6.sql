--
-- Added Reply to field
--
-- @author ddao@arsdigita.com
--
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/messaging/messaging-4.8.5-4.8.6.sql#2 $

alter table messages add (
    reply_to varchar2(250)
);
