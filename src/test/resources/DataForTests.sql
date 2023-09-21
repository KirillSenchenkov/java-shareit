insert into users
    (name, email)
values ('Maikoo', 'Maikoo@nmicrk.ru'),
       ('Heikoo', 'Heikoo@nmicrk.ru'),
       ('Kirill', 'Kirill@nmicrk.ru'),
       ('Mariya', 'Mariya@nmicrk.ru'),
       ('Daniel', 'Daniel@nmicrk.ru'),
       ('Victoria', 'Victoria@nmicrk.ru');

insert into items
    (name, description, is_available, owner_id)
values ('сумка', 'кожанная сумка', TRUE, 5),
       ('пистолет', 'травматическое оружие', TRUE, 4),
       ('дрель', 'электрическая дрель', TRUE, 1),
       ('поводок', 'для малых пород собак', TRUE, 3),
       ('велосипед', 'трехколесный детский', TRUE, 3),
       ('очиститель воздуха', 'бытовой', TRUE, 2),
       ('игрушечная машинка', 'на пульте управления', TRUE, 1),
       ('чайник', 'электрический чайник', TRUE, 1),
       ('кран', 'шаровый кран', TRUE, 1),
       ('газонокосилка', 'бензиновая', TRUE, 6);

insert into bookings
    (start_date, end_date, item_id, booker_id, status)
values ('2023-03-05 12:00:00.00', '2023-10-05 12:00:00.00', 1, 3, 'WAITING'),
       ('2023-11-05 12:00:00.00', '2023-12-05 12:00:00.00', 1, 3, 'WAITING'),
       ('2023-09-05 12:00:00.00', '2023-09-25 12:00:00.00', 5, 2, 'WAITING'),
       ('2023-07-05 12:00:00.00', '2023-08-05 12:00:00.00', 5, 2, 'REJECTED'),
       ('2023-04-05 12:00:00.00', '2023-05-05 12:00:00.00', 2, 1, 'APPROVED'),
       ('2023-06-05 12:00:00.00', '2023-07-05 12:00:00.00', 2, 6, 'APPROVED'),
       ('2023-07-05 12:00:00.00', '2023-08-05 12:00:00.00', 9, 2, 'APPROVED'),
       ('2023-08-05 12:00:00.00', '2023-09-05 12:00:00.00', 2, 3, 'WAITING'),
       ('2023-09-05 12:00:00.00', '2023-09-15 12:00:00.00', 9, 5, 'WAITING'),
       ('2023-10-05 12:00:00.00', '2023-10-25 12:00:00.00', 7, 5, 'WAITING'),
       ('2023-07-05 12:00:00.00', '2023-08-05 12:00:00.00', 7, 6, 'APPROVED'),
       ('2023-09-05 12:00:00.00', '2023-11-05 12:00:00.00', 6, 1, 'WAITING'),
       ('2023-07-05 12:00:00.00', '2023-08-05 12:00:00.00', 4, 5, 'CANCELED');

insert into requests
    (description, requester_id, created_date)
values ('нужен спальный мешок', 6, '2023-10-25 14:30:00.00');

insert into comments
    (text, item_id, author_id, created_date)
values ('работает исправно', 2, 1, '2023-08-05 12:00:00.00'),
       ('качественная очистка', 9, 2, '2023-08-05 12:00:00.00'),
       ('удобный бак для травы', 7, 6, '2023-08-05 12:00:00.00');
