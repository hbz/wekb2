-- Default for user not logged in
CREATE ROLE wekb_without_login NOINHERIT NOLOGIN;

-- Tenant for user logged in
CREATE ROLE wekb_with_login NOINHERIT NOLOGIN;

-- Granting roles so db_user can SET ROLE
GRANT wekb_without_login TO db_user;
GRANT wekb_with_login TO db_user;

-- Grants for tenant users
GRANT ALL ON ALL TABLES IN SCHEMA public TO wekb_with_login;
GRANT ALL ON ALL SEQUENCES IN SCHEMA public TO wekb_with_login;
GRANT CONNECT ON DATABASE wekb TO wekb_with_login;
GRANT USAGE ON SCHEMA public TO wekb_with_login;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO wekb_with_login;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO wekb_with_login;

GRANT CONNECT ON DATABASE wekb TO wekb_without_login;
GRANT USAGE ON SCHEMA public TO wekb_without_login;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO wekb_without_login;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO wekb_without_login;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO wekb_without_login;


ALTER ROLE wekb_without_login set statement_timeout TO 60000;
ALTER ROLE wekb_with_login set statement_timeout TO 300000;
