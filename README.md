# Custom Event Handler (WSO2 IS 7.1.0)

This project includes a custom event handler `CustomEventHandler` that handles the WSO2 Identity Server's
`POST_REVOKE_ACESS_TOKEN`, `POST_REVOKE_ACESS_TOKEN_BY_ID`, `SESSION_TERMINATE`, and `SESSION_EXPIRE` events.

[Look here](https://is.docs.wso2.com/en/7.1.0/references/extend/user-mgt/write-a-custom-event-handler/) for more information on event handlers.

---

### Configuration:

Add the below to the `<IS_HOME>/repository/conf/deployment.toml` file:
   ```toml
   [[event_handler]]
   name = "CustomEventHandler"
   subscriptions = ["POST_REVOKE_ACESS_TOKEN", "POST_REVOKE_ACESS_TOKEN_BY_ID", "SESSION_TERMINATE", "SESSION_EXPIRE"]
   ```

You can add custom properties to the event handler by adding it under `properties`:
   ```toml
   [[event_handler]]
   name = "CustomEventHandler"
   subscriptions = ["POST_REVOKE_ACESS_TOKEN", "POST_REVOKE_ACESS_TOKEN_BY_ID", "SESSION_TERMINATE", "SESSION_EXPIRE"]
   properties.custom_property = "custom_value"
   ```

Then, read it in the `CustomEventHandler` class using the `AbstractEventHandler#config` field:
   ```java
   configs.getModuleProperties().getProperty("CustomEventHandler.custom_property");
   ```
_The property name will always be in the format `event_handler_name.property_name`._

----

### Usage

1. Build the project using Maven:
   ```bash
   mvn clean install
   ```
2. Copy the `target/custom_event_handler-1.0.0-SNAPSHOT.jar` file to the `<IS_HOME>/repository/components/dropins` directory.
3. Add the configuration mentioned in the [_Configuration_](#configuration) section.
4. Start the server.

---

### Logging:

For this component's logs to be printed, you need to do the following steps in to the `<IS_HOME>/repository/conf/log4j2.properties` file:

1. Create a [Log4J2 Logger](https://logging.apache.org/log4j/2.x/manual/configuration.html#configuring-loggers) named `org-sample` mapped to the `org.sample` package:
   ```properties
   logger.org-sample.name = org.sample
   logger.org-sample.level = DEBUG
   ```
2. Add the new `org-sample` logger to the `loggers` variable:
   ```properties
   loggers = AUDIT_LOG, . . ., org-sample
   ```

#### Example output:
```
. . . DEBUG {org.sample.event.handler.CustomEventHandler} - POST_REVOKE_ACESS_TOKEN_BY_ID event received to CustomEventHandler.
. . .  INFO {org.sample.event.handler.CustomEventHandler} - Session ID or Authenticated User is not available in the event properties.
. . . DEBUG {org.sample.event.handler.CustomEventHandler} - SESSION_TERMINATE event received to CustomEventHandler.
. . .  INFO {org.sample.event.handler.CustomEventHandler} - Session ID: eee2cae36bfe4f96fb269458b708036c71cb9efdb3696c2cfa241834e2b9602f, Authenticated User: admin@carbon.super
```

---

### Debugging:

To debug this component while the Identity Server is running:

1. Run the Identity Server in debug mode:
   ```sh
   sh $IS_HOME/bin/wso2server.sh --debug 5005
   ```
2. Attach the JVM to your IDE:
   - For IntelliJ IDEA:
      - Go to `Run` > `Edit Configurations...`
      - Click on the `+` icon and select `Remote JVM Debug`.
      - Set the port to `5005` and click `OK`.
      - Add your breakpoints in the code.
      - Click on the green debug icon to start debugging.
   - For VSCode:
      - Go to the `Run` tab on the left sidebar.
      - Click on `create a launch.json file`.
      - Select `Java` and then select `Remote`.
      - Set the port to `5005` and click `OK`.
      - Add your breakpoints in the code.
      - Click on the green debug icon to start debugging.

_Note: The Identity Server's startup will be blocked until you connect the debugger._
