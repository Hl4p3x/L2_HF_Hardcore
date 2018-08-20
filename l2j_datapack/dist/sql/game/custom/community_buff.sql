CREATE TABLE IF NOT EXISTS community_buff_lists (
 	list_id int NOT NULL AUTO_INCREMENT,
 	owner_id int,
 	list_name varchar(255),
 	PRIMARY KEY(list_id),
 	UNIQUE (owner_id, list_name)
);


CREATE TABLE IF NOT EXISTS community_buff_items (
   list_id int,
   skill_id int,
   skill_level int,
   FOREIGN KEY(list_id) REFERENCES community_buff_lists(list_id),
   UNIQUE (list_id, skill_id)
);
