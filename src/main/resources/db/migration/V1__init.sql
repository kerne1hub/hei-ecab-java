CREATE TABLE IF NOT EXISTS "speciality"
(
    id       serial primary key not null,
    name     varchar unique     not null,
    duration smallint check (duration > 0)
);

CREATE TABLE IF NOT EXISTS "group"
(
    id            serial primary key not null,
    name          varchar unique     not null,
    speciality_id bigint             not null,
    foreign key (speciality_id) references speciality on delete set null
);

CREATE TABLE IF NOT EXISTS "user"
(
    id         serial primary key                      not null,
    firstname  varchar                                 not null,
    lastname   varchar                                 not null,
    patronymic varchar                  default null,
    username   varchar unique                          not null,
    email      varchar unique                          not null,
    password   varchar                                 not null,
    type       varchar                  default 'USER' not null,
    created_at timestamp with time zone default current_timestamp
);

CREATE TABLE IF NOT EXISTS "student"
(
    id       int primary key not null,
    group_id int             not null,
    foreign key (id) references "user" on delete cascade,
    foreign key (group_id) references "group" on delete cascade
);

CREATE TABLE IF NOT EXISTS "lecturer"
(
    id       int primary key not null,
    position varchar         not null,
    degree   varchar         not null,
    foreign key (id) references "user" on delete cascade
);

CREATE TABLE IF NOT EXISTS "subject"
(
    id          serial primary key not null,
    name        varchar unique     not null,
    description varchar            not null,
    created_at  timestamp with time zone default current_timestamp,
    updated_at  timestamp with time zone default current_timestamp
);

CREATE TABLE IF NOT EXISTS "task"
(
    id          serial primary key not null,
    name        varchar            not null,
    description varchar            not null,
    author_id   int                not null,
    subject_id  int                not null,
    type        varchar            not null,
    created_at  timestamp with time zone default current_timestamp,
    updated_at  timestamp with time zone default current_timestamp,
    foreign key (author_id) references lecturer on delete set null,
    foreign key (subject_id) references subject on delete cascade
);

CREATE TABLE IF NOT EXISTS "report"
(
    id          serial primary key not null,
    student_id  int                not null,
    lecturer_id int                not null,
    task_id     int                not null,
    grade       smallint check (grade >= 0 and grade <= 100)
);

CREATE TABLE IF NOT EXISTS "active_task"
(
    id          serial primary key not null,
    student_id  int                not null,
    lecturer_id int                not null,
    task_id     int                not null,
    status      varchar default 'TO_DO',
    report_id   int     default null,
    foreign key (student_id) references student on delete cascade,
    foreign key (lecturer_id) references lecturer,
    foreign key (task_id) references task,
    foreign key (report_id) references report
);
