drop table if exists job;

create table job (
  id varchar(256) not null primary key,
  status varchar(256) not null,
  creation_date timestamp not null,
  data text not null
);

drop table if exists job_task;

create table job_task (
  id varchar(256) not null primary key,
  status varchar(256) not null,
  job_id varchar(256) not null,
  data text not null
);