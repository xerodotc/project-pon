Project Pon
===========

A combination between pong and breakout game with networking feature.
Originally, this project is a part of Programming Methodology class in my university.

Requirements
------------

* [JRE 7 or higher](http://www.java.com/) is required for running this game (may work with older version)
* [JDK 7 or higher](http://www.oracle.com/technetwork/java/javase/downloads/index.html) is required for building this project

Downloads
---------

Please go to [releases page](https://github.com/xerodotc/project-pon/releases) for binary releases.
For source code, you can clone or fork this project.

FAQ
---

> There's a music option in the game but there's no music playing at all.

The background music are removed due to copyright issues, but you can place your own music
in `res/bgm` directory beside the JAR file under the name `title.ogg` for title screen and
`ingame.ogg` for in-game music. Obviously, your music need to be in Ogg Vorbis format.

Building
--------

This project is targeted at Java SE 7, but it maybe work with older version.

This project use Gradle for building.
To start building this project, run `./gradlew build` (or just `gradlew build` for Windows)
from the command line while in the project directory.

License
-------

This project itself (excluding its dependencies and resources) is licensed under MIT License.
For more information please see [COPYRIGHTS.md](COPYRIGHTS.md).
