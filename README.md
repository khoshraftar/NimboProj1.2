# Nimbo Github Trends

This program give github trend events in a specific time duration.(This is a test program.)

## How to bulid project
Go to the project directory.
Use this command in treminal to build the project:

```
mvn clean install
```

## How to run project

Use this command in treminal to run the project:

```
java -jar [jar file name]
```
## How to set input

Please enter command in this format:

```
[Events(by code)] [Output mode] [Time unit] [Period start time] [Period final time]
```

Events codes: 0 -> Pull 1 -> Push 2-> Pull & Push 3 -> Others 4-> All

Output mode: D -> Devolopers R -> Repositories T -> Both

Time units : H -> Hour M -> Minutes S -> Seconds

### example:

```
2 R H 0 1
```

It means push & pull events of Repositories in one hour ago.
