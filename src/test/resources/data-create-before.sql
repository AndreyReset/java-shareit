INSERT INTO users(name, email) VALUES
('Василий', 'email@yandex.ru'),
('Тамара', 'tamara@mail.ru');

INSERT INTO items(name, description, is_available, owner_id, request_id) VALUES
('Отвёртка', 'Крестовая отвёртка', true, 1, 0),
('Молоток', 'Очень тяжелый', true, 1, 0),
('Отвёртка', 'Очень ржавая', true, 2, 0);

INSERT INTO bookings(start_date, end_date, item_id, booker_id, status) VALUES
('2022-09-10T17:56:15', '2022-09-11T17:56:15', 1, 2, 'APPROVED'),
('2022-09-10T17:56:15', '2022-09-11T17:56:15', 2, 2, 'WAITING'),
('2022-09-10T17:56:15', '2022-09-11T17:56:15', 3, 1, 'REJECTED'),
('2022-09-25T17:56:15', '2022-09-26T17:56:15', 1, 2, 'APPROVED'),
('2022-09-10T17:56:15', '2022-09-26T17:56:15', 1, 2, 'APPROVED');

INSERT INTO requests(description, requester_id, created) VALUES
('Лопата для огорода', 1, '2022-09-11T17:56:15'),
('Нож для резки мяса', 2, '2022-09-11T17:56:15');