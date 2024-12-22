-- changeset David.Rabko:11_MED_210_Login
INSERT INTO roles (name)
SELECT name FROM (VALUES 
    ('PATIENT'), 
    ('DOCTOR'), 
    ('ADMIN')
) AS temp(name)
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE roles.name = temp.name);
-- rollback DELETE FROM roles WHERE name IN ('PATIENT', 'DOCTOR', 'ADMIN');

-- changeset Katarzyna.Osowska:3_MED_265_Admin_console
INSERT INTO user_statuses (name)
SELECT name FROM (VALUES 
    ('ACTIVE'), 
    ('WAITING_FOR_CONFIRMATION'), 
    ('DEACTIVATED'), 
    ('DELETED')
) AS temp(name)
WHERE NOT EXISTS (SELECT 1 FROM user_statuses WHERE user_statuses.name = temp.name);
-- rollback DELETE FROM user_statuses WHERE name IN
--        ('ACTIVE'),
--        ('WAITING_FOR_CONFIRMATION'),
--        ('DEACTIVATED');
--        ('DELETED');

-- changeset Uladzislau.Lukashevich:33_MED_144_Appointment
INSERT INTO appointment_statuses (status)
SELECT status FROM (VALUES 
    ('SCHEDULED'), 
    ('SCHEDULED_CONFIRMED'), 
    ('CANCELLED_PATIENT'), 
    ('CANCELLED_CLINIC'), 
    ('IN_CONSULTATION'), 
    ('COMPLETED'), 
    ('MISSED')
) AS temp(status)
WHERE NOT EXISTS (SELECT 1 FROM appointment_statuses WHERE appointment_statuses.status = temp.status);
-- rollback DELETE FROM appointment_statuses WHERE status IN ('SCHEDULED',
--        'SCHEDULED_CONFIRMED',
--        'CANCELLED_PATIENT',
--        'CANCELLED_CLINIC',
--        'IN_CONSULTATION',
--        'COMPLETED',
--        'MISSED');

-- changeset Anton.Dybko:6_MED_196_Recommendations_section
INSERT INTO genders (gender)
SELECT gender
FROM (VALUES 
    ('MALE'),
    ('FEMALE'),
    ('NEUTRAL')
) AS temp(gender)
WHERE NOT EXISTS (
    SELECT 1 
    FROM genders 
    WHERE genders.gender = temp.gender
);
-- rollback DELETE FROM genders WHERE gender IN
--       ('MALE'),
--       ('FEMALE'),
--       ('NEUTRAL');

-- changeset Anton.Dybko:3_MED_113_Appointment
INSERT INTO appointment_types (type)
SELECT *
FROM (VALUES 
    ('ONLINE'),
    ('ONSITE')
) AS temp(type)
WHERE NOT EXISTS (SELECT 1 FROM appointment_types WHERE type = temp.type);
-- rollback DELETE FROM appointment_types WHERE name IN ('ONLINE', 'ONSITE');

-- changeset Anton.Dybko:MED_284_Refactor_Exceptions
INSERT INTO contact_infos (id,created_date,last_modified_date,created_by,last_modified_by,phone)
SELECT id, created_date, last_modified_date, created_by, last_modified_by, phone
FROM (VALUES
    (1, NOW(), NOW(), 'system', 'system', '15551234567')
) AS temp(id, created_date, last_modified_date, created_by, last_modified_by, phone)
WHERE NOT EXISTS (
    SELECT 1
    FROM contact_infos
    WHERE contact_infos.id = temp.id
);
-- rollback DELETE FROM contact_infos WHERE id = 1;

-- changeset Anton.Dybko:MED_284_Refactor_Exceptions
INSERT INTO persons (id, name, surname, birth_date, created_date, last_modified_date, created_by, last_modified_by, sex, citizenship, contact_info_id)
SELECT id, name, surname, birth_date, created_date, last_modified_date, created_by, last_modified_by, sex, citizenship, contact_info_id
FROM (VALUES
  (1, 'John', 'Smith', '1990-05-15'::date, NOW(), NOW(), 'system', 'system', 'MALE', 'USA', 1)
) AS temp(id, name, surname, birth_date, created_date, last_modified_date, created_by, last_modified_by, sex, citizenship, contact_info_id)
WHERE NOT EXISTS (
    SELECT 1
    FROM persons
    WHERE persons.id = temp.id
    OR (persons.name = temp.name AND persons.surname = temp.surname AND persons.birth_date = temp.birth_date)
);
-- rollback DELETE FROM persons;

-- changeset Uladzislau.Lukashevich:MED_230_Admin_User
INSERT INTO users (id, enabled, email, password, person_id, user_status, role, created_by, last_modified_by)
SELECT id, enabled, email, password, person_id, user_status, role, created_by, last_modified_by
FROM (VALUES
    (1, TRUE, 'admin@medfast.com', '$2a$12$AeV4RRxknSpgrbkdxcb5KuMs6XkVZ65DaLKIfC3gKLn2S/KIz1yGK', 1, 'ACTIVE', 'ADMIN', 'system', 'system')
) AS temp(id, enabled, email, password, person_id, user_status, role, created_by, last_modified_by)
WHERE NOT EXISTS (
    SELECT 1 FROM users
    WHERE users.id = temp.id OR users.email = temp.email
);
-- rollback DELETE FROM users WHERE email = 'admin@medfast.com';

