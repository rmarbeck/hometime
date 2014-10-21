# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table brand (
  id                        bigint not null,
  internal_name             varchar(255),
  display_name              varchar(255),
  seo_name                  varchar(255),
  logo_url                  varchar(255),
  alt                       varchar(255),
  supported                 boolean,
  description               varchar(10000),
  remarks                   varchar(10000),
  constraint uq_brand_internal_name unique (internal_name),
  constraint pk_brand primary key (id))
;

create table contact_request (
  id                        bigint not null,
  title                     varchar(255),
  message                   varchar(1000),
  name                      varchar(255),
  email                     varchar(255),
  request_date              timestamp,
  constraint pk_contact_request primary key (id))
;

create table customer (
  id                        bigint not null,
  email                     varchar(255),
  alternative_email         varchar(255),
  creation_date             timestamp,
  last_communication_date   timestamp,
  phone_number              varchar(255),
  alternative_phone_number  varchar(255),
  firstname                 varchar(255),
  name                      varchar(255),
  billing_address           varchar(10000),
  pickup_address            varchar(10000),
  return_address            varchar(10000),
  shared_infos              varchar(10000),
  private_infos             varchar(10000),
  customer_status           varchar(40),
  value                     bigint,
  potentiality              bigint,
  constraint ck_customer_customer_status check (customer_status in ('PROSPECT','ALMOST_CUSTOMER','REAL_CUSTOMER')),
  constraint uq_customer_email unique (email),
  constraint pk_customer primary key (id))
;

create table customer_watch (
  id                        bigint not null,
  creation_date             timestamp,
  brand                     varchar(255),
  model                     varchar(255),
  additionnal_model_infos   varchar(255),
  reference                 varchar(255),
  serial                    varchar(255),
  serial2                   varchar(255),
  movement                  varchar(255),
  pictures_url              varchar(255),
  end_of_main_waranty       timestamp,
  end_of_water_waranty      timestamp,
  next_service              timestamp,
  next_partial_service      timestamp,
  last_status_update        timestamp,
  service_infos             varchar(10000),
  other_infos               varchar(10000),
  customer_id               bigint,
  customer_watch_status     varchar(40),
  constraint ck_customer_watch_customer_watch_status check (customer_watch_status in ('STORED_BY_WATCH_NEXT','STORED_BY_STH','STORED_BY_BRAND','STORED_BY_OTHER_PARTNER','BACK_TO_CUSTOMER')),
  constraint pk_customer_watch primary key (id))
;

create table feedback (
  id                        bigint not null,
  creation_date             timestamp,
  display_date              timestamp,
  body                      varchar(50000),
  author                    varchar(1000),
  picture_url               varchar(255),
  feeedback_type            varchar(40),
  should_display            boolean,
  may_be_emphasized         boolean,
  constraint ck_feedback_feeedback_type check (feeedback_type in ('FACEBOOK','LINKED_IN','INTERNAL','OTHER')),
  constraint pk_feedback primary key (id))
;

create table live_config (
  key                       varchar(255) not null,
  valuestring               varchar(255),
  valueboolean              boolean,
  valuelong                 bigint,
  valuedate                 timestamp,
  constraint pk_live_config primary key (key))
;

create table order_table (
  id                        bigint not null,
  request_id                bigint,
  creation_date             timestamp,
  pick_up_real_date         timestamp,
  start_working_real_date   timestamp,
  end_of_work_real_date     timestamp,
  start_of_control_date     timestamp,
  return_real_date          timestamp,
  closing_date              timestamp,
  last_comm_date            timestamp,
  order_type                varchar(255),
  order_status              varchar(40),
  brand                     varchar(255),
  model                     varchar(1000),
  order_method              varchar(255),
  remark                    varchar(1000),
  watch_chosen              varchar(255),
  customer_id               bigint,
  constraint ck_order_table_order_status check (order_status in ('INFORMAL_AGREEMENT_10','FORMAL_AGREEMENT_20','PICK_UP_PLANNED_30','PREPACKAGE_SENT_32','PREPACKAGE_RECEIVED_34','WATCH_SENT_36','PICK_UP_DONE_40','WATCH_RECEIVED_42','NEW_INFORMAL_PROPOSAL_SUBMITED_50','NEW_FORMAL_PROPOSAL_SUBMITED_55','PROPOSAL_AGREED_60','WORK_STARTED_70','WORK_ISSUE_WAITING_INTERNAL_SOLUTION_75','WORK_ISSUE_WAITING_CUSTOMER_77','WORK_FINISHED_80','WATCH_CONTROL_90','REWORK_92','BILL_SENT_100','BILL_PAYED_110','RETURN_PLANNED_120','RETURN_DONE_130','WATCH_SENT_BACK_132','WATCH_RECEIVED_BACK_134','FEEDBACK_ASKED_140','FEEDBACK_RECEIVED_150','ORDER_CLOSED_200','ORDER_CANCELED_300')),
  constraint pk_order_table primary key (id))
;

create table order_request (
  id                        bigint not null,
  request_date              timestamp,
  order_type                integer,
  brand_id                  bigint,
  model                     varchar(1000),
  method                    integer,
  remark                    varchar(1000),
  watch_chosen_id           bigint,
  name_of_customer          varchar(255),
  email                     varchar(255),
  phone_number              varchar(255),
  city                      varchar(255),
  constraint ck_order_request_order_type check (order_type in (0,1,2,3,4,5)),
  constraint ck_order_request_method check (method in (0,1,2)),
  constraint pk_order_request primary key (id))
;

create table picture (
  id                        bigint not null,
  url                       varchar(255),
  thumbnail_url             varchar(255),
  alt                       varchar(255),
  description               varchar(1000),
  watch_id                  bigint,
  display_order             bigint,
  constraint pk_picture primary key (id))
;

create table preset_quotation_for_brand (
  id                        bigint not null,
  preset_name               varchar(255),
  brand_id                  bigint,
  delay                     varchar(255),
  delay_brand1              varchar(255),
  delay_brand2              varchar(255),
  delay_return              varchar(255),
  price_service_low_bound   bigint,
  price_service_high_bound  bigint,
  waranty_time              varchar(255),
  price_is_not_final        boolean,
  delay_is_not_sure         boolean,
  delay_can_be_reduced      boolean,
  remark1                   varchar(255),
  remark2                   varchar(255),
  remark3                   varchar(255),
  hypothesis1               varchar(255),
  hypothesis2               varchar(255),
  hypothesis3               varchar(255),
  constraint pk_preset_quotation_for_brand primary key (id))
;

create table service_test (
  id                        bigint not null,
  request_date              timestamp,
  movement_type             integer,
  movement_complexity       integer,
  build_period              integer,
  last_service_year         integer,
  performance_issue         boolean,
  power_reserve_issue       boolean,
  water_issue               boolean,
  doing_sport               boolean,
  usage_frequency           integer,
  model                     varchar(1000),
  name_of_customer          varchar(255),
  email                     varchar(255),
  constraint ck_service_test_movement_type check (movement_type in (0,1)),
  constraint ck_service_test_movement_complexity check (movement_complexity in (0,1,2)),
  constraint ck_service_test_build_period check (build_period in (0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16)),
  constraint ck_service_test_last_service_year check (last_service_year in (0,1,2,3,4,5,6,7,8,9,10,11,12,13)),
  constraint ck_service_test_usage_frequency check (usage_frequency in (0,1,2,3)),
  constraint pk_service_test primary key (id))
;

create table user_table (
  id                        bigint not null,
  email                     varchar(255),
  password                  varchar(255),
  role                      integer,
  firstname                 varchar(255),
  name                      varchar(255),
  active                    boolean,
  constraint ck_user_table_role check (role in (0,1,2,3)),
  constraint uq_user_table_email unique (email),
  constraint pk_user_table primary key (id))
;

create table watch (
  id                        bigint not null,
  should_display            boolean,
  is_available              boolean,
  brand                     varchar(255),
  full_name                 varchar(255),
  short_name                varchar(255),
  seo_name                  varchar(255),
  movement                  varchar(1000),
  case_infos                varchar(1000),
  functions                 varchar(1000),
  water_resistance          varchar(255),
  specials                  varchar(1000),
  production_period         varchar(1000),
  item_year                 varchar(255),
  item_strap                varchar(255),
  item_reference            varchar(255),
  price                     float,
  main_picture_url          varchar(255),
  main_thumbnail_url        varchar(255),
  model_history             varchar(10000),
  item_history              varchar(10000),
  reason_why                varchar(10000),
  constraint pk_watch primary key (id))
;

create sequence brand_seq;

create sequence contact_request_seq;

create sequence customer_seq;

create sequence customer_watch_seq;

create sequence feedback_seq;

create sequence live_config_seq;

create sequence order_table_seq;

create sequence order_request_seq;

create sequence picture_seq;

create sequence preset_quotation_for_brand_seq;

create sequence service_test_seq;

create sequence user_table_seq;

create sequence watch_seq;

alter table customer_watch add constraint fk_customer_watch_customer_1 foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_customer_watch_customer_1 on customer_watch (customer_id);
alter table order_table add constraint fk_order_table_request_2 foreign key (request_id) references order_request (id) on delete restrict on update restrict;
create index ix_order_table_request_2 on order_table (request_id);
alter table order_table add constraint fk_order_table_customer_3 foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_order_table_customer_3 on order_table (customer_id);
alter table order_request add constraint fk_order_request_brand_4 foreign key (brand_id) references brand (id) on delete restrict on update restrict;
create index ix_order_request_brand_4 on order_request (brand_id);
alter table order_request add constraint fk_order_request_watchChosen_5 foreign key (watch_chosen_id) references watch (id) on delete restrict on update restrict;
create index ix_order_request_watchChosen_5 on order_request (watch_chosen_id);
alter table picture add constraint fk_picture_watch_6 foreign key (watch_id) references watch (id) on delete restrict on update restrict;
create index ix_picture_watch_6 on picture (watch_id);
alter table preset_quotation_for_brand add constraint fk_preset_quotation_for_brand__7 foreign key (brand_id) references brand (id) on delete restrict on update restrict;
create index ix_preset_quotation_for_brand__7 on preset_quotation_for_brand (brand_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists brand;

drop table if exists contact_request;

drop table if exists customer;

drop table if exists customer_watch;

drop table if exists feedback;

drop table if exists live_config;

drop table if exists order_table;

drop table if exists order_request;

drop table if exists picture;

drop table if exists preset_quotation_for_brand;

drop table if exists service_test;

drop table if exists user_table;

drop table if exists watch;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists brand_seq;

drop sequence if exists contact_request_seq;

drop sequence if exists customer_seq;

drop sequence if exists customer_watch_seq;

drop sequence if exists feedback_seq;

drop sequence if exists live_config_seq;

drop sequence if exists order_table_seq;

drop sequence if exists order_request_seq;

drop sequence if exists picture_seq;

drop sequence if exists preset_quotation_for_brand_seq;

drop sequence if exists service_test_seq;

drop sequence if exists user_table_seq;

drop sequence if exists watch_seq;

