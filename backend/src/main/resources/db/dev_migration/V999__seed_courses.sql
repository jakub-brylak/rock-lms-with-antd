INSERT INTO courses (id, title, description, duration, status, published_at)
SELECT 1001, 'Electric Guitar Essentials',
       'Learn the fundamentals of playing electric guitar in a rock band setting.',
       30, 'PUBLISHED', now()
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 1001);

INSERT INTO courses (id, title, description, duration, status, published_at)
SELECT 1002, 'The History of Rock',
       'Explore the evolution of rock music from the 1950s to today.',
       25, 'PUBLISHED', now()
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 1002);

INSERT INTO courses (id, title, description, duration, status, published_at)
SELECT 1003, 'Legends of Rock: Biographies',
       'Dive into the life stories of iconic rock musicians like Hendrix, Joplin, and Cobain.',
       20, 'PUBLISHED', now()
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 1003);

INSERT INTO courses (id, title, description, duration, status, published_at)
SELECT 1004, 'Drumming for Rock Beginners',
       'Basic drumming techniques tailored to rock and alternative genres.',
       15, 'PUBLISHED', now()
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 1004);

INSERT INTO courses (id, title, description, duration, status, published_at)
SELECT 1005, 'Bass Lines in Classic Rock',
       'Master foundational bass lines used in legendary rock songs.',
       18, 'PUBLISHED', now()
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 1005);

INSERT INTO courses (id, title, description, duration, status, published_at)
SELECT 1006, 'Rock Band Arrangement 101',
       'Learn how to arrange music for a classic four-piece rock band.',
       22, 'DRAFT', NULL
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 1006);

INSERT INTO courses (id, title, description, duration, status, published_at)
SELECT 1007, 'Stage Presence for Rock Performers',
       'Improve your live performance skills and stage charisma.',
       12, 'PUBLISHED', now()
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 1007);

INSERT INTO courses (id, title, description, duration, status, published_at)
SELECT 1008, 'The British Invasion: A Cultural Shift',
       'Understand how UK bands like The Beatles and The Rolling Stones reshaped global music.',
       16, 'ARCHIVED', '2023-01-10 14:00:00'
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 1008);

INSERT INTO courses (id, title, description, duration, status, published_at)
SELECT 1009, 'Advanced Guitar Solos: From Slash to Santana',
       'Break down and master legendary guitar solos from the greatest rock guitarists.',
       28, 'PUBLISHED', now()
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 1009);

INSERT INTO courses (id, title, description, duration, status, published_at)
SELECT 1010, 'Grunge Guitar Riffs',
       'Learn the gritty tones and techniques of the 90s Seattle sound.',
       17, 'PUBLISHED', now()
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 1010);

INSERT INTO courses (id, title, description, duration, status, published_at)
SELECT 1011, 'Women Who Shaped Rock',
       'Explore the legacy of powerful female voices in rock, from Janis Joplin to Joan Jett.',
       15, 'PUBLISHED', now()
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 1011);

INSERT INTO courses (id, title, description, duration, status, published_at)
SELECT 1012, 'Punk Rock Attitude and Ethos',
       'Discover the DIY spirit and social context behind punk rock’s explosive rise.',
       14, 'DRAFT', NULL
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 1012);

INSERT INTO courses (id, title, description, duration, status, published_at)
SELECT 1013, 'Keyboard Techniques in Prog Rock',
       'Learn how keyboardists shaped the sound of Pink Floyd, Genesis, and Dream Theater.',
       19, 'PUBLISHED', now()
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 1013);

INSERT INTO courses (id, title, description, duration, status, published_at)
SELECT 1014, 'Rock Vocal Training: Scream, Belt, Control',
       'Practice safe and powerful rock vocal techniques for any range.',
       20, 'PUBLISHED', now()
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 1014);

INSERT INTO courses (id, title, description, duration, status, published_at)
SELECT 1015, 'Classic Rock Album Analysis',
       'Track-by-track breakdown of essential albums like Led Zeppelin IV and Dark Side of the Moon.',
       24, 'PUBLISHED', now()
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 1015);

INSERT INTO courses (id, title, description, duration, status, published_at)
SELECT 1016, 'The Rise of Alternative Rock',
       'Follow the genre’s path from underground to mainstream in the 90s.',
       18, 'PUBLISHED', now()
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 1016);

INSERT INTO courses (id, title, description, duration, status, published_at)
SELECT 1017, 'Rock Lyrics Writing Workshop',
       'Write compelling, emotionally resonant lyrics inspired by your favorite rock icons.',
       21, 'DRAFT', NULL
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 1017);

INSERT INTO courses (id, title, description, duration, status, published_at)
SELECT 1018, 'Rock Subgenres Explained',
       'From glam to garage to post-rock — understand the nuances and evolution of rock music.',
       20, 'ARCHIVED', '2022-11-30 12:00:00'
WHERE NOT EXISTS (SELECT 1 FROM courses WHERE id = 1018);
