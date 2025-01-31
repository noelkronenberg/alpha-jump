# Alpha Jump

High-performance AI for the board game [Jump Sturdy](https://www.mindsports.nl/index.php/the-pit/576-jumpsturdy).  
Developed as part of [course work](https://moseskonto.tu-berlin.de/moses/modultransfersystem/bolognamodule/beschreibung/anzeigen.html?number=40658&version=5&sprache=1) (and beyond) by students at [TU Berlin](https://www.tu.berlin/en).

![](https://github.com/noelkronenberg/projekt-ki/actions/workflows/junit.yml/badge.svg)

## Structure

- ```.github/workflows``` [GitHub Actions](https://docs.github.com/en/actions) for automatic workflows
- ```app``` web app (**under development**)
- ```jump-sturdy-ai``` Jump Sturdy AI
  - ```pom.xml``` [Maven](https://www.jetbrains.com/help/idea/maven-support.html) configuration
  - ```src``` 
    - ```main``` main files
      - ```java``` Java files
        - ```app``` human-AI gameplay
        - ```benchmark``` benchmarking of algorithms
          - ```simulation``` game simulations
        - ```communication``` communication with a game server
        - ```debug``` algorithms for debugging
        - ```game``` actual game logic
        - ```search``` algorithms for optimising gameplay
          - ```ab``` alpha-beta pruning
          - ```mcts_lib``` Monte Carlo tree search for generating an opening book
          - ```mcts``` Monte Carlo tree search
          - ```optimisation``` algorithms for parameter optimisation
      - ```resources``` resource files
    - ```test``` [JUnit](https://www.jetbrains.com/help/idea/junit.html) tests
   
## Documentation

[noelkronenberg.github.io/alpha-jump/](https://noelkronenberg.github.io/alpha-jump/)
