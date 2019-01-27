package helper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DBHelper {

    public static int getQueryResultRowSize(ResultSet resultSet) throws SQLException {

        int size = 0;

        if ( resultSet.last() ) {

            size = resultSet.getRow();
            resultSet.beforeFirst();

        }

        return size;

    }

}
