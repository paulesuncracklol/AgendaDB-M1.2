-- Crea tablas para direcciones y relación muchos-a-muchos
CREATE TABLE IF NOT EXISTS Direcciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    direccion VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS Persona_Direccion (
    personalId INT NOT NULL,
    direccionId INT NOT NULL,
    PRIMARY KEY (personalId, direccionId),
    CONSTRAINT fk_pd_persona FOREIGN KEY (personalId) REFERENCES Personas(id) ON DELETE CASCADE,
    CONSTRAINT fk_pd_direccion FOREIGN KEY (direccionId) REFERENCES Direcciones(id) ON DELETE RESTRICT
);

-- Migración opcional: poblar Direcciones con la columna legacy Personas.direccion
INSERT INTO Direcciones (direccion)
SELECT DISTINCT p.direccion FROM Personas p
WHERE p.direccion IS NOT NULL AND p.direccion <> '' 
AND NOT EXISTS (SELECT 1 FROM Direcciones d WHERE d.direccion = p.direccion);

-- Crear vínculos iniciales usando la dirección legacy (uno a uno por ahora)
INSERT IGNORE INTO Persona_Direccion (personalId, direccionId)
SELECT p.id, d.id
FROM Personas p
JOIN Direcciones d ON d.direccion = p.direccion;
