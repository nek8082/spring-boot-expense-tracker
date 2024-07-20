ALTER TABLE app_user ADD COLUMN user_role VARCHAR(50) DEFAULT 'ROLE_UNVERIFIED';

-- Optional: Default-Wert für bereits vorhandene Einträge setzen
UPDATE app_user SET user_role = 'ROLE_UNVERIFIED' WHERE user_role IS NULL;
