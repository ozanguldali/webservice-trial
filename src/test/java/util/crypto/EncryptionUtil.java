package util.crypto;

import com.google.gson.JsonObject;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.impl.AAD;
import com.nimbusds.jose.crypto.impl.AESGCM;
import com.nimbusds.jose.crypto.impl.AuthenticatedCipherText;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.Container;
import helper.crypto.EncryptionHelper;
import util.ParserUtil;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;
import java.util.HashMap;

import static helper.crypto.EncryptionHelper.getA128cbc_hs256Content;
import static helper.crypto.EncryptionHelper.getA128gcmContent;

public class EncryptionUtil {

    public static final String sdkPrivateKey = ParserUtil.jsonFileParsing( "key-bases/sdkPrivateKey" ).getAsJsonObject().toString();

    public static String toBase64URL(JsonObject jsonObject) {

        return Base64.getUrlEncoder().encodeToString( jsonObject.toString().getBytes( StandardCharsets.UTF_8 ) );

    }

    public static String toJWE(JsonObject jsonObject, String rule) {

        final String encJWE = jsonObject.get("encJWE").getAsString();

        switch ( encJWE ) {

            case "A128GCM":
                return getA128gcmContent( jsonObject, rule, encJWE);

            case "A128CBC_HS256":
                return getA128cbc_hs256Content( jsonObject, rule, encJWE );

            default:
                return jsonObject.toString();

        }

    }

    public static void generateEncryptionMap(HashMap<Object, Object> hashMap, ECPrivateKey privateKey, ECPublicKey peerPublicKey, byte[] partyUInfo, byte[] partyVInfo, String algoName, int algoKeyBitLen) {

        hashMap.put( "ecPrivateKey", privateKey );
        hashMap.put( "ecPublicKey", peerPublicKey );
        hashMap.put( "partyUInfo", partyUInfo );
        hashMap.put( "partyVInfo", partyVInfo );
        hashMap.put( "algoName", algoName );
        hashMap.put( "algoKeyBitLen", algoKeyBitLen );

    }

//    public static byte[] generateIV(JsonObject jsonObject, int ivSize, String rule) {
//
//        StringBuilder buffer = EncryptionHelper.setBufferJWE( jsonObject, rule );
//
//        byte[] iv = new byte[ ivSize ];
//
//        for ( int i = 0; i < buffer.length(); i++ ) {
//
//            char charAt = buffer.reverse().charAt( i );
//            iv[ i ] = ( byte ) Character.getNumericValue( charAt );
//
//        }
//
//        for ( int i = 3; i < iv.length ; i++ ) {
//
//            iv[ i ] = 0;
//
//        }
//
//        return iv;
//
//    }

    public static void generateJWEElements(HashMap<Object, Object> jweMap, HashMap<Object, Object> encryptionMap) {

        jweMap.put( "ecPrivateKey", encryptionMap.get( "ecPrivateKey" ) );
        jweMap.put( "ecPublicKey", encryptionMap.get( "ecPublicKey" ) );
        jweMap.put( "partyUInfo", encryptionMap.get( "partyUInfo" ) );
        jweMap.put( "partyVInfo", encryptionMap.get( "partyVInfo" ) );
        jweMap.put( "algoName", encryptionMap.get( "algoName" ) );
        jweMap.put( "algoKeyBitLen", encryptionMap.get( "algoKeyBitLen" ) );

    }

    public static class TransactionEncryptors extends DirectEncrypter {

        private byte counter;

        public TransactionEncryptors(byte[] key, byte counter) throws KeyLengthException {

            super( new SecretKeySpec(key, "AES" ) );

            this.counter = counter;

        }

        @Override
        public JWECryptoParts encrypt( JWEHeader headers, byte[] clearText ) throws JOSEException {

            byte[] iv = EncryptionHelper.getGcmIvStoA( counter );

            byte[] aad = AAD.compute( headers );

            Container<byte[]> ivContainer = new Container<>();
            ivContainer.set( iv );

            AuthenticatedCipherText cipherText = AESGCM.encrypt( getKey(), ivContainer, clearText, aad, getJCAContext().getContentEncryptionProvider() );

            return new JWECryptoParts( headers, null, Base64URL.encode( iv ), Base64URL.encode( cipherText.getCipherText() ), Base64URL.encode( cipherText.getAuthenticationTag() ) );

        }

    }

}
