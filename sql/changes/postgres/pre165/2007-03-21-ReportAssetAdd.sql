CREATE TABLE report_asset
(
inode bigint NOT NULL,
report_name varchar(255) NOT NULL,
report_description varchar(1000) NOT NULL,
requires_input boolean NOT NULL DEFAULT false,
ds varchar(100),
CONSTRAINT report_asset_pkey PRIMARY KEY (inode),
CONSTRAINT report_assetfk FOREIGN KEY (inode) REFERENCES inode(inode)
)