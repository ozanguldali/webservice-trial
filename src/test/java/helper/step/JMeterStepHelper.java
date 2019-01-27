package helper.step;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.threads.ThreadGroup;
import org.junit.Assert;

import java.util.Map;
import java.util.Set;

import static util.EnvironmentUtil.CACHE_CONTROL;
import static util.LoggingUtil.LOGGER;

public class JMeterStepHelper {

    public static void setUrl(String restFile, String host, JsonObject restObject, HTTPSamplerProxy httpSampler) {

        if ( restObject.has( "url" ) ) {

            host = restObject.get( "url" ).getAsString();

            if ( host.lastIndexOf( "/" ) == host.length() )
                host = host.substring(0, host.length() - 1);

            String protocol = setHttpProtocol( host, httpSampler );
            assert protocol != null;

            host = host.replace( protocol, "" );

            String port = getHttpPort( host );

            host = setHttpPort(host, httpSampler, port);

            httpSampler.setDomain( host );

            LOGGER.info( String.format( "\tHTTP Domain has been set as: [ %s ]\t\n", host + port) );

        } else if ( restObject.has( "path" ) ) {

            if ( host.lastIndexOf( "/" ) == host.length() - 1 )
                host = host.substring(0, host.length() - 1);

            String protocol =  setHttpProtocol( host, httpSampler );
            assert protocol != null;

            host = host.replace( protocol, "" );

            String port = getHttpPort( host );

            host = setHttpPort( host, httpSampler, port );

            httpSampler.setDomain( host );

            LOGGER.info( String.format( "\tHTTP Domain has been set as: [ %s ]\t\n", host ) );

            httpSampler.setPath( restObject.get("path").getAsString() );

            LOGGER.info( String.format( "\tHTTP Path has been set as: [ %s ]\t\n", restObject.get("path").getAsString() ) );

            if ( port.equals( "" ) )
                LOGGER.info( String.format( "\tHTTP URI has been set as: [ %s ]\t\n", protocol + host + restObject.get("path").getAsString() ) );
            else
                LOGGER.info( String.format( "\tHTTP URI has been set as: [ %s ]\t\n", protocol + host + ":" + port + restObject.get("path").getAsString() ) );

        } else {

            LOGGER.info( String.format( "\tThe request page [%s] does not include URI information\t\n", restFile ) );
            Assert.fail( String.format( "\tThe request page [%s] does not include URI information\t\n", restFile ) );

        }

    }

    private static String setHttpPort(String host, HTTPSamplerProxy httpSampler, String port) {

        if ( !port.equals( "" ) ) {

            host = host.replace( ":" + port, "" );

            try {

                httpSampler.setPort( Integer.valueOf( port ) );
                LOGGER.info( String.format( "\tHTTP Port has been set as: [ %s ]\t\n", port ) );

            } catch ( AssertionError ae ) {

                LOGGER.info( "\tThe URI [%s] does not include valid port information\t\n" );
                Assert.fail( "\tThe URI [%s] does not include valid port  information\t\n" );

            }

        }

        return host;
    }

    private static String getHttpPort(String host) {

        String port = "";

        if ( host.matches( "(.*):(\\d){1,4}" ) ) {

            port = host.substring(host.lastIndexOf(":") + 1);

        }

        return port;

    }

    private static String setHttpProtocol(String uri, HTTPSamplerProxy httpSampler) {

        if ( String.valueOf( uri ).contains( "https://" ) ) {

            httpSampler.setProtocol("https");

            LOGGER.info( "\tHTTP Protocol has been set as: [ https ]\t\n" );

            return "https://";

        } else if ( String.valueOf( uri ).contains( "http://" ) ) {

            httpSampler.setProtocol("http");

            LOGGER.info( "\tHTTP Protocol has been set as: [ http ]\t\n" );

            return  "http://";

        } else {

            LOGGER.info(String.format( "\tThe URI [%s] does not include protocol information\t\n", String.valueOf( uri )));
            Assert.fail(String.format( "\tThe URI [%s] does not include protocol information\t\n", String.valueOf( uri )));

            return null;

        }

    }

    public static void setThreadNumber(String threadNumber, ThreadGroup threadGroup) {

        if ( threadNumber == null ) {

            threadGroup.setNumThreads(1);

            LOGGER.info( "\tThe number of threads has been set as: [ 1 ]\t\n" );

        } else {

            try {

                int threadNumber_int = Integer.parseInt( threadNumber );

                if ( threadNumber_int > 0 ) {

                    threadGroup.setNumThreads(threadNumber_int);

                    LOGGER.info( String.format( "\tThe number of threads has been set as: [ %d ]\t\n", threadNumber_int ) );

                } else {

                    LOGGER.info( String.format( "\tInvalid value of thread number: [%s]\t\n", threadNumber) );
                    Assert.fail( String.format( "\tInvalid value of thread number: [%s]\t\n", threadNumber) );

                }

            } catch ( AssertionError ae ) {

                LOGGER.info( String.format( "\tInvalid type of thread number: [%s]\t\n", threadNumber ) );
                Assert.fail( String.format( "\tInvalid type of thread number: [%s]\t\n", threadNumber ) );

            }

        }

    }

    public static void setRampUp(String rampUp, ThreadGroup threadGroup) {

        if ( rampUp == null ) {

            threadGroup.setRampUp(1);

            LOGGER.info( "\tThe ramp-up time has been set as: [ 1 ]\t\n" );

        } else {

            try {

                int rampUp_int = Integer.parseInt( rampUp );

                if ( rampUp_int > 0 ) {

                    threadGroup.setRampUp(rampUp_int);

                    LOGGER.info( String.format( "\tThe ramp-up time has been set as: [ %d ]\t\n", rampUp_int ) );

                } else {
                    LOGGER.info( String.format( "\tInvalid value of thread number: [%s]\t\n", rampUp ) );
                    Assert.fail( String.format( "\tInvalid value of thread number: [%s]\t\n", rampUp ) );
                }

            } catch ( AssertionError ae ) {

                LOGGER.info( String.format( "\tInvalid type of thread number: [%s]\t\n", rampUp ) );
                Assert.fail( String.format( "\tInvalid type of thread number: [%s]\t\n", rampUp ) );

            }

        }

    }

    public static void setLoop(String loop, ThreadGroup threadGroup, HTTPSamplerProxy httpSampler) {

        LoopController loopController = new LoopController();

        if ( loop == null ) {

            loopController.setLoops( 1 );

            LOGGER.info( "\tThe number of loops has been set as: [ 1 ]\t\n" );

        } else {

            try {

                int loop_int = Integer.parseInt( loop );

                if ( loop_int > 0 ) {

                    loopController.setLoops( loop_int );

                    LOGGER.info( String.format( "\tThe number of loops has been set as: [ %d ]\t\n", loop_int ) );

                } else {
                    LOGGER.info( String.format( "\tInvalid value of thread number: [%s]\t\n", loop) );
                    Assert.fail( String.format( "\tInvalid value of thread number: [%s]\t\n", loop) );
                }

            } catch ( AssertionError ae ) {

                LOGGER.info( String.format( "\tInvalid type of thread number: [%s]\t\n", loop) );
                Assert.fail( String.format( "\tInvalid type of thread number: [%s]\t\n", loop) );

            }

        }

        loopController.addTestElement( httpSampler );
        loopController.setFirst( true );
        loopController.initialize();

        threadGroup.setSamplerController( loopController );

    }

    public static void setHeaders(JsonObject restObject, HeaderManager headerManager) {

        Header[] headers    = new Header[5];
        headers[ 0 ]       =   new Header();

        String headerKey    =   "Cache-Control";
        String headerValue  =   CACHE_CONTROL;

        headers[ 0 ].setName( headerKey );
        headers[ 0 ].setValue( headerValue );

        headerManager.add( headers[ 0 ] );

        LOGGER.info( String.format( "\tThe header has been set as: [ %s ] - [ %s ]\t\n", headerKey, headerValue ) );

        int i = 1;

        if ( restObject.has( "headers" ) ) {

            JsonObject headerObject = restObject.get( "headers" ).getAsJsonObject();

            Set< Map.Entry<String, JsonElement> > entrySet = headerObject.getAsJsonObject().entrySet();

            for ( Map.Entry<String, JsonElement> entry : entrySet ) {

                headerKey    =   entry.getKey();
                headerValue  =   headerObject.get( headerKey ).getAsString();

                try {

                    headers[ i ]       =   new Header();

                    headers[ i ].setName(headerKey);
                    headers[ i ].setValue(headerValue);

                    headerManager.add( headers[ i ] );

                    LOGGER.info( String.format( "\tThe header has been set as: [ %s ] - [ %s ]\t\n", headerKey, headerValue ) );

                } catch ( AssertionError ae ) {

                    LOGGER.info( String.format( "\tThe header [ %s ] - [ %s ] could NOT been set\t\n", headerKey, headerValue ) );
                    Assert.fail( String.format( "\tThe header [ %s ] - [ %s ] could NOT been set\t\n", headerKey, headerValue ) );

                }

                i++;

            }

        }

    }
}
