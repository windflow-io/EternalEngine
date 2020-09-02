package io.windflow.server.exceptions;

public enum WindflowError {
    ERROR_001("500 Internal Server Error", "An unexpected server error occurred"),
    ERROR_002("404 Not Found","Page was not found"),
    ERROR_003("404 Domain Not Found", "Domain was not found"),
    ERROR_004("No Sites Configured", "No Windflow.io sites are configured in this database"),
    ERROR_005("Database is Empty", "The Windflow database contains no data");

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
