![Windfllow logo](https://i.imgur.com/zgUXAF6.png)
# windflow server
Windflow.io server is a dynamic web-component based platform for building apps and web sites. Components are styled with TailwindCSS.

The JavaScript code is written in plain JavaScript with no build tools using the ES6 (ESM) modules provided by Vue and VueX. By default index.html and app.mjs are un-opinionated. All the data, layouts, graphics, fonts, etc are/will be requested from the server. The application is then "built" on the client side.

The process is as follows:

1. On the first visit, index.html is served to the client on a clean slate with no html.
2. The client-side app then requests a layout and list of components to be displayed on the page.
3. The client-side app then requests the individual components and displays them.
4. Components are hydrated with data from vuex (that came with the list of components).

### running the application
The server-side application is Spring Boot, packaged in Gradle. Executing `./gradlew/bootRun` from the root directory will start Gradle, download the necessary dependencies, start the Tomcat web server and serve the application on port 8080. (Note that there is a gradlew.bat for windows users too.)

* The layout and components are currently

### state of the application
1. Although the data is intended to be stored as jsonb in a Postges database, the database has not yet been implemented.
2. The data is being served by Java controllers in (`/src/main/java/io.windflow.server/controllers`).
3. These controllers are in turn serving static text files (json stubs) from `.src/main/resources/stubs`
    1. actual components are stored as .mjs modules in subfolder ./components
    2. html layouts are stored as html in subfolder ./layouts
    3. page data (lists of components for each area in the page) are stored in subfolder ./pages
    
### next steps
1. Get the routing working (some kind of an pop state event listener needs to listen for route changes)
2. The page (components and data) and layout need to update and re-render on route update
3. Some kind of data needs to "populate" (components must be hydrated with content) from the page.  
