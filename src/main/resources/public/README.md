# Windflow.io
These are general notes for Mark van Wyk. Required tests to make sure the new system is working correctly:

## Hosting is with Heroku (server-side )
* Use GraalVM

Tasks
 * Create themed component test
 * Test GraalVM (JavaScript)
 * Create server component example
 * Create websocket thing (VueX)
 * Test
 * Database implementation
 * Monoco editor
 * Actual UI with bubble up, etc.


   ## Predeploy
   2. Errors
   3. Preload MJS Components (server look through components one file export)
   4. Check IntelliJ JavaScript tools for treeshaking
   5. Pre-Render with Puppeteer-Prerender
   6. Remove CSS with uncss
   7. Maybe Treeshake Vue and VueX but this can probably be done once.
   8. Allow the addition of meta data including charset and viewport
   9. Implement WebSockets
   10. Make a lovely 404

   * http://iamnotmyself.com/2020/02/14/implementing-websocket-plugins-for-vuex/
   * https://tutorialedge.net/javascript/vuejs/vuejs-websocket-tutorial/

# Browserify

./browserify/bin/cmd.js purgecss -o bundle.js
