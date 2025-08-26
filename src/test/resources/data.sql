-- src/test/resources/data.sql (ou src/main/resources/data.sql)
-- Utiliser AUTO_INCREMENT pour H2 au lieu des séquences

-- Suppression des tables dans l'ordre correct
DROP TABLE IF EXISTS productions;
DROP TABLE IF EXISTS plantations;
DROP TABLE IF EXISTS kit_products;
DROP TABLE IF EXISTS kits;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS planters;
DROP TABLE IF EXISTS supervisors;
DROP TABLE IF EXISTS app_params;

-- Création des tables avec AUTO_INCREMENT
CREATE TABLE products (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) UNIQUE,
                          description VARCHAR(255),
                          price DECIMAL(10,2)
);

CREATE TABLE kits (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(255) NOT NULL UNIQUE,
                      total_cost DECIMAL(10,2),
                      description VARCHAR(255)
);

CREATE TABLE kit_products (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              product_id BIGINT,
                              quantity INT,
                              total_cost DECIMAL(10,2),
                              kit BIGINT,
                              FOREIGN KEY (product_id) REFERENCES products(id),
                              FOREIGN KEY (kit) REFERENCES kits(id)
);

CREATE TABLE supervisors (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             firstname VARCHAR(255),
                             lastname VARCHAR(255),
                             email VARCHAR(255),
                             password VARCHAR(255)
);

CREATE TABLE planters (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          lastname VARCHAR(255),
                          firstname VARCHAR(255),
                          birthday DATE,
                          gender VARCHAR(10),
                          marital_status VARCHAR(20),
                          children_number INT,
                          village VARCHAR(255),
                          supervisor_id BIGINT,
                          FOREIGN KEY (supervisor_id) REFERENCES supervisors(id)
);

CREATE TABLE plantations (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             farmed_area DECIMAL(10,2),
                             gps_location_latitude DECIMAL(10,6),
                             gps_location_longitude DECIMAL(10,6),
                             planter_id BIGINT,
                             kit_id BIGINT,
                             FOREIGN KEY (planter_id) REFERENCES planters(id),
                             FOREIGN KEY (kit_id) REFERENCES kits(id)
);

CREATE TABLE productions (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             prod_in_kg DECIMAL(10,2),
                             purchase_price DECIMAL(10,2),
                             must_be_paid BOOLEAN,
                             plantation_id BIGINT,
                             FOREIGN KEY (plantation_id) REFERENCES plantations(id)
);

CREATE TABLE app_params (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(255),
                            description VARCHAR(255),
                            params_value VARCHAR(255),
                            codeParams VARCHAR(50),
                            encrypted BOOLEAN
);

-- Insertion des données de test
INSERT INTO products (name, description, price) VALUES
                                                    ('Engrais NPK', 'Engrais complet 15-15-15', 15000),
                                                    ('Semences de maïs', 'Variété précoce', 5000),
                                                    ('Pesticide bio', 'Pesticide à base de neem', 12000),
                                                    ('Irrigation goutte-à-goutte', 'Kit de base 100m', 25000);

INSERT INTO kits (name, total_cost, description) VALUES
                                                     ('Kit Céréales', 35000, 'Kit pour culture de céréales'),
                                                     ('Kit Maraîchage', 42000, 'Kit pour cultures maraîchères'),
                                                     ('Kit Bio', 38000, 'Kit agriculture biologique'),
                                                     ('Kit Bambou', 38000, 'Kit agriculture pour bambous');

INSERT INTO kit_products (product_id, quantity, total_cost, kit) VALUES
                                                                     (1, 2, 30000, 1),
                                                                     (2, 5, 25000, 1),
                                                                     (3, 1, 12000, 2),
                                                                     (4, 1, 25000, 2),
                                                                     (1, 1, 15000, 3),
                                                                     (3, 2, 24000, 3);

INSERT INTO supervisors (firstname, lastname, email, password) VALUES
                                                                   ('Jean', 'Dupont', 'jean@test.com', 'password'),
                                                                   ('Marie', 'Martin', 'marie@test.com', 'password'),
                                                                   ('Pierre', 'Durand', 'pierre@test.com', 'password');

INSERT INTO planters (lastname, firstname, birthday, gender, marital_status, children_number, village, supervisor_id) VALUES
                                                                                                                          ('Traoré', 'Amadou', '1980-05-15', '0', '0', 3, 'Sokoura', 1),
                                                                                                                          ('Koné', 'Aminata', '1975-11-22', '1', '2', 2, 'Nambeguela', 2),
                                                                                                                          ('Diakité', 'Moussa', '1990-03-10', '0', '3', 0, 'Farakala', 1),
                                                                                                                          ('Sissoko', 'Fatoumata', '1985-07-30', '1', '0', 4, 'Sokoura', 3);

INSERT INTO plantations (farmed_area, gps_location_latitude, gps_location_longitude, planter_id, kit_id) VALUES
                                                                                                             (2.5, 11.3167, -5.6667, 1, 1),
                                                                                                             (1.8, 11.3180, -5.6680, 2, 2),
                                                                                                             (3.2, 11.3200, -5.6700, 3, 3),
                                                                                                             (0.5, 11.3170, -5.6670, 4, 1);

INSERT INTO productions (prod_in_kg, purchase_price, must_be_paid, plantation_id) VALUES
                                                                                      (1500, 500, TRUE, 1),
                                                                                      (1200, 450, TRUE, 1),
                                                                                      (800, 550, FALSE, 2),
                                                                                      (2000, 600, TRUE, 3),
                                                                                      (300, 700, FALSE, 4);