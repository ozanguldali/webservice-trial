package util.step;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.List;

import static util.EnvironmentUtil.OS_VALUE;
import static util.LoggingUtil.LOGGER;

public class SeleniumStepUtil {

    public static void navigateToURL(WebDriver webDriver, String urlString) {

        try {

            webDriver.get( urlString );
            LOGGER.info( String.format( "\tNavigated to the website: %s\t\n", urlString ) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tDriver could NOT been navigated to: [%s], because { error: [%s] }\t\n", urlString, e.getMessage() ) );
            Assert.fail( String.format( "\tDriver could NOT been navigated to: [%s], because { error: [%s] }\t\n", urlString, e.getMessage() ) );

        }

    }

    public static void switchToTabNumber(WebDriver webDriver, int tabNumber) {

        try {

            ArrayList<String> tabs = new ArrayList<>( webDriver.getWindowHandles() );
            webDriver.switchTo().window( tabs.get( tabNumber ) );

            LOGGER.info( "\tThe webDriver object is switched to the new tab\t\n" );

        } catch (Exception e) {

            LOGGER.info( "\tThe webDriver object could NOT been switched to the new tab\t\n" );
            Assert.fail( "\tThe webDriver object could NOT been switched to the new tab\t\n" );

        }

    }

    public static void switchToLastTab(WebDriver webDriver) {

        try {

            ArrayList<String> tabs = new ArrayList<>( webDriver.getWindowHandles() );
            webDriver.switchTo().window( tabs.get( tabs.size() - 1 ) );

            LOGGER.info( "\tThe webDriver object is switched to the new tab\t\n" );

        } catch (Exception e) {

            LOGGER.info( "\tThe webDriver object could NOT been switched to the new tab\t\n" );
            Assert.fail( "\tThe webDriver object could NOT been switched to the new tab\t\n" );

        }

    }

    public static WebElement findWebElement(WebDriver webDriver, By by, String elementKey, String elementValue) {

        WebElement webElement = null;

        try {

            webElement = webDriver.findElement( by );
            LOGGER.info( String.format( "\tThe WebElement [%s] : [%s] is found on the page\t\n", elementKey, elementValue ) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe WebElement [%s] : [%s] could NOT been found on the page, " +
                    "because { error : [%s] }\t\n", elementKey, elementValue, e.getMessage() ) );
            Assert.fail( String.format( "\tThe WebElement [%s] : [%s] could NOT been found on the page, " +
                    "because { error : [%s] }\t\n", elementKey, elementValue, e.getMessage() ) );

        }

        return webElement;

    }

    public static List< WebElement > findWebElements(WebDriver webDriver, By by, String elementKey, String elementValue) {

        List< WebElement > webElement = null;

        try {

            webElement = webDriver.findElements( by );
            LOGGER.info( String.format( "\tThe iFrame WebElement [%s] : [%s] is found on the page\t\n", elementKey, elementValue ) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe iFrame WebElement [%s] : [%s] could NOT been found on the page, " +
                    "because { error : [%s] }\t\n", elementKey, elementValue, e.getMessage() ) );
            Assert.fail( String.format( "\tThe iFrame WebElement [%s] : [%s] could NOT been found on the page, " +
                    "because { error : [%s] }\t\n", elementKey, elementValue, e.getMessage() ) );

        }

        return webElement;

    }

    public static void sendKeyToWebElement(WebDriver webDriver, By by, String elementKey, String elementValue, String value) {

        try {

            findWebElement( webDriver, by, elementKey, elementValue).sendKeys( value );
            LOGGER.info( String.format( "\tFilled the key: [%s] with the value: [%s]\t\n", elementKey, value ) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tFailed during filling the key: [%s] with the value: [%s]," +
                    "because { error : [%s] }\t\n", elementKey, value, e.getMessage() ) );
            Assert.fail( String.format( "\tFailed during filling the key: [%s] with the value: [%s]," +
                    "because { error : [%s] }\t\n", elementKey, value, e.getMessage() ) );

        }

    }

    public static void rightClickToElement(Actions actions) {

        if ( OS_VALUE.toLowerCase().contains( "win" )
                || OS_VALUE.toLowerCase().contains( "sunos" )
                || OS_VALUE.toLowerCase().contains( "nix" )
                || OS_VALUE.toLowerCase().contains( "nux" )
                || OS_VALUE.toLowerCase().contains( "aix" ) ) {

            actions.sendKeys( Keys.CONTROL, "t"  );

        } else if ( OS_VALUE.toLowerCase().contains( "mac" ) ) {

            actions.sendKeys( Keys.COMMAND, "t"  );

        } else {

            LOGGER.info( "\tOperating System Could NOT Been Found !!!\t\n" );
            Assert.fail( "\tOperating System Could NOT Been Found !!!\t\n" );

        }

    }

}
