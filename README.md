# CleoPetra
<p>
<i>Computer Science project, 3rd semester AAU. [DS306e18]</i> <br>
CleoPetra aims to create a custom tournament runner for the RLBot-community that can help host, manage and run a tournament. The system supports multiple formats such as single elimination, Swiss-system and round robin. It also supports multiple stages and the seeding of participants, allowing for a easy transfer of the best participants within stages.
Apart from these features the system also adds support specifically for the [RLBot-framework](https://github.com/RLBot/RLBot). It allows to generate the rlbot.cfg file based on the bots of a match and can also start a match using the framework.
</p>

### Preqreuiqsties
```
Java 8 JRE installed.
Optional: RLBot-framework installed.
```

## Setup IntelliJ-environment
For using the IDE-environment to compile, debug and build artifacts, the following setup must be carried out. The project is running with Gradle and this will be used to build and run both the tests and the project.

1. Start IntelliJ, click open and choose the folder *tournamentsystem*.
2. When the project has been loaded a pop-up message will appear in the lower right corner og the screen. Click the *Import Gradle Project*.
3. Keep the default settings and press *OK* and wait for the import to finish.
4. The project is now imported and can be run within IntelliJ through Gradle.

## Run application and tests.
The above setup is required in order to run the application using Gradle. The following functionality is accessed using the Gradle panel found in the right side of IntelliJ. 
To **run the application** use the task: ``tournamentsystem -> Tasks -> application -> run``.
To **run the tests** use the task: ``tournamentsystem -> Tasks -> verification -> test``.
To **build an executable jar** use the task: ``tournamentsystem -> Task -> other -> fatJar``. The executable jar will now be located ``\tournamentsystem\build\libs\tournamentsystem.jar``.