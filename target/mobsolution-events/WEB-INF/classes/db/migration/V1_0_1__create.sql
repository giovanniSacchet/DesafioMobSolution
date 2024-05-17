create table if not exists public.events
(
    id bigserial
        constraint events_pk primary key,
    name       varchar(255) not null,
    end_date   date         not null,
    start_date date         not null
);

create table if not exists public.participants
(
    id bigserial
        constraint participants_pk primary key,
    name     varchar(255) not null,
    email    varchar(255) not null,
    cpf      varchar(255) not null,
    event_id bigint       not null
        constraint participants_events_fk
        references events
        on update restrict on delete restrict
);

create table if not exists public.presences
(
    id bigserial
        constraint presences_pk primary key,
    pdate          date   not null,
    participant_id bigint not null
        constraint presences_participants_fk
        references participants
        on update restrict on delete restrict
);
