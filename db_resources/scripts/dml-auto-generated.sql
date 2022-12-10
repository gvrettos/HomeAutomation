-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: home_automation
-- ------------------------------------------------------
-- Server version	5.7.21

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `device`
--

LOCK TABLES `device` WRITE;
/*!40000 ALTER TABLE `device` DISABLE KEYS */;
INSERT INTO `device` VALUES (17,'Game play station',1,'3',11,35),(18,'Watering system',1,'70',13,34),(19,'Air Condition #1',1,'23',5,30),(20,'Lighting #1',0,'50',5,31),(32,'Air Condition #2',0,NULL,5,30),(33,'Lighting #2',1,'10',5,31),(34,'Air Condition #3',0,NULL,5,30),(35,'Lighting #3',0,'40',5,31),(36,'Air Condition #4',0,NULL,5,30),(37,'Lighting #4',0,'20',5,31),(38,'otinanai',0,NULL,14,65);
/*!40000 ALTER TABLE `device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `device_type`
--

LOCK TABLES `device_type` WRITE;
/*!40000 ALTER TABLE `device_type` DISABLE KEYS */;
INSERT INTO `device_type` VALUES (30,'Thermostat','Target temp.','°C',10,35,'plusminus','fa-thermometer-three-quarters','bg-warning'),(31,'Lights','Illumination','%',0,100,'plusminus','fa-lightbulb','bg-primary'),(32,'Door Lock','Lock Status',NULL,NULL,NULL,NULL,'fa-lock','bg-danger'),(33,'Fan','Speed','rpm',1000,4000,'plusminus','fa-gear','bg-info'),(34,'Lawn Sprinkler','Humidity','%',0,100,NULL,'fa-building','bg-success'),(35,'Smart TV','Channel',NULL,1,100,'plusminus','fa-tv','bg-secondary'),(53,'air_condition','temperature',NULL,NULL,NULL,NULL,NULL,NULL),(54,'laundry_machine','laundry_state',NULL,NULL,NULL,NULL,NULL,NULL),(55,'lighting','illumination_percentage',NULL,NULL,NULL,NULL,NULL,NULL),(56,'air_condition','temperature',NULL,NULL,NULL,NULL,NULL,NULL),(57,'laundry_machine','laundry_state',NULL,NULL,NULL,NULL,NULL,NULL),(58,'lighting','illumination_percentage',NULL,NULL,NULL,NULL,NULL,NULL),(59,'air_condition','temperature',NULL,NULL,NULL,NULL,NULL,NULL),(60,'laundry_machine','laundry_state',NULL,NULL,NULL,NULL,NULL,NULL),(61,'lighting','illumination_percentage',NULL,NULL,NULL,NULL,NULL,NULL),(65,'!!!!!','^%^%^%^HGFGF',NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `device_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `person`
--

LOCK TABLES `person` WRITE;
/*!40000 ALTER TABLE `person` DISABLE KEYS */;
INSERT INTO `person` VALUES (71,'user1','surname','user1@foo.com','$2a$10$YJv4k/jJ5eUaJ3kHkWO9jOEY/6WQNaR/HY81lHqOIzIh8MveBisN2','USER'),(72,'user2','surname','user2@foo.com','$2a$10$YJv4k/jJ5eUaJ3kHkWO9jOEY/6WQNaR/HY81lHqOIzIh8MveBisN2','USER'),(73,'admin','surname','admin@foo.com','$2a$10$n2EvHIrPI5EpG6aNMEjYW.Xum6pciEcKZNBqi8vOgg4jTPPaimbYm','ADMIN');
/*!40000 ALTER TABLE `person` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `person_device`
--

LOCK TABLES `person_device` WRITE;
/*!40000 ALTER TABLE `person_device` DISABLE KEYS */;
INSERT INTO `person_device` VALUES (71,17),(71,18),(71,19),(71,20),(71,33),(71,35),(71,37);
/*!40000 ALTER TABLE `person_device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
INSERT INTO `room` VALUES (5,'Dining Room'),(6,'Kitchen'),(7,'Bedroom'),(8,'Bathroom'),(9,'Living Room'),(11,'Play Room'),(13,'Garden'),(14,'foo_room1');
/*!40000 ALTER TABLE `room` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-12-10 18:19:37
