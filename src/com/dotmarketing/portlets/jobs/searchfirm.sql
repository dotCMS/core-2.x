
DROP table searchfirm;

CREATE TABLE searchfirm (
        inode bigint primary key NOT NULL,
        name character varying(100),
        organization character varying(100),
        title character varying(100),
        streetaddress1 character varying(100),
        streetaddress2 character varying(100),
        phone character varying(100),
        fax character varying(100),
        email character varying(100),
        url character varying(200),
        description text,
        contactinfo text,
        cctype character varying(20),
        ccnum character varying(20),
        ccexp character varying(10),
        creationdate timestamp with time zone DEFAULT now() NOT NULL,
        expirationdate timestamp without time zone,
        active boolean not null default 'f',
        linking boolean not null default 'f'
);

CREATE CONSTRAINT TRIGGER "<unnamed>" AFTER INSERT OR UPDATE ON searchfirm  FROM inode NOT DEFERRABLE INITIALLY IMMEDIATE FOR EACH ROW EXECUTE PROCEDURE "RI_FKey_check_ins" ('<unnamed>', 'searchfirm', 'inode', 'UNSPECIFIED', 'inode', 'inode');
