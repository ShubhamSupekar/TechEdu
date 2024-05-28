create DATABASE traders;
use traders;
select * from user;
select * from videos;
drop table user;

INSERT INTO videos (title, description, file_path, uploaded_by)
VALUES ('AI Index Report', 'Showing measuring treands of AI in 2024', 'Artificial Intelligence Index Report 2024 is Here- Measuring Trends In AI.mp4', 1);

INSERT INTO videos (title, description, file_path, uploaded_by)
VALUES ('Generative AI', 'Exploring job market of Generative AI', 'Exploring Job Market Of Generative AI Engineers- Must Skillset Required By Companies.mp4', 1);

INSERT INTO videos (title, description, file_path, uploaded_by)
VALUES ('Generative AI on cloud', 'an Generative AI project', 'Generative AI Project Lifecycle-GENAI On Cloud.mp4', 1);

INSERT INTO videos (title, description, file_path, uploaded_by)
VALUES ('Starting', 'Starting an Generative AI series', 'videoplayback.mp4', 1);


UPDATE videos SET uploaded_by = 2 WHERE id = 9;



DELETE FROM videos WHERE id = 23;

DELETE FROM videos ORDER BY id ASC LIMIT 3;

delete from user where id = 3;


ALTER TABLE videos
DROP COLUMN filepath;


ALTER TABLE videos
CHANGE COLUMN file_path filepath VARCHAR(255);
