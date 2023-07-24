alter table item
drop column weight;

--;;

alter table list
add weight integer default 0;
