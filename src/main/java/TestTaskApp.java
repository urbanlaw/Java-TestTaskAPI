import components.ApiServer;
import handlers.*;

public class TestTaskApp
{
    public static void main(String[] args)
    {
        ApiServer apiServer = new ApiServer();
        apiServer.addRoute("/api/install", new Install());
        apiServer.addRoute("/api/login", new Login());
        apiServer.addRoute("/api/logout", new Logout());
        apiServer.addRoute("/api/payment", new Payment());
        apiServer.start();
    }
}


