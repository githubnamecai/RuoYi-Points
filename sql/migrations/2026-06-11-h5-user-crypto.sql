ALTER TABLE t_user
  MODIFY COLUMN id_number varchar(256) DEFAULT NULL,
  MODIFY COLUMN phone varchar(256) NOT NULL,
  ADD COLUMN id_number_hash char(64) DEFAULT NULL AFTER id_number,
  ADD COLUMN phone_hash char(64) NOT NULL AFTER phone;

UPDATE t_user
SET
  phone_hash = SHA2(phone, 256),
  id_number_hash = CASE
    WHEN id_number IS NULL OR id_number = '' THEN NULL
    ELSE SHA2(id_number, 256)
  END
WHERE
  phone_hash IS NULL OR phone_hash = '';

ALTER TABLE t_user
  DROP INDEX uniq_phone,
  ADD UNIQUE KEY uniq_phone_hash (phone_hash);

