alter table item
add weight integer default 0;

--;;

alter table list
drop column weight;
