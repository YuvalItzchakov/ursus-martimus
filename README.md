Ursus martimus (A.K.A Polar Bear) is a tool for processing synthetically generated data in a streaming fashion.

### Architecture:

```markdown
+------------------------------------------------------------------------+
|                                                                        |
|                                                                        |
|   Data Generation Source             HTTP Endpoint                     |
|             +                              +                           |
|             |                              |                           |
|             |                              |                           |
|             v                              |                           |
|        JSON parser                         |  Query aggregated events  |
|             +                              |                           |
|             |                              |                           |
|             |                              |                           |
|             v         Publish to           v                           |
|       Raw event store +--------> Accumulated event store               |
|                                                                        |
|                                                                        |
+------------------------------------------------------------------------+
```

The architecture resembles the CQRS architecture where the write database
is an append only log. After committing to the log, events are published to the
read database which saves data in a more efficient manner for querying the data
via the HTTP API.

### The following are prerequisites to running the application:

1. Scala 2.12
2. Java 8
3. sbt
3. Data generation tool which generates events with the following JSON schema:

```json
{
  "$schema": "http://json-schema.org/draft-04/schema#",
  "type": "object",
  "properties": {
    "event_type": {
      "type": "string"
    },
    "data": {
      "type": "string"
    },
    "timestamp": {
      "type": "integer"
    }
  },
  "required": [
    "event_type",
    "data",
    "timestamp"
  ]
}
```

### How to run the application:

- Via sbt: sbt run [path to data generator]
- Via IDEA: 
    - Load the project
    - Refresh sbt dependencies
    - Go to "Edit Configuration"
    - Select the `+` sign to add a new app (or just CTRL + SHIFT + F9 on the `StreamRunner` file):
        - Main class: `com.yuvalitzchakov.asyncpc.StreamRunner`
        - Program arguments: should be the path to your data generation executable
        - Use classpath on module: `async-producer-consumer`
        - JRE: should be 1.8 (Java 8)
        
    
### Querying the data API

After running the service, the process starts an HTTP service which by default binds 
to "0.0.0.0:8080". If you'd like to change the IP or port, this can be configured via 
the **application.conf**, and a restart to the service.

#### Endpoints exposed:

1. Event count by type - Represents events received so far, grouped by event type:
    - URL: [IP:Port]/eventsbytype
    - Verb: GET
    
2. Event count by data - Represents events received so far, grouped by event data (the payload supplied by the event)
    - URL: [IP:Port]/eventsbydata
    - Verb: GET
    
### To improve:

1. Consider adding stronger back pressure semantics (currently provided by buffering queues in `observeAsync`, maybe an `async.Queue[T]` would help)
2. HTTP server path docs should be auto generated such that once API evolves, we maintain stable documentation
3. Event source should be configurable by configuration, such that we can choose at runtime different methods of retrieving JSON data.
4. Consider having a more strict validation of the process path supplied by the user
    - Currently the program only checks that the path to the file exists, but does not validate the process itself (perhaps MD5?)
5. Method documentation should be enhanced to generate docs via [tut](https://github.com/tpolecat/tut)