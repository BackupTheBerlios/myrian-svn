--
-- Added RFC 822 Message ID
--
-- @author ddao@arsdigita.com
--
-- $Id: //core-platform/dev/sql/oracle-se/upgrade/messaging/messaging-4.8.4-4.8.5.sql#2 $

alter table messages add (
    rfc_message_id varchar2(250)
);
