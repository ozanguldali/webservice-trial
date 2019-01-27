package step;

import com.google.gson.JsonObject;
import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import gherkin.formatter.model.DataTableRow;
import helper.JsonHelper;
import io.restassured.path.json.JsonPath;
import okhttp3.Response;
import org.junit.Assert;
import util.ContextMapUtil;
import util.JsonUtil;
import util.ParserUtil;
import util.crypto.EncryptionUtil;
import util.step.RestStepUtil;

import java.util.HashMap;

import static helper.step.ParserHelper.getJsonPathValue;
import static helper.step.RestStepHelper.setResponseObjects;
import static helper.step.RestStepHelper.setSavedElements;
import static step.CommonStepDefinitions.ifAnySaved;
import static util.ContextMapUtil.context;
import static util.EnvironmentUtil.REST_HOST;
import static util.LoggingUtil.LOGGER;
import static util.step.CommonStepUtil.*;
import static util.step.RestStepUtil.checkStatusCode;

public class RestStepDefinitions {

    public static Response response;
    public static JsonPath responsePath;
    private static JsonPath requestPath;
    private static HashMap< HashMap< String, Object >, JsonPath> responseMap = new HashMap<>();

    @When("^I go (\\w+(?: \\w+)*) rest service$")
    public void goRestService(String restPage) {

        JsonObject restObject = ParserUtil.jsonFileParsing( "rest-bases/" + restPage );

        if ( CommonStepDefinitions.ifAnySaved )
            restObject = setSavedElements( restObject, ContextMapUtil.context);

        requestPath = JsonPath.from( String.valueOf( restObject ) );

        responseMap.clear();
        responseMap = RestStepUtil.restResponseHandler( restPage, REST_HOST, restObject );

        setResponseObjects( responseMap );

    }

    @When("^I go (\\w+(?: \\w+)*) rest service by playing with existing parameters:$")
    public void goRestServiceWithParameters(String restPage, DataTable dataTable) {

        JsonObject restObject   =   ParserUtil.jsonFileParsing( "rest-bases/" + restPage );

        if ( CommonStepDefinitions.ifAnySaved )
            restObject = setSavedElements( restObject, ContextMapUtil.context );

        JsonObject headerObject =   restObject.getAsJsonObject( "headers" );

        for ( DataTableRow row : dataTable.getGherkinRows() ) {

            JsonObject bodyObject   =   restObject.getAsJsonObject( "jsonBody" );

            String key      =   row.getCells().get(0);
            String value    =   row.getCells().get(1);

            HashMap<String, Object> restMap = new HashMap<>();
            restMap.clear();

            if ( key.equals( "toBase64URL" ) ) {

                JsonObject encodedObject = new JsonObject();

                try {

                    encodedObject.addProperty( value, EncryptionUtil.toBase64URL( bodyObject ) );
                    LOGGER.info( String.format( "\tThe body of rest page [%s] is encoded as base64url: [%s]\t\n", restPage, encodedObject.get( value ).getAsString() ) );

                } catch (Exception e) {

                    LOGGER.info( "\tThe body of rest page [%s] could NOT been encoded as base64url\t\n" );
                    Assert.fail( "\tThe body of rest page [%s] could NOT been encoded as base64url\t\n" );

                }

                restObject.remove( "jsonBody" );

                restObject.add( "jsonBody", encodedObject );

            }  else if ( key.contains( "toJWE" ) ) {

                JsonObject encodedObject = new JsonObject();

                try {

                    encodedObject.addProperty( value, EncryptionUtil.toJWE( bodyObject, restPage ) );
                    LOGGER.info( String.format( "\tThe body of rest page [%s] is encoded as JWE: [%s]\t\n", restPage, encodedObject.get( value ).getAsString() ) );

                } catch (Exception e) {

                    LOGGER.info( String.format( "\tThe body of rest page [%s] could NOT been encoded as JWE\t\n", restPage ) );
                    Assert.fail( String.format( "\tThe body of rest page [%s] could NOT been encoded as JWE\t\n", restPage ) );

                }

                restObject.remove( "jsonBody" );

                restObject.add( "jsonBody", encodedObject );

            } else if ( key.contains( "header_" ) ) {

                try {

                    headerObject.addProperty( key.replace( "header_", "" ), value );
                    LOGGER.info( String.format( "\tRequest sending [%s] with replacing header [%s] : [%s]\t\n", restPage, key, value ) );

                } catch (Exception e) {

                    LOGGER.info( String.format( "\tRest page [%s] does not contain header [%s]\t\n", restPage, key.replace( "header_", "" ) ) );
                    Assert.fail( String.format( "\tRest page [%s] does not contain header [%s]\t\n", restPage, key.replace( "header_", "" ) ) );

                }

            } else if ( key.contains( "query_" ) ) {

                try {

                    headerObject.addProperty( key.replace( "query_", "" ), value );
                    LOGGER.info( String.format( "\tRequest sending [%s] with replacing query [%s] : [%s]\t\n", restPage, key, value ) );

                } catch (Exception e) {

                    LOGGER.info( String.format( "\tRest page [%s] does not contain query [%s]\t\n", restPage, key.replace( "query_", "" ) ) );
                    Assert.fail( String.format( "\tRest page [%s] does not contain query [%s]\t\n", restPage, key.replace( "query_", "" ) ) );

                }

            } else {

                 if ( key.contains( "add_" ) ) {

                    JsonHelper.setJsonElement( bodyObject, key.replace( "add_", "" ) );

                    restMap = JsonUtil.reachJsonElement( bodyObject, key.replace( "add_", "" ) );

                    restMap.put( "operation", "add" );

                } else {


                    restMap = JsonUtil.reachJsonElement( bodyObject, key );

                    if ( value.equals( "remove" ) )
                        restMap.put( "operation", "remove" );

                    else
                        restMap.put( "operation", "update" );

                }

                 JsonUtil.manageJsonElement( restMap, restPage, value );
                 requestPath = JsonPath.from( String.valueOf( restObject ) );

            }

        }

        responseMap.clear();
        responseMap = RestStepUtil.restResponseHandler( restPage, REST_HOST, restObject );

        setResponseObjects( responseMap );

    }

    @Then("^I save the values of response:$")
    public static void saveValuesOfResponse(DataTable dataTable) {

        for ( DataTableRow row : dataTable.getGherkinRows() ) {

            String leftKey      =   row.getCells().get( 0 );
            String rightKey     =   row.getCells().get( 1 );
            String value        =   responsePath.getString( rightKey );

            saveValues( leftKey, rightKey, value );

        }

        if ( !ifAnySaved )
            ifAnySaved = true;

    }

    @And("^the status is (\\d+) in the response$")
    public void iSeeStatus(int expectedStatusCode) {

        int actualStatusCode = 0;

        try {

            actualStatusCode = response.code();

        } catch (Exception e) {

            LOGGER.info( "\tThe status code could NOT been recognised\t\n" );
            Assert.fail( "\tThe status code could NOT been recognised\t\n" );

        }

        checkStatusCode(actualStatusCode, expectedStatusCode);

    }

    @And("^the elements equal to the followings in the response$")
    public void equalsToFollowings(DataTable dataTable) {

        for ( DataTableRow row : dataTable.getGherkinRows() ) {

            String key              =   row.getCells().get( 0 );
            String expectedValue    =   row.getCells().get( 1 );
            String actualValue      =   responsePath.getString( key );

            if ( responsePath.getJsonObject( key ) == null )
                actualValue = "null";

            if ( doesEqualTwoStrings( actualValue, expectedValue ) )
                LOGGER.info( String.format( "\tIn the parameter [%s]; the expected: [%s] equals to the actual: [%s]\t\n", key, expectedValue, actualValue ) );

            else {

                LOGGER.info( String.format( "\tIn the parameter [%s]; the expected: [%s] BUT the actual: [%s]\t\n", key, expectedValue, actualValue ) );
                Assert.fail( String.format( "\tIn the parameter [%s]; the expected: [%s] BUT the actual: [%s]\t\n", key, expectedValue, actualValue ) );

            }

        }

    }

    @And("^the elements contains the followings in the response$")
    public void containsFollowings(DataTable dataTable) {

        for ( DataTableRow row : dataTable.getGherkinRows() ) {

            String key              =   row.getCells().get( 0 );
            String expectedValue    =   row.getCells().get( 1 );
            String actualValue      =   responsePath.getString( key );

            if ( responsePath.getJsonObject( key ) == null )
                actualValue = "null";

            if ( doesContainsTwoStrings( actualValue, expectedValue ) ) {
                LOGGER.info( String.format( "\tIn the parameter [%s]; the actual: [%s] contains the element: [%s]\t\n", key, actualValue, expectedValue ) );
            }

            else {

                LOGGER.info( String.format( "\tIn the parameter [%s]; the actual: [%s] DOES NOT contain the element: [%s]\t\n", key, actualValue, expectedValue ) );
                Assert.fail( String.format( "\tIn the parameter [%s]; the actual: [%s] DOES NOT contain the element: [%s]\t\n", key, actualValue, expectedValue ) );

            }

        }

    }

    @And("^the elements are included in the response$")
    public void included(DataTable dataTable) {

        for ( DataTableRow row : dataTable.getGherkinRows() ) {

            String expectedKey = row.getCells().get( 0 );

            if ( responsePath.getJsonObject( expectedKey ) != null )
                LOGGER.info( String.format("\tThe expected: [ %s ] is contained in the response\t\n", expectedKey) );

            else {

                LOGGER.info( String.format("\tThe expected: [ %s ] IS NOT contained in the response\t\n", expectedKey) );
                Assert.fail( String.format("\tThe expected: [ %s ] IS NOT contained in the response\t\n", expectedKey) );

            }

        }

    }

    @And("^the elements are not included in the response$")
    public void notIncluded(DataTable dataTable) {

        for ( DataTableRow row : dataTable.getGherkinRows() ) {

            String expectedKey = row.getCells().get( 0 );

            if ( responsePath.getJsonObject( expectedKey ) == null )
                LOGGER.info( String.format("\tThe expected: [ %s ] is not contained in the response\t\n", expectedKey) );

            else {

                LOGGER.info( String.format("\tThe expected: [ %s ] is UNEXPECTEDLY contained in the response\t\n", expectedKey) );
                Assert.fail( String.format("\tThe expected: [ %s ] is UNEXPECTEDLY contained in the response\t\n", expectedKey) );

            }

        }

    }

    @And("^I compare context values with the response$")
    public void compareResponse(DataTable dataTable) {

        for ( DataTableRow row : dataTable.getGherkinRows() ) {

            String keyContext       =   row.getCells().get( 0 );
            String valueContext     =   null;
            String expectedKey      =   row.getCells().get( 1 );
            String expectedValue    =   responsePath.getString( expectedKey );

            try {

                valueContext = ContextMapUtil.context.getValue( keyContext );

            } catch (Exception e) {

                LOGGER.info( String.format( "\tThe key [%s] does NOT been included in the context\t\n", keyContext ) );
                Assert.fail( String.format( "\tThe key [%s] does NOT been included in the context\t\n", keyContext ) );

            }

            if ( responsePath.getJsonObject( expectedKey ) == null )
                expectedValue = "null";

            if ( doesEqualTwoStrings( valueContext, expectedValue ) )
                LOGGER.info( String.format( "\tIn the parameter [%s]; the expected: [%s] equals to the context value: [%s]\t\n", expectedKey, valueContext, expectedValue ) );

            else {

                LOGGER.info( String.format( "\tIn the parameter [%s]; the expected: [%s] BUT the actual: [%s]\t\n", expectedKey, valueContext, expectedValue ) );
                Assert.fail( String.format( "\tIn the parameter [%s]; the expected: [%s] BUT the actual: [%s]\t\n", expectedKey, valueContext, expectedValue ) );

            }

        }

    }

    @And("^I save the values of request:$")
    public void iSaveTheValuesOfRequest(DataTable dataTable) {

        for (DataTableRow row : dataTable.getGherkinRows()) {

            String leftKey = row.getCells().get(0);
            String rightKey = row.getCells().get(1);
            String value = (String) getJsonPathValue(requestPath, "jsonBody." + rightKey);

            saveValues(leftKey, rightKey, value);

        }

    }

    @And("^I set restful timeouts in seconds as:$")
    public void setTimeout(DataTable dataTable) {

        for (DataTableRow row : dataTable.getGherkinRows()) {

            String key = row.getCells().get(0);
            String value = row.getCells().get(1);

            if ( value.matches( "\\d+" ) && Integer.parseInt( value ) != 0 ) {

                if ( key.equals( "connectTimeout" ) || key.equals( "readTimeout" ) || key.equals( "writeTimeout" ) ) {

                    context.putPair( key, value );

                    LOGGER.info( String.format( "\tThe timeout [%s] is set as [%s] seconds\t\n", key, value ) );


                } else {

                    LOGGER.info( String.format( "\tThe timeout key is unknown: [%s]\t\n", key ) );
                    Assert.fail( String.format( "\tThe timeout key is unknown: [%s]\t\n", key ) );

                }

            } else {

                LOGGER.info( String.format( "\tThe timeout value is NOT a positive integer: [%s]\t\n", value ) );
                Assert.fail( String.format( "\tThe timeout value is NOT a positive integer: [%s]\t\n", value ) );

            }


        }

    }

}