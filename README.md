# Projekt KI

High-performance AI for the board game [Jump Sturdy](https://www.mindsports.nl/index.php/the-pit/576-jumpsturdy).

![](https://github.com/noelkronenberg/projekt-ki/actions/workflows/junit.yml/badge.svg)

## Structure

- ```.github/workflows``` [GitHub Actions](https://docs.github.com/en/actions) for automatic unit testing
- ```jump-sturdy-ai``` project work
  - ```pom.xml``` [Maven](https://www.jetbrains.com/help/idea/maven-support.html) configuration (e.g. [dependencies](https://mvnrepository.com/))
  - ```src``` 
    - ```main``` Java files
      - ```benchmark``` benchmarking of algorithms
      - ```communication``` communication with (demo) game server
      - ```debug``` algorithms (only) for debugging
      - ```game``` actual game logic
      - ```search``` algorithms for optimising gameplay
        - ```optimisation``` algorithms for parameter optimisation
    - ```test``` [JUnit](https://www.jetbrains.com/help/idea/junit.html) tests
- ```game-server``` Jump Sturdy server for playing against other players (given by [course organisors](https://git.tu-berlin.de/lengfeld8/jump-sturdy-game-server))

## Playing

> Note: [JDK](https://www.oracle.com/java/technologies/downloads/) version 22.0.1 should be installed on the computer.

1. start server: ```python game-server/server.py```
2. start AI: ```jump-sturdy-ai/GruppeC_KI.exe```
3. start other AI (can be another instance of ```jump-sturdy-ai/GruppeC_KI.exe```)
4. wait for game to end
5. stop execution of ```GruppeC_KI.exe``` (e.g. via Task Manager)
