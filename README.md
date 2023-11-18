# Retroware (Spring implementation)
RETROWARE is a web application that hosts JavaScript browser games. Visitors may create user accounts and edit their profile, comment on games, and rate games (like or dislike).

I initially developed RETROWARE in my second year of university for an assignment on web applications and was implemented using Java Server Faces (JSF) and Apache Derby database in Java 8. The project was not configured properly, setting up the project and running it was a hassle.

The version of RETROWARE in this repository uses Maven as a project manager and is implemented in Java 17 with Spring (3.1.5) and uses the H2 database. The project has been simplified, from source code to setting up the project and running it.

# How to run the project
1. Clone/download the project
2. Load it into your IDE of choice
3. Your IDE should download all dependencies automatically. If not simply run "mvnw" (Mac/Linux) or "mvnw.cmd" (Windows)
4. Launch application from "RetrowareApplication.java"

# H2 Database
This database was chosen because it is a fast in memory database that requires little to no configuration to be used. This made H2 ideal to demo the RETROWARE web application.

The database's console is enabled and can be accessed by accessing "localhost:8080/h2-console". Make sure that the "JDBC URL" form entry contains: "jdbc:h2:mem:retroware-db" (this URL is also printed in the console after running the project). The username is "sa" and no password is set, however the console is accessible only from the host machine as remote access has been disabled from settings.

# List of dependencies used
1. Spring Web
2. Spring Session
3. Thymeleaf
4. Spring Security
5. Spring Data JDBC
6. JDBC API
7. Validation
8. H2 Database
9. Lombok

# Notable improvements and changes from previous version
- Improved security via Spring Security
- All values used in SQL queries that originate from users are passed through prepared statements
- Improved play count for games
- Improved rating system by using the "lower bound of Wilson score confidence interval for a Bernoulli parameter" which provides a more accurate score over the proportion of positive ratings.
- Password rules on registration are replaced with a password strength score determined by calculating the password's entropy.
- Added user authorities
- Better logging via Slf4j and a shortened version of the counts (play count and ratings) is displayed to the user (eg: 1K instead of 1,000;  9M instead of 9,000,000)
