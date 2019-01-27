package util.step;

import java.sql.ResultSet;

public class OracleStepUtil {

    public static boolean hasColumn(ResultSet resultSet, String column ) {

        try {

            resultSet.findColumn( column );

            return true;

        } catch ( Exception e ) {

            return false;

        }

    }

}
