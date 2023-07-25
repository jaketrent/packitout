-- Place your queries here. Docs available https://www.hugsql.org/

-- :name insert-item! :! :n
INSERT INTO item
(name, details, tags, weight)
VALUES (:name, :details, :tags, :weight);

-- :name select-items :? :*
SELECT * FROM item;

