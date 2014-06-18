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

create table live_config (
  key                       varchar(255) not null,
  valuestring               varchar(255),
  valueboolean              boolean,
  valuelong                 bigint,
  valuedate                 timestamp,
  constraint pk_live_config primary key (key))
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

create sequence live_config_seq;

create sequence order_request_seq;

create sequence picture_seq;

create sequence service_test_seq;

create sequence user_table_seq;

create sequence watch_seq;

alter table order_request add constraint fk_order_request_brand_1 foreign key (brand_id) references brand (id) on delete restrict on update restrict;
create index ix_order_request_brand_1 on order_request (brand_id);
alter table order_request add constraint fk_order_request_watchChosen_2 foreign key (watch_chosen_id) references watch (id) on delete restrict on update restrict;
create index ix_order_request_watchChosen_2 on order_request (watch_chosen_id);
alter table picture add constraint fk_picture_watch_3 foreign key (watch_id) references watch (id) on delete restrict on update restrict;
create index ix_picture_watch_3 on picture (watch_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists brand;

drop table if exists contact_request;

drop table if exists live_config;

drop table if exists order_request;

drop table if exists picture;

drop table if exists service_test;

drop table if exists user_table;

drop table if exists watch;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists brand_seq;

drop sequence if exists contact_request_seq;

drop sequence if exists live_config_seq;

drop sequence if exists order_request_seq;

drop sequence if exists picture_seq;

drop sequence if exists service_test_seq;

drop sequence if exists user_table_seq;

drop sequence if exists watch_seq;

