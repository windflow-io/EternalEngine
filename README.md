![Windflow logo](https://i.imgur.com/Yt2FfIY.png)
# Windflow.io's Eternal Engine

> This project is in early development phase and not ready for use. Please 'Watch' us for updates. Send questions or queries to [support@windflow.io](mailto:support@windflow.io)

Windflow Eternal Engine is an open-source, web-component-based platform for building apps and web sites in Vue.js and TailwindCSS. It serves web sites and apps as an assortment of web components dynamically constructed from a database.
The components, order, layout, styles and content are all dynamic.
Eternal Engine provides out-of-the-box component and content management functionality, bringing Wordpress-like features to Vue.js developers.
Eternal Engine includes bare-necessity web components to get you going. They are 100% editable in-place, via the front-end editor. A vast array of more complex components are freely copy-and-pastable from Windflow.io's component library

Windflow Eternal Engine is for you if:
 1. You're a Vue.js developer or TailwindCSS fan.
 2. You want to build hand-crafted Vue.js web sites and apps.
 3. You want to offer your clients Wordpress-like CMS functionality
 4. You don't want to rewrite components for everything.
 5. You don't want to bother with build tools or databases.

<blockquote>If you would like to try Windflow Eternal Engine without installing it, go create a free developer account on our hosted service at <a href="https://windflow.io">windflow.io</a> and give it a test run.</blockquote>

Client code is written in highly-readable plain-old ES6 modular JavaScript with no client-side build tools. By default index.html and app.js are un-opinionated. All the data, layouts, graphics, fonts, etc are requested from the server. The application is then "built" on the client side.

The process is as follows:

1. On the first visit, index.html is served to the client on a clean slate with no html.
2. The client-side Vue3 app then requests a layout and list of components to be displayed on the page.
3. The client-side Vue3 app then requests the individual components and displays them.
4. Components are hydrated with data page data from the server (that came with the list of components).

### running the application
To run the application:
  1. Clone the application to your local machine.
  2. Install PostgreSQL and create an empty database.
  3. Edit database credentials in `src/main/resources/application.properties`
  4. Create a `src/main/resources/secret.development.properties`
        ```properties
        eternalengine.encryption.password=chosen_secret_keyphrase_here
        ```
  5. Create an OpenID app on Github. <a href="docs/Github.md">More info</a>
  6. Create a `src/main/resources/openid.development.properties` and pop the github info in it.
        ```properties
        eternalengine.auth.github_client_id=from_github
        eternalengine.auth.github_client_secret=from_github
        eternalengine.auth.github_auth_domain=your_domain_name
        ```
  7. Edit `src/main/resources/eternalengine.development.properties` and configure the app properties as below:
        ```properties
        eternalengine.resetDataOnStartup=true
        eternalengine.cdn=https://cdn.windflow.io/app
        eternalengine.appDomain=windflow.app.local
        eternalengine.systemNamespace=system.windflow.io
        ```

        (in IntelliJ idea, you can pop `--spring.profiles.active=development` into the 'environment variables' )
  8. Start the application using the Gradle Wrapper 
        ```properties
         ./gradlew bootRun --args='--spring.profiles.active=development'
        ``` 

* **Note 1**: As above, there is a gradlew.bat for windows users.
* **Note 2**: The application checks `window.location.host` to decide which site to serve. This doesn't help much when running locally as http://localhost so we added a feature that allows you to fudge hosts. To fudge a host, just add a querystring parameter `?host=www.example.com` (for example) to any url.
* **Note 3**: Default database credentials are `postgres:postgres@windflow`   
