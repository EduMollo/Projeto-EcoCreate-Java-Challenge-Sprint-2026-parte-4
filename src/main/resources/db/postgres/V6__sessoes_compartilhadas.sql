-- Schema oficial Spring Session JDBC PostgreSQL, em schema privado gerenciado pelo Flyway.
CREATE TABLE clyvovet.spring_session (
 primary_id char(36) NOT NULL PRIMARY KEY,
 session_id char(36) NOT NULL,
 creation_time bigint NOT NULL,
 last_access_time bigint NOT NULL,
 max_inactive_interval integer NOT NULL,
 expiry_time bigint NOT NULL,
 principal_name varchar(100));
CREATE UNIQUE INDEX spring_session_ix1 ON clyvovet.spring_session(session_id);
CREATE INDEX spring_session_ix2 ON clyvovet.spring_session(expiry_time);
CREATE INDEX spring_session_ix3 ON clyvovet.spring_session(principal_name);
CREATE TABLE clyvovet.spring_session_attributes (
 session_primary_id char(36) NOT NULL REFERENCES clyvovet.spring_session(primary_id) ON DELETE CASCADE,
 attribute_name varchar(200) NOT NULL,
 attribute_bytes bytea NOT NULL,
 PRIMARY KEY(session_primary_id,attribute_name));
ALTER TABLE clyvovet.spring_session ENABLE ROW LEVEL SECURITY;
ALTER TABLE clyvovet.spring_session_attributes ENABLE ROW LEVEL SECURITY;
REVOKE ALL ON clyvovet.spring_session, clyvovet.spring_session_attributes FROM PUBLIC;
DO $$ DECLARE papel text; BEGIN
 FOREACH papel IN ARRAY ARRAY['anon','authenticated'] LOOP
  IF EXISTS(SELECT 1 FROM pg_catalog.pg_roles WHERE rolname=papel) THEN
   EXECUTE format('REVOKE ALL ON clyvovet.spring_session, clyvovet.spring_session_attributes FROM %I',papel);
  END IF;
 END LOOP;
END $$;
