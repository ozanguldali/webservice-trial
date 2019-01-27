package util.step;

import com.google.gson.JsonObject;
import helper.step.JMeterStepHelper;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.threads.ThreadGroup;
import org.junit.Assert;

import static util.LoggingUtil.LOGGER;

public class JMeterUtil {

    public static void getJMeterRequestMethod(String restFile, String host, JsonObject restObject, HTTPSamplerProxy httpSampler, HeaderManager headerManager) {

        String method = restObject.get("method").getAsString();

        switch ( method ) {

            case "post" :
                postJMeterRequest(restFile, host, restObject, httpSampler, headerManager);
                break;

            case "get" :
                getJMeterRequest(restFile, host, restObject, httpSampler, headerManager);
                break;

            default:
                LOGGER.info( String.format( "\tInformal Type of Method: [ %s ]\t\n", method ) );

        }

    }

    private static void postJMeterRequest(String restFile, String host, JsonObject restObject, HTTPSamplerProxy httpSampler, HeaderManager headerManager) {

        try {

            httpSampler.setMethod("POST");

            LOGGER.info( "\tPOST request is preparing...\t\n" );

        } catch ( AssertionError ae ) {

            LOGGER.info( "\tPOST request is preparing...\t\n" );
            Assert.fail( "\tPOST request is preparing...\t\n" );

        }

        JMeterStepHelper.setUrl( restFile, host, restObject, httpSampler );

        JMeterStepHelper.setHeaders( restObject, headerManager );

        try {

            httpSampler.setMethod("POST");

            httpSampler.setPostBodyRaw( true );

            httpSampler.addNonEncodedArgument( "body", String.valueOf( restObject.get( "jsonBody" ) ), "=" );

            LOGGER.info( String.format( "\tRequest body has been set as: [ %s ]", String.valueOf( restObject.get( "jsonBody" ) ) ) );

        } catch ( AssertionError ae ) {

            LOGGER.info( "\tRequest body could NOT been set...\t\n" );
            Assert.fail( "\tRequest body could NOT been set...\t\n" );

        }

    }

    private static void getJMeterRequest(String restFile, String host, JsonObject restObject, HTTPSamplerProxy httpSampler, HeaderManager headerManager) {

        try {

            httpSampler.setMethod("GET");

            LOGGER.info( "\tGET request is preparing...\t\n" );

        } catch ( AssertionError ae ) {

            LOGGER.info( "\tGET request is preparing...\t\n" );
            Assert.fail( "\tGET request is preparing...\t\n" );

        }

        JMeterStepHelper.setUrl( restFile, host, restObject, httpSampler );

        JMeterStepHelper.setHeaders( restObject, headerManager  );

    }

    public static void setJMeterThreadProperties(String threadNumber, String rampUp, String loop, ThreadGroup threadGroup, HTTPSamplerProxy httpSampler ) {

        JMeterStepHelper.setThreadNumber( threadNumber, threadGroup);
        JMeterStepHelper.setRampUp( rampUp, threadGroup);
        JMeterStepHelper.setLoop( loop, threadGroup, httpSampler );

    }

}
