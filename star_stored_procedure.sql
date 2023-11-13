USE moviedb;

DELIMITER $$

CREATE PROCEDURE InsertStar(
    IN starName VARCHAR(255),
    IN starBirthYear INT,
    OUT starId VARCHAR(10)
)
BEGIN
    DECLARE newStarNum INT;
    DECLARE newStarId VARCHAR(10);

    SELECT MAX(CAST(SUBSTRING(id, 3) AS UNSIGNED)) + 1 INTO newStarNum FROM stars;
    SET newStarId = CONCAT('nm', LPAD(newStarNum, 7, '0'));

    INSERT INTO stars (id, name, birthYear)
    VALUES (newStarId, starName, starBirthYear);

    SET starId = newStarId;
END $$

DELIMITER ;
