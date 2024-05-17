set foreign_key_checks = 0;

truncate table account;
truncate table trader;
truncate table iso_data;
truncate table account_balance;
truncate table account_transaction;
truncate table bank_account;
truncate table bank_account_transaction;
truncate table watch_list;
truncate table portfolio;
truncate table trading_transaction;

set foreign_key_checks = 1;

insert into account (email, password, creation_datetime, admin_access, active)
values ('webadmin@yktsang.com', 'admin', current_timestamp(), 'Y', 'Y');

update account
set admin_request_datetime=creation_datetime,
admin_approval_datetime=creation_datetime,
admin_approve_by='system'
where email='webadmin@yktsang.com';
commit;

insert into trader (email, full_name, date_of_birth, hide_date_of_birth,
  risk_tolerance, auto_transfer_to_bank, allow_reset, creation_datetime)
values ('webadmin@yktsang.com', 'Admin', '2008-10-09', 'Y', 
  'LOW', 'N', 'Y', current_timestamp());
commit;
