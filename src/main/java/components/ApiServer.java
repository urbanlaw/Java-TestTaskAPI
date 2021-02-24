package components;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import com.sun.net.httpserver.HttpServer;
import global.Config;
import basic.ApiHandler;

/**
 * Api server class - instantiate, add routes, start
 */
public class ApiServer
{
    private static final int PORT = 8000;

    private HttpServer httpServer;
    private Logger log;
    private DB db;

    public ApiServer()
    {
        try
        {
            log = logFactory();

            try
            {
                db = new DB(Config.DB_URL, Config.DB_USER, Config.DB_PSWD);
            }
            catch (SQLException ex)
            {
                log.log(Level.SEVERE, "", ex);
            }

            this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            this.httpServer.setExecutor(null);
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * binds api handler to route
     */
    public void addRoute(String path, ApiHandler apiHandler)
    {
        apiHandler.setLog(this.log);
        apiHandler.setDB(db);
        apiHandler.init();
        this.httpServer.createContext(path, apiHandler);
    }

    /**
     * starts api server
     */
    public void start()
    {
        this.httpServer.start();
    }

    private Logger logFactory() throws IOException
    {
        Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        FileHandler fileHandler = new FileHandler(Config.LOG_FILE, Config.LOG_APPEND);
        fileHandler.setFormatter(new SimpleFormatter());
        log.addHandler(fileHandler);

        return log;
    }
}
