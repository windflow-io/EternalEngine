package io.windflow.server.error;

public enum WindflowError {
    ERROR_001("500 Internal Server Error", "An unexpected server error occurred"),
    ERROR_002("404 Not Found","Page was not found"),
    ERROR_003("404 Domain Not Found", "No matching domain in the database"),
    ERROR_004("No Sites Configured", "No EngineEternal sites are configured in this database"),
    ERROR_005("Database is Empty", "The EngineEternal database contains no data"),
    ERROR_006("Invalid Page Data", "Cannot Parse JSON page data"),
    ERROR_007("Component Not Found", "Missing component");

    String title;
    String description;

    WindflowError(String title, String description) {
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
