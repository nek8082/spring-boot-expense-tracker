CREATE SCHEMA IF NOT EXISTS saas;

-- Don't forget to create your postgres role in the psql shell with: CREATE ROLE nek;
ALTER SCHEMA saas OWNER TO nek;
ALTER DEFAULT privileges IN SCHEMA saas GRANT ALL ON tables TO nek;
ALTER DEFAULT privileges IN SCHEMA saas GRANT ALL ON sequences TO nek;