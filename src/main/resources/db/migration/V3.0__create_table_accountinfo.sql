
CREATE TABLE account_info(
  id BIGINT NOT NULL AUTO_INCREMENT,
  street VARCHAR(255) NOT NULL,
  telephone_number VARCHAR(16) NOT NULL,

  PRIMARY KEY(id)
);

ALTER TABLE account ADD info_id BIGINT NOT NULL;
ALTER TABLE account ADD CONSTRAINT info_id FOREIGN KEY (info_id) REFERENCES account_info(id);