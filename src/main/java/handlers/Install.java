package handlers;

import java.util.HashMap;
import java.util.logging.Level;
import basic.ApiHandler;
import helpers.EncryptHelper;

/**
 * database installation api handler, FOR TESTING ONLY, MUST BE REMOVED ON PRODUCTION!
 */
public class Install extends ApiHandler
{

    @Override
    public void init()
    {
        authorizationRequired = false;
    }

    protected void handleRequest()
    {
        String query;

        try
        {
            db.update("DROP TABLE IF EXISTS user");
            query = "CREATE TABLE IF NOT EXISTS user  (" +
                    "id INT UNSIGNED NOT NULL auto_increment," +
                    "login VARCHAR(32) NOT NULL," +
                    "pswd VARCHAR (32) NOT NULL," +
                    "token VARCHAR(64)," +
                    "tokenExpire TIMESTAMP NOT NULL DEFAULT 0," +
                    "failedLoginAt TIMESTAMP NOT NULL," +
                    "failedLoginCount TINYINT UNSIGNED NOT NULL DEFAULT 0," +
                    "balance INT UNSIGNED NOT NULL DEFAULT 800," +
                    "PRIMARY KEY (id)," +
                    "UNIQUE (login)," +
                    "UNIQUE (token))";

            db.update(query);

            db.update("DROP TABLE IF EXISTS transaction");
            query = "CREATE TABLE IF NOT EXISTS transaction (" +
                    "id INT NOT NULL auto_increment," +
                    "checkId INT NOT NULL," +
                    "amount DOUBLE NOT NULL," +
                    "PRIMARY KEY (id))";

            db.update(query);

            db.update("INSERT INTO user (login, pswd) VALUES('user1', '" + EncryptHelper.Encode("password1") + "') ON DUPLICATE KEY UPDATE id = id");
            db.update("INSERT INTO user (login, pswd) VALUES('user2', '" + EncryptHelper.Encode("password2") + "') ON DUPLICATE KEY UPDATE id = id");
            db.update("INSERT INTO user (login, pswd) VALUES('user3', '" + EncryptHelper.Encode("password3") + "') ON DUPLICATE KEY UPDATE id = id");
        }
        catch(Exception ex)
        {
            log.log(Level.SEVERE, "", ex);
            sendError("Not installed", 500);
        }

        HashMap<String, String> data = new HashMap<>();
        data.put("msg", "Installed!");
        sendData(data);
    }
}
