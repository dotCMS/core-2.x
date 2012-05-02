update structure set page_detail = 
(select inode.identifier from inode where inode.inode = page_detail)
where page_detail not in 
(select inode from identifier);

update structure set page_detail = 0 where page_detail is null;