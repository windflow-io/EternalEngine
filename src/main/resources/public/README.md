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
   * javax.servlet.ServletException: Circular view path [error]: would dispatch back to the current handler URL [/error] again. Check your ViewResolver setup! (Hint: This may be the result of an unspecified view, due to default view name generation.)
   * https://devcenter.heroku.com/articles/increasing-application-performance-with-http-cache-headers
   * Cache production

PERFORMANCE OPTIMISATION:
   * Convert all SVG icons into .mjs functional components
   * Rollup.js can come later.
   * Implement PurgeCSS
   * Render the head
