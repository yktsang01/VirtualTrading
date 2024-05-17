set foreign_key_checks = 0;

drop table if exists account;
create table if not exists account (
	email varchar(100) not null,
	password varchar(255) not null,
  creation_datetime datetime not null,
  last_updated_datetime datetime null,
  admin_access enum('Y','N') not null default 'N',
  admin_request_datetime datetime null,
  admin_approval_datetime datetime null,
  admin_approve_by varchar(100) null,
  active enum('Y','N') not null default 'Y',
  deactivation_datetime datetime null,
  deactivation_reason varchar(255) null,
	constraint pk_account primary key (email)
);

CREATE INDEX idx_request_admin 
ON account (admin_access, admin_request_datetime); 

CREATE INDEX idx_already_admin 
ON account (admin_access, email); 

insert into account (email, password, creation_datetime, admin_access, active)
values ('webadmin@yktsang.com', 'admin', current_timestamp(), 'Y', 'Y');

update account
set admin_request_datetime=creation_datetime,
admin_approval_datetime=creation_datetime,
admin_approve_by='system'
where email='webadmin@yktsang.com';
commit;

drop table if exists trader;
create table if not exists trader (
  email varchar(100) not null,
  full_name varchar(255) not null,
  date_of_birth date not null,
  hide_date_of_birth enum('Y','N') not null default 'N',
  risk_tolerance enum('HIGH', 'MEDIUM', 'LOW') not null default 'MEDIUM',
  auto_transfer_to_bank enum('Y','N') not null default 'N',
  allow_reset enum('Y','N') not null default 'N',
  creation_datetime datetime not null,
  last_updated_datetime datetime null,
  deactivation_datetime datetime null,
  constraint pk_trader primary key (email)
);

insert into trader (email, full_name, date_of_birth, hide_date_of_birth,
  risk_tolerance, auto_transfer_to_bank, allow_reset, creation_datetime)
values ('webadmin@yktsang.com', 'Admin', '2008-10-09', 'Y', 
  'LOW', 'N', 'Y', current_timestamp());
commit;

drop table if exists iso_data;
create table if not exists iso_data (
  country_alpha2_code varchar(2) not null,
  country_name varchar(255) not null,
  currency_alpha_code varchar(3) not null,
  currency_name varchar(255) not null,
  currency_minor_units int null,
  creation_datetime datetime not null,
  created_by varchar(100) not null,
  active enum('Y','N') not null default 'N',
  activation_datetime datetime null,
  activated_by varchar(100) null,
  deactivation_datetime datetime null,
  deactivated_by varchar(100) null,
  last_updated_datetime datetime null,
  last_updated_by varchar(100) null,
  constraint pk_iso_data primary key (country_alpha2_code)
);

CREATE INDEX idx_active 
ON iso_data (active); 

CREATE INDEX idx_currency
ON iso_data (currency_alpha_code); 

drop table if exists account_balance;
create table if not exists account_balance (
  email varchar(100) not null,
  currency varchar(3) not null,
  trading_amount decimal(18,4) not null default 0.0000,
  non_trading_amount decimal(18,4) not null default 0.0000,
  creation_datetime datetime not null,
  last_updated_datetime datetime null,
  constraint pk_account_balance primary key (email, currency)
);

CREATE INDEX idx_email 
ON account_balance (email); 

drop table if exists account_transaction;
create table if not exists account_transaction (
  atid bigint unsigned not null auto_increment,
  email varchar(100) not null,
  currency varchar(3) not null,
  transaction_datetime datetime not null,
  transaction_description varchar(255) not null,
  constraint pk_account_transaction primary key (atid)
);

CREATE INDEX idx_email 
ON account_transaction (email); 

CREATE INDEX idx_email_currency 
ON account_transaction (email, currency); 

drop table if exists bank_account;
create table if not exists bank_account (
  baid bigint unsigned not null auto_increment,
  email varchar(100) not null,
  currency varchar(3) not null,
  bank_name varchar(255) not null,
  bank_account_number varchar(100) not null,
  in_use enum('Y','N') not null default 'Y',
  creation_datetime datetime not null,
  last_updated_datetime datetime null,
  constraint pk_bank_account primary key (baid)
);

CREATE INDEX idx_email_inuse 
ON bank_account (email, in_use); 

CREATE INDEX idx_email_currency_inuse 
ON bank_account (email, currency, in_use); 

CREATE INDEX idx_email 
ON bank_account (email); 

CREATE INDEX idx_email_currency 
ON bank_account (email, currency); 

drop table if exists bank_account_transaction;
create table if not exists bank_account_transaction (
  batid bigint unsigned not null auto_increment,
  email varchar(100) not null,
  currency varchar(3) not null,
  transaction_datetime datetime not null,
  transaction_description varchar(255) not null,
  constraint pk_bank_account_transaction primary key (batid)
);

CREATE INDEX idx_email 
ON bank_account_transaction (email); 

CREATE INDEX idx_email_currency 
ON bank_account_transaction (email, currency); 

drop table if exists watch_list;
create table if not exists watch_list (
  wlid bigint unsigned not null auto_increment,
  email varchar(100) not null,
  trading_symbol varchar(50) not null,
  symbol_name varchar(255) not null,
  currency varchar(3) not null,
  addition_datetime datetime not null,
  removal_datetime datetime null,
  constraint pk_watch_list primary key (wlid)
);

CREATE INDEX idx_email_remove 
ON watch_list (email, removal_datetime); 

CREATE INDEX idx_email_currency_remove 
ON watch_list (email, currency, removal_datetime); 

CREATE INDEX idx_email 
ON watch_list (email); 

CREATE INDEX idx_email_currency 
ON watch_list (email, currency); 

drop table if exists portfolio;
create table if not exists portfolio (
  pid bigint unsigned not null auto_increment,
  email varchar(100) not null,
  portfolio_name varchar(255) not null,
  currency varchar(3) not null,
  invested_amount decimal(18,4) not null default 0.0000,
  current_amount decimal(18,4) not null default 0.0000,
  profit_loss decimal(18,4) not null default 0.0000,
  creation_datetime datetime not null,
  last_updated_datetime datetime null,
  constraint pk_portfolio primary key (pid)
);

CREATE INDEX idx_email 
ON portfolio (email); 

CREATE INDEX idx_email_currency 
ON portfolio (email, currency); 

drop table if exists trading_transaction;
create table if not exists trading_transaction (
  ttid bigint unsigned not null auto_increment,
  email varchar(100) not null,
  trading_symbol varchar(50) not null,
  symbol_name varchar(255) not null,
  transaction_date date not null,
  trading_deed enum('BUY','SELL') not null,
  quantity int unsigned not null default 0,
  transaction_currency varchar(3) not null,
  transaction_price decimal(18,4) not null default 0.0000,
  transaction_cost decimal(18,4) not null default 0.0000,
  portfolio_id bigint unsigned null,
  creation_datetime datetime not null,
  last_updated_datetime datetime null,
  constraint pk_trading_transaction primary key (ttid),
  constraint fk_portfolio foreign key (portfolio_id) references portfolio (pid)
);

CREATE INDEX idx_email 
ON trading_transaction (email); 

CREATE INDEX idx_email_currency 
ON trading_transaction (email, transaction_currency); 

CREATE INDEX idx_email_portid 
ON trading_transaction (email, portfolio_id); 

CREATE INDEX idx_email_symbol_deed 
ON trading_transaction (email, trading_symbol, trading_deed); 

set foreign_key_checks = 1;
