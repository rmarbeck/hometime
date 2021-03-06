# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table accounting_document (
  id                        bigint not null,
  creation_date             timestamp,
  customer_id               bigint,
  document_data             blob,
  constraint pk_accounting_document primary key (id))
;

create table accounting_line (
  id                        bigint not null,
  description               varchar(10000),
  ranking                   integer,
  unit                      bigint,
  unit_price                float,
  info                      varchar(10000),
  document_id               bigint,
  line_type                 varchar(40),
  preset_id                 bigint,
  one_time_cost             float,
  constraint ck_accounting_line_line_type check (line_type in ('WITH_VAT_BY_UNIT','WITHOUT_VAT_BY_UNIT','FREE_INCLUDED','FREE_OFFERED','FREE_SPECIAL','INFO_LINE','SUB_TOTAL')),
  constraint pk_accounting_line primary key (id))
;

create table accounting_line_analytic (
  id                        bigint not null,
  analytic_code_id          bigint,
  price                     float,
  cost                      float,
  checked                   boolean,
  double_checked            boolean,
  remark                    varchar(10000),
  reserved                  varchar(10000),
  accounting_line_id        bigint,
  constraint pk_accounting_line_analytic primary key (id))
;

create table accounting_line_analytic_preset (
  id                        bigint not null,
  meta_analytic_code        bigint,
  description               varchar(255),
  remark                    varchar(255),
  fully_filled              boolean,
  active                    boolean,
  constraint uq_accounting_line_analytic_pres unique (meta_analytic_code),
  constraint pk_accounting_line_analytic_pres primary key (id))
;

create table accounting_preset_item_table (
  id                        bigint not null,
  analytic_code_id          bigint,
  fixed_price               float,
  proportional_price        float,
  fixed_cost                float,
  proportional_cost         float,
  one_time_cost             boolean,
  order_number              integer,
  accounting_line_analytic_preset_id bigint,
  constraint pk_accounting_preset_item_table primary key (id))
;

create table analytic_code (
  id                        bigint not null,
  analytic_code             bigint,
  lbl                       varchar(255),
  description               varchar(255),
  active                    boolean,
  accounting_line_analytic_preset_id bigint,
  constraint uq_analytic_code_analytic_code unique (analytic_code),
  constraint pk_analytic_code primary key (id))
;

create table appointment_request (
  id                        bigint not null,
  unique_key                varchar(255),
  creation_date             timestamp,
  last_action_date          timestamp,
  end_of_option_date        timestamp,
  appointment_as_date       timestamp,
  appointment_as_string     varchar(255),
  customer_details          varchar(255),
  customer_phone_number     varchar(255),
  customer_remark           varchar(10000),
  private_remark            varchar(10000),
  appointment_status        varchar(18),
  appointment_reason        varchar(7),
  constraint ck_appointment_request_appointment_status check (appointment_status in ('WAITING_VALIDATION','VALIDATED','CANCELED','EXPIRED','NO_MORE_AVAILABLE','IN_ERROR')),
  constraint ck_appointment_request_appointment_reason check (appointment_reason in ('DEPOSIT','PICKUP')),
  constraint pk_appointment_request primary key (id))
;

create table authentication (
  id                        bigint not null,
  creation_date             timestamp,
  document_date             timestamp,
  watch_id                  bigint,
  document_data             blob,
  constraint pk_authentication primary key (id))
;

create table auto_order (
  id                        bigint not null,
  request_date              timestamp,
  movement_type             integer,
  movement_complexity       integer,
  emergency_level           integer,
  working_condition         integer,
  constraint ck_auto_order_movement_type check (movement_type in (0,1,2,3)),
  constraint ck_auto_order_movement_complexity check (movement_complexity in (0,1,2,3,4)),
  constraint ck_auto_order_emergency_level check (emergency_level in (0,1,2,3)),
  constraint ck_auto_order_working_condition check (working_condition in (0,1,2)),
  constraint pk_auto_order primary key (id))
;

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
  quartz_category           integer,
  constraint uq_brand_internal_name unique (internal_name),
  constraint pk_brand primary key (id))
;

create table buy_request (
  id                        bigint not null,
  request_date              timestamp,
  watch_story               varchar(40),
  watch_package             varchar(40),
  request_timeframe         varchar(40),
  brand_id                  bigint,
  model                     varchar(1000),
  criteria                  varchar(10000),
  remark                    varchar(10000),
  expected_price            varchar(255),
  price_higher_bound        varchar(255),
  name_of_customer          varchar(255),
  email                     varchar(255),
  phone_number              varchar(255),
  city                      varchar(255),
  replied                   boolean,
  waiting_for_customer      boolean,
  closed                    boolean,
  feedback_asked            boolean,
  constraint ck_buy_request_watch_story check (watch_story in ('NEW_ONLY','WORN_ONLY','BOTH')),
  constraint ck_buy_request_watch_package check (watch_package in ('FULL_SET_ONLY','WATCH_ONLY','BOTH')),
  constraint ck_buy_request_request_timeframe check (request_timeframe in ('EMERGENCY','WITHIN_MONTH','WITHIN_3_MONTHS','OPPORTUNITY')),
  constraint pk_buy_request primary key (id))
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
  id_infos                  varchar(10000),
  customer_status           varchar(40),
  customer_civility         varchar(40),
  value                     bigint,
  potentiality              bigint,
  is_topic_open             boolean,
  should_print_address      boolean,
  constraint ck_customer_customer_status check (customer_status in ('PROSPECT','ALMOST_CUSTOMER','REAL_CUSTOMER')),
  constraint ck_customer_customer_civility check (customer_civility in ('MONSIEUR','MADAME','MISTER','MISS','EMPTY')),
  constraint uq_customer_email unique (email),
  constraint pk_customer primary key (id))
;

create table customer_watch (
  id                        bigint not null,
  partner_id                bigint,
  b2b_id                    varchar(255),
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
  collecting_date           timestamp,
  last_status_update        timestamp,
  service_infos             varchar(10000),
  other_infos               varchar(10000),
  partner_from_infos        varchar(10000),
  partner_to_infos          varchar(10000),
  final_customer_watch_infos varchar(10000),
  final_customer_from_infos varchar(10000),
  final_customer_to_infos   varchar(10000),
  final_customer_service_price_accepted_infos varchar(10000),
  expected_service_end_date timestamp,
  first_entry_in_partner_workshop_date timestamp,
  service_due_date          timestamp,
  service_due_date_must_have timestamp,
  customer_has_constraint   boolean,
  customer_has_called_for_delay boolean,
  last_customer_date        timestamp,
  last_due_date_communicated timestamp,
  last_customer_call_information varchar(255),
  service_status            bigint,
  emergency_level           bigint,
  service_price             bigint,
  service_price_accepted    boolean,
  service_definitively_refused boolean,
  new_service_price_needed  boolean,
  service_paid              boolean,
  final_customer_service_price bigint,
  final_customer_quotation_sent boolean,
  final_customer_service_price_accepted boolean,
  final_customer_acceptance_date timestamp,
  final_customer_service_paid boolean,
  final_customer_emergency_level bigint,
  final_customer_service_status bigint,
  back_to_customer_date     timestamp,
  service_needed            boolean,
  service_on_hold           boolean,
  service_invoiced          boolean,
  quotation                 bigint,
  tester_email              varchar(255),
  pictures_done_on_collect  boolean,
  working_conditions_on_collect varchar(10000),
  box_conditions_on_collect varchar(10000),
  hands_conditions_on_collect varchar(10000),
  crown_and_pushers_conditions_on_collect varchar(10000),
  back_conditions_on_collect varchar(10000),
  bracelet_or_strap_conditions_on_collect varchar(10000),
  watering_conditions_on_collect varchar(10000),
  glass_conditions_on_collect varchar(10000),
  other_conditions_remarks_on_collect varchar(10000),
  customer_id               bigint,
  authentication_needed     boolean,
  is_authentic              boolean,
  authentication_watch_details varchar(255),
  authentication_informations varchar(10000),
  authentication_private_informations varchar(10000),
  customer_watch_status     varchar(40),
  customer_watch_type       varchar(40),
  managed_by_id             bigint,
  service_beginning         timestamp,
  last_manager_change       timestamp,
  expected_service_ending   timestamp,
  sparepart_to_find         boolean,
  sparepart_found           boolean,
  need_help                 boolean,
  no_solution               boolean,
  quick_win_possible        boolean,
  service_info_from_watchmaker varchar(255),
  entered_under_waranty     boolean,
  to_work_on_under_waranty  boolean,
  waranty_is_void           boolean,
  constraint ck_customer_watch_customer_watch_status check (customer_watch_status in ('STORED_BY_WATCH_NEXT','STORED_BY_STH','STORED_BY_BRAND','STORED_BY_OTHER_PARTNER','STORED_BY_A_REGISTERED_PARTNER','BACK_TO_CUSTOMER','STORED_BY_WATCH_NEXT_OUTSIDE','STORED_BY_WATCH_NEXT_SECURED_1','STORED_BY_WATCH_NEXT_SECURED_2','STORED_BY_WATCH_NEXT_SECURED_3')),
  constraint ck_customer_watch_customer_watch_type check (customer_watch_type in ('MECA_SIMPLE','MECA_CHRONO','MECA_COMPLEX','AUTO_SIMPLE','AUTO_CHRONO','AUTO_COMPLEX','QUARTZ_SIMPLE','QUARTZ_COMPLEX','RESERVED_1','RESERVED_2','RESERVED_3','OTHER')),
  constraint pk_customer_watch primary key (id))
;

create table external_document (
  id                        bigint not null,
  creation_date             timestamp,
  name                      varchar(255),
  description               varchar(10000),
  url                       varchar(255),
  constraint uq_external_document_name unique (name),
  constraint pk_external_document primary key (id))
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
  constraint ck_feedback_feeedback_type check (feeedback_type in ('FACEBOOK','LINKED_IN','INTERNAL','GOOGLE','OTHER')),
  constraint pk_feedback primary key (id))
;

create table incoming_call (
  id                        bigint not null,
  phone_number              varchar(255),
  missed                    boolean,
  call_date                 timestamp,
  constraint pk_incoming_call primary key (id))
;

create table internal_message (
  id                        bigint not null,
  body                      varchar(10000),
  internal_message_type     varchar(40),
  creation_date             timestamp,
  dead_line                 timestamp,
  auto_end_date             timestamp,
  last_disabling_date       timestamp,
  read_more_url             varchar(255),
  active                    boolean,
  should_display_on_dash    boolean,
  private_infos             varchar(255),
  constraint ck_internal_message_internal_message_type check (internal_message_type in ('ALERT','WARNING','TODO','INFO','RESERVED_1','RESERVED_2')),
  constraint pk_internal_message primary key (id))
;

create table invoice (
  id                        bigint not null,
  description               varchar(10000),
  document_id               bigint,
  payment_conditions        varchar(10000),
  supported_payment_methods varchar(10000),
  payment_method_used       varchar(10000),
  already_payed             float,
  unique_accounting_number  varchar(255),
  invoice_type              integer,
  from_date                 timestamp,
  to_date                   timestamp,
  private_infos             varchar(10000),
  constraint ck_invoice_invoice_type check (invoice_type in (0,1,2,3,4,5)),
  constraint uq_invoice_unique_accounting_num unique (unique_accounting_number),
  constraint pk_invoice primary key (id))
;

create table live_config (
  key                       varchar(255) not null,
  valuestring               varchar(255),
  valueboolean              boolean,
  valuelong                 bigint,
  valuedate                 timestamp,
  constraint pk_live_config primary key (key))
;

create table mail_template (
  id                        bigint not null,
  template_name             varchar(255),
  type_id                   bigint,
  display_name              varchar(255),
  title                     varchar(255),
  body                      varchar(20000),
  sorting_index             bigint,
  active                    boolean,
  constraint pk_mail_template primary key (id))
;

create table mail_template_type (
  id                        bigint not null,
  template_type_name        varchar(255),
  display_name              varchar(255),
  sorting_index             bigint,
  active                    boolean,
  constraint pk_mail_template_type primary key (id))
;

create table news (
  id                        bigint not null,
  title                     varchar(1000),
  body                      varchar(10000),
  news_type                 varchar(40),
  date                      timestamp,
  read_more_url             varchar(255),
  active                    boolean,
  private_infos             varchar(255),
  constraint ck_news_news_type check (news_type in ('ONE_PICTURE','VIDEO','DIAPORAMA','RESERVED_1','RESERVED_2')),
  constraint pk_news primary key (id))
;

create table news_alt (
  id                        bigint not null,
  news_id                   bigint,
  value                     varchar(255),
  constraint pk_news_alt primary key (id))
;

create table news_category (
  id                        bigint not null,
  name                      varchar(1000),
  description               varchar(10000),
  constraint pk_news_category primary key (id))
;

create table news_url (
  id                        bigint not null,
  news_id                   bigint,
  value                     varchar(255),
  constraint pk_news_url primary key (id))
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

create table order_document (
  id                        bigint not null,
  description               varchar(10000),
  document_id               bigint,
  payment_conditions        varchar(10000),
  supported_payment_methods varchar(10000),
  detailed_infos            varchar(10000),
  delay                     varchar(10000),
  unique_accounting_number  varchar(255),
  invoice_type              integer,
  valid_until_date          timestamp,
  constraint ck_order_document_invoice_type check (invoice_type in (0,1,2,3,4,5)),
  constraint uq_order_document_unique_account unique (unique_accounting_number),
  constraint pk_order_document primary key (id))
;

create table order_request (
  id                        bigint not null,
  request_date              timestamp,
  order_type                integer,
  brand_id                  bigint,
  model                     varchar(1000),
  method                    integer,
  remark                    varchar(1000),
  managed_by_id             bigint,
  name_of_customer          varchar(255),
  email                     varchar(255),
  phone_number              varchar(255),
  city                      varchar(255),
  replied                   boolean,
  waiting_for_customer      boolean,
  closed                    boolean,
  feedback_asked            boolean,
  private_infos             varchar(10000),
  watch_chosen_id           bigint,
  constraint ck_order_request_order_type check (order_type in (0,1,2,3,4,5)),
  constraint ck_order_request_method check (method in (0,1,2)),
  constraint pk_order_request primary key (id))
;

create table partner (
  id                        bigint not null,
  email                     varchar(255),
  alternative_email         varchar(255),
  phone_number              varchar(255),
  alternative_phone_number  varchar(255),
  name                      varchar(255),
  internal_name             varchar(255),
  address                   varchar(10000),
  private_infos             varchar(10000),
  active                    boolean,
  constraint pk_partner primary key (id))
;

create table payment (
  id                        bigint not null,
  creation_date             timestamp,
  paymentDate               timestamp,
  in_bank_date              timestamp,
  amount_in_euros           float,
  description               varchar(255),
  description2              varchar(255),
  payment_method            varchar(15),
  invoice_id                bigint,
  constraint ck_payment_payment_method check (payment_method in ('CB','WIRETRANSFER','CHECK','CASH','PAYPAL','TRUSTEDCHECKOUT','OTHER1','OTHER2')),
  constraint pk_payment primary key (id))
;

create table payment_request (
  id                        bigint not null,
  creation_date             timestamp,
  first_try_date            timestamp,
  last_try_date             timestamp,
  closing_date              timestamp,
  valid_until_date          timestamp,
  price_in_euros            float,
  access_key                varchar(255),
  customer_id               bigint,
  order_number              varchar(255),
  description               varchar(255),
  description2              varchar(255),
  is_open                   boolean,
  allow_amex                boolean,
  amex_only                 boolean,
  solution_to_use           varchar(10),
  type_of_payment           varchar(18),
  request_status            varchar(22),
  status_info               varchar(10000),
  delay_in_days             integer,
  constraint ck_payment_request_solution_to_use check (solution_to_use in ('SYSTEM_PAY','RESERVED_1','RESERVED_2')),
  constraint ck_payment_request_type_of_payment check (type_of_payment in ('ONE_SHOT_IMMEDIATE','ONE_SHOT_POSTPONED','MULTI','RESERVED_1','RESERVED_2','RESERVED_3','RESERVED_4')),
  constraint ck_payment_request_request_status check (request_status in ('OPEN','EXPIRED','PENDING','CANCELED','VALIDATED_NO_WARNING','VALIDATED_WITH_WARNING','IN_ERROR','RESERVED_1','RESERVED_2','RESERVED_3','RESERVED_4')),
  constraint pk_payment_request primary key (id))
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

create table post_selling_certificate (
  id                        bigint not null,
  creation_date             timestamp,
  document_date             timestamp,
  owner_id                  bigint,
  watch_id                  bigint,
  private_infos             varchar(10000),
  test_result               varchar(255),
  waterproof_waranted       boolean,
  waterproof_waranty_date   timestamp,
  brand_waranted            boolean,
  brand_waranty_date        timestamp,
  seller_waranted           boolean,
  seller_waranty_date       timestamp,
  next_service_recommended_year integer,
  next_waterproofing_recommended_year integer,
  usage_tips                varchar(10000),
  display_quick_date_tip    boolean,
  display_water_leak_tip    boolean,
  display_low_water_resistance_tip boolean,
  display_winding_tip       boolean,
  display_screwing_down_crown_tip boolean,
  display_pushing_crown_tip boolean,
  display_closing_crown_tip boolean,
  display_read_manual_tip   boolean,
  constraint pk_post_selling_certificate primary key (id))
;

create table post_service_certificate (
  id                        bigint not null,
  creation_date             timestamp,
  document_date             timestamp,
  owner_id                  bigint,
  watch_id                  bigint,
  work_done                 varchar(10000),
  private_infos             varchar(10000),
  mechanical_test_result    varchar(255),
  quartz_test_result        varchar(255),
  waterproofing_test_result varchar(255),
  waterproof_waranted       boolean,
  waterproof_waranty_date   timestamp,
  working_waranted          boolean,
  working_waranty_date      timestamp,
  next_service_recommended_year integer,
  next_waterproofing_recommended_year integer,
  usage_tips                varchar(10000),
  display_quick_date_tip    boolean,
  display_water_leak_tip    boolean,
  display_low_water_resistance_tip boolean,
  display_winding_tip       boolean,
  display_screwing_down_crown_tip boolean,
  display_pushing_crown_tip boolean,
  display_closing_crown_tip boolean,
  display_read_manual_tip   boolean,
  constraint pk_post_service_certificate primary key (id))
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
  should_talk_about_water_resistance boolean,
  remark1                   varchar(255),
  remark2                   varchar(255),
  remark3                   varchar(255),
  hypothesis1               varchar(255),
  hypothesis2               varchar(255),
  hypothesis3               varchar(255),
  constraint pk_preset_quotation_for_brand primary key (id))
;

create table price (
  id                        bigint not null,
  name                      varchar(255),
  active                    boolean,
  description               varchar(10000),
  battery_change            bigint,
  battery_change_and_water  bigint,
  battery_change_and_water_and_polish bigint,
  low_service_price_simple  bigint,
  high_service_price_simple bigint,
  low_service_price_chrono  bigint,
  high_service_price_chrono bigint,
  low_service_price_gmt     bigint,
  high_service_price_gmt    bigint,
  low_service_price_complex bigint,
  high_service_price_complex bigint,
  high_emergency_factor     bigint,
  low_emergency_factor      bigint,
  constraint uq_price_name unique (name),
  constraint pk_price primary key (id))
;

create table quick_service_watch (
  id                        bigint not null,
  customer_phone_number     varchar(255),
  customer_civility         varchar(40),
  customer_name             varchar(255),
  customer_first_name       varchar(255),
  customer_email            varchar(255),
  customer_address          varchar(255),
  customer_info             varchar(255),
  brand                     varchar(255),
  model                     varchar(255),
  reference                 varchar(255),
  serial                    varchar(255),
  to_do                     varchar(255),
  remarks                   varchar(255),
  private_infos             varchar(255),
  creation_date             timestamp,
  constraint ck_quick_service_watch_customer_civility check (customer_civility in ('MONSIEUR','MADAME','MISTER','MISS','EMPTY')),
  constraint pk_quick_service_watch primary key (id))
;

create table registered_email (
  id                        bigint not null,
  email                     varchar(255),
  creation_date             timestamp,
  active                    boolean,
  modification_date         timestamp,
  constraint uq_registered_email_email unique (email),
  constraint pk_registered_email primary key (id))
;

create table sms (
  id                        bigint not null,
  phone_number              varchar(255),
  sender                    varchar(255),
  sending_date              timestamp,
  message                   varchar(255),
  status                    varchar(255),
  sms_count                 integer,
  message_id                varchar(255),
  constraint pk_sms primary key (id))
;

create table selling_document (
  id                        bigint not null,
  description               varchar(10000),
  document_id               bigint,
  payment_method_used       varchar(10000),
  unique_accounting_number  varchar(255),
  purchase_date             timestamp,
  constraint uq_selling_document_unique_accou unique (unique_accounting_number),
  constraint pk_selling_document primary key (id))
;

create table selling_warrant (
  id                        bigint not null,
  creation_date             timestamp,
  document_date             timestamp,
  watch_id                  bigint,
  document_data             blob,
  constraint pk_selling_warrant primary key (id))
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

create table spare_part (
  id                        bigint not null,
  creation_date             timestamp,
  expected_availability_date timestamp,
  order_date                timestamp,
  reception_date            timestamp,
  last_status_update        timestamp,
  description               varchar(10000),
  other_infos               varchar(10000),
  part_reference            varchar(255),
  provider                  varchar(255),
  unit_needed               integer,
  expected_in_price         bigint,
  real_in_price             bigint,
  out_price                 bigint,
  found                     boolean,
  paid                      boolean,
  confirmed                 boolean,
  in_stock                  boolean,
  to_order                  boolean,
  ordered                   boolean,
  got                       boolean,
  checked_ok                boolean,
  checked_ko                boolean,
  closed                    boolean,
  emergency_level           bigint,
  watch_id                  bigint,
  constraint pk_spare_part primary key (id))
;

create table usefull_link (
  id                        bigint not null,
  creation_date             timestamp,
  description               varchar(50000),
  name                      varchar(1000),
  url                       varchar(255),
  link_type                 varchar(40),
  should_display            boolean,
  constraint ck_usefull_link_link_type check (link_type in ('PARTNER','RESERVED_1','RESERVED_2','RESERVED_3')),
  constraint pk_usefull_link primary key (id))
;

create table user_table (
  id                        bigint not null,
  email                     varchar(255),
  password                  varchar(255),
  role                      integer,
  firstname                 varchar(255),
  name                      varchar(255),
  active                    boolean,
  number_of_bad_passwords   integer,
  partner_id                bigint,
  customer_id               bigint,
  constraint ck_user_table_role check (role in (0,1,2,3,4)),
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
  number_of_loans           integer,
  owning_cost               float,
  lower_selling_price       float,
  best_selling_price        float,
  acquisition_date          timestamp,
  constraint pk_watch primary key (id))
;

create table watch_to_sell (
  id                        bigint not null,
  creation_date             timestamp,
  pruchase_date             timestamp,
  start_selling_date        timestamp,
  selling_date              timestamp,
  brand_id                  bigint,
  model                     varchar(255),
  additionnal_model_infos   varchar(255),
  reference                 varchar(255),
  serial                    varchar(255),
  serial2                   varchar(255),
  movement                  varchar(255),
  strap                     varchar(255),
  seller                    varchar(255),
  customer_that_sells_the_watch_id bigint,
  additionnal_infos         varchar(10000),
  private_infos             varchar(10000),
  year                      varchar(255),
  has_box                   boolean,
  has_papers                boolean,
  is_new                    boolean,
  customer_that_bought_the_watch_id bigint,
  owner_status              varchar(28),
  status                    varchar(23),
  purchase_invoice_available boolean,
  selling_price             bigint,
  purchasing_price          bigint,
  purchase_invoice_id       bigint,
  should_be_in_registry     boolean,
  is_in_registry            boolean,
  reporting_infos           varchar(255),
  constraint ck_watch_to_sell_owner_status check (owner_status in ('OWNED_BY_US','OWNED_BY_CUSTOMER','OWNED_BY_PARTNER','OWNED_AND_STORED_BY_CUSTOMER','RESERVED_2')),
  constraint ck_watch_to_sell_status check (status in ('TO_SELL','RESERVED_FOR_A_CUSTOMER','SOLD','SELLING_CANCELED','RESERVED_1','RESERVED_2')),
  constraint pk_watch_to_sell primary key (id))
;


create table news_category_news (
  news_category_id               bigint not null,
  news_id                        bigint not null,
  constraint pk_news_category_news primary key (news_category_id, news_id))
;
create sequence accounting_document_seq;

create sequence accounting_line_seq;

create sequence accounting_line_analytic_seq;

create sequence accounting_line_analytic_preset_seq;

create sequence accounting_preset_item_table_seq;

create sequence analytic_code_seq;

create sequence appointment_request_seq;

create sequence authentication_seq;

create sequence auto_order_seq;

create sequence brand_seq;

create sequence buy_request_seq;

create sequence contact_request_seq;

create sequence customer_seq;

create sequence customer_watch_seq;

create sequence external_document_seq;

create sequence feedback_seq;

create sequence incoming_call_seq;

create sequence internal_message_seq;

create sequence invoice_seq;

create sequence live_config_seq;

create sequence mail_template_seq;

create sequence mail_template_type_seq;

create sequence news_seq;

create sequence news_alt_seq;

create sequence news_category_seq;

create sequence news_url_seq;

create sequence order_table_seq;

create sequence order_document_seq;

create sequence order_request_seq;

create sequence partner_seq;

create sequence payment_seq;

create sequence payment_request_seq;

create sequence picture_seq;

create sequence post_selling_certificate_seq;

create sequence post_service_certificate_seq;

create sequence preset_quotation_for_brand_seq;

create sequence price_seq;

create sequence quick_service_watch_seq;

create sequence registered_email_seq;

create sequence sms_seq;

create sequence selling_document_seq;

create sequence selling_warrant_seq;

create sequence service_test_seq;

create sequence spare_part_seq;

create sequence usefull_link_seq;

create sequence user_table_seq;

create sequence watch_seq;

create sequence watch_to_sell_seq;

alter table accounting_document add constraint fk_accounting_document_custome_1 foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_accounting_document_custome_1 on accounting_document (customer_id);
alter table accounting_line add constraint fk_accounting_line_document_2 foreign key (document_id) references accounting_document (id) on delete restrict on update restrict;
create index ix_accounting_line_document_2 on accounting_line (document_id);
alter table accounting_line add constraint fk_accounting_line_preset_3 foreign key (preset_id) references accounting_line_analytic_preset (id) on delete restrict on update restrict;
create index ix_accounting_line_preset_3 on accounting_line (preset_id);
alter table accounting_line_analytic add constraint fk_accounting_line_analytic_an_4 foreign key (analytic_code_id) references analytic_code (id) on delete restrict on update restrict;
create index ix_accounting_line_analytic_an_4 on accounting_line_analytic (analytic_code_id);
alter table accounting_line_analytic add constraint fk_accounting_line_analytic_ac_5 foreign key (accounting_line_id) references accounting_line (id) on delete restrict on update restrict;
create index ix_accounting_line_analytic_ac_5 on accounting_line_analytic (accounting_line_id);
alter table accounting_preset_item_table add constraint fk_accounting_preset_item_tabl_6 foreign key (analytic_code_id) references analytic_code (id) on delete restrict on update restrict;
create index ix_accounting_preset_item_tabl_6 on accounting_preset_item_table (analytic_code_id);
alter table accounting_preset_item_table add constraint fk_accounting_preset_item_tabl_7 foreign key (accounting_line_analytic_preset_id) references accounting_line_analytic_preset (id) on delete restrict on update restrict;
create index ix_accounting_preset_item_tabl_7 on accounting_preset_item_table (accounting_line_analytic_preset_id);
alter table analytic_code add constraint fk_analytic_code_accountingLin_8 foreign key (accounting_line_analytic_preset_id) references accounting_line_analytic_preset (id) on delete restrict on update restrict;
create index ix_analytic_code_accountingLin_8 on analytic_code (accounting_line_analytic_preset_id);
alter table authentication add constraint fk_authentication_watch_9 foreign key (watch_id) references customer_watch (id) on delete restrict on update restrict;
create index ix_authentication_watch_9 on authentication (watch_id);
alter table buy_request add constraint fk_buy_request_brand_10 foreign key (brand_id) references brand (id) on delete restrict on update restrict;
create index ix_buy_request_brand_10 on buy_request (brand_id);
alter table customer_watch add constraint fk_customer_watch_partner_11 foreign key (partner_id) references partner (id) on delete restrict on update restrict;
create index ix_customer_watch_partner_11 on customer_watch (partner_id);
alter table customer_watch add constraint fk_customer_watch_customer_12 foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_customer_watch_customer_12 on customer_watch (customer_id);
alter table customer_watch add constraint fk_customer_watch_managedBy_13 foreign key (managed_by_id) references user_table (id) on delete restrict on update restrict;
create index ix_customer_watch_managedBy_13 on customer_watch (managed_by_id);
alter table invoice add constraint fk_invoice_document_14 foreign key (document_id) references accounting_document (id) on delete restrict on update restrict;
create index ix_invoice_document_14 on invoice (document_id);
alter table mail_template add constraint fk_mail_template_type_15 foreign key (type_id) references mail_template_type (id) on delete restrict on update restrict;
create index ix_mail_template_type_15 on mail_template (type_id);
alter table news_alt add constraint fk_news_alt_news_16 foreign key (news_id) references news (id) on delete restrict on update restrict;
create index ix_news_alt_news_16 on news_alt (news_id);
alter table news_url add constraint fk_news_url_news_17 foreign key (news_id) references news (id) on delete restrict on update restrict;
create index ix_news_url_news_17 on news_url (news_id);
alter table order_table add constraint fk_order_table_request_18 foreign key (request_id) references order_request (id) on delete restrict on update restrict;
create index ix_order_table_request_18 on order_table (request_id);
alter table order_table add constraint fk_order_table_customer_19 foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_order_table_customer_19 on order_table (customer_id);
alter table order_document add constraint fk_order_document_document_20 foreign key (document_id) references accounting_document (id) on delete restrict on update restrict;
create index ix_order_document_document_20 on order_document (document_id);
alter table order_request add constraint fk_order_request_brand_21 foreign key (brand_id) references brand (id) on delete restrict on update restrict;
create index ix_order_request_brand_21 on order_request (brand_id);
alter table order_request add constraint fk_order_request_managedBy_22 foreign key (managed_by_id) references user_table (id) on delete restrict on update restrict;
create index ix_order_request_managedBy_22 on order_request (managed_by_id);
alter table order_request add constraint fk_order_request_watchChosen_23 foreign key (watch_chosen_id) references watch (id) on delete restrict on update restrict;
create index ix_order_request_watchChosen_23 on order_request (watch_chosen_id);
alter table payment add constraint fk_payment_invoice_24 foreign key (invoice_id) references invoice (id) on delete restrict on update restrict;
create index ix_payment_invoice_24 on payment (invoice_id);
alter table payment_request add constraint fk_payment_request_customer_25 foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_payment_request_customer_25 on payment_request (customer_id);
alter table picture add constraint fk_picture_watch_26 foreign key (watch_id) references watch (id) on delete restrict on update restrict;
create index ix_picture_watch_26 on picture (watch_id);
alter table post_selling_certificate add constraint fk_post_selling_certificate_o_27 foreign key (owner_id) references customer (id) on delete restrict on update restrict;
create index ix_post_selling_certificate_o_27 on post_selling_certificate (owner_id);
alter table post_selling_certificate add constraint fk_post_selling_certificate_w_28 foreign key (watch_id) references watch_to_sell (id) on delete restrict on update restrict;
create index ix_post_selling_certificate_w_28 on post_selling_certificate (watch_id);
alter table post_service_certificate add constraint fk_post_service_certificate_o_29 foreign key (owner_id) references customer (id) on delete restrict on update restrict;
create index ix_post_service_certificate_o_29 on post_service_certificate (owner_id);
alter table post_service_certificate add constraint fk_post_service_certificate_w_30 foreign key (watch_id) references customer_watch (id) on delete restrict on update restrict;
create index ix_post_service_certificate_w_30 on post_service_certificate (watch_id);
alter table preset_quotation_for_brand add constraint fk_preset_quotation_for_brand_31 foreign key (brand_id) references brand (id) on delete restrict on update restrict;
create index ix_preset_quotation_for_brand_31 on preset_quotation_for_brand (brand_id);
alter table selling_document add constraint fk_selling_document_document_32 foreign key (document_id) references accounting_document (id) on delete restrict on update restrict;
create index ix_selling_document_document_32 on selling_document (document_id);
alter table selling_warrant add constraint fk_selling_warrant_watch_33 foreign key (watch_id) references watch_to_sell (id) on delete restrict on update restrict;
create index ix_selling_warrant_watch_33 on selling_warrant (watch_id);
alter table spare_part add constraint fk_spare_part_watch_34 foreign key (watch_id) references customer_watch (id) on delete restrict on update restrict;
create index ix_spare_part_watch_34 on spare_part (watch_id);
alter table user_table add constraint fk_user_table_partner_35 foreign key (partner_id) references partner (id) on delete restrict on update restrict;
create index ix_user_table_partner_35 on user_table (partner_id);
alter table user_table add constraint fk_user_table_customer_36 foreign key (customer_id) references customer (id) on delete restrict on update restrict;
create index ix_user_table_customer_36 on user_table (customer_id);
alter table watch_to_sell add constraint fk_watch_to_sell_brand_37 foreign key (brand_id) references brand (id) on delete restrict on update restrict;
create index ix_watch_to_sell_brand_37 on watch_to_sell (brand_id);
alter table watch_to_sell add constraint fk_watch_to_sell_customerThat_38 foreign key (customer_that_sells_the_watch_id) references customer (id) on delete restrict on update restrict;
create index ix_watch_to_sell_customerThat_38 on watch_to_sell (customer_that_sells_the_watch_id);
alter table watch_to_sell add constraint fk_watch_to_sell_customerThat_39 foreign key (customer_that_bought_the_watch_id) references customer (id) on delete restrict on update restrict;
create index ix_watch_to_sell_customerThat_39 on watch_to_sell (customer_that_bought_the_watch_id);
alter table watch_to_sell add constraint fk_watch_to_sell_purchaseInvo_40 foreign key (purchase_invoice_id) references external_document (id) on delete restrict on update restrict;
create index ix_watch_to_sell_purchaseInvo_40 on watch_to_sell (purchase_invoice_id);



alter table news_category_news add constraint fk_news_category_news_news_ca_01 foreign key (news_category_id) references news_category (id) on delete restrict on update restrict;

alter table news_category_news add constraint fk_news_category_news_news_02 foreign key (news_id) references news (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists accounting_document;

drop table if exists accounting_line;

drop table if exists accounting_line_analytic;

drop table if exists accounting_line_analytic_preset;

drop table if exists accounting_preset_item_table;

drop table if exists analytic_code;

drop table if exists appointment_request;

drop table if exists authentication;

drop table if exists auto_order;

drop table if exists brand;

drop table if exists buy_request;

drop table if exists contact_request;

drop table if exists customer;

drop table if exists customer_watch;

drop table if exists external_document;

drop table if exists feedback;

drop table if exists incoming_call;

drop table if exists internal_message;

drop table if exists invoice;

drop table if exists live_config;

drop table if exists mail_template;

drop table if exists mail_template_type;

drop table if exists news;

drop table if exists news_category_news;

drop table if exists news_alt;

drop table if exists news_category;

drop table if exists news_url;

drop table if exists order_table;

drop table if exists order_document;

drop table if exists order_request;

drop table if exists partner;

drop table if exists payment;

drop table if exists payment_request;

drop table if exists picture;

drop table if exists post_selling_certificate;

drop table if exists post_service_certificate;

drop table if exists preset_quotation_for_brand;

drop table if exists price;

drop table if exists quick_service_watch;

drop table if exists registered_email;

drop table if exists sms;

drop table if exists selling_document;

drop table if exists selling_warrant;

drop table if exists service_test;

drop table if exists spare_part;

drop table if exists usefull_link;

drop table if exists user_table;

drop table if exists watch;

drop table if exists watch_to_sell;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists accounting_document_seq;

drop sequence if exists accounting_line_seq;

drop sequence if exists accounting_line_analytic_seq;

drop sequence if exists accounting_line_analytic_preset_seq;

drop sequence if exists accounting_preset_item_table_seq;

drop sequence if exists analytic_code_seq;

drop sequence if exists appointment_request_seq;

drop sequence if exists authentication_seq;

drop sequence if exists auto_order_seq;

drop sequence if exists brand_seq;

drop sequence if exists buy_request_seq;

drop sequence if exists contact_request_seq;

drop sequence if exists customer_seq;

drop sequence if exists customer_watch_seq;

drop sequence if exists external_document_seq;

drop sequence if exists feedback_seq;

drop sequence if exists incoming_call_seq;

drop sequence if exists internal_message_seq;

drop sequence if exists invoice_seq;

drop sequence if exists live_config_seq;

drop sequence if exists mail_template_seq;

drop sequence if exists mail_template_type_seq;

drop sequence if exists news_seq;

drop sequence if exists news_alt_seq;

drop sequence if exists news_category_seq;

drop sequence if exists news_url_seq;

drop sequence if exists order_table_seq;

drop sequence if exists order_document_seq;

drop sequence if exists order_request_seq;

drop sequence if exists partner_seq;

drop sequence if exists payment_seq;

drop sequence if exists payment_request_seq;

drop sequence if exists picture_seq;

drop sequence if exists post_selling_certificate_seq;

drop sequence if exists post_service_certificate_seq;

drop sequence if exists preset_quotation_for_brand_seq;

drop sequence if exists price_seq;

drop sequence if exists quick_service_watch_seq;

drop sequence if exists registered_email_seq;

drop sequence if exists sms_seq;

drop sequence if exists selling_document_seq;

drop sequence if exists selling_warrant_seq;

drop sequence if exists service_test_seq;

drop sequence if exists spare_part_seq;

drop sequence if exists usefull_link_seq;

drop sequence if exists user_table_seq;

drop sequence if exists watch_seq;

drop sequence if exists watch_to_sell_seq;

