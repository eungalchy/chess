# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAHZM9qBACu2AMQALADMABwATACcIDD+yPYAFmA6CD6GAEoo9kiqFnJIEGiYiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAEQDlGjAALYo43XjMOMANCu46gDu0ByLy2srKLPASAj7KwC+mMK1MJWs7FyUDRNTUDPzF4fjm6o7UD2SxW63Gx1O52B42ubE43FgD1uogaUCyOTAlAAFJlsrlKJkAI5pXIAShuNVE9yqsnkShU6ga9hQYAAqoNMe9PigyTTFMo1KoqUYdHUAGJITgwNmUXkwHSWGCcuZiHSo4AAaylgxgWyQYASisGXJgwAQao4CpQAA90RpeXSBfdERSVA1pVBeeSRConVVbi8YAozShgBaOhr0ABRK0qbAEQpeu5lB4lcwNQJOYIjCbzdTAJmLFaRqDeeqG6bKk3B0MK+Tq9DQsycTD2-nqX3Vb0oBpoHwIBCJykPVv01R1EBqjHujmDXk87QO9sPYx1BQcDhamXaQc+4cLttjichjEKHz6zHAM8JOct-ejoUrtcb0-6z1I3cPWHPMs49H4tR9lgX7wh2-plm8RrKssDQHKCl76h0ED1mg0ErFciaUB2qYYA04ROE42aTJBXwwDBIIrPBCSIchqEHNc6AcKYXi+AE0DsEysSinAkbSHACgwAAMhA2RFNhzDOtQAYtO03R9AY6gFGg2ZKvM6x-ACHDXGBVLAQGowqSgan6P8uwwk8IGVO+3YwAgwkSpiQkiYSxJgGSVlUiOAqMsy04GfOtIHpUK4wOKkrurK8rlh8yqYKqIaau6MBoBAzAAGa+JK0A6t4DgwOpuy3gF97JhUVk9n2A7uSVYFujMV7QEgABeKAcNGsbxkU2klWJ6ZOAAjIRuaqPmCwwcWpYND4dX6g1zV7PRzaeeo1UutZ4XblVFRLWOMBHnIKAvgkF5Xje21BSKgbroGJ0batJW6WWjkSpkqiAZgD0rZJ4FERWXxQnBV7UQ2UJaTUlDdcgaYwHhBFjD90V-bBFGA0h6Ag02jGeN4fj+F4KDoLE8RJHjBOOb4WBiUKNVNNIkYCZGHSRj0vTyaoikjJRQOdWDUCVA9dSc6jRQfaVq0NLZ9jkw5wnk85aiuTuKCVNt3lgIdx0IUL-l8qO50NKFz43fIcoKoLyGxWqmpmwTyVpRljFnZZYtJRViufbUtWUbNLVtSgcaKRhvOVD1MAZgNcNDSNhbjON0CTdNCTe-NGOFTrAru8i12vrdXYeXeXlGCg3AnleGtUVrqeLqoesyEXzKGIdb6rb6-OCTLZ4vW9D2+tToyg19EOlGAuH4dmC2Y8xOOohu-jYBKmoCeiMAAOLKholMSR7TTLwzzP2MqHMo+bYF8+ZAbW8LZ+86LXbi+iq+5g599r3LpJu1t+cMjATJq6XF-a1XGuBss7Xm0CbE0R90AW3ihAzWyEkopRgOlKaDtP7VydrfF2-ZFY9x5p7eqUAmo+xjH7DqgdB5QzDoNAUUcxoljjoqBOSdGwMUrgeXBmDG45yoEOD+RUC7IFyA-NQmIAHsOXBdYBpoEAr2VJ6baHCeGukEuiAAPMI3kFQ3anzhAGReQiX4AQQEBK+oE8EwAmPvXMhZGguDsV0fuSZg6QxwtDUecMrFqBsXYlwDiU5YxYv4DgbhIhOBQE4WIkZghwG4gANngJOQwwiYDFBceJP05jpKdD3gfBOXNsyeIAHLKkcZhT8V8GgXwKcqYp8wzK6IRBkzBe0MTCMxHARJwjX4K02jINBqt1b-zYcVKowVgFcONpFC+0CNSwPLvA22SD7bDMdCVTsSjrK9mwVVTeAYWRMMIXNX2-sExdRTGk3q4ccw0ILHQiajCvaHJaiwxaaCzGcKNsAHBe5+FfxaSgNpnjTpvIkQ0J8sj5jyJBZvTOniACS0hvnlIaQ0Dpx4AXKk7sY96pi1m93GPC6QhY+rhGCIEUpCJzlDxHrDSxyoEXEtJeS-xk8AiWCLrZLYhMkCJDAOy-sEAuUACkIASghYYfwKRQDqlSUPKmmTmgslkr0Txh84HoGzNgBAwB2VQDgBAWyUB1iEopUKVuVSxhap1ZQfVhrjX0ukPU788rMEACtRVoDaSKiUXSUBEnlm5ZuPy05fx-oMyBaAxEjOFPrCUhts6TNNhGmZVsI0ILtiglZS4YXKK2ZVINuyyz7MeUQ1qJCTncy+lhC5od+rULzLcos9CyxTRLXNF5qDfmCjxc7CZXzekq2-syQFDqo2rNGZIuN4qIoKkJSm7+lZFnINeV295GzyrbILU0reAAhUM3TjlkLOVUEOGZaXjEjo2mOzaGh6A3Kif1uQO1Zu7Tm6yGjtxxVmVa3VtqsqmnNLWCMlaNlroDEGQD4ZkKHoDse+ANaMxZgjjc0aTb7kAZDBaGAdYGzj2+dSfpipsBaFacqTEQLtBjqXBOt0xH9rTu4UOZF34Gjes9ZioxJiGlgfAqatZIcYZjxZdjAIXgdXct5WJhUiAQywGANgLVhB8iFBSRvbdUlab00ZszYwgczUVJpnTBmTM+i6e7m+hoIBuB4HadZqA3TA252DVXSzdnREvofBdaQdcMRVhketeQ6w+3rA-fIDzPbMF5qRVUVuMm8BYq486iLW8+56f4zWwTYw8NAA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
