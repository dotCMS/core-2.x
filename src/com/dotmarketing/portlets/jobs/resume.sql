DROP TABLE resume;
--
-- TOC Entry ID 2 (OID 12329174)
--
-- Name: jobs Type: TABLE Owner: root
--

CREATE TABLE resume (
        inode bigint primary key NOT NULL,
        member boolean not null default 'f',
        name character varying(100) NOT NULL,
        streetname1 character varying(100) NOT NULL,
        streetname2 character varying(100),
        city character varying(100) NOT NULL,
        state character varying(100),
        zip character varying(20),
        phone character varying(20),
        fax character varying(20),
        email character varying(100),
        exclusion1 character varying(100),
        exclusion2 character varying(100),
        exclusion3 character varying(100),
        exclusion4 character varying(100),
        verified boolean not null default 'f',
        objective text,
        location text,
        qualification text,
        salary text,
        cctype character varying(20),
        ccnum character varying(20),
        ccexp character varying(10),
        creationdate timestamp without time zone DEFAULT 'now()',
        expirationdate timestamp without time zone DEFAULT 'now()',
        active boolean not null default 'f'
);

CREATE CONSTRAINT TRIGGER "<unnamed>" AFTER INSERT OR UPDATE ON resume FROM inode NOT DEFERRABLE INITIALLY IMMEDIATE FOR EACH ROW EXECUTE PROCEDURE "RI_FKey_check_ins" ('<unnamed>', 'resume', 'inode', 'UNSPECIFIED', 'inode', 'inode');
