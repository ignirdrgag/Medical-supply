-- Ajoute le role Medecin a une base MediSupply deja installee.
USE medisupply;

ALTER TABLE users
    MODIFY role ENUM('ADMIN', 'STAFF', 'MANAGER', 'DOCTOR', 'SUPPLIER') NOT NULL;

INSERT IGNORE INTO users (username, password, full_name, role, supplier_id)
VALUES ('doctor', 'doctor123', 'Medecin', 'DOCTOR', NULL)
