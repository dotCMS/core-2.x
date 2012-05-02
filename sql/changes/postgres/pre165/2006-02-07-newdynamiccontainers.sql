--postgres

alter table containers drop column min_contentlets;

alter table containers drop column contentlet_fields;

alter table containers add column lucene_query text;

--mysql

alter table containers drop column min_contentlets;

alter table containers drop column contentlet_fields;

alter table containers add column lucene_query text;

--mssql

alter table containers drop column min_contentlets;

alter table containers drop column contentlet_fields;

alter table containers add column lucene_query text;

--oracle

alter table containers drop column min_contentlets;

alter table containers drop column contentlet_fields;

alter table containers add lucene_query clob;
