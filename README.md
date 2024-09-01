# Alpha Jump

High-performance AI for the board game [Jump Sturdy](https://www.mindsports.nl/index.php/the-pit/576-jumpsturdy).  
Developed as part of [course work](https://moseskonto.tu-berlin.de/moses/modultransfersystem/bolognamodule/beschreibung/anzeigen.html?number=40658&version=5&sprache=1) by students at [TU Berlin](https://www.tu.berlin/en).

![](https://github.com/noelkronenberg/projekt-ki/actions/workflows/junit.yml/badge.svg)

>**Note:** Unit Tests (see badge above) might fail if GitHub Actions limit has been reached. If so, run them locally.

## Structure

- ```.github/workflows``` [GitHub Actions](https://docs.github.com/en/actions) for automatic unit testing
- ```jump-sturdy-ai``` project work
  - ```pom.xml``` [Maven](https://www.jetbrains.com/help/idea/maven-support.html) configuration
  - ```src``` 
    - ```main``` main files
      - ```java``` Java files
        - ```app``` playing against AI (as human)
        - ```benchmark``` benchmarking of algorithms
          - ```simulation``` game simulations
        - ```communication``` communication with demo game server
        - ```debug``` algorithms for debugging
        - ```game``` actual game logic
        - ```search``` algorithms for optimising gameplay
          - ```ab``` alpha-beta pruning
          - ```mcts_lib``` Monte Carlo tree search for generating an opening book
          - ```mcts``` Monte Carlo tree search
          - ```optimisation``` algorithms for parameter optimisation
      - ```resources``` resource files
    - ```test``` [JUnit](https://www.jetbrains.com/help/idea/junit.html) tests
- ```game-server``` Jump Sturdy server for playing against other players (given by [course organisors](https://git.tu-berlin.de/lengfeld8/jump-sturdy-game-server))

## Playing

>**Note:** [JDK](https://www.oracle.com/java/technologies/downloads/) version 22.0.1 should be installed on the computer.

1. start server: ```python game-server/server.py```
2. if not up to date, generate new ```.exe```
    1. set ```twoPlayer = false``` in ```communication.Connection```
    2. temporarily remove ```test``` folder
    3. generate ```.jar```: ```mvn clean package```
    4. generate ```.exe``` using [Launch4j](https://genuinecoder.com/online-converter/jar-to-exe/)
3. start AI: ```jump-sturdy-ai/GruppeC_KI.exe```
4. start other AI (can be another instance of ```jump-sturdy-ai/GruppeC_KI.exe```)
5. wait for game to end
6. stop execution of ```GruppeC_KI.exe``` (e.g. via Task Manager)
