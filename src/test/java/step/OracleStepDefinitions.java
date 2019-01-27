package step;

import com.google.gson.JsonObject;
import cucumber.api.DataTable;
import cucumber.api.java.en.Then;
import gherkin.formatter.model.DataTableRow;
import org.junit.Assert;
import util.DBUtil;
import util.ParserUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static helper.DBHelper.getQueryResultRowSize;
import static helper.step.OracleStepHelper.*;
import static step.CommonStepDefinitions.ifAnySaved;
import static util.ContextMapUtil.context;
import static util.LoggingUtil.LOGGER;
import static util.step.CommonStepUtil.saveValues;
import static util.step.OracleStepUtil.hasColumn;

public class OracleStepDefinitions {

    private static Statement statement;
    private static ResultSet resultSet;

    @Then("^I lookup with values (\\w+(?: \\w+)*)$")
    public void iLookupWith(String queryKey, DataTable table) throws SQLException {

        try {

            statement = DBUtil.setConnection();

        } catch ( SQLException | ClassNotFoundException e ) {

            LOGGER.info( String.format( "\tThe connection could NOT been set, because { error: [ %s ] }\t\n", e.getMessage() ) );
            Assert.fail( String.format( "\tThe connection could NOT been set, because { error: [ %s ] }\t\n", e.getMessage() ) );

        }

        JsonObject jsonObject   =   ParserUtil.jsonFileParsing( "sql" );
        String queryValue       =   jsonObject.get( queryKey ).getAsString();

        if ( ifAnySaved )
            queryValue = setSavedElements( queryValue, context );

        for ( DataTableRow row : table.getGherkinRows() ) {

            String key      =   ":" + row.getCells().get( 0 );
            String value    =   row.getCells().get( 1 );

            queryValue = queryValue.replace( key, value );

            LOGGER.info(String.format( "\tRunning DB Query [%s] with [%s] : [%s]\t\n", queryKey, key.replace( ":","" ), value ) );

        }

        LOGGER.info(String.format( "\tSending DB Query [%s]\t\n", queryValue ) );

        resultSet = statement.executeQuery( queryValue );

    }

    @Then("^I lookup for (\\w+(?: \\w+)*)$")
    public void iLookupFor(String queryKey) throws SQLException {

        try {

            statement = DBUtil.setConnection();

        } catch ( SQLException | ClassNotFoundException e ) {

            LOGGER.info( String.format( "\tThe connection could NOT been set, because { error: [ %s ] }\t\n", e.getMessage() ) );
            Assert.fail( String.format( "\tThe connection could NOT been set, because { error: [ %s ] }\t\n", e.getMessage() ) );

        }

        JsonObject jsonObject   =   ParserUtil.jsonFileParsing( "sql" );
        String queryValue       =   jsonObject.get( queryKey ).getAsString();

        if ( ifAnySaved )
            queryValue = setSavedElements( queryValue, context );

        LOGGER.info(String.format( "\tSending DB Query [%s]\t\n", queryValue ) );

        resultSet = statement.executeQuery( queryValue );

    }

    @Then("^I check sql query single return contains$")
    public void lookupReturnsContain(DataTable tableReturns) throws SQLException {

        resultSet.beforeFirst();

        int counter = 0;

        while ( resultSet.next() && counter == 0 ) {

            for ( DataTableRow row : tableReturns.getGherkinRows() ) {

                String keyTable     =   row.getCells().get( 0 );
                String valueTable   =   row.getCells().get( 1 );

                if ( hasColumn( resultSet, keyTable ) ) {

                    String valueQuery = getQueryValue( resultSet, keyTable );

                    if ( !( valueQuery.trim() ).contains( valueTable ) ) {

                        LOGGER.info( String.format( "\tThe column [%s] having the value [%s] does NOT contain the expected [%s]\t\n", keyTable, valueQuery, valueTable ) );
                        Assert.fail( String.format( "\tThe column [%s] having the value [%s] does NOT contain the expected [%s]\t\n", keyTable, valueQuery, valueTable ) );

                    } else
                        LOGGER.info( String.format( "\tThe column [%s] having the value [%s] contains the expected [%s]\t\n", keyTable, valueQuery, valueTable ) );

                } else {

                    LOGGER.info( String.format( "\tThe column value [%s] is NOT contained in the query response\t\n", keyTable ) );
                    Assert.fail( String.format( "\tThe column value [%s] is NOT contained in the query response\t\n", keyTable ) );

                }

            }

            counter++;

        }

    }

    @Then("^I check sql query single return equals$")
    public void lookupReturnsEquals(DataTable tableReturns) throws SQLException {

        resultSet.beforeFirst();

        int counter = 0;

        while ( resultSet.next() && counter == 0 ) {

            for ( DataTableRow row : tableReturns.getGherkinRows() ) {

                String keyTable     =   row.getCells().get( 0 );
                String valueTable   =   row.getCells().get( 1 );

                if ( hasColumn( resultSet, keyTable ) ) {

                    String valueQuery = getQueryValue( resultSet, keyTable );

                    if ( !( valueQuery.trim() ).contains( valueTable ) ) {

                        LOGGER.info( String.format( "\tThe column [%s] having the value [%s] does NOT equal to the expected [%s]\t\n", keyTable, valueQuery, valueTable ) );
                        Assert.fail( String.format( "\tThe column [%s] having the value [%s] does NOT equal to the expected [%s]\t\n", keyTable, valueQuery, valueTable ) );

                    } else
                        LOGGER.info( String.format( "\tThe column [%s] having the value [%s] equals to the expected [%s]\t\n", keyTable, valueQuery, valueTable ) );

                } else {

                    LOGGER.info( String.format( "\tThe column value [%s] is NOT contained in the query response\t\n", keyTable ) );
                    Assert.fail( String.format( "\tThe column value [%s] is NOT contained in the query response\t\n", keyTable ) );

                }

            }

            counter++;

        }

    }

    @Then("^I verify multiple values of sql query contain$")
    public void verifyValuesContain(DataTable tableReturns) throws SQLException {

        resultSet.beforeFirst();

        int counter = 1;

        List< String > columnsTable = tableReturns.getGherkinRows().get( counter - 1 ).getCells();

        int columnsTableNumber  =   columnsTable.size();
        int rowsTableNumber     =   getDataTableRowSize( tableReturns );
        int rowsQueryNumber     =   getQueryResultRowSize( resultSet );

        if ( rowsTableNumber - 1 > rowsQueryNumber ) {
            LOGGER.info( String.format( "\tThe DataTable [%d] has more than SQL Query has [%d]\t\n", rowsTableNumber - 1, rowsQueryNumber ) );
            Assert.fail( String.format( "\tThe DataTable [%d] has more than SQL Query has [%d]\t\n", rowsTableNumber - 1, rowsQueryNumber ) );
        }

        while ( resultSet.next() && counter <= rowsTableNumber - 1 ) {

            List< String > rowsTable = tableReturns.getGherkinRows().get( counter ).getCells();

            for ( int i = 0; i < columnsTableNumber; i++ ) {

                String valueTable   =   rowsTable.get( i );
                String keyTable     =   columnsTable.get( i );

                String valueQuery = getQueryValue( resultSet, keyTable );

                if ( !( valueQuery.trim() ).contains( valueTable ) ) {

                    LOGGER.info( String.format( "\tThe column [%s] having the value [%s] does NOT contain the expected [%s]\t\n", keyTable, valueQuery, valueTable ) );
                    Assert.fail( String.format( "\tThe column [%s] having the value [%s] does NOT contain the expected [%s]\t\n", keyTable, valueQuery, valueTable ) );

                } else {
                    LOGGER.info( String.format( "\tThe column [%s] having the value [%s] contains the expected [%s]\t\n", keyTable, valueQuery, valueTable ) );
                }

            }

            counter++;

        }

    }

    @Then("^I verify multiple values of sql query equal$")
    public void verifyValuesEqual(DataTable tableReturns) throws SQLException {

        resultSet.beforeFirst();

        int counter = 1;

        List< String > columnsTable = tableReturns.getGherkinRows().get( counter - 1 ).getCells();

        int columnsTableNumber  =   columnsTable.size();
        int rowsTableNumber     =   getDataTableRowSize( tableReturns );
        int rowsQueryNumber     =   getQueryResultRowSize( resultSet );

        if ( rowsTableNumber - 1 > rowsQueryNumber ) {
            LOGGER.info( String.format( "\t\nThe DataTable [%d] has more than SQL Query has [%d]\t\n", rowsTableNumber - 1, rowsQueryNumber ) );
            Assert.fail( String.format( "\t\nThe DataTable [%d] has more than SQL Query has [%d]\t\n", rowsTableNumber - 1, rowsQueryNumber ) );
        }

        while ( resultSet.next() && counter <= rowsTableNumber - 1 ) {

            List< String > rowsTable = tableReturns.getGherkinRows().get( counter ).getCells();

            for ( int i = 0; i < columnsTableNumber; i++ ) {

                String valueTable   =   rowsTable.get( i );
                String keyTable     =   columnsTable.get( i );

                String valueQuery = getQueryValue( resultSet, keyTable );

                if ( !( valueQuery.trim() ).contains( valueTable ) ) {

                    LOGGER.info( String.format( "\tThe column [%s] having the value [%s] does NOT equal to the expected [%s]\t\n", keyTable, valueQuery, valueTable ) );
                    Assert.fail( String.format( "\tThe column [%s] having the value [%s] does NOT equal to the expected [%s]\t\n", keyTable, valueQuery, valueTable ) );

                } else {
                    LOGGER.info( String.format( "\tThe column [%s] having the value [%s] equals to the expected [%s]\t\n", keyTable, valueQuery, valueTable ) );
                }

            }

            counter++;

        }

    }

    @Then("^I save the values of query return$")
    public static void saveValuesOfQueryReturn(DataTable dataTable) {

        for ( DataTableRow row : dataTable.getGherkinRows() ) {

            String leftKey      =   row.getCells().get( 0 );
            String rightKey     =   row.getCells().get( 1 );
            String value        =   getQueryValue( resultSet, rightKey );

            saveValues( leftKey, rightKey, value );

        }

        if ( !ifAnySaved )
            ifAnySaved = true;

    }
    
}
