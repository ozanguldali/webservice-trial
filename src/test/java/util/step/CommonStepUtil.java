package util.step;

import org.junit.Assert;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static util.ContextMapUtil.context;
import static util.LoggingUtil.LOGGER;

public class CommonStepUtil {

    public static String replaceSavedElement(String string,  HashMap dataMap) {

        HashMap tempMap = ( HashMap ) dataMap.clone();

        Iterator iterator = tempMap.entrySet().iterator();

        while ( iterator.hasNext() ) {

            Map.Entry pair = (Map.Entry) iterator.next();

            String savedKey     =   "{{" + ( pair.getKey() ) + "}}";
            String savedValue   =   String.valueOf( pair.getValue() );

            string = string.replace( savedKey, savedValue );

            iterator.remove();

        }

        return string;

    }

    public static void saveValues(String leftKey, String rightKey, String value){

        if ( value == null ) {

            LOGGER.info(String.format("\tThe parameter [%s] could NOT been saved since it is NOT included in the response\t\n", rightKey));
            Assert.fail(String.format("\tThe parameter [%s] could NOT been saved since it is NOT included in the response\t\n", rightKey));

        }

        if ( Arrays.asList("\"", "'", "{", "}", "[", "]", "(", ")")
                .contains( String.valueOf( value.charAt( 0 ) ) ) )
            value = value.replace( String.valueOf( value.charAt( 0 ) ), "" );

        if ( Arrays.asList("\"", "'", "{", "}", "[", "]", "(", ")")
                .contains( String.valueOf( value.charAt( value.length() - 1 ) ) ) )
            value = value.replace( String.valueOf( value.charAt( value.length() - 1 ) ), "" );

        context.putPair( leftKey, value );

        LOGGER.info( String.format( "\tSaving parameter [%s] <- [%s] : [%s]\t\n", leftKey, rightKey, value ) );

    }

    public static void quitWebDriver(WebDriver webDriver) {

        try {

            webDriver.quit();
            LOGGER.info( String.format( "\tThe webdriver: [ %s ] has been closed.\t\n", webDriver.toString() ) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe webdriver: [ %s ] could NOT been closed\t\n", webDriver.toString() ) );
            Assert.fail( String.format( "\tThe webdriver: [ %s ] could NOT been closed\t\n", webDriver.toString() ) );

        }

    }

    public static boolean doesEqualTwoStrings(String current, String expected) {

        if ( current.equals( expected ) ) {

            LOGGER.info( String.format( "\tThe expected: [%s] == actual: [%s]\t\n", current, expected ) );
            return true;

        } else {

            LOGGER.info( String.format( "\tThe expected: [%s] BUT actual: [%s]\t\n", current, expected ) );
            return false;

        }

    }

    public static boolean doesContainsTwoStrings(String current, String expected) {

        if ( current.contains( expected ) ) {

            LOGGER.info( String.format( "\tThe expected: [%s] == actual: [%s]\t\n", current, expected ) );
            return true;

        } else {

            LOGGER.info( String.format( "\tThe expected: [%s] BUT actual: [%s]\t\n", current, expected ) );
            return false;

        }

    }

}
