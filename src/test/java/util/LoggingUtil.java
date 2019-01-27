package util;


import gherkin.deps.com.google.gson.Gson;
import gherkin.deps.com.google.gson.GsonBuilder;
import gherkin.deps.com.google.gson.JsonParser;
import io.restassured.path.json.JsonPath;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.Logger;
import org.junit.Assert;

import java.util.HashMap;

public class LoggingUtil {

    public static final Logger LOGGER = Logger.getLogger( LoggingUtil.class );

    private static final Gson beautifier = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static void postRequestLogger(String restPage, String jsonBody, Request request) {

        String prettyRequestUrl         =   beautifier.toJson( request.url().url() );
        String prettyRequestMethod      =   beautifier.toJson( request.method() );
        String prettyRequestHeaders     =   beautifier.toJson( request.headers() );
        String prettyRequestBody;

        if ( JsonUtil.isJsonFormed( jsonBody ) )
            prettyRequestBody   =   beautifier.toJson( new JsonParser().parse( jsonBody ) );
        else
            prettyRequestBody   =   jsonBody;

        LOGGER.info( String.format( "\tThe request to [ %s ] is:\n" +
                "|>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>|" +
                "\n" +
                "Url:\t" + prettyRequestUrl +
                "\n" +
                "Method:\t" + prettyRequestMethod +
                "\n" +
                "Headers:\t" + prettyRequestHeaders +
                "\n" +
                "Body:\t" + prettyRequestBody +
                "\n" +
                "|>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>|\n\n", restPage ) );

    }

    public static void getRequestLogger(String restPage, Request request) {

        String prettyRequestUrl         =   beautifier.toJson( request.url().url() );
        String prettyRequestMethod      =   beautifier.toJson( request.method() );
        String prettyRequestHeaders     =   beautifier.toJson( request.headers() );

        LOGGER.info(String.format( "\tThe request to [ %s ] is:\n" +
                "|>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>|" +
                "\n" +
                "Url:\t" + prettyRequestUrl +
                "\n" +
                "Method:\t" + prettyRequestMethod +
                "\n" +
                "Headers:\t" + prettyRequestHeaders +
                "\n" +
                "|>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>|\n\n", restPage ) );

    }

    public static void optionsRequestLogger(String restPage, Request request) {

        String prettyRequestUrl         =   beautifier.toJson( request.url().url() );
        String prettyRequestMethod      =   beautifier.toJson( request.method() );
        String prettyRequestHeaders     =   beautifier.toJson( request.headers() );

        LOGGER.info(String.format( "\tThe request to [ %s ] is:\n" +
                "|>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>|" +
                "\n" +
                "Url:\t" + prettyRequestUrl +
                "\n" +
                "Method:\t" + prettyRequestMethod +
                "\n" +
                "Headers:\t" + prettyRequestHeaders +
                "\n" +
                "|>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>|\n\n", restPage ) );

    }

    public static void putRequestLogger(String restPage, Request request) {

        String prettyRequestUrl         =   beautifier.toJson( request.url().url() );
        String prettyRequestMethod      =   beautifier.toJson( request.method() );
        String prettyRequestHeaders     =   beautifier.toJson( request.headers() );

        LOGGER.info(String.format( "\tThe request to [ %s ] is:\n" +
                "|>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>|" +
                "\n" +
                "Url:\t" + prettyRequestUrl +
                "\n" +
                "Method:\t" + prettyRequestMethod +
                "\n" +
                "Headers:\t" + prettyRequestHeaders +
                "\n" +
                "|>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>|\n\n", restPage ) );

    }

    public static void responseLogger(String restPage, HashMap<String, Object> responseMap, JsonPath responsePath) {

        if ( responseMap.containsKey( "error" ) )
            errorLogger( restPage, responsePath );

        else {

            Response response = (Response) responseMap.get( "response" );
            String prettyResponseCode       =   beautifier.toJson( response.code() );
            String prettyResponseHeaders    =   beautifier.toJson( response.headers() );
            String prettyResponseMessage    =   beautifier.toJson( response.message() );
            String prettyResponseBody       =   beautifier.toJson( responsePath.getMap( "" ) );

            LOGGER.info( String.format( "\tThe response of [ %s ] is:", restPage) );
            System.out.println( "|<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<|" +
                    "\n" +
                    "Status:\t" + prettyResponseCode + " " + prettyResponseMessage +
                    "\n" +
                    "Headers:\t" + prettyResponseHeaders +
                    "\n" +
                    "Body:\t" + prettyResponseBody +
                    "\n" +
                    "|<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<|\n" );

        }


    }

    private static void errorLogger(String restPage, JsonPath responsePath) {

        String prettyResponseBody = beautifier.toJson( responsePath.getMap( "" ) );

        LOGGER.info( String.format( "\tThe error of [ %s ] is:", restPage) );
        System.out.println( "|<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<|" +
                "\n" +
                "Body:\t" + prettyResponseBody +
                "\n" +
                "|<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<|\n" );

    }

    public static void jsonElementRemoveLogger(String restPage, String key) {

        LOGGER.info( String.format( "\tRequest sending [%s] with removing [%s]\t\n", restPage, key ) );

    }

    public static void jsonElementSetLogger(String restPage, String key, String value) {

        LOGGER.info( String.format( "\tRequest sending [%s] with adding [%s] : [%s]\t\n", restPage, key, value ) );

    }

    public static void jsonElementUpdateLogger(String restPage, String key, String value) {

        LOGGER.info( String.format( "\tRequest sending [%s] with replacing [%s] : [%s]\t\n", restPage, key, value ) );

    }

    public static void jsonElementManageExceptionLogger(String restPage, String key) {

        LOGGER.info(String.format("\tRest page [%s] does not contain key [%s]\t\n", restPage, key));
        Assert.fail(String.format("\tRest page [%s] does not contain key [%s]\t\n", restPage, key));

    }

}
