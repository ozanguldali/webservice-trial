package helper.step;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.path.json.JsonPath;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.Assert;
import util.step.RestStepUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static step.RestStepDefinitions.response;
import static step.RestStepDefinitions.responsePath;
import static util.ContextMapUtil.context;
import static util.JsonUtil.isJsonFormed;
import static util.LoggingUtil.LOGGER;
import static util.step.CommonStepUtil.replaceSavedElement;

public class RestStepHelper {

    private static HashMap< String, Object > responseMap = new HashMap<>();
    private static JsonObject errorObject = new JsonObject();

    public static HashMap< String, Object > getMethodType(String restPage, String host, JsonObject pageObject) {

        String method = null;

        try {
            method = pageObject.get( "method" ).getAsString();
        } catch (NullPointerException npe) {

            LOGGER.info( "\tMethod could not been recognized\t\n" );
            Assert.fail( "\tMethod could not been recognized\t\n" );

        }

        switch ( method ) {

            case "post" :
                responseMap = RestStepUtil.postTypeRequest( restPage, host, pageObject );
                break;

            case "get" :
                responseMap = RestStepUtil.getTypeRequest( restPage, host, pageObject );
                break;

            case "options" :
                responseMap = RestStepUtil.optionsTypeRequest( restPage, host, pageObject );
                break;

            case "put" :
                responseMap = RestStepUtil.putTypeRequest( restPage, host, pageObject );
                break;

            default:
                responseMap = null;
                LOGGER.info( String.format( "\tInformal Type of Method: [ %s ]\t\n", method ) );
                Assert.fail( String.format( "\tInformal Type of Method: [ %s ]\t\n", method ) );
                break;

        }

        return responseMap;

    }

    public static HashMap< String, Object > getResponse(String restPage, Request request) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(setTimeoutValues("connectTimeout"), TimeUnit.SECONDS)
                .readTimeout(setTimeoutValues("readTimeout"), TimeUnit.SECONDS)
                .writeTimeout(setTimeoutValues("writeTimeout"), TimeUnit.SECONDS)
                .build();

        context.putPair( "connectTimeout", "3" );
        context.putPair( "readTimeout", "3" );
        context.putPair( "readTimeout", "5" );

        try {

            responseMap.put( "response", client.newCall( request ).execute() );

            return responseMap;

        } catch ( Exception e ) {

            errorObject.addProperty( "messageType", "applicationError" );
            setErrorMessageType( restPage );
            setErrorDescription( e );

            responseMap.put( "error", errorObject );

            return responseMap;

        }

    }

    private static int setTimeoutValues(String key) {

        switch ( key ) {

            case "connectTimeout":

                if ( context.hasValue( key ) )
                    return Integer.parseInt( context.getValue( key ) );

                else
                    return 3;

            case "readTimeout":

                if ( context.hasValue( key ) )
                    return Integer.parseInt( context.getValue( key ) );

                else
                    return 3;

            case "writeTimeout":

                if ( context.hasValue( key ) )
                    return Integer.parseInt( context.getValue( key ) );

                else
                    return 5;

        }

        return 0;

    }

    private static void setErrorDescription(Exception e) {

        errorObject.addProperty( "errorDescription", e.getMessage() );

    }

    private static void setErrorMessageType(String restPage) {

        errorObject.addProperty( "errorMessageType", restPage );

    }

    public static Request.Builder setRestServiceHeader(Request.Builder requestBuilder, JsonObject pageObject) {

        if ( pageObject.has( "headers" ) ) {

            JsonObject headerObject = pageObject.get( "headers" ).getAsJsonObject();

            Set< Map.Entry< String, JsonElement > > entrySet = headerObject.getAsJsonObject().entrySet();

            for ( Map.Entry< String, JsonElement > entry : entrySet ) {

                String headerKey    =   entry.getKey();
                String headerValue  =   headerObject.get( headerKey ).getAsString();

                requestBuilder.addHeader( headerKey, headerValue );

            }

        }

        return requestBuilder;

    }

    public static void setUrl(String restPage, String host, JsonObject pageObject, Request.Builder requestBuilder) {

        if ( pageObject.has( "url" ) )
            requestBuilder.url( pageObject.get( "url" ).getAsString() );

        else if ( pageObject.has( "path" ) )
            requestBuilder.url( host + pageObject.get( "path" ).getAsString() );

        else {

            LOGGER.info( String.format("\tThe request page [%s] does not include URI information\t\n", restPage) );
            Assert.fail( String.format("\tThe request page [%s] does not include URI information\t\n", restPage) );

        }

    }

    public static JsonObject setSavedElements(JsonObject restObject, HashMap dataMap) {

        return new JsonParser().parse( replaceSavedElement( String.valueOf( restObject ), dataMap ) ).getAsJsonObject();

    }

    public static JsonPath setResponsePath( HashMap< String, Object > responseMap ) {

        JsonPath jsonPath;

        JsonObject responseJson = new JsonObject();
        ResponseBody responseBody = null;

        if ( responseMap.containsKey( "response" ) ) {

            Response response = (Response) responseMap.get("response");


            String responseContentType = null;

            try {

                responseContentType = response.header( "Content-Type" );

                if ( responseContentType == null ) {

                    LOGGER.info( "\tThe response does NOT have a content-type.\t\n" );
                    Assert.fail( "\tThe response does NOT have a content-type.\t\n" );

                }

            } catch (Exception e) {

                LOGGER.info( String.format( "\tThe response content-type could NOT been get, because { error : [%s] }\t\n", e.getMessage() ) );
                Assert.fail( String.format( "\tThe response content-type could NOT been get, because { error : [%s] }\t\n", e.getMessage() ) );

            }

            try {

                responseBody = response.body();

                if ( responseBody == null ) {

                    LOGGER.info( "\tThe response does NOT have a body.\t\n" );
                    Assert.fail( "\tThe response does NOT have a body.\t\n" );

                }

            } catch (Exception e) {

                LOGGER.info( String.format( "\tThe response body could NOT been get, because { error : [%s] }\t\n", e.getMessage() ) );
                Assert.fail( String.format( "\tThe response body could NOT been get, because { error : [%s] }\t\n", e.getMessage() ) );

            }

            try {

                String responseBodyString = responseBody.string();

                if ( responseContentType.contains( "application/json" ) )
                    responseJson = new JsonParser().parse( responseBodyString ).getAsJsonObject();

                else if ( responseContentType.contains( "application/jose" ) ) {

                    responseJson.addProperty( "text_plainValue", String.valueOf( responseBodyString ) );

                    if ( !"".equalsIgnoreCase( responseJson.get( "text_plainValue" ).getAsString() ) && isJsonFormed( responseJson.get( "text_plainValue" ).getAsString() ) ) {

                        responseJson = new JsonObject();
                        responseJson = new JsonParser().parse( responseBodyString ).getAsJsonObject();

                    }

                } else if ( responseContentType.contains( "text/html" ) )
                    responseJson.addProperty( "text_htmlValue", String.valueOf( responseBodyString ) );

                else if ( responseContentType.contains( "text/plain" ) ) {

                    responseJson.addProperty( "text_plainValue", String.valueOf( responseBodyString ) );

                    if ( !"".equalsIgnoreCase( responseJson.get( "text_plainValue" ).getAsString() ) && isJsonFormed( responseJson.get( "text_plainValue" ).getAsString() ) ) {

                        responseJson = new JsonObject();
                        responseJson = new JsonParser().parse( responseBodyString ).getAsJsonObject();

                    }

                }

            } catch ( IOException ioe ) {

                LOGGER.info( "\t" + ioe.getMessage() + "\t\n" );
                Assert.fail( "\t" + ioe.getMessage() + "\t\n" );

            }

            String resultString     =   String.valueOf( responseJson );
            jsonPath                =   JsonPath.from( resultString );

            return jsonPath;


        } else if ( responseMap.containsKey( "error" ) ) {

            String responseBodyString = responseMap.get( "error" ).toString();

            responseJson = new JsonParser().parse( responseBodyString ).getAsJsonObject();

            String resultString     =   String.valueOf( responseJson );
            jsonPath                =   JsonPath.from( resultString );

            return jsonPath;

        } else
            return null;


    }

    public static void setResponseObjects(HashMap< HashMap< String, Object >, JsonPath> responseMap) {

        if ( responseMap.keySet().iterator().next().containsKey( "response" ) ){

            response       =  ( Response ) responseMap.keySet().iterator().next().get( "response" );
            responsePath   =  responseMap.get( responseMap.keySet().iterator().next() );

        } else if ( responseMap.keySet().iterator().next().containsKey( "error" ) ) {

            responsePath = JsonPath.from( String.valueOf( new JsonParser().parse( String.valueOf( responseMap.keySet().iterator().next().get( "error" ) ) ).getAsJsonObject() ) );

        }

    }

}
