-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema home_automation
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `home_automation` ;

-- -----------------------------------------------------
-- Schema home_automation
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `home_automation` DEFAULT CHARACTER SET utf8 ;
USE `home_automation` ;

-- -----------------------------------------------------
-- Table `home_automation`.`Person`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `home_automation`.`Person` ;

CREATE TABLE IF NOT EXISTS `home_automation`.`Person` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL,
  `surname` VARCHAR(45) NULL,
  `email` VARCHAR(45) NULL,
  `password` VARCHAR(45) NULL,
  `role` VARCHAR(45) NULL COMMENT 'Initially, this will be either ADMIN or USER.',
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `home_automation`.`Room`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `home_automation`.`Room` ;

CREATE TABLE IF NOT EXISTS `home_automation`.`Room` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `home_automation`.`Device_Type`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `home_automation`.`Device_Type` ;

CREATE TABLE IF NOT EXISTS `home_automation`.`Device_Type` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `type` VARCHAR(45) NULL,
  `information_type` VARCHAR(45) NULL COMMENT 'This is the kind of the information we will keep for a device type. For instance, for oven we keep \'temperature\', for lighting the \'illumination percentage\'.',
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `home_automation`.`Device`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `home_automation`.`Device` ;

CREATE TABLE IF NOT EXISTS `home_automation`.`Device` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL,
  `status` CHAR(1) DEFAULT 0 COMMENT 'This will be either ON or OFF.',
  `information_value` VARCHAR(45) NULL COMMENT 'It keeps a value for the specific device related to the type of the device. For instance, for the oven we keep the temperature in Celsius degrees, for lighting we keep the percentage of the illumination, etc.',
  `Room_id` INT NOT NULL,
  `Device_Type_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Device_Room_idx` (`Room_id` ASC),
  INDEX `fk_Device_Device_Type_idx` (`Device_Type_id` ASC),
  CONSTRAINT `fk_Device_Room`
    FOREIGN KEY (`Room_id`)
    REFERENCES `home_automation`.`Room` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Device_Device_Type`
    FOREIGN KEY (`Device_Type_id`)
    REFERENCES `home_automation`.`Device_Type` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `home_automation`.`Person_Device`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `home_automation`.`Person_Device` ;

CREATE TABLE IF NOT EXISTS `home_automation`.`Person_Device` (
  `Person_id` INT NOT NULL,
  `Device_id` INT NOT NULL,
  PRIMARY KEY (`Person_id`, `Device_id`),
  INDEX `fk_Person_Device_Device_idx` (`Device_id` ASC),
  INDEX `fk_Person_Device_Person_idx` (`Person_id` ASC),
  CONSTRAINT `fk_Person_Device_Person`
    FOREIGN KEY (`Person_id`)
    REFERENCES `home_automation`.`Person` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Person_Device_Device`
    FOREIGN KEY (`Device_id`)
    REFERENCES `home_automation`.`Device` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
