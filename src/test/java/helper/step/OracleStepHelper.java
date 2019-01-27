package helper.step;

import cucumber.api.DataTable;
import org.junit.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static util.LoggingUtil.LOGGER;
import static util.step.CommonStepUtil.replaceSavedElement;

public class OracleStepHelper {

    public static String getQueryValue(ResultSet resultSet, String columnValue) {

        try {

            if ( resultSet.getString( columnValue ) != null )
                return resultSet.getString( columnValue );

        } catch (SQLException e) {

            LOGGER.info( String.format( "The column value [%s] is NOT contained in the query response", columnValue ) );
            Assert.fail( String.format( "The column value [%s] is NOT contained in the query response", columnValue ) );

        }

        return "null";

    }

    public static String setSavedElements(String string,  HashMap dataMap) {

        return replaceSavedElement( string, dataMap );

    }

    public static int getDataTableRowSize( DataTable dataTable ) {

        return dataTable.asLists( String.class ).size();

    }

}
