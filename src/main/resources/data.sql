-- 🔹 Users
INSERT INTO users (username, password, enabled, email) VALUES
                                                           ('monteur1', '$2a$10$ZSwUaC8niKMocp7a905u0.mOChOYvgoUmYlQ0Y4k8TcMiwzlbnJ8W', true, 'monteur1@example.com'),
                                                           ('medewerker1', '$2a$10$/HAlRCERguEDw0FwpoPhbe8ptj7xJuYD.k1UN7piwGzjLaYaxcGSe', true, 'medewerker1@example.com'),
                                                           ('klant1', '$2a$10$K4r9Q8GLloQmzmweFFApV.7K.xWYLoEIdrCIJqyKHtaIqr49N0ZXO', true, 'klant1@example.com'),
                                                           ('klant2', '$2a$10$57b4yTb9WDwr/Sa8azosiOBiddn4afScJBl27Cflfq.qdH4fiKkkG', true, 'klant2@example.com'),

                                                           -- 3 testrollen om zelf een Password voor te maken
                                                           ('medewerkertest', 'PasswordEncoder-password-here', true, 'medewerkertest@example.com'),
                                                           ('monteurtest', 'PasswordEncoder-password-here', true, 'monteurtest@example.com'),
                                                           ('klanttest', 'PasswordEncoder-password-here', true, 'klanttest@example.com');


-- 🔹 Authorities
INSERT INTO authorities (username, authority) VALUES
                                                  ('monteur1', 'ROLE_MONTEUR'),
                                                  ('medewerker1', 'ROLE_MEDEWERKER'),
                                                  ('klant1', 'ROLE_KLANT'),
                                                  ('klant2', 'ROLE_KLANT'),

                                                  -- De 3 testrollen hun Roles
                                                    ('medewerkertest', 'ROLE_MEDEWERKER'),
                                                  ('monteurtest', 'ROLE_MONTEUR'),
                                                  ('klanttest', 'ROLE_KLANT');


-- 🔹 Parts
INSERT INTO part (id, name, price, stock) VALUES
                                              (1, 'Engine', 1500.0, 5),
                                              (2, 'Brake Pads', 250.0, 10),
                                              (3, 'Tires', 100.0, 20),
                                              (4, 'Battery', 200.0, 8),
                                              (5, 'Oil Filter', 50.0, 15),
                                              (6, 'Brake Fluid', 30.0, 12),
                                              (7, 'Engine Oil', 60.0, 10);

-- 🔹 Repair Types
INSERT INTO repair_type (id, name, cost, description) VALUES
                                                          (1, 'Engine Repair', 700.0, 'Full engine diagnostics and repair'),
                                                          (2, 'Brake Replacement', 300.0, 'Replace worn-out brake pads and discs'),
                                                          (3, 'Tire Change', 100.0, 'Replace all 4 tires'),
                                                          (4, 'Battery Replacement', 250.0, 'Replace and test car battery'),
                                                          (5, 'Oil Change', 80.0, 'Drain and replace engine oil and filter'),
                                                          (6, 'Full Service Maintenance', 1200.0, 'Complete vehicle maintenance');

-- 🔹 Cars
INSERT INTO car (id, car_type, repair_request_date, owner_username, total_repair_cost) VALUES
                                                                                           (1, 'Sedan', '01-01-2025', 'klant1', 2200.0), -- (RepairType: 700 + Engine: 1500)
                                                                                           (2, 'Nissan', '15-01-2025', 'klant2', 0.0);

-- 🔹 Repairs
INSERT INTO repair (id, car_id, repair_type_id, repair_request_date, repair_date, total_repair_cost)
VALUES (1, 1, 1, '01-01-2025', '25-01-2025', 2200.0); -- (RepairType: 700 + Engine: 1500)

-- 🔹 Repair-Parts Relation (Many-to-Many)
INSERT INTO repair_parts (repair_id, parts_id) VALUES
                                                   (1, 1),  -- Engine (1500.0)
                                                   (1, 2);  -- Brake Pads (250.0)

-- ID sequences reset
SELECT setval('repair_type_id_seq', (SELECT MAX(id) FROM repair_type));
SELECT setval('car_id_seq', (SELECT MAX(id) FROM car));
SELECT setval('repair_id_seq', (SELECT MAX(id) FROM repair));
SELECT setval('part_id_seq', (SELECT MAX(id) FROM part));
SELECT setval('pdf_attachment_id_seq', (SELECT MAX(id) FROM pdf_attachment));


