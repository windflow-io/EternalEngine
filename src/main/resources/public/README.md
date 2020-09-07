# Windflow.io
These are general notes for Mark van Wyk. Required tests to make sure the new system is working correctly:

Important Now:
 * Create the meta tags in the actual page (force noindex on error pages)
 * Create all the header tags for each page in JavaScript
 * Do the CDN thing (CDN is not in the page. Remove it completely)
    - Add a meta tag and read it
    - CORS Proxy (test first)
 * Preload everything in page
 * Only load Monoco on edit
 

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
   * Memcache: https://devcenter.heroku.com/articles/spring-boot-memcache
   * Mark: Java to "machine compiled" to save Heroku cash
   * We don't have to host their domains (hosting their domains in pricing)
   * DNSimple allows sharing
   * Cache Breaking URL's
   * CORS JavaScript on CDN
   * Prerender HTML Tags
   * Do not use the UI to upload content to Google Cloud Storage
     gsutil cp -z html,css,js,mjs,svg,ttf * gs://cdn.windflow.io/app
  
