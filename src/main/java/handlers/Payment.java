package handlers;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.logging.Level;
import basic.ApiHandler;
import components.Money;

/**
 * payment api handler, thread safe
 * params token
 */
public class Payment extends ApiHandler
{

    @Override
    protected void handleRequest()
    {
        String token = request.getOrDefault("token", "");

        Integer cost = 110;

        try
        {
            ResultSet rs = db.query("SELECT * FROM user WHERE token = ?", token);
            if(rs.next())
            {
                Money balance = new Money(rs.getInt("balance"));

                if (balance.withdraw(cost))
                {
                    int rowCount = db.update("UPDATE user SET balance = balance - ? WHERE token = ?", cost.toString(), token);

                    if (rowCount > 0)
                    {
                        HashMap<String, String> data = new HashMap<>();
                        data.put("payment", "ok");
                        data.put("balance", balance.toString());
                        this.sendData(data);
                        return;
                    }
                }
                else
                {
                    HashMap<String, String> data = new HashMap<>();
                    data.put("payment", "failed");
                    data.put("reason", "No money");
                    data.put("balance", balance.toString());
                    this.sendData(data);
                    return;
                }
            }
        }
        catch (Exception ex)
        {
            log.log(Level.WARNING, "", ex);
        }

        sendError("Payment failed", 200);
    }
}