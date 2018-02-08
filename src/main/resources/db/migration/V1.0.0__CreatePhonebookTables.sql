CREATE TABLE IF NOT EXISTS `address` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `number` VARCHAR(128) NOT NULL,
  `street_name` VARCHAR(128) NOT NULL,
  `postcode` VARCHAR(10) NOT NULL,
  `country` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `unique_address_id` (`id` ASC));

CREATE TABLE IF NOT EXISTS `phone_number` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(10) NOT NULL,
  `country_code` INT(2) NOT NULL,
  `area_code` INT(11) NOT NULL,
  `number` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `unique_phone_number_id` (`id` ASC));

CREATE TABLE IF NOT EXISTS `contact` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(6) NOT NULL,
  `first_name` VARCHAR(50) NOT NULL,
  `middle_name` VARCHAR(50) NULL DEFAULT NULL,
  `last_name` VARCHAR(50) NOT NULL,
  `address` BIGINT(20),
  PRIMARY KEY (`id`),
  UNIQUE INDEX `unique_contact_id` (`id` ASC),
  CONSTRAINT `contact_address_ibfk_1`
    FOREIGN KEY (`address`)
    REFERENCES `address` (`id`));

CREATE TABLE IF NOT EXISTS `contact_phone_number` (
  `contact` BIGINT(20) NOT NULL,
  `phone_number` BIGINT(20) NOT NULL,
  PRIMARY KEY (`contact`, `phone_number`),
  UNIQUE INDEX `contact_phone_number` (`contact` ASC, `phone_number` ASC),
  CONSTRAINT `contact_phone_number_ibfk_1`
    FOREIGN KEY (`contact`)
    REFERENCES `contact` (`id`),
  CONSTRAINT `contact_phone_number_ibfk_2`
    FOREIGN KEY (`phone_number`)
    REFERENCES `phone_number` (`id`));