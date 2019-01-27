package helper.crypto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.DirectDecrypter;
import org.junit.Assert;

import java.util.Arrays;
import java.util.Base64;

import static util.ContextMapUtil.context;
import static util.LoggingUtil.LOGGER;

public class DecryptionHelper {

    public static JsonObject getJWEHeader( String[] jweElements ) {

        String jweHeaderEncoded = jweElements[ 0 ];
        String jweHeaderDecoded = null;

        try {

            jweHeaderDecoded = new String( Base64.getDecoder().decode( jweHeaderEncoded ) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe jweHeader could NOT been decrypted: [%s]\t\n", jweHeaderEncoded ) );
            Assert.fail( String.format( "\tThe jweHeader could NOT been decrypted: [%s]\t\n", jweHeaderEncoded ) );

        }

        JsonObject jweHeaderObject = new JsonObject();

        try {

            jweHeaderObject = new JsonParser().parse( jweHeaderDecoded ).getAsJsonObject();

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe jweHeader element is NOT in json format: [%s]\t\n", jweHeaderDecoded ) );
            Assert.fail( String.format( "\tThe jweHeader element is NOT in json format: [%s]\t\n", jweHeaderDecoded ) );

        }

        return jweHeaderObject;

    }

    public static String getEncryptionMethod(JsonObject jweHeaderObject) {

        try {

            return jweHeaderObject.get( "enc" ).getAsString();

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe encrpytion rule (enc) is NOT in the correct format: [%s]\t\n", jweHeaderObject.toString() ) );
            Assert.fail( String.format( "\tThe encrpytion rule (enc) is NOT in the correct format: [%s]\t\n", jweHeaderObject.toString() ) );
            return null;

        }

    }

    public static JsonObject getA128gcmContent(JWEObject jweObject, String rule) {

        byte[] cek = new byte[ 0 ];

        JsonObject challengeResponse = new JsonObject();

        String requestCounter   = null;
        String responseCounter  = null;

        try {

            cek = Base64.getDecoder().decode( context.getValue( "pureCEK" ) );

        } catch (Exception e) {

            LOGGER.info( "\tThe byte array CEK could NOT been decoded\t\n" );
            Assert.fail( "\tThe byte array CEK could NOT been decoded\t\n" );

        }

        cek = Arrays.copyOfRange( cek, cek.length - 16, cek.length );

        try {

            jweObject.decrypt( new DirectDecrypter( cek ) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe payload could NOT been decrypted, because { error: [%s] }\t\n", e.getMessage() ) );
            Assert.fail( String.format( "\tThe payload could NOT been decrypted, because { error: [%s] }\t\n", e.getMessage() ) );

        }

        try {

            challengeResponse = new JsonParser().parse( jweObject.getPayload().toString() ).getAsJsonObject();

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe payload could NOT been parsed, because { error: [%s] }\t\n", e.getMessage() ) );
            Assert.fail( String.format( "\tThe payload could NOT been parsed, because { error: [%s] }\t\n", e.getMessage() ) );

        }

        getBufferJWE( challengeResponse, rule );

        try {

            requestCounter = context.getValue( "requestCounter" );


        } catch (Exception e) {

            LOGGER.info( "\tThe context does NOT include requestCounter\t\n" );
            Assert.fail( "\tThe context does NOT include requestCounter\t\n" );

        }

        try {

            responseCounter = context.getValue( "responseCounter" );


        } catch (Exception e) {

            LOGGER.info( "\tThe context does NOT include responseCounter\t\n" );
            Assert.fail( "\tThe context does NOT include responseCounter\t\n" );

        }


        if ( !requestCounter.equals( responseCounter ) ) {

            LOGGER.info( String.format( "\tThe requestCounter [%s] is NOT equal to responseCounter [%s]\t\n", requestCounter, responseCounter ) );
            Assert.fail( String.format( "\tThe requestCounter [%s] is NOT equal to responseCounter [%s]\t\n", requestCounter, responseCounter ) );

        }

        return challengeResponse;

    }

    private static void getBufferJWE(JsonObject jsonObject, String rule) {

        if ( rule.toLowerCase().contains( "cres" ) ) {

            String acsCounterAtoS = "000";

            if (jsonObject.has( "acsCounterAtoS" ) ) {

                if (jsonObject.get( "acsCounterAtoS" ).getAsString().length() < 4
                        && jsonObject.get( "acsCounterAtoS" ).getAsString().matches( "-?(0|[1-9]\\d*)" ) ) {

                    try {

                        acsCounterAtoS = jsonObject.get("acsCounterAtoS").getAsString();

                    } catch (Exception e) {

                        LOGGER.info( "\tResponse does NOT include acsCounterAtoS\t\n" );
                        Assert.fail( "\tResponse does NOT include acsCounterAtoS\t\n" );

                    }

                }

            }

            context.putPair( "responseCounter", acsCounterAtoS );

            new StringBuilder(acsCounterAtoS);

        } else {

            context.putPair( "responseCounter", "000" );
            new StringBuilder("000");

        }

    }

}
