-- changeset David.Rabko:5_MED_210_Login
CREATE INDEX refresh_tokens_users__fk ON refresh_tokens (user_id);
-- rollback DROP INDEX refresh_tokens_users__fk;

-- changeset David.Rabko:6_MED_210_Login
CREATE INDEX verification_tokens_users__fk ON verification_tokens (user_id);
-- rollback DROP INDEX verification_tokens_users__fk;

-- changeset David.Rabko:7_MED_210_Login
CREATE INDEX users_roles__fk ON users (role);
-- rollback DROP INDEX users_roles__fk;

-- changeset Uladzislau.Lukashevich:29_MED_144_Appointment
CREATE INDEX consultation_appointments_doctors__fk ON consultation_appointments (doctor_id);
-- rollback DROP INDEX consultation_appointments_doctors__fk;

-- changeset Uladzislau.Lukashevich:30_MED_144_Appointment
CREATE INDEX consultation_appointments_patients__fk ON consultation_appointments (patient_id);
-- rollback DROP INDEX consultation_appointments_patients__fk;

-- changeset Uladzislau.Lukashevich:31_MED_144_Appointment
CREATE INDEX consultation_appointments_locations__fk ON consultation_appointments (location_id);
-- rollback DROP INDEX consultation_appointments_locations__fk;

-- changeset Uladzislau.Lukashevich:32_MED_144_Appointment
CREATE INDEX consultation_appointments_appointment_statuses__fk ON consultation_appointments (appointment_status);
-- rollback DROP INDEX consultation_appointments_appointment_statuses__fk;

-- changeset Katarzyna.Osowska:41_MED_146_Medical_Tests
CREATE INDEX test_appointments_patients__fk ON test_appointments (patient_id);
-- rollback DROP INDEX test_appointments_patients__fk;

-- changeset Katarzyna.Osowska:8_MED_163_user_info_management
CREATE INDEX persons_contact_info__fk ON persons (contact_info_id);
-- rollback DROP INDEX persons_contact_info__fk;

-- changeset Katarzyna.Osowska:9_MED_163_user_info_management
CREATE INDEX patients_address__fk ON patients (address_id);
-- rollback DROP INDEX patients_address__fk;

-- changeset Katarzyna.Osowska:10_MED_163_user_info_management
CREATE INDEX profile_picture_metadata_user__fk ON profile_picture_metadata (user_id);
-- rollback DROP INDEX profile_picture_metadata_user__fk;

-- changeset Anton.Dybko:2_MED_113_onsite_visit_scheduling
CREATE INDEX persons_full_name__fk ON persons USING gin ((name || ' ' || surname) gin_trgm_ops);
-- rollback DROP INDEX doctors_full_name__fk;

-- changeset Anton.Dybko:6_MED_107_Medical_Tests
CREATE INDEX tests_test__fk ON tests USING gin (test gin_trgm_ops);
-- rollback DROP tests_test__fk;

-- changeset Yana.Yeustratsyeva:1_MED_269_Medical_Test_Appointments_Get_Locations
CREATE INDEX test_appointments_test__fk ON test_appointments (test_id);
-- rollback DROP INDEX test_appointments_test__fk;

-- changeset Yana.Yeustratsyeva:2_MED_269_Medical_Test_Appointments_Get_Locations
CREATE INDEX test_appointments_location__fk ON test_appointments (location_id);
-- rollback DROP INDEX test_appointments_location__fk;
