-- Default for user not logged in
CREATE ROLE wekb_without_login NOINHERIT LOGIN PASSWORD 'wekb_without_login';

-- Tenant for user logged in
CREATE ROLE wekb_with_login NOINHERIT LOGIN PASSWORD 'wekb_with_login';

-- Granting roles so db_user can SET ROLE
GRANT wekb_without_login TO db_user;
GRANT wekb_with_login TO db_user;

-- Grants for tenant users
GRANT ALL ON ALL TABLES IN SCHEMA "public" TO wekb_with_login;
GRANT SELECT ON ALL TABLES IN SCHEMA "public" TO wekb_without_login;


ALTER ROLE wekb_without_login set statement_timeout TO 60000;