create table companies
(
    id          integer not null
        constraint companies_pk
            primary key autoincrement,
    name        text    default 'N/A' not null,
    description text    default '',
    money       text    default '0' not null,
    employees   integer default 0,
    owner       text    default ''
        constraint companies_players_username_fk
            references players (username),
    established text    default '',
    paycheck    text    default '10'
);
--
create table jobs
(
    id          INTEGER not null
        primary key autoincrement,
    name        TEXT    not null,
    description TEXT default ''
);
--
create table players
(
    id          integer not null
        constraint players_pk
            primary key autoincrement,
    username    text    not null,
    uuid        text    not null,
    name        text default '',
    join_date   text default '',
    job         text default ''
        constraint players_jobs_name_fk
            references jobs (name),
    company     text default ''
        constraint players_companies_name_fk
            references companies (name),
    notes       text default '',
    punishments int  default 0 not null
);
--
create table transactions
(
    id          INTEGER not null
        constraint transactions_pk
            primary key autoincrement,
    companyId   INTEGER not null
        references companies,
    direction   TEXT    not null,
    title       TEXT,
    description text,
    amount      TEXT default '0' not null
);
--
