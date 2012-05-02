DROP table entity;
CREATE TABLE entity (
        inode integer NOT NULL Primary Key REFERENCES inode 
              ON UPDATE CASCADE  
              ON DELETE CASCADE, 
        entity_name varchar(255) NOT NULL
);
create index idx_entity1 on entity (entity_name);