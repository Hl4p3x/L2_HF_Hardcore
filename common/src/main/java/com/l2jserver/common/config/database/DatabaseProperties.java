package com.l2jserver.common.config.database;

public class DatabaseProperties {

    public String driver = "org.mariadb.jdbc.Driver";
    public String url = "jdbc:mariadb://localhost/l2jls";
    public String login = "root";
    public String password = "";
    public Integer maximumConnections = 10;
    public Integer maximumIdleTime = 0;
    public Integer connectionTimeout = 60000;

    public DatabaseProperties() {
    }

    public DatabaseProperties(String driver, String url, String login, String password, Integer maximumConnections, Integer maximumIdleTime, Integer connectionTimeout) {
        this.driver = driver;
        this.url = url;
        this.login = login;
        this.password = password;
        this.maximumConnections = maximumConnections;
        this.maximumIdleTime = maximumIdleTime;
        this.connectionTimeout = connectionTimeout;
    }

}
