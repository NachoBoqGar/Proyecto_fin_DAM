CREATE TABLE IF NOT EXISTS `bd_proxectofct`.`roles` (
  `idrol` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idrol`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bd_proxectofct`.`empleados`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_proxectofct`.`empleados` (
  `idempleado` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  `apellido1` VARCHAR(45) NOT NULL,
  `apellido2` VARCHAR(45) NOT NULL,
  `correo` VARCHAR(45) NULL,
  `fecha_alta` DATE NOT NULL,
  `dni` VARCHAR(20) NOT NULL,
  `pass` VARCHAR(20) NOT NULL,
  `telefono` VARCHAR(45) NULL,
  `direccion` VARCHAR(150) NULL,
  `idrol` INT NOT NULL,
  `activo` TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`idempleado`),
  UNIQUE INDEX `dni_UNIQUE` (`dni` ASC) VISIBLE,
  INDEX `FK_rol_empleado_idx` (`idrol` ASC) VISIBLE,
  CONSTRAINT `FK_rol_empleado`
    FOREIGN KEY (`idrol`)
    REFERENCES `bd_proxectofct`.`roles` (`idrol`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bd_proxectofct`.`clientes`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_proxectofct`.`clientes` (
  `idcliente` INT NOT NULL AUTO_INCREMENT,
  `dni` VARCHAR(20) NOT NULL,
  `correo` VARCHAR(45) NULL,
  `nombre` VARCHAR(45) NOT NULL,
  `apellido1` VARCHAR(45) NOT NULL,
  `apellido2` VARCHAR(45) NOT NULL,
  `telefono` VARCHAR(45) NULL,
  `direccion` VARCHAR(150) NULL,
  `fecha_alta` DATE NULL,
  `activo` TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`idcliente`),
  UNIQUE INDEX `idcliente_UNIQUE` (`idcliente` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bd_proxectofct`.`estados`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_proxectofct`.`estados` (
  `idestado` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idestado`),
  UNIQUE INDEX `nombre_UNIQUE` (`nombre` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `bd_proxectofct`.`citas`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `bd_proxectofct`.`citas` (
  `idcita` INT NOT NULL,
  `comentario` VARCHAR(300) NULL,
  `fecha` DATETIME NOT NULL,
  `idcliente` INT NOT NULL,
  `idempleado` INT NOT NULL,
  `idestado` INT NOT NULL,
  PRIMARY KEY (`idcita`),
  UNIQUE INDEX `idcita_UNIQUE` (`idcita` ASC) VISIBLE,
  INDEX `FK_cita_cliente_idx` (`idcliente` ASC) VISIBLE,
  INDEX `FK_cita_empleado_idx` (`idempleado` ASC) VISIBLE,
  INDEX `FK_cita_estado_idx` (`idestado` ASC) VISIBLE,
  CONSTRAINT `FK_cita_cliente`
    FOREIGN KEY (`idcliente`)
    REFERENCES `bd_proxectofct`.`clientes` (`idcliente`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `FK_cita_empleado`
    FOREIGN KEY (`idempleado`)
    REFERENCES `bd_proxectofct`.`empleados` (`idempleado`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `FK_cita_estado`
    FOREIGN KEY (`idestado`)
    REFERENCES `bd_proxectofct`.`estados` (`idestado`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

-- Insertar los valores admin y usuario en la tabla roles
INSERT INTO bd_proxectofct.roles (nombre) VALUES
  ('ADMIN'),
  ('USUARIO');

-- Insertar los valores tabal estados
INSERT INTO bd_proxectofct.estados (nombre) VALUES
  ('Pendiente'),
  ('Cancelada'),
  ('No Presentado'),
  ('Finalizada');

-- Insertar los valores tabla empleados iniciales
INSERT INTO bd_proxectofct.empleados (nombre, apellido1, apellido2, fecha_alta, dni, pass, idrol, activo)
VALUES ('admin', 'admin', 'admin', CURRENT_DATE(), 'admin', 'admin', 1, 1);
INSERT INTO bd_proxectofct.empleados (nombre, apellido1, apellido2, fecha_alta, dni, pass, idrol, activo)
VALUES ('usuario', 'usuario', 'usuario', CURRENT_DATE(), 'usuario', 'usuario', 2, 1);