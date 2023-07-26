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
