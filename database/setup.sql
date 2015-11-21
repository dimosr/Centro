CREATE DATABASE centro;
CREATE USER 'centro-usr'@'localhost' IDENTIFIED BY 'centro-passwd';
GRANT ALL ON centro.* to 'centro-usr'@localhost;