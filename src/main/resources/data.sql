INSERT INTO users (username, password, enabled, email) VALUES
    ('monteur1', '$2a$10$4ICWRk6bEAD7db2NdbdI6ejZ69h0xhdEM2EXquCViEZL409fIrnze', true, 'monteur1@example.com'); -- password is "Monteur"

INSERT INTO authorities (username, authority) VALUES
    ('monteur1', 'ROLE_MONTEUR');
