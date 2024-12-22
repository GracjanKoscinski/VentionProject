-- changeset Yana.Yeustratsyeva:1_MED_274_Insert_Test_Data
INSERT INTO roles (name)
SELECT name FROM (VALUES 
    ('PATIENT'), 
    ('DOCTOR'), 
    ('ADMIN')
) AS temp(name)
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE roles.name = temp.name);
-- rollback DELETE FROM roles WHERE name IN ('PATIENT', 'DOCTOR', 'ADMIN');

-- changeset Yana.Yeustratsyeva:2_MED_274_Insert_Test_Data
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

-- changeset Yana.Yeustratsyeva:3_MED_274_Insert_Test_Data
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
-- rollback DELETE FROM appointment_statuses WHERE status IN (
--     'SCHEDULED',
--     'SCHEDULED_CONFIRMED',
--     'CANCELLED_PATIENT',
--     'CANCELLED_CLINIC',
--     'IN_CONSULTATION',
--     'COMPLETED',
--     'MISSED'
--);

-- changeset Yana.Yeustratsyeva:4_MED_274_Insert_Genders_Data
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
-- rollback DELETE FROM genders WHERE gender IN ('MALE', 'FEMALE', 'NEUTRAL');

-- changeset Yana.Yeustratsyeva:5_MED_274_Insert_Contact_Infos_Data
INSERT INTO contact_infos (
    id,
    created_date,
    last_modified_date,
    created_by,
    last_modified_by,
    phone
)
SELECT id, created_date, last_modified_date, created_by, last_modified_by, phone
FROM (VALUES
    (1,   NOW(), NOW(), 'system_admin', 'system_admin', '15551234599'),
    (100, NOW(), NOW(), 'system_admin', 'system_admin', '15551234567'),
    (101, NOW(), NOW(), 'system_admin', 'system_admin', '15551234568'),
    (102, NOW(), NOW(), 'system_admin', 'system_admin', '15551234569')
) AS temp(id, created_date, last_modified_date, created_by, last_modified_by, phone)
WHERE NOT EXISTS (
    SELECT 1 
    FROM contact_infos 
    WHERE contact_infos.id = temp.id
);
-- rollback DELETE FROM contact_infos WHERE id = 100;

-- changeset Yana.Yeustratsyeva:6_MED_274_Insert_Test_Data
INSERT INTO persons (id, name, surname, birth_date, created_date, last_modified_date, created_by, last_modified_by, sex, citizenship, contact_info_id)
SELECT id, name, surname, birth_date, created_date, last_modified_date, created_by, last_modified_by, sex, citizenship, contact_info_id
FROM (VALUES
    (1, 'John', 'Smith', '1990-05-15'::date, NOW(), NOW(), 'system', 'system', 'MALE', 'USA', 1),
    (500, 'John', 'Doe', '1990-05-15'::date, NOW(), NOW(), 'system', 'system', 'MALE', 'USA', 100),
    (600, 'Jane', 'Smith', '1985-03-22'::date, NOW(), NOW(), 'system', 'system', 'FEMALE', 'USA', 101)
) AS temp(id, name, surname, birth_date, created_date, last_modified_date, created_by, last_modified_by, sex, citizenship, contact_info_id)
WHERE NOT EXISTS (
    SELECT 1 
    FROM persons 
    WHERE persons.id = temp.id 
    OR (persons.name = temp.name AND persons.surname = temp.surname AND persons.birth_date = temp.birth_date)
);
-- rollback DELETE FROM persons;

-- changeset Yana.Yeustratsyeva:7_MED_274_Insert_Test_Data
-- password for patient is Patient123$
-- password for doctor is Doctor123$
INSERT INTO users (id, enabled, email, password, checkbox_terms_and_conditions, person_id, user_status, role, created_by, last_modified_by)
SELECT id, enabled, email, password, checkbox_terms_and_conditions, person_id, user_status, role, created_by, last_modified_by 
FROM (VALUES 
    (1, TRUE, 'admin@medfast.com', '$2a$12$AeV4RRxknSpgrbkdxcb5KuMs6XkVZ65DaLKIfC3gKLn2S/KIz1yGK', TRUE, 1, 'ACTIVE', 'ADMIN', 'system', 'system'),
    (500, TRUE, 'patient@gmail.com', '$2b$12$8.KOPMuQxvPSpLoiHREmW.a1y.K3K04KTwa/HB7fFvBXIZFAn7D2a', TRUE, 500, 'ACTIVE', 'PATIENT', 'system', 'system'),
    (600, TRUE, 'doctor@gmail.com', '$2b$12$8.KOPMuQxvPSpLoiHREmW.4E9eni8bFwIdpxjehDyTAgEXMKVcCgi', TRUE, 600, 'ACTIVE', 'DOCTOR', 'system', 'system') 
) AS temp(id, enabled, email, password, checkbox_terms_and_conditions, person_id, user_status, role, created_by, last_modified_by)
WHERE NOT EXISTS (
    SELECT 1 FROM users 
    WHERE users.id = temp.id OR users.email = temp.email
);
-- rollback DELETE FROM users';

-- changeset Yana.Yeustratsyeva:8_MED_274_Insert_Test_Data
INSERT INTO appointment_types (type)
SELECT *
FROM (VALUES 
    ('ONLINE'),
    ('ONSITE')
) AS temp(type)
WHERE NOT EXISTS (SELECT 1 FROM appointment_types WHERE type = temp.type);
-- rollback DELETE FROM appointment_types WHERE name IN ('ONLINE', 'ONSITE');

-- changeset Yana.Yeustratsyeva:9_MED_274_Insert_Test_Data
INSERT INTO tests (id, test, duration)
SELECT id, test, duration FROM (VALUES 
    (2, 'Allergy Skin Test', 30),
    (3, 'Amniocentesis', 45),
    (4, 'Arterial Blood Gas (ABG) Test', 15),
    (5, 'Audiometry (Hearing Test)', 45),
    (6, 'Basic Metabolic Panel (BMP)', 15),
    (7, 'Blood Culture', 15),
    (8, 'Blood Glucose Test', 15),
    (9, 'Blood Typing', 15),
    (10, 'Blood Urea Nitrogen (BUN) Test', 15),
    (11, 'Bone Density Scan (DEXA)', 30),
    (12, 'Bone Marrow Biopsy', 60),
    (13, 'Breast Ultrasound', 30),
    (14, 'Bronchoscopy', 45),
    (15, 'Carotid Doppler', 45),
    (16, 'Chest X-ray', 15),
    (17, 'Cholesterol Test', 15),
    (18, 'Colonoscopy', 45),
    (19, 'Complete Blood Count (CBC)', 15),
    (20, 'Comprehensive Metabolic Panel (CMP)', 15),
    (21, 'C-Reactive Protein (CRP)', 15),
    (22, 'Creatinine Clearance Test', 15),
    (23, 'Crossmatch (for Blood Transfusion)', 60),
    (24, 'CT Scan (Head/Body)', 30),
    (25, 'D-Dimer Test', 15),
    (26, 'Echocardiogram', 45),
    (27, 'EEG (Electroencephalogram)', 45),
    (28, 'Electrocardiogram (ECG/EKG)', 15),
    (29, 'Electrolyte Panel', 15),
    (30, 'EMG (Electromyography)', 60),
    (31, 'Endoscopy', 30),
    (32, 'Erythrocyte Sedimentation Rate (ESR)', 30),
    (33, 'Fetal Ultrasound', 45),
    (34, 'Hemoglobin A1c (HbA1c)', 15),
    (35, 'Hepatitis Panel', 15),
    (36, 'HIV Test (ELISA)', 60),
    (37, 'HIV Test (Rapid)', 30),
    (38, 'HIV Test (Western Blot)', 90),
    (39, 'HLA Typing (for Transplantation)', 60),
    (40, 'Holter Monitoring', 15),
    (41, 'Lipid Panel', 15),
    (42, 'Liver Function Test (LFT)', 15),
    (43, 'Lumbar Puncture (Spinal Tap)', 60),
    (44, 'Lung Biopsy', 45),
    (45, 'Mammogram', 30),
    (46, 'MRI Scan (Head/Body)', 45),
    (47, 'Nerve Conduction Study (NCS)', 60),
    (48, 'Pap Smear', 15),
    (49, 'Pelvic Ultrasound', 30),
    (50, 'PET Scan', 90),
    (51, 'Pregnancy Test (Blood)', 15),
    (52, 'Pregnancy Test (Urine)', 15),
    (53, 'Prothrombin Time (PT/INR)', 15),
    (54, 'Pulmonary Function Test (PFT)', 30),
    (55, 'Rapid Strep Test', 15),
    (56, 'Skin Biopsy', 30),
    (57, 'Spirometry (Lung Function Test)', 15),
    (58, 'Sputum Culture', 15),
    (59, 'STD Testing Panel', 15),
    (60, 'Stool Culture', 15),
    (61, 'Stress Test (Cardiac)', 60),
    (62, 'Throat Culture', 15),
    (63, 'Thyroid Function Test (TFT)', 15),
    (64, 'Treadmill Test (Cardiac Stress Test)', 60),
    (65, 'Urinalysis', 15),
    (66, 'Urine Culture', 15),
    (67, 'Visual Acuity Test', 15)
) AS temp(id, test, duration)
WHERE NOT EXISTS (
    SELECT 1 FROM tests 
    WHERE tests.id = temp.id
);
--rollback DELETE FROM tests;

-- changeset Yana.Yeustratsyeva:10_MED_274_Insert_Test_Data
INSERT INTO locations (id, hospital_name, street_address, house)
SELECT id, hospital_name, street_address, house 
FROM (VALUES 
    (2, 'Green Valley Medical Center', '789 Elm St.', '3C'),
    (3, 'Sunnydale Clinic', '101 Pine Rd.', '4D'),
    (4, 'Lakeside Health Facility', '202 Lakeview Dr.', '5E'),
    (5, 'Riverside Health Center', '303 River St.', '6F'),
    (6, 'Mountain View Hospital', '123 Mountain Rd.', '1A'),
    (7, 'Pine Hills Medical Center', '234 Pine Hills Ave.', '7B'),
    (8, 'Oakwood General Hospital', '345 Oakwood Dr.', '8C'),
    (9, 'Silver Valley Clinic', '456 Silver Valley Blvd.', '9D'),
    (10, 'Cedar Creek Health Center', '567 Cedar Creek Rd.', '10E'),
    (11, 'Valley Forge Medical Facility', '678 Valley Forge Ln.', '11F'),
    (12, 'Maple Leaf Hospital', '789 Maple St.', '12G'),
    (13, 'Springfield Community Hospital', '890 Springfield Ave.', '13H'),
    (14, 'Downtown Medical Plaza', '901 Downtown Blvd.', '14I'),
    (15, 'Clearwater Regional Hospital', '234 Clearwater Rd.', '15J'),
    (16, 'Summit Medical Center', '345 Summit St.', '16K'),
    (17, 'Coastal Health Clinic', '456 Coastal Ave.', '17L'),
    (18, 'Health First Hospital', '567 Health First Blvd.', '18M'),
    (19, 'Innovative Care Medical Center', '678 Innovative Care St.', '19N'),
    (20, 'Starlight Health Services', '789 Starlight Rd.', '20O')
) AS temp(id, hospital_name, street_address, house)
WHERE NOT EXISTS (
    SELECT 1 FROM locations 
    WHERE locations.id = temp.id OR locations.hospital_name = temp.hospital_name
);
--rollback DELETE FROM locations;

-- changeset Yana.Yeustratsyeva:11_MED_274_Insert_Test_Data
INSERT INTO public.test_location (test_id, location_id)
SELECT test_id, location_id
FROM (VALUES 
    (2, 2), (2, 5),
    (3, 6),
    (4, 2),
    (5, 3), (5, 4), (5, 5),
    (6, 2),
    (7, 6),
    (8, 2), (8, 3),
    (9, 7),
    (10, 2), (10, 8),
    (11, 2), (11, 5), (11, 10),
    (12, 2), (12, 3),
    (13, 8),
    (14, 2), (14, 4), (14, 5), (14, 7),
    (15, 9),
    (16, 2),
    (17, 2), (17, 5), (17, 6),
    (18, 2), (18, 3), (18, 4),
    (19, 2),
    (20, 2),
    (21, 2),
    (22, 3),
    (23, 2), (23, 8),
    (24, 5),
    (25, 2),
    (26, 3), (26, 6),
    (27, 2), (27, 4),
    (28, 2), (28, 3),
    (29, 6),
    (30, 5),
    (31, 2),
    (32, 2),
    (33, 2),
    (34, 6),
    (35, 2),
    (36, 2), (36, 8),
    (37, 3),
    (38, 2),
    (39, 2), (39, 5),
    (40, 4),
    (41, 2),
    (42, 2),
    (43, 2), (43, 6),
    (44, 5),
    (45, 3),
    (46, 2), (46, 4), (46, 7),
    (47, 2), (47, 5),
    (48, 2),
    (49, 2),
    (50, 8),
    (51, 6),
    (52, 4),
    (53, 2),
    (54, 2),
    (55, 2),
    (56, 6),
    (57, 2),
    (58, 2),
    (59, 2),
    (60, 2), (60, 3),
    (61, 3),
    (62, 4),
    (63, 2),
    (64, 2),
    (65, 2), (65, 6),
    (66, 2),
    (67, 5)
) AS temp(test_id, location_id)
WHERE NOT EXISTS (
    SELECT 1 FROM public.test_location 
    WHERE public.test_location.test_id = temp.test_id 
      AND public.test_location.location_id = temp.location_id
);
--rollback DELETE FROM test_location;

-- changeset Yana.Yeustratsyeva:12_MED_274_Insert_Test_Data
INSERT INTO specializations (id, specialization, created_by, last_modified_by)
SELECT id, specialization, created_by, last_modified_by
FROM (VALUES 
    (300, 'Orthopedics', 'admin', 'admin'),
    (400, 'Pediatrics', 'system', 'system'),
    (500, 'Dermatology', 'system', 'system'),
    (600, 'Radiology', 'system', 'system')
) AS temp(id, specialization, created_by, last_modified_by)
WHERE NOT EXISTS (SELECT 1 FROM specializations WHERE specializations.id = temp.id);
--rollback DELETE FROM specializations;

-- changeset Yana.Yeustratsyeva:13_MED_274_Insert_Test_Data
INSERT INTO test_appointments (date_time, test_id, location_id, patient_id, appointment_status, created_by, last_modified_by)
SELECT date_time::timestamp, test_id, location_id, patient_id, appointment_status, created_by, last_modified_by
FROM (VALUES 
    ('2024-10-12 09:00:00'::timestamp, 2, 2, 500, 'SCHEDULED', 'admin', 'admin')
) AS temp(date_time, test_id, location_id, patient_id, appointment_status, created_by, last_modified_by)
WHERE NOT EXISTS (
    SELECT 1 
    FROM test_appointments 
    WHERE test_appointments.date_time = temp.date_time 
      AND test_appointments.test_id = temp.test_id 
      AND test_appointments.location_id = temp.location_id 
      AND test_appointments.patient_id = temp.patient_id
);
--rollback DELETE FROM test_appointments;

-- changeset Yana.Yeustratsyeva:14_MED_274_Insert_Test_Data
INSERT INTO services (id, service, duration, created_by, last_modified_by)
SELECT id, service, duration, created_by, last_modified_by
FROM (VALUES 
    (200, 'MRI', 60, 'system', 'system'),
    (300, 'CT Scan', 45, 'system', 'system'),
    (400, 'Ultrasound', 30, 'admin', 'admin'),
    (500, 'Physiotherapy', 60, 'system', 'system')
) AS temp(id, service, duration, created_by, last_modified_by)
WHERE NOT EXISTS (
    SELECT 1 
    FROM services 
    WHERE services.id = temp.id
);
--rollback DELETE FROM services;

-- changeset Yana.Yeustratsyeva:15_MED_274_Insert_Test_Data
INSERT INTO specializations_services_bridge (specialization_id, service_id)
SELECT specialization_id, service_id
FROM (VALUES 
    (300, 300),  -- Neurology -> CT Scan
    (300, 400),  -- Orthopedics -> Ultrasound
    (400, 500)   -- Orthopedics -> Physiotherapy
) AS temp(specialization_id, service_id)
WHERE NOT EXISTS (
    SELECT 1 
    FROM specializations_services_bridge 
    WHERE specializations_services_bridge.specialization_id = temp.specialization_id
    AND specializations_services_bridge.service_id = temp.service_id
);
--rollback DELETE FROM specializations_services_bridge;

-- changeset Yana.Yeustratsyeva:16_MED_274_Insert_Test_Data
INSERT INTO addresses (id, created_by, last_modified_by, street_address, house, apartment, city, state, zip)
SELECT id, created_by, last_modified_by, street_address, house, apartment, city, state, zip
FROM (VALUES
    (1, 'system', 'system', '123 Main St', '10', '1A', 'New York', 'NY', '10001'),
    (2, 'admin', 'admin', '456 Elm St', '5', NULL, 'Los Angeles', 'CA', '90001'),
    (3, 'user', 'user', '789 Oak St', '15', '2B', 'Chicago', 'IL', '60601')
) AS temp(id, created_by, last_modified_by, street_address, house, apartment, city, state, zip)
WHERE NOT EXISTS (
    SELECT 1 FROM addresses 
    WHERE addresses.id = temp.id
);
--rollback DELETE FROM addresses;

-- changeset Yana.Yeustratsyeva:17_MED_274_Insert_Test_Data
INSERT INTO patients (id, address_id)
SELECT id, address_id
FROM (VALUES 
    (500, 3)
) AS temp(id, address_id)
WHERE NOT EXISTS (
    SELECT 1 FROM patients WHERE patients.id = temp.id
);
--rollback DELETE FROM patients;

-- changeset Yana.Yeustratsyeva:18_MED_274_Insert_Test_Data
INSERT INTO doctors (id, license_number, location_id)
SELECT id, license_number, location_id
FROM (VALUES 
    (600, 'DOC12345', 2)
) AS temp(id, license_number, location_id)
WHERE NOT EXISTS (
    SELECT 1 FROM doctors WHERE doctors.id = temp.id
);
--rollback DELETE FROM doctors;

-- changeset Yana.Yeustratsyeva:19_MED_274_Insert_Test_Data
INSERT INTO doctors_specializations_bridge (doctor_id, specializations_id)
SELECT doctor_id, specializations_id
FROM (VALUES 
    (600, 300)
) AS temp(doctor_id, specializations_id)
WHERE NOT EXISTS (
    SELECT 1 FROM doctors_specializations_bridge 
    WHERE doctors_specializations_bridge.doctor_id = temp.doctor_id 
    AND doctors_specializations_bridge.specializations_id = temp.specializations_id
);
--rollback DELETE FROM doctors_specializations_bridge;

-- changeset Yana.Yeustratsyeva:20_MED_274_Insert_Test_Data
INSERT INTO consultation_appointments (id, doctor_id, patient_id, service_id, date_from, date_to, location_id, type, appointment_status, created_date, last_modified_date, created_by, last_modified_by)
SELECT id, doctor_id, patient_id, service_id, date_from, date_to, location_id, type, appointment_status, created_date, last_modified_date, created_by, last_modified_by
FROM (VALUES 
    (1, 600, 500, 300, '2024-10-20T09:00:00Z', '2024-10-20T10:00:00Z', 2, 'ONSITE', 'SCHEDULED', NOW(), NOW(), 'system', 'system'), 
    (2, 600, 500, 300, NOW() + INTERVAL '2 days', NOW() + INTERVAL '2 days' + INTERVAL '1 hour', 2, 'ONSITE', 'IN_CONSULTATION', NOW(), NOW(), 'system', 'system'),
    (3, 600, 500, 300, NOW() + INTERVAL '3 days', NOW() + INTERVAL '3 days' + INTERVAL '2 hour', 2, 'ONSITE', 'COMPLETED', NOW(), NOW(), 'system', 'system'),
    (4, 600, 500, 300, NOW() + INTERVAL '4 days', NOW() + INTERVAL '4 days' + INTERVAL '1 hour', 2, 'ONSITE', 'COMPLETED', NOW(), NOW(), 'system', 'system')
) AS temp(id, doctor_id, patient_id, service_id, date_from, date_to, location_id, type, appointment_status, created_date, last_modified_date, created_by, last_modified_by)
WHERE NOT EXISTS (
    SELECT 1 FROM consultation_appointments WHERE consultation_appointments.id = temp.id
);
-- rollback DELETE FROM consultation_appointments;

-- changeset Yana.Yeustratsyeva:21_MED_274_Insert_Test_Data
INSERT INTO profile_picture_metadata (id, user_id, created_date, last_modified_date, created_by, last_modified_by, file_path, content_type)
SELECT id, user_id, created_date, last_modified_date, created_by, last_modified_by, file_path, content_type
FROM (VALUES 
    (1, 600, NOW(), NOW(), 'system', 'system', './uploads/profilePictures/doctor.jpg', 'image/jpg')
) AS temp(id, user_id, created_date, last_modified_date, created_by, last_modified_by, file_path, content_type)
WHERE NOT EXISTS (
    SELECT 1 FROM profile_picture_metadata WHERE profile_picture_metadata.user_id = temp.user_id
);
-- rollback DELETE FROM profile_picture_metadata;

-- changeset Yana.Yeustratsyeva:22_MED_274_Insert_Test_Data
INSERT INTO referrals (id, issued_by, specialization_id, patient_id, appointment_id, issue_date, expiration_date, created_date, last_modified_date, created_by, last_modified_by)
SELECT id, issued_by, specialization_id, patient_id, appointment_id, issue_date, expiration_date, created_date, last_modified_date, created_by, last_modified_by
FROM (VALUES 
    (1, 600, 300, 500, 1, '2024-10-20'::date, '2025-10-20'::date, NOW(), NOW(), 'system', 'system'),
    (2, 600, 300, 500, 2, NOW(), '2025-11-21'::date, NOW(), NOW(), 'system', 'system'),
    (3, 600, 400, 500, NULL, '2024-10-22'::date, '2025-11-28'::date, NOW(), NOW(), 'system', 'system')
) AS temp(id, issued_by, specialization_id, patient_id, appointment_id, issue_date, expiration_date, created_date, last_modified_date, created_by, last_modified_by)
WHERE NOT EXISTS (
    SELECT 1 FROM referrals WHERE referrals.id = temp.id
);
-- rollback DELETE FROM referrals;

-- changeset Yana.Yeustratsyeva:23_MED_274_Insert_Test_Data
INSERT INTO recommendations (id, service_id, age_from, age_to, legal_gender, created_date, last_modified_date, created_by, last_modified_by)
SELECT id, service_id, age_from, age_to, legal_gender, created_date, last_modified_date, created_by, last_modified_by
FROM (VALUES 
    (1, 300, 18, 65, 'MALE', NOW(), NOW(), 'system', 'system')
) AS temp(id, service_id, age_from, age_to, legal_gender, created_date, last_modified_date, created_by, last_modified_by)
WHERE NOT EXISTS (
    SELECT 1 FROM recommendations WHERE recommendations.id = temp.id
);
-- rollback DELETE FROM recommendations;