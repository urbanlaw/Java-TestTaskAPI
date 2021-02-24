package handlers;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.logging.Level;
import basic.ApiHandler;
import global.Config;
import helpers.DateHelper;
import helpers.EncryptHelper;

/**
 * login api handler
 * params login and pswd
 */
public class Login extends ApiHandler
{
    private ResultSet userRs;

    @Override
    public void init()
    {
        authorizationRequired = false;
    }

    public void handleRequest()
    {
        String login = request.getOrDefault("login", "");
        String pswd = request.getOrDefault("pswd", "");

        try
        {
            userRs = db.query("SELECT * FROM user WHERE login = ?", login);
            if (userRs.next())
            {
                int failedCount = userRs.getShort("failedLoginCount");
                long failedTime = DateHelper.StrToMillis(userRs.getString("failedLoginAt"));

                if (!this.isUserBlocked(failedCount, failedTime))
                {
                    String userPswd = userRs.getString("pswd");
                    long currentTime = System.currentTimeMillis();

                    if (userPswd.equals(EncryptHelper.Encode(pswd)))
                    {
                        onLoginSuccess();

                        String expireStr = userRs.getString("tokenExpire");
                        long expireTime = DateHelper.StrToMillis(expireStr);

                        String token = userRs.getString("token");

                        if (expireTime < currentTime || token.isEmpty())
                        {
                            token = this.getUserToken(login);
                            userRs.updateString("token", token);
                        }

                        Timestamp tokenExpire = new Timestamp(currentTime + Config.TOKEN_TTL);
                        userRs.updateTimestamp("tokenExpire", tokenExpire);
                        userRs.updateRow();

                        HashMap<String, String> data = new HashMap<>();
                        data.put("token", token);
                        this.sendData(data);
                        return;
                    }
                    else
                    {
                        onLoginFail();
                        userRs.updateRow();
                        return;
                    }
                }
                else
                {
                    sendError("You was blocked, wait a bit", 423);
                    return;
                }
            }
        }
        catch (Exception ex)
        {
            log.log(Level.WARNING, "", ex);
        }

        sendError("Login or password incorrect", 401);
    }

    private boolean isUserBlocked(int failedCount, long failedTime)
    {
        return failedCount >= Config.LOGIN_FAIL_MAX && System.currentTimeMillis() < failedTime + Config.LOGIN_FAIL_PERIOD;
    }

    /**
     * generates unique user token
     * @param login user login
     * @return token
     */
    private String getUserToken(String login) throws SQLException, NoSuchAlgorithmException
    {
        String token;
        ResultSet rs;

        do
        {
            token = EncryptHelper.UserToken(login);
            rs = db.query("SELECT * FROM user WHERE token = ?", token);
        }
        while (rs.next());

        return token;
    }

    private void onLoginSuccess() throws SQLException
    {
        userRs.updateInt("failedLoginCount", 0);
    }

    private void onLoginFail() throws SQLException
    {
        long currentTime = System.currentTimeMillis();
        long failedTime = DateHelper.StrToMillis(userRs.getString("failedLoginAt"));
        int failedCount = userRs.getShort("failedLoginCount") + 1;

        if(failedTime + Config.LOGIN_FAIL_PERIOD < currentTime)
        {
            userRs.updateInt("failedLoginCount", 1);
        }
        else
        {
            userRs.updateInt("failedLoginCount", failedCount);
        }

        userRs.updateTimestamp("failedLoginAt", new Timestamp(currentTime));

        if(isUserBlocked(failedCount, failedTime))
        {
            sendError("Too many failed login attempts, you are blocked", 401);
        }
        else
        {
            sendError("Login or password incorrect", 401);
        }
    }
}
