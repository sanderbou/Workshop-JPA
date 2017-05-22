DELETE FROM account;
DELETE FROM account_info;
ALTER TABLE account_info ADD city VARCHAR(255) NOT NULL;