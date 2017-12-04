# --- !Ups

create table account (
  id                            bigserial not null,
  username                      varchar(255) not null,
  password                      varchar(255) not null,
  created_at                    timestamptz,
  constraint uq_account_username unique (username),
  constraint pk_account primary key (id)
);

create table endorsement (
  id                            bigserial not null,
  endorser_account_id           bigint not null,
  skill_id                      bigint not null,
  constraint pk_endorsement primary key (id)
);

create table skill (
  id                            bigserial not null,
  account_id                    bigint not null,
  name                          varchar(255) not null,
  constraint pk_skill primary key (id)
);

alter table endorsement add constraint fk_endorsement_endorser_account_id foreign key (endorser_account_id) references account (id) on delete cascade on update cascade;
create index ix_endorsement_endorser_account_id on endorsement (endorser_account_id);

alter table endorsement add constraint fk_endorsement_skill_id foreign key (skill_id) references skill (id) on delete cascade on update cascade;
create index ix_endorsement_skill_id on endorsement (skill_id);

alter table skill add constraint fk_skill_account_id foreign key (account_id) references account (id) on delete cascade on update cascade;
create index ix_skill_account_id on skill (account_id);


# --- !Downs

alter table if exists endorsement drop constraint if exists fk_endorsement_endorser_account_id;
drop index if exists ix_endorsement_endorser_account_id;

alter table if exists endorsement drop constraint if exists fk_endorsement_skill_id;
drop index if exists ix_endorsement_skill_id;

alter table if exists skill drop constraint if exists fk_skill_account_id;
drop index if exists ix_skill_account_id;

drop table if exists account cascade;

drop table if exists endorsement cascade;

drop table if exists skill cascade;

