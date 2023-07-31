-- Place your queries here. Docs available https://www.hugsql.org/

-- :name insert-item! :! :n
INSERT INTO item
(name, details, tags, weight)
VALUES (:name, :details, :tags, :weight);

-- :name update-item! :! :n
update item
set name = :name
, details = :details
, tags = :tags
, weight = :weight
where id = :id;

-- :name delete-item! :! :n
delete from item
where id = :id;
-- TODO: transaction to delete list_item
-- see https://github.com/twl8n/hugsql-demo/blob/master/src/hugsql_demo/core.clj

-- :name select-items :? :*
SELECT * FROM item;

-- :name find-item :? :1
SELECT *
FROM item
where id = :id;

-- :name insert-list! :! :n
INSERT INTO list
(name, details, date_start, date_end)
VALUES (:name, :details, :date-start, :date-end);

-- :name update-list! :! :n
update list
set name = :name
, details = :details
, date_start = :date-start
, date_end = :date-end
where id = :id;

-- :name delete-list! :! :n
delete from list
where id = :id;
-- TODO: transaction to delete list_list
-- see https://github.com/twl8n/hugsql-demo/blob/master/src/hugsql_demo/core.clj

-- :name select-lists :? :*
SELECT * FROM list;

-- :name find-list :? :1
SELECT *
FROM list
where id = :id;

-- :name select-list-items :? :*
select i.*
from list_item li
join item i on li.item_id = i.id
where li.list_id = :id;

-- :name select-available-items :? :*
-- overlap clause, see: https://stackoverflow.com/a/325964
select *
from item
where id not in (
  select i.id 
  from item i
  join list_item li on li.item_id = i.id
  join list l on li.list_id = l.id
  where l.date_start <= :date_end
  or l.date_end >= :date_start
);
