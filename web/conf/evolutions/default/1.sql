# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table brand (
  id                        bigint not null,
  internal_name             varchar(255),
  display_name              varchar(255),
  logo_url                  varchar(255),
  alt                       varchar(255),
  supported                 boolean,
  description               varchar(10000),
  remarks                   varchar(10000),
  constraint uq_brand_internal_name unique (internal_name),
  constraint pk_brand primary key (id))
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
  type                      integer,
  model                     varchar(1000),
  method                    integer,
  remark                    varchar(1000),
  name_of_customer          varchar(255),
  email                     varchar(255),
  phone_number              varchar(255),
  city                      varchar(255),
  constraint ck_order_request_type check (type in (0,1)),
  constraint ck_order_request_method check (method in (0,1)),
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

create table watch (
  id                        bigint not null,
  should_display            boolean,
  is_available              boolean,
  brand                     varchar(255),
  full_name                 varchar(255),
  short_name                varchar(255),
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

create sequence live_config_seq;

create sequence order_request_seq;

create sequence picture_seq;

create sequence watch_seq;

alter table picture add constraint fk_picture_watch_1 foreign key (watch_id) references watch (id) on delete restrict on update restrict;
create index ix_picture_watch_1 on picture (watch_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists brand;

drop table if exists live_config;

drop table if exists order_request;

drop table if exists picture;

drop table if exists watch;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists brand_seq;

drop sequence if exists live_config_seq;

drop sequence if exists order_request_seq;

drop sequence if exists picture_seq;

drop sequence if exists watch_seq;

