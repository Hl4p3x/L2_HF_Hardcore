package com.l2jserver.loginserver.config;

import com.l2jserver.common.config.PropertiesParser;

public class Config {

    public static final String MMO_CONFIG_FILE = "./config/server/MMO.properties";
    public static final String EMAIL_CONFIG_FILE = "./config/server/Email.properties";
    public static final String LOGIN_CONFIGURATION_FILE = "./config/server/LoginServer.properties";

    public static boolean LOGIN_SERVER_SCHEDULE_RESTART;
    public static long LOGIN_SERVER_SCHEDULE_RESTART_TIME;

    public static boolean SHOW_LICENCE;
    public static boolean ACCEPT_NEW_GAMESERVER;

    public static boolean AUTO_CREATE_ACCOUNTS;

    public static int PORT_LOGIN;
    public static String LOGIN_BIND_ADDRESS;
    public static int LOGIN_TRY_BEFORE_BAN;
    public static int LOGIN_BLOCK_AFTER_BAN;

    public static boolean FLOOD_PROTECTION;
    public static int FAST_CONNECTION_LIMIT;
    public static int NORMAL_CONNECTION_TIME;
    public static int FAST_CONNECTION_TIME;
    public static int MAX_CONNECTION_PER_IP;

    // Email
    public static String EMAIL_SERVERINFO_NAME;
    public static String EMAIL_SERVERINFO_ADDRESS;
    public static boolean EMAIL_SYS_ENABLED;
    public static String EMAIL_SYS_HOST;
    public static int EMAIL_SYS_PORT;
    public static boolean EMAIL_SYS_SMTP_AUTH;
    public static String EMAIL_SYS_FACTORY;
    public static boolean EMAIL_SYS_FACTORY_CALLBACK;
    public static String EMAIL_SYS_USERNAME;
    public static String EMAIL_SYS_PASSWORD;
    public static String EMAIL_SYS_ADDRESS;
    public static String EMAIL_SYS_SELECTQUERY;
    public static String EMAIL_SYS_DBFIELD;

    public static boolean SHOW_LICENSE;

    public static String GAME_SERVER_LOGIN_HOST;
    public static int GAME_SERVER_LOGIN_PORT;

    public static void load() {
        final PropertiesParser ServerSettings = new PropertiesParser(LOGIN_CONFIGURATION_FILE);

        LOGIN_BIND_ADDRESS = ServerSettings.getString("LoginserverHostname", "*");
        PORT_LOGIN = ServerSettings.getInt("LoginserverPort", 2106);

        ACCEPT_NEW_GAMESERVER = ServerSettings.getBoolean("AcceptNewGameServer", true);

        LOGIN_TRY_BEFORE_BAN = ServerSettings.getInt("LoginTryBeforeBan", 5);
        LOGIN_BLOCK_AFTER_BAN = ServerSettings.getInt("LoginBlockAfterBan", 900);

        LOGIN_SERVER_SCHEDULE_RESTART = ServerSettings.getBoolean("LoginRestartSchedule", false);
        LOGIN_SERVER_SCHEDULE_RESTART_TIME = ServerSettings.getLong("LoginRestartTime", 24);

        SHOW_LICENCE = ServerSettings.getBoolean("ShowLicence", true);

        AUTO_CREATE_ACCOUNTS = ServerSettings.getBoolean("AutoCreateAccounts", true);

        FLOOD_PROTECTION = ServerSettings.getBoolean("EnableFloodProtection", true);
        FAST_CONNECTION_LIMIT = ServerSettings.getInt("FastConnectionLimit", 15);
        NORMAL_CONNECTION_TIME = ServerSettings.getInt("NormalConnectionTime", 700);
        FAST_CONNECTION_TIME = ServerSettings.getInt("FastConnectionTime", 350);
        MAX_CONNECTION_PER_IP = ServerSettings.getInt("MaxConnectionPerIP", 50);

        // Email
        final PropertiesParser emailSettings = new PropertiesParser(EMAIL_CONFIG_FILE);

        EMAIL_SERVERINFO_NAME = emailSettings.getString("ServerInfoName", "Unconfigured L2J Server");
        EMAIL_SERVERINFO_ADDRESS = emailSettings.getString("ServerInfoAddress", "info@myl2jserver.com");

        EMAIL_SYS_ENABLED = emailSettings.getBoolean("EmailSystemEnabled", false);
        EMAIL_SYS_HOST = emailSettings.getString("SmtpServerHost", "smtp.gmail.com");
        EMAIL_SYS_PORT = emailSettings.getInt("SmtpServerPort", 465);
        EMAIL_SYS_SMTP_AUTH = emailSettings.getBoolean("SmtpAuthRequired", true);
        EMAIL_SYS_FACTORY = emailSettings.getString("SmtpFactory", "javax.net.ssl.SSLSocketFactory");
        EMAIL_SYS_FACTORY_CALLBACK = emailSettings.getBoolean("SmtpFactoryCallback", false);
        EMAIL_SYS_USERNAME = emailSettings.getString("SmtpUsername", "user@gmail.com");
        EMAIL_SYS_PASSWORD = emailSettings.getString("SmtpPassword", "password");
        EMAIL_SYS_ADDRESS = emailSettings.getString("EmailSystemAddress", "noreply@myl2jserver.com");
        EMAIL_SYS_SELECTQUERY = emailSettings.getString("EmailDBSelectQuery", "SELECT value FROM account_data WHERE account_name=? AND var='email_addr'");
        EMAIL_SYS_DBFIELD = emailSettings.getString("EmailDBField", "value");
    }

}
