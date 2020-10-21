package io.windflow.eternalengine.error;

/**
 * @TODO: Refactor to "EternalEngine" as opposed to "Windflow"
 */
public enum EternalEngineError {
    ERROR_001("500 Internal Server Error", "An unexpected server error occurred"),
    ERROR_002("404 Not Found","Page was not found"),
    ERROR_003("404 Domain Not Configured", "This domain has not been configured"),
    ERROR_004("404 Unused Domain", "This domain is not in use. You may create a site here."),
    ERROR_005("No Sites Configured", "No Windflow Eternal Engine sites are configured in this database"),
    ERROR_006("Database is Empty", "The Windflow Eternal Engine database contains no data"),
    ERROR_007("Corrupt Page Data", "Cannot Parse JSON page data"),
    ERROR_008("Component Not Found", "Missing component"),
    ERROR_009("Authentication Error", "Redirects to an authentication provider must have a referer"),
    ERROR_010("Authorization Error", "Token verification failed"),
    ERROR_011("GitHub Authentication Failed", "GitHub returned an error");

    String title;
    String description;

    EternalEngineError(String title, String description) {
        this.title = title;
        this.description = description;
    }

    /*** Methods ***/

    @Override
    public String toString() {
        return this.name();
    }

    /*** Getters and Setters ***/

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
