package basic;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.*;
import global.Config;
import components.DB;

/**
 * Base class for all api endpoints
 */
public abstract class ApiHandler implements HttpHandler
{
    private String handlePath;
    protected Logger log;
    protected DB db;
    protected HttpExchange exchange;
    protected HashMap<String, String> request;

    protected boolean authorizationRequired = true;

    protected ApiHandler()
    {

    }

    /**
     * Api handler method, write business logic in override
     */
    protected abstract void handleRequest() throws Exception;

    @Override
    public final void handle(HttpExchange exchange) throws IOException
    {
        if(exchange.getRequestURI().getPath().equals(this.handlePath))
        {
            this.exchange = exchange;
            this.request = this.parseRequest();

            try
            {
                if (authorize())
                {
                    this.handleRequest();
                }
            }
            catch (Exception ex)
            {
                log.log(Level.SEVERE, "", ex);
                exchange.sendResponseHeaders(500, -1);
            }
        }
        else
        {
            exchange.sendResponseHeaders(404, -1);
        }

        exchange.close();
    }

    /*
    public void setContext(HttpContext httpContext)
    {
        this.context = httpContext;
    }
     */

    public void setLog(Logger logger)
    {
        this.log = logger;
    }

    public void setDB(DB db)
    {
        this.db = db;
    }

    public void setHandlePath(String path)
    {
        this.handlePath = path;
    }

    /**
     * Override it to customize super class or do some logic before api start
     */
    public void init()
    {
    }


    protected void sendError(String msg, int code)
    {
        HashMap<String, String> map = new HashMap<>();
        map.put("status", "error");
        map.put("errorMsg", msg);
        this.sendResponse(map, code);
    }

    protected void sendData(HashMap<String, String> data)
    {
        this.sendData(data, 200);
    }

    protected void sendData(HashMap<String, String> data, int code)
    {
        HashMap<String, String> map = new HashMap<>();
        map.put("status", "ok");
        map.putAll(data);
        this.sendResponse(map, code);
    }

    protected boolean authorize()
    {
        if (!authorizationRequired)
        {
            return true;
        }

        String token = request.getOrDefault("token", "");
        try
        {
            long currentTime = System.currentTimeMillis();
            Timestamp currentTS = new Timestamp(System.currentTimeMillis());

            ResultSet rs = db.query("SELECT * FROM user WHERE ? < tokenExpire AND token = ?", currentTS.toString(), token);

            if (rs.next())
            {
                Timestamp tokenExpire = new Timestamp(currentTime + Config.TOKEN_TTL);
                rs.updateTimestamp("tokenExpire", tokenExpire);
                rs.updateRow();
                return true;
            }
        }
        catch (Exception ex)
        {
            log.log(Level.WARNING, "", ex);
        }

        sendError("Unauthorized", 401);
        return false;
    }

    private void sendResponse(HashMap<String, String> data, int code)
    {
        try
        {
            JSONObject json = new JSONObject(data);
            String dataString = json.toString();

            Headers headers = exchange.getResponseHeaders();
            //headers.set("Content-Type", "application/json");

            exchange.sendResponseHeaders(code, dataString.getBytes().length);
            OutputStream output = exchange.getResponseBody();
            output.write(dataString.getBytes());
            output.flush();
        }
        catch (IOException ex)
        {
            // shit happens
        }
    }

    private HashMap<String, String> parseRequest()
    {
        HashMap<String, String> result = new HashMap<>();

        String queryStr = this.exchange.getRequestURI().getQuery();
        if (queryStr != null)
        {
            for (String param : queryStr.split("&"))
            {
                String[] pair = param.split("=");
                if (pair.length > 1)
                {
                    result.put(pair[0], pair[1]);
                }
                else if (pair.length == 1)
                {
                    result.put(pair[0], "");
                }
            }
        }

        return result;
    }
}
