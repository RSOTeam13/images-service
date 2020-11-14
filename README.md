![Albify](https://raw.githubusercontent.com/RSOTeam13/documentation/main/resources/logo-dark.png)
# Albify - Images service

![Travis-Build](https://travis-ci.com/RSOTeam13/images-service.svg?branch=main)
 
Micro service for operations on images in Albify application. You can find project documentation [here](https://github.com/RSOTeam13/documentation).

#### Config server
Get consul and run it (locally) with `./consul agent -dev`

Values are first loaded when they are used, so you also need to call `localhost:8081/v1/images/config` (new entry should be visible in logs).

Then either connect to `localhost:8500` or use some other method to set `environments/dev/services/image-service-service/1.0.0/config/rest-config/maintenance-mode` to a boolean (preferably `false` to start).

Setting the variable anew should provide new log entries with the changed value. 

