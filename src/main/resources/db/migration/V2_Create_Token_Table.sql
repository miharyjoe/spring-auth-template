CREATE TABLE IF NOT EXISTS token (
          id SERIAL PRIMARY KEY,
          token VARCHAR(255),
          created_date TIMESTAMP
  );