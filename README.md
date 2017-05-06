To run backend build it with maven:
```
cd backend
mvn clean
mvn package
```
then create db and run application
```
java -jar target/pda-1.0-SNAPSHOT.jar db migrate config.yml
java -jar target/pda-1.0-SNAPSHOT.jar server config.yml
```
