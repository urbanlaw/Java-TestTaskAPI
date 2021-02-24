package global;

/**
 * global configuration class
 */
public class Config
{
    public static final String DB_URL = "jdbc:mysql://localhost:3306/test"; // db url
    public static final String DB_USER = "qwe"; // db user
    public static final String DB_PSWD = "qwe"; // db password

    public static final String LOG_FILE = "ApiLog.txt"; // log file path
    public static final boolean LOG_APPEND = true; // false - clear log file on startup

    public static final int TOKEN_TTL = 15 * 60 * 1000; // after this time unused token will expire
    public static final int LOGIN_FAIL_MAX = 3; // max failed login attempts before ban
    public static final int LOGIN_FAIL_PERIOD = 15 * 60 * 1000; // ban time and fail login attempts period
}
