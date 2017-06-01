CREATE TABLE location (
  id BIGINT NOT NULL AUTO_INCREMENT,
  location_name VARCHAR(255) NOT NULL,
  PRIMARY KEY(id)
);

CREATE TABLE artist (
  id BIGINT NOT NULL AUTO_INCREMENT,
  genre VARCHAR(255),
  name VARCHAR(255),
    PRIMARY KEY(id)
);

CREATE TABLE concert (
  id BIGINT NOT NULL AUTO_INCREMENT,
  artist_id BIGINT NOT NULL,
  genre VARCHAR(255) NOT NULL,
  location_id BIGINT NOT NULL,
  PRIMARY KEY(id),
  FOREIGN KEY (artist_id) REFERENCES artist(id),
  FOREIGN KEY (location_id) REFERENCES location(id)
);

DELETE FROM ticket;

ALTER TABLE ticket
ADD COLUMN concert_id BIGINT NOT NULL,
DROP PRIMARY KEY,
DROP COLUMN id,
DROP COLUMN artist,
DROP COLUMN genre,
DROP COLUMN location,
ADD PRIMARY KEY (concert_id, account_id),
ADD FOREIGN KEY (concert_id) REFERENCES concert(id);
