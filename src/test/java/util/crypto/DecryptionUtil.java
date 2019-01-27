package util.crypto;

import com.google.gson.JsonObject;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEObject;
import org.junit.Assert;

import java.util.Arrays;
import java.util.Base64;

import static helper.crypto.DecryptionHelper.getA128gcmContent;
import static util.LoggingUtil.LOGGER;

public class DecryptionUtil {

    public static JsonObject fromJWE(String payload, String rule) {

        JWEObject jweObject;

        EncryptionMethod jweMethod;

        try {

            jweObject = JWEObject.parse( payload );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe payload could NOT been decrypted, because { error: [%s] }\t\n", e.getMessage() ) );
            Assert.fail( String.format( "\tThe payload could NOT been decrypted, because { error: [%s] }\t\n", e.getMessage() ) );
            return null;

        }

        try {

            jweMethod = jweObject.getHeader().getEncryptionMethod();

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe encryption method could NOT been get, because { error: [%s] }\t\n", e.getMessage() ) );
            Assert.fail( String.format( "\tThe encryption method could NOT been get, because { error: [%s] }\t\n", e.getMessage() ) );
            return null;

        }

        switch ( jweMethod.getName().toLowerCase().trim() ) {

            case "a128gcm":
                return getA128gcmContent( jweObject, rule );

            case "a128cbc_hs256":
                return null;

            default:
                return null;

        }

    }

    public static String fromBase64(String encodedKeyString) {

        try {

            return Arrays.toString( Base64.getDecoder().decode( encodedKeyString ) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe encryption method could NOT been get, because { error: [%s] }\t\n", e.getMessage() ) );
            Assert.fail( String.format( "\tThe encryption method could NOT been get, because { error: [%s] }\t\n", e.getMessage() ) );
            return null;

        }

    }

}
