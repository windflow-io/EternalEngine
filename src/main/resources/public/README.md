# Windflow.io
These are general notes for Mark van Wyk. Required tests to make sure the new system is working correctly:


Tasks
 * Create server component example
 * Create websocket thing (VueX)
 * Database implementation
 * Monoco editor
 * Actual UI with bubble up, etc.


   ## Predeploy
   1. Make sure a full error system is implemented.
   2. Preload MJS Components (server look through components one file export)
   3. Check IntelliJ JavaScript tools for treeshaking
   4. Pre-Render with Puppeteer-Prerender (or just serve the head)
   5. Remove CSS with PurifyCSS
   6. Maybe Treeshake Vue and VueX but this can probably be done once.
   7. Allow the addition of meta data including charset and viewport
   8. Implement WebSockets
   9. Make a lovely 404

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
