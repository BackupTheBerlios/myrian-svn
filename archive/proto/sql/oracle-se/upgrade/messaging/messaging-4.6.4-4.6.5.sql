--
-- Added a not null constraint to the senders column.  This is also
-- verified in the Message class, but it never hurts to be safe.
--
-- @author ron@arsdigita.com
--
-- $Id: //core-platform/proto/sql/oracle-se/upgrade/messaging/messaging-4.6.4-4.6.5.sql#1 $

alter table messages modify sender constraint messages_sender_nn not null;
