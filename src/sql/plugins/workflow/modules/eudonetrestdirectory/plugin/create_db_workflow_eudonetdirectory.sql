--
-- Table structure for table task_create_eudonetdirectory_cf
--
DROP TABLE IF EXISTS task_create_eudonetdirectory_cf;
CREATE TABLE task_create_eudonetdirectory_cf(
  id_task INT NOT NULL,
  id_directory INT DEFAULT NULL,
  id_table varchar(255) DEFAULT NULL,
  base_url varchar(255) default NULL,
  subscriber_login varchar(255) default NULL,
  subscriber_password varchar(255) default NULL,
  base_name varchar(255) default NULL,
  user_login varchar(255) default NULL,
  user_password varchar(255) default NULL,
  user_lang varchar(255) default NULL,
  product_name varchar(255) default NULL,
  
  PRIMARY KEY (id_task)
);

--
-- Table structure for table task_create_eudonet_data_cf
--
DROP TABLE IF EXISTS task_create_eudonet_data_cf;
CREATE TABLE task_create_eudonet_data_cf(
  id_attribut INT NOT NULL,
  id_task INT DEFAULT NULL,
  order_entry INT DEFAULT NULL,
  eudonet_key varchar(255) default NULL,
  eudonet_key_table varchar(255) default NULL,
  eudonet_key_table_link varchar(255) default NULL,
  
  PRIMARY KEY ( id_attribut ),
  CONSTRAINT `fk_eudonet_directory_id_task` FOREIGN KEY(`id_task`) references task_create_eudonetdirectory_cf(`id_task`)

);

--
-- Table structure for table task_eudonetrest_table_link
--
DROP TABLE IF EXISTS task_eudonetrest_table_link;
CREATE TABLE task_eudonetrest_table_link(
  id INT NOT NULL,
  id_ressource INT DEFAULT NULL,
  id_field varchar(255) default NULL,
  id_table varchar(255) default NULL,
  id_table_link varchar(255) default NULL,
  
  PRIMARY KEY ( id )
);

alter table task_create_eudonet_data_cf
add column eudonet_default_value varchar(255) default NULL;
  