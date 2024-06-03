# Projekt KI

High-performance AI for the board game [Jump Sturdy](https://www.mindsports.nl/index.php/the-pit/576-jumpsturdy).

![](https://github.com/noelkronenberg/projekt-ki/workflows/tests/badge.svg)

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

## Game Server

> ```middleware.getMove(fen)``` is a translation between the Java implementation and Python. It will return the best move for a given fen string (e.g. ```game["board"]```).

### Steps

1. adjust ```path``` to ```target``` folder in ```middleware.py``` (if needed)
2. start server: ```python server.py```
3. start first client (in new terminal): ```python client.py```
4. start second client (in new terminal): ```python client.py```

### Demo

https://github.com/noelkronenberg/projekt-ki/assets/79874249/407ad9ac-230e-4874-bb2a-90bbcb663c02
