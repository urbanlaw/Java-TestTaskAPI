package handlers;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.logging.Level;
import basic.ApiHandler;

/**
 * logout api handler
 * params token
 */
public class Logout extends ApiHandler
{
    public void handleRequest()
    {
        String token = request.getOrDefault("token", "");

        try
        {
            ResultSet rs = db.query("SELECT * FROM user WHERE token = ?", token);

            if(rs.next())
            {
                rs.updateNull("tokenExpire");
                rs.updateNull("token");
                rs.updateRow();

                HashMap<String, String> data = new HashMap<>();
                data.put("token", "");
                this.sendData(data);
                return;
            }
        }
        catch (Exception ex)
        {
            log.log(Level.WARNING, "", ex);
        }

        sendError("Logout failed", 200);
    }
}
