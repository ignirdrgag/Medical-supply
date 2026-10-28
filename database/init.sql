-- ---------------------------------------------------------------------------
-- MediSupply - schema MySQL et donnees de demonstration
-- Executer avec : mysql -u root -p < database/init.sql
-- ATTENTION : ce script supprime puis recree les tables de la base medisupply.
-- ---------------------------------------------------------------------------

CREATE DATABASE IF NOT EXISTS medisupply
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE medisupply;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS stock_movements;
DROP TABLE IF EXISTS purchase_order_lines;
DROP TABLE IF EXISTS purchase_orders;
DROP TABLE IF EXISTS lots;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS suppliers;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE suppliers (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(120) NOT NULL,
    contact    VARCHAR(120) NOT NULL,
    email      VARCHAR(160) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB;

CREATE TABLE items (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(150) NOT NULL,
    category      VARCHAR(100) NOT NULL,
    unit          VARCHAR(50) NOT NULL,
    minimum_stock INT NOT NULL DEFAULT 0,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_items_minimum CHECK (minimum_stock >= 0)
) ENGINE = InnoDB;

CREATE TABLE users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(60) NOT NULL UNIQUE,
    password    VARCHAR(120) NOT NULL,
    full_name   VARCHAR(120) NOT NULL,
    role        ENUM('ADMIN', 'STAFF', 'MANAGER', 'DOCTOR', 'SUPPLIER') NOT NULL,
    supplier_id INT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_supplier FOREIGN KEY (supplier_id)
        REFERENCES suppliers (id) ON DELETE SET NULL
) ENGINE = InnoDB;

CREATE TABLE lots (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    item_id         INT NOT NULL,
    supplier_id     INT NOT NULL,
    quantity        INT NOT NULL,
    expiration_date DATE NOT NULL,
    received_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_lots_quantity CHECK (quantity > 0),
    CONSTRAINT fk_lots_item FOREIGN KEY (item_id)
        REFERENCES items (id) ON DELETE CASCADE,
    CONSTRAINT fk_lots_supplier FOREIGN KEY (supplier_id)
        REFERENCES suppliers (id)
) ENGINE = InnoDB;

CREATE TABLE purchase_orders (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    supplier_id INT NOT NULL,
    order_date  DATE NOT NULL,
    status      ENUM('ORDERED', 'RECEIVED') NOT NULL DEFAULT 'ORDERED',
    created_by  INT NULL,
    CONSTRAINT fk_orders_supplier FOREIGN KEY (supplier_id)
        REFERENCES suppliers (id),
    CONSTRAINT fk_orders_user FOREIGN KEY (created_by)
        REFERENCES users (id) ON DELETE SET NULL
) ENGINE = InnoDB;

CREATE TABLE purchase_order_lines (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    item_id  INT NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT chk_order_lines_quantity CHECK (quantity > 0),
    CONSTRAINT fk_lines_order FOREIGN KEY (order_id)
        REFERENCES purchase_orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_lines_item FOREIGN KEY (item_id)
        REFERENCES items (id)
) ENGINE = InnoDB;

CREATE TABLE stock_movements (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    item_id     INT NOT NULL,
    quantity    INT NOT NULL,
    destination VARCHAR(160) NOT NULL,
    moved_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id     INT NULL,
    CONSTRAINT chk_movements_quantity CHECK (quantity > 0),
    CONSTRAINT fk_movements_item FOREIGN KEY (item_id)
        REFERENCES items (id),
    CONSTRAINT fk_movements_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE SET NULL
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------------
-- Donnees de demonstration
-- Les mots de passe sont en clair : connexion de demonstration uniquement.
-- ---------------------------------------------------------------------------

INSERT INTO suppliers (id, name, contact, email) VALUES
    (1, 'MediSante Distribution', '01 42 00 00 00', 'commandes@medisante.example'),
    (2, 'PharmaLog', '01 55 12 34 56', 'contact@pharmalog.example'),
    (3, 'BioReactifs France', '04 78 90 12 34', 'ventes@bioreactifs.example');

INSERT INTO users (id, username, password, full_name, role, supplier_id) VALUES
    (1, 'admin',    'admin123',    'Administrateur',        'ADMIN',    NULL),
    (2, 'staff',    'staff123',    'Agent de stock',        'STAFF',    NULL),
    (3, 'manager',  'manager123',  'Responsable achats',    'MANAGER',  NULL),
    (4, 'supplier', 'supplier123', 'Fournisseur MediSante', 'SUPPLIER', 1),
    (5, 'pharmalog','supplier123', 'Fournisseur PharmaLog', 'SUPPLIER', 2),
    (6, 'doctor',   'doctor123',   'Medecin',                'DOCTOR',   NULL);

INSERT INTO items (id, name, category, unit, minimum_stock) VALUES
    (1, 'Gants nitrile', 'EPI', 'boites', 20),
    (2, 'Masques chirurgicaux', 'EPI', 'boites', 15),
    (3, 'Reactif de diagnostic', 'Reactifs', 'flacons', 10),
    (4, 'Seringues 5 ml', 'Consommables', 'boites', 25),
    (5, 'Compresses steriles', 'Pansements', 'sachets', 30);

INSERT INTO lots (item_id, supplier_id, quantity, expiration_date, received_at) VALUES
    (1, 1, 45, DATE_ADD(CURDATE(), INTERVAL 18 MONTH), DATE_SUB(NOW(), INTERVAL 15 DAY)),
    (2, 1, 12, DATE_ADD(CURDATE(), INTERVAL 8 MONTH),  DATE_SUB(NOW(), INTERVAL 10 DAY)),
    (3, 3, 8,  DATE_ADD(CURDATE(), INTERVAL 20 DAY),   DATE_SUB(NOW(), INTERVAL 5 DAY)),
    (4, 2, 60, DATE_ADD(CURDATE(), INTERVAL 24 MONTH), DATE_SUB(NOW(), INTERVAL 40 DAY)),
    (5, 2, 20, DATE_ADD(CURDATE(), INTERVAL 10 DAY),   DATE_SUB(NOW(), INTERVAL 3 DAY));

INSERT INTO purchase_orders (id, supplier_id, order_date, status, created_by) VALUES
    (1, 1, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'ORDERED',  3),
    (2, 2, DATE_SUB(CURDATE(), INTERVAL 20 DAY), 'RECEIVED', 3),
    (3, 3, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'ORDERED',  3);

INSERT INTO purchase_order_lines (order_id, item_id, quantity) VALUES
    (1, 2, 30),
    (2, 4, 100),
    (3, 3, 20);

INSERT INTO stock_movements (item_id, quantity, destination, moved_at, user_id) VALUES
    (3, 2, 'Urgences', DATE_SUB(NOW(), INTERVAL 2 DAY), 2),
    (5, 5, 'Bloc operatoire', DATE_SUB(NOW(), INTERVAL 1 DAY), 2),
    (1, 10, 'Consultations', DATE_SUB(NOW(), INTERVAL 6 HOUR), 2);
