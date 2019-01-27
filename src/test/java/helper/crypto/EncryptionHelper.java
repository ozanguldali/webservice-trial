package helper.crypto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectEncrypter;
import org.apache.cxf.rs.security.jose.jwk.JsonWebKey;
import org.apache.cxf.rs.security.jose.jwk.JwkUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import util.LoggingUtil;
import util.ParserUtil;
import util.crypto.EncryptionUtil;

import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static util.ContextMapUtil.context;
import static util.LoggingUtil.LOGGER;
import static util.crypto.EncryptionUtil.sdkPrivateKey;

public class EncryptionHelper {

    private static JWEHeader headers;
    private static JWEObject object;

    private static ECGenParameterSpec getCurveParam(String crv) {

        switch (crv) {

            case "P-256":
                return new ECGenParameterSpec(JsonWebKey.EC_CURVE_P256);

            case "P-384":
                return new ECGenParameterSpec(JsonWebKey.EC_CURVE_P384);

            case "P-521":
                return new ECGenParameterSpec(JsonWebKey.EC_CURVE_P521);

            default:
                LoggingUtil.LOGGER.info(String.format("\tThe curve type is not valid for: [ %s ]\t\n", crv));
                Assert.fail(String.format("\tThe curve type is not valid for: [ %s ]\t\n", crv));
                return null;

        }

    }

    private static ECGenParameterSpec getRSAParam(String crv) {

        switch (crv) {

            case "n":
                return new ECGenParameterSpec(JsonWebKey.RSA_MODULUS);

            case "e":
                return new ECGenParameterSpec(JsonWebKey.RSA_PUBLIC_EXP);

            case "d":
                return new ECGenParameterSpec(JsonWebKey.EC_PRIVATE_KEY);

            case "p":
                return new ECGenParameterSpec(JsonWebKey.RSA_FIRST_PRIME_FACTOR);

            case "q":
                return new ECGenParameterSpec(JsonWebKey.RSA_SECOND_PRIME_FACTOR);

            case "dp":
                return new ECGenParameterSpec(JsonWebKey.RSA_FIRST_PRIME_CRT);

            case "dq":
                return new ECGenParameterSpec(JsonWebKey.RSA_SECOND_PRIME_CRT);

            case "qi":
                return new ECGenParameterSpec(JsonWebKey.RSA_FIRST_CRT_COEFFICIENT);

            default:

                LoggingUtil.LOGGER.info(String.format("\tThe curve type is not valid for: [ %s ]\t\n", crv));
                Assert.fail(String.format("\tThe curve type is not valid for: [ %s ]\t\n", crv));
                return null;

        }

    }

    private static ECGenParameterSpec getOctetParam(String crv) {

        if (crv.equals("k"))
            return new ECGenParameterSpec(JsonWebKey.EC_CURVE_P256);

        LOGGER.info(String.format("\tThe curve type is not valid for: [ %s ]\t\n", crv));
        Assert.fail(String.format("\tThe curve type is not valid for: [ %s ]\t\n", crv));
        return null;

    }

    private static KeyPair getEphermalKeys(String kty, String crv) throws InvalidAlgorithmParameterException, NoSuchProviderException, NoSuchAlgorithmException {

        ECGenParameterSpec ecGenSpec;

        switch (kty) {

            case "EC":
                ecGenSpec = getCurveParam(crv);
                break;

            case "RSA":
                ecGenSpec = getRSAParam(crv);
                break;

            case "oct":
                ecGenSpec = getOctetParam(crv);
                break;

            default:
                ecGenSpec = getCurveParam(crv);
                break;
        }

        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDH", "BC");

        keyPairGenerator.initialize(ecGenSpec, new SecureRandom());

        return keyPairGenerator.generateKeyPair();

    }

    public static ECPrivateKey getPrivateKey(KeyPair keyPair) {

        return (ECPrivateKey) keyPair.getPrivate();

    }

    public static ECPublicKey getPublicKey(KeyPair keyPair) {

        return (ECPublicKey) keyPair.getPublic();

    }

    public static void setEncryptionData(HashMap<Object, Object> hashMap, String rule) {

        if (rule.toLowerCase().contains("areq")) {

            JsonObject acsSignedContent = new JsonParser().parse(new String(Base64.getDecoder().decode(context.getValue("acsSignedContent").split(Pattern.quote("."))[1]))).getAsJsonObject();
            JsonObject acsEphemPubKey = acsSignedContent.get("acsEphemPubKey").getAsJsonObject();
            ECPublicKey acsPublicKey = JwkUtils.toECPublicKey(JwkUtils.readJwkKey(acsEphemPubKey.toString()));

            JsonWebKey sdkPrivKey = JwkUtils.readJwkKey( sdkPrivateKey );
            ECPrivateKey sdkPrivateKey = JwkUtils.toECPrivateKey( sdkPrivKey );

            byte[] partyUInfo = null;

            String sdkReferenceNumber = ParserUtil.jsonFileParsing( "rest-bases/" + rule ).getAsJsonObject( "jsonBody" ).get( "sdkReferenceNumber" ).getAsString();

            String algoName = "ECDH-ES";

            String algoKeyBitLen = "256";

            EncryptionUtil.generateEncryptionMap( hashMap, sdkPrivateKey, acsPublicKey, partyUInfo, sdkReferenceNumber.getBytes(), algoName, Integer.valueOf( algoKeyBitLen ) );

        } else

            hashMap.clear();

    }

    private static StringBuilder setBufferJWE(JsonObject jsonObject, String rule) {

        if ( rule.toLowerCase().contains( "creq" ) ) {

            String sdkCounterStoA = "000";

            if (jsonObject.has( "sdkCounterStoA" ) ) {

                if (jsonObject.get( "sdkCounterStoA" ).getAsString().length() < 4
                        && jsonObject.get( "sdkCounterStoA" ).getAsString().matches( "(0|[1-9]\\d*)" ) ) {

                    try {

                        sdkCounterStoA = jsonObject.get("sdkCounterStoA").getAsString();

                    } catch (Exception e) {

                        LOGGER.info(String.format( "\tRest page [%s] does NOT contain element [sdkCounterStoA]\t\n", rule ) );
                        Assert.fail(String.format( "\tRest page [%s] does NOT contain element [sdkCounterStoA]\t\n", rule ) );

                    }

                }

            }

            context.putPair( "requestCounter", sdkCounterStoA );

            return new StringBuilder( sdkCounterStoA );

        } else {

            context.putPair( "requestCounter", "000" );
            return new StringBuilder( "000" );

        }

    }

    private static JWEHeader setHeaderJWE( String rule, String method) {

        String value = "";

        if ( rule.toLowerCase().contains( "creq" ) ) {


            try {

                value = context.getValue("acsTransID");

            } catch (Exception e) {

                LOGGER.info( "\tThe project storage map does NOT contain element [acsTransID]\t\n" );
                Assert.fail( "\tThe project storage map does NOT contain element [acsTransID]\t\n" );

            }

        }

        switch ( method ) {

            case "A128GCM":
                headers = new JWEHeader.Builder( JWEAlgorithm.DIR, EncryptionMethod.A128GCM ).keyID( value ).build();

                break;

            case "A128CBC_HS256":
                Map<String, Object> customHeaders = new HashMap<>();
                customHeaders.put( "kid", value );

                headers = new JWEHeader( JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256,
                        null, null, null, null, null, null,
                        null, null, null, null, null, null,
                        null, null, null, 0, null, null,
                        customHeaders, null );

                break;

        }

        return headers;


    }

    public static String getA128gcmContent(JsonObject jsonObject, String rule, String method) {

        String rawCReqContent = jsonObject.toString();

        byte[] cek = new byte[0];

        try {

            cek = Base64.getDecoder().decode( context.getValue( "pureCEK" ) );

        } catch (Exception e) {

            LOGGER.info( "\tThe byte array CEK could NOT been decoded\t\n" );
            Assert.fail( "\tThe byte array CEK could NOT been decoded\t\n" );

        }

        byte[] leftMostCEK = Arrays.copyOfRange( cek, 0, cek.length/2 );

        headers = setHeaderJWE( rule, method );

        object = new JWEObject( headers, new Payload( rawCReqContent ) );

        try {

            object.encrypt( new EncryptionUtil.TransactionEncryptors( leftMostCEK, ( byte ) Integer.parseInt( setBufferJWE( jsonObject, rule ).toString() ) ) );

        } catch ( JOSEException e ) {

            LOGGER.info("\t" + e.getMessage() + "\t\n");
            Assert.fail("\t" + e.getMessage() + "\t\n");

        }

        return object.serialize();

    }

    public static String getA128cbc_hs256Content(JsonObject jsonObject, String rule, String method) {


        String rawCReqContent = jsonObject.toString();

        byte[] cek = new byte[0];

        try {

            cek = Base64.getDecoder().decode( context.getValue( "pureCEK" ) );

        } catch (Exception e) {

            LOGGER.info( "\tThe byte array CEK could NOT been decoded\t\n" );
            Assert.fail( "\tThe byte array CEK could NOT been decoded\t\n" );

        }

        headers = setHeaderJWE( rule, method );

        object = new JWEObject( headers, new Payload( rawCReqContent ) );

        try {

            object.encrypt( new DirectEncrypter( cek ) );

        } catch ( JOSEException e ) {

            LOGGER.info("\t" + e.getMessage() + "\t\n");
            Assert.fail("\t" + e.getMessage() + "\t\n");

        }

        return object.serialize();

    }

    private static byte[] getGcmId(byte pad, byte counter) {

        byte[] iv = new byte[ 12 ];

        Arrays.fill( iv, pad );

        iv[ iv.length - 1 ] = counter;

        return iv;

    }

    public static byte[] getGcmIvStoA(byte sdkCounterStoA) {

        return getGcmId( ( byte ) 0xFF, sdkCounterStoA );

    }

    public static byte[] getGcmIvAtoS(byte sdkCounterAtoS) {

        return getGcmId( ( byte ) 0xFF, sdkCounterAtoS );

    }

}

