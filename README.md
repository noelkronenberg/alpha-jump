# Projekt KI

High-performance AI for the board game [Jump Sturdy](https://www.mindsports.nl/index.php/the-pit/576-jumpsturdy).

## Structure

- ```jump-sturdy-ai``` project work
  - ```pom.xml``` [Maven](https://www.jetbrains.com/help/idea/maven-support.html) configuration (e.g. [dependencies](https://mvnrepository.com/))
  - ```src``` 
    - ```main``` Java files
      - ```benchmark``` benchmarking of algorithms
      - ```communication``` communication with (demo) game server
      - ```debug``` algorithms (only) for debugging
      - ```game``` actual game logic
      - ```search``` algorithms for optimising gameplay
    - ```test``` [JUnit](https://www.jetbrains.com/help/idea/junit.html) tests
- ```working``` individual work

## Status

![](https://github.com/noelkronenberg/projekt-ki/workflows/tests/badge.svg)
