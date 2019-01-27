package util;

import java.sql.*;

import static util.EnvironmentUtil.*;
import static util.LoggingUtil.LOGGER;

public class DBUtil {

    public static Statement setConnection() throws SQLException, ClassNotFoundException {

        Class.forName( DB_DRIVER_CLASS );

        Connection connection = DriverManager.getConnection(
                "jdbc:oracle:thin:@" + DB_HOST + ":" + SSH_PORT + ":XE"
                , DB_USERNAME,
                DB_PASSWORD
        );

        LOGGER.info( String.format( "\tConnecting to the database [%s]\t\n", DB_USERNAME ) );

        return connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
        );

    }

}
