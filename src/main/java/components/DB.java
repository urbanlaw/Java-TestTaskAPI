package components;

import java.sql.*;

/**
 * Database adapter/facade class
 */
public class DB
{
    private Connection connection;

    public DB(String url, String user, String pswd) throws SQLException
    {
        connection = DriverManager.getConnection(url, user, pswd);
    }

    /**
     * selects data from db
     * @return ResultSet
     */
    public ResultSet query(String queryStr, String... values) throws SQLException
    {
        PreparedStatement preparedStatement = connection.prepareStatement(queryStr, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        for (int i = 0; i < values.length; i++)
        {
            preparedStatement.setString(i + 1, values[i]);
        }

        return preparedStatement.executeQuery();
    }

    /**
     * updates data in db
     * @return updated rows count
     */
    public int update(String queryStr, String... values) throws SQLException
    {
        PreparedStatement preparedStatement = connection.prepareStatement(queryStr);

        for (int i = 0; i < values.length; i++)
        {
            preparedStatement.setString(i + 1, values[i]);
        }

        return preparedStatement.executeUpdate();
    }
}
