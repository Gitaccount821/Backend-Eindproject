--Users

INSERT INTO users (username, password, enabled, email) VALUES
                                                           ('monteur1', '$2a$10$ZSwUaC8niKMocp7a905u0.mOChOYvgoUmYlQ0Y4k8TcMiwzlbnJ8W', true, 'monteur1@example.com'),
                                                           ('medewerker1', '$2a$10$/HAlRCERguEDw0FwpoPhbe8ptj7xJuYD.k1UN7piwGzjLaYaxcGSe', true, 'medewerker1@example.com'),
                                                           ('klant1', '$2a$10$K4r9Q8GLloQmzmweFFApV.7K.xWYLoEIdrCIJqyKHtaIqr49N0ZXO', true, 'klant1@example.com'),
                                                           ('klant2', '$2a$10$57b4yTb9WDwr/Sa8azosiOBiddn4afScJBl27Cflfq.qdH4fiKkkG', true, 'klant2@example.com');


INSERT INTO authorities (username, authority) VALUES
                                                  ('monteur1', 'ROLE_MONTEUR'),
                                                  ('medewerker1', 'ROLE_MEDEWERKER'),
                                                  ('klant1', 'ROLE_KLANT'),
                                                  ('klant2', 'ROLE_KLANT');

-- Parts

INSERT INTO part (name, price, stock) VALUES
                                           ('Engine', 1500.0, 5),
                                           ('Brake Pads', 250.0, 10),
                                           ('Tires', 100.0, 20),
                                           ('Battery', 200.0, 8),
                                           ('Oil Filter', 50.0, 15),
                                           ('Brake Fluid', 30.0, 12),
                                           ('Engine Oil', 60.0, 10);
-- Repairtypes

INSERT INTO repair_type (name, cost, description) VALUES
                                                       ('Engine Repair', 700.0, 'Full engine diagnostics and repair'),
                                                       ('Brake Replacement', 300.0, 'Replace worn-out brake pads and discs'),
                                                       ('Tire Change', 100.0, 'Replace all 4 tires'),
                                                       ('Battery Replacement', 250.0, 'Replace and test car battery'),
                                                       ('Oil Change', 80.0, 'Drain and replace engine oil and filter'),
                                                       ('Full Service Maintenance', 1200.0, 'Complete vehicle maintenance of Oil and brake fluid change and a new filter');



