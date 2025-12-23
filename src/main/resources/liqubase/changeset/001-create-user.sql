CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       birth_date DATE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO users (name, email, birth_date) VALUES
                                                ('Иван Иванов', 'ivan@example.com', '1990-05-15'),
                                                ('Мария Петрова', 'maria@example.com', '1985-11-23'),
                                                ('Алексей Сидоров', 'alex@example.com', '1995-03-08'),
                                                ('Елена Смирнова', 'elena@example.com', '1992-09-30'),
                                                ('Дмитрий Козлов', 'dmitry@example.com', '1988-07-12'),
                                                ('Ольга Новикова', 'olga@example.com', '1993-12-05'),
                                                ('Сергей Волков', 'sergey@example.com', '1980-02-28'),
                                                ('Анна Морозова', 'anna@example.com', '1998-06-19'),
                                                ('Павел Зайцев', 'pavel@example.com', '1991-10-11'),
                                                ('Татьяна Лебедева', 'tatyana@example.com', '1987-04-25')
    ON CONFLICT (email) DO NOTHING;