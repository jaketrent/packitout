
create table item
( id integer primary key autoincrement
, name varchar(200)
, details text
, tags varchar(200)
, weight integer default 0
, date_created timestamp default current_timestamp);

--;;

create table list
( id integer primary key autoincrement
, name varchar(200)
, details text
, date_start timestamp
, date_end timestamp
, date_created timestamp default current_timestamp);

--;;

create table list_item
( id integer primary key autoincrement
, item_id integer
, list_id integer
, foreign key (item_id) references item(id) on delete cascade
, foreign key (list_id) references list(id) on delete cascade);
