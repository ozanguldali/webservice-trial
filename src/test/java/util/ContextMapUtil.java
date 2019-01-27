package util;

import org.testng.Assert;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static util.LoggingUtil.LOGGER;

public class ContextMapUtil<K,V> extends HashMap<K,V>
        implements Map<K,V>, Cloneable, Serializable {

    public static ContextMapUtil<String, String> context = new ContextMapUtil<>();

    private ContextMapUtil() {

        setContext( context );

    }

    public String getValue(String key){

        try {

            return context.get( key );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe key [ %s ] could NOT been get from hashMap\t\n", key ) );
            Assert.fail( String.format( "\tThe key [ %s ] could NOT been get from hashMap\t\n", key ) );

        }

        return null;

    }

    public void putPair(String key, String value) {

        try {

            context.put( key, value );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe key [ %s ] could NOT been set with value [%s] from hashMap\t\n", key, value ) );
            Assert.fail( String.format( "\tThe key [ %s ] could NOT been set with value [%s] from hashMap\t\n", key, value ) );

        }

    }

    public boolean hasValue(String key) {

        Object value = context.getValue( key );

        return value != null;

    }

    public void setContext(ContextMapUtil<String, String> context) {

        ContextMapUtil.context = context;

    }

}
