INSERT INTO users (id, email, password, name, role, weight, height) VALUES
    (1, 'admin@admin.com', '$2a$10$zxE.Sn7DdC11lZy6jf6jUudQl46YctAhqdVaQqqrxFlhInwyB6hFi', 'Administrator', 'ADMIN', NULL, NULL),
    (2, 'user@user.com', '$2a$10$4/AA24LnuopHjOxSCCtmZu1fAZlWRSU28Mwuddhl4vAbUZxEMtfu6', 'Default User', 'USER', NULL, NULL);

ALTER TABLE users ALTER COLUMN id RESTART WITH 10;
