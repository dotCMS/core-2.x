DROP TABLE jobs;
--
-- TOC Entry ID 2 (OID 12329174)
--
-- Name: jobs Type: TABLE Owner: root
--

CREATE TABLE jobs (
        inode bigint primary key NOT NULL,
        organization character varying(100) NOT NULL,
        name character varying(100) NOT NULL,
        title character varying(100),
        streetaddress1 character varying(100) NOT NULL,
        streetaddress2 character varying(100),
        city character varying(100) NOT NULL,
        state character varying(100),
        zip character varying(20),
        phone character varying(20),
        fax character varying(20),
        email character varying(100),
        jobtitle character varying(100) NOT NULL,
        joblocation character varying(100),
        salary character varying(100),
        description text,
        requirements text,
        contactinfo text,
        cctype character varying(20),
        ccnum character varying(20),
        ccexp character varying(10),
        expdate timestamp without time zone,
        entrydate timestamp with time zone DEFAULT 'now()' NOT NULL,
        active boolean not null default 'f',
        blind boolean not null default 'f',
        premiumlisting boolean not null default 'f'
);


CREATE CONSTRAINT TRIGGER "<unnamed>" AFTER INSERT OR UPDATE ON jobs  FROM inode NOT DEFERRABLE INITIALLY IMMEDIATE FOR EACH ROW EXECUTE PROCEDURE "RI_FKey_check_ins" ('<unnamed>', 'jobs', 'inode', 'UNSPECIFIED', 'inode', 'inode');
