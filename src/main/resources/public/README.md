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

## MARKUS
 * Repeating page calls
 * Complex PageLoad Calls

## Tasks
1. VueX Action  (don't repeat yourself)
2. Add the layout component name to the page data.
3. Do the routing
4. Where do we store the layouts
5. How do we split between domains
6. 404
7. Title and Meta

   ## Predeploy
   1. Module not found!
   2. Errors
   3. Preload MJS Components (server look through components one file export)
   4. Check IntelliJ JavaScript tools for treeshaking
   

