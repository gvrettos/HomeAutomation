-- person
INSERT INTO person(id, name, surname, email, password, role)
VALUES (101, 'AdminName', 'AdminSurname', 'testadmin@foo.com', '***', 'ADMIN');

INSERT INTO person(id, name, surname, email, password, role)
VALUES (102, 'User1', 'UserSurname', 'testuser1@foo.com', '***', 'USER');

INSERT INTO person(id, name, surname, email, password, role)
VALUES (103, 'User2', 'UserSurname', 'testuser2@foo.com', '***', 'USER');

-- room
INSERT INTO room(id, name)
VALUES (1, 'Living Room');

INSERT INTO room(id, name)
VALUES (2, 'Kitchen');

INSERT INTO room(id, name)
VALUES (3, 'Bedroom');

-- device_type
INSERT INTO device_type(id, type, information_type)
VALUES (1, 'Thermostat', 'Target temp.');

INSERT INTO device_type(id, type, information_type)
VALUES (2, 'Lights', 'Illumination');

INSERT INTO device_type(id, type, information_type)
VALUES (3, 'Door Lock', 'Lock Status');

-- device
INSERT INTO device(id, name, status, information_value, room_id, device_type_id)
VALUES (1, 'Air Condition #1', 1, 23, 1, 1);

INSERT INTO device(id, name, status, information_value, room_id, device_type_id)
VALUES (2, 'Lighting #1', 1, 50, 1, 2);

INSERT INTO device(id, name, status, information_value, room_id, device_type_id)
VALUES (3, 'Door #1', 1, null, 1, 3);

INSERT INTO device(id, name, status, information_value, room_id, device_type_id)
VALUES (4, 'Lighting #2', 0, 100, 2, 2);

INSERT INTO device(id, name, status, information_value, room_id, device_type_id)
VALUES (5, 'Door #2', 1, null, 2, 3);

INSERT INTO device(id, name, status, information_value, room_id, device_type_id)
VALUES (6, 'Air Condition #2', 1, 25, 3, 1);

INSERT INTO device(id, name, status, information_value, room_id, device_type_id)
VALUES (7, 'Lighting #3', 0, 0, 3, 2);

-- person_device
INSERT INTO person_device(person_id, device_id)
VALUES (101, 1);

INSERT INTO person_device(person_id, device_id)
VALUES (101, 2);

INSERT INTO person_device(person_id, device_id)
VALUES (101, 3);

INSERT INTO person_device(person_id, device_id)
VALUES (101, 4);

INSERT INTO person_device(person_id, device_id)
VALUES (101, 5);

INSERT INTO person_device(person_id, device_id)
VALUES (101, 6);

INSERT INTO person_device(person_id, device_id)
VALUES (101, 7);

INSERT INTO person_device(person_id, device_id)
VALUES (102, 2);

INSERT INTO person_device(person_id, device_id)
VALUES (102, 4);

INSERT INTO person_device(person_id, device_id)
VALUES (102, 7);

INSERT INTO person_device(person_id, device_id)
VALUES (103, 1);

INSERT INTO person_device(person_id, device_id)
VALUES (103, 2);

INSERT INTO person_device(person_id, device_id)
VALUES (103, 4);

INSERT INTO person_device(person_id, device_id)
VALUES (103, 6);

INSERT INTO person_device(person_id, device_id)
VALUES (103, 7);
