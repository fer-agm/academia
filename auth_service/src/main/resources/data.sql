-- Login credentials (default password: 1234) for the user profiles seeded in user-service.
-- 'clave' stores the SHA-1 hash of the password, matching HashService (lowercase hex).
-- INSERT IGNORE keeps this idempotent across restarts (run is unique).
INSERT IGNORE INTO auth_usuarios (run, clave) VALUES ('10492048-9', SHA1('1234'));
INSERT IGNORE INTO auth_usuarios (run, clave) VALUES ('7004888-4',  SHA1('1234'));
INSERT IGNORE INTO auth_usuarios (run, clave) VALUES ('5701779-1',  SHA1('1234'));
INSERT IGNORE INTO auth_usuarios (run, clave) VALUES ('13828053-5', SHA1('1234'));
INSERT IGNORE INTO auth_usuarios (run, clave) VALUES ('6760604-3',  SHA1('1234'));
INSERT IGNORE INTO auth_usuarios (run, clave) VALUES ('9472308-6',  SHA1('1234'));
INSERT IGNORE INTO auth_usuarios (run, clave) VALUES ('10985515-4', SHA1('1234'));
INSERT IGNORE INTO auth_usuarios (run, clave) VALUES ('19429788-2', SHA1('1234'));
INSERT IGNORE INTO auth_usuarios (run, clave) VALUES ('20076124-3', SHA1('1234'));
