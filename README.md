# Infinispan Query - Weather demo

## Requirements

```https://github.com/gustavonalle/noaaparser``` available on local mvn repo:

```
git clone https://github.com/gustavonalle/noaaparser
cd noaaparser && ./sbt +publish
```

## Configuration

Refer to ```src/main/resources/config.properties```

## Execution

Run 

```
mvn exec:java -Dexec.mainClass="org.infinispan.demo.weather.service.rest.MainServer"
```

Demo will listen at ```http://localhost:8080```
