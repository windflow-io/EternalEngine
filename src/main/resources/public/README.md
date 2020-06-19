# Windflow.io
These are general notes for Mark van Wyk. Required tests to make sure the new system is working correctly:

## Hosting is with Heroku (server-side )
* Use multiple buildpacks (this allows npm)
* Use Rollup with Grunt. Have Gradle tasks execute Grunt. You can call Gradle tasks through the wrapper from Java.
* Rollup can minify, uglify, treeshake and purge CSS.
* https://stackoverflow.com/questions/49876189/how-to-run-a-gradle-task-from-a-java-code
* https://devcenter.heroku.com/articles/using-multiple-buildpacks-for-an-app
* Add multiple build backs to Heroku (https://devcenter.heroku.com/articles/using-grunt-with-java-and-maven-to-automate-javascript-tasks)
* Create grunt tasks to pre-render (https://www.npmjs.com/package/grunt-prerender)
 
## Solution for Compression
 - Use Tomcat / Jetty compression through Spring (https://docs.spring.io/spring-boot/docs/2.0.0.RELEASE/reference/htmlsingle/#how-to-enable-http-response-compression)

## Tasks
1. Routing
2. Component Arrays
3. (Component Loader)

   
## Predeploy
   1. Tomcat Compression!
   2. Module not found!
``````
