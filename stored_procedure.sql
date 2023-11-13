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
    INSERT INTO stars (id, name, birthYear) VALUES (newStarId, starName, starBirthYear);
    SET starId = newStarId;
END $$


CREATE PROCEDURE InsertMovie(IN movieTitle VARCHAR(100), IN movieYear INT, IN movieDirector VARCHAR(100),
                              IN movieStarName VARCHAR(100), IN movieStarYear INT, IN movieGenre VARCHAR(32),
                              OUT movieId VARCHAR(10), OUT starId VARCHAR(10), OUT genreId INT)
BEGIN
    DECLARE newMovieId, newStarId, newGenreId VARCHAR(10);
    DECLARE newMovieNum, newStarNum, newGenreNum INT;

    IF (SELECT id FROM movies WHERE title = movieTitle AND year = movieYear AND director = movieDirector) IS NULL THEN
        SELECT MAX(CAST(SUBSTRING(id, 3) AS UNSIGNED)) + 1 INTO newMovieNum FROM movies WHERE id REGEXP '^tt[0-9]+$';
        SET newMovieId = CONCAT('tt', LPAD(newMovieNum, 7, '0'));

        SELECT id INTO newStarId FROM stars WHERE name = movieStarName;
        IF newStarId IS NULL THEN
            SELECT MAX(CAST(SUBSTRING(id, 3) AS UNSIGNED)) + 1 INTO newStarNum FROM stars WHERE id LIKE 'nm%';
            SET newStarId = CONCAT('nm', LPAD(newStarNum, 7, '0'));
            INSERT INTO stars (id, name, birthYear) VALUES (newStarId, movieStarName, movieStarYear);
        END IF;

        SELECT id INTO newGenreId FROM genres WHERE name = movieGenre;
        IF newGenreId IS NULL THEN
            SELECT MAX(id) + 1 INTO newGenreNum FROM genres;
            SET newGenreId = newGenreNum;
            INSERT INTO genres (id, name) VALUES (newGenreId, movieGenre);
        END IF;

        INSERT INTO movies (id, title, year, director) VALUES (newMovieId, movieTitle, movieYear, movieDirector);
        INSERT INTO stars_in_movies (starId, movieId) VALUES (newStarId, newMovieId);
        INSERT INTO genres_in_movies (genreId, movieId) VALUES (newGenreId, newMovieId);

        SET movieId = newMovieId;
        SET starId = newStarId;
        SET genreId = newGenreId;

    ELSE
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Movie already exists.';
    END IF;

END $$

