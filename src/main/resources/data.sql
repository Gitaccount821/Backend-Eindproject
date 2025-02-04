
INSERT INTO users (username, password, enabled, email) VALUES
    ('monteur1', '$2a$10$4ICWRk6bEAD7db2NdbdI6ejZ69h0xhdEM2EXquCViEZL409fIrnze', true, 'monteur1@example.com'); -- Password: "Monteur"
INSERT INTO authorities (username, authority) VALUES
    ('monteur1', 'ROLE_MONTEUR');


INSERT INTO users (username, password, enabled, email) VALUES
    ('klant1', '$2a$10$crKcube2z0zTtnZ1PNGQx.C4N9/ej2vQgLr6LCIc0BXsO18CtPdDO', true, 'klant1@example.com'); -- Password: "Klant"
INSERT INTO authorities (username, authority) VALUES
    ('klant1', 'ROLE_KLANT');


INSERT INTO users (username, password, enabled, email) VALUES
    ('medewerker1', '$2a$10$1On.tCPNIv3NPCzzroSSBeqgTu5GGNlkW.HXCBpMQjips3NPGcICu', true, 'medewerker1@example.com'); -- Password: "Medewerker"
INSERT INTO authorities (username, authority) VALUES
    ('medewerker1', 'ROLE_MEDEWERKER');

