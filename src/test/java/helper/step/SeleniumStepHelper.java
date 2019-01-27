package helper.step;

import com.google.gson.JsonObject;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import util.ParserUtil;

import static util.LoggingUtil.LOGGER;

public class SeleniumStepHelper {

    private static JsonObject elementsObject;

    private static void getElementJsonObject(JsonObject jsonObject) {

        try {

            elementsObject = jsonObject.getAsJsonObject( "elements" );
            LOGGER.info( "\tThe elements of the page is parsed from pages.json\t\n" );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe elements of the page is parsed from pages.json, because { error : [%s] }\t\n", e.getMessage() ) );
            Assert.fail( String.format( "\tThe elements of the page is parsed from pages.json, because { error : [%s] }\t\n", e.getMessage() ) );

        }

    }

    public static JsonObject getPageElementJsonObject() {

        JsonObject pageElementObject = null;

        try {

            pageElementObject = ParserUtil.jsonFileParsing( "pages" );
            LOGGER.info( "\tPROJ_DIR/src/test/resources/pages.json is parsed\t\n" );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tPROJ_DIR/src/test/resources/pages.json could NOT been parsed, because { error : [%s] }\t\n", e.getMessage() ) );
            Assert.fail( String.format( "\tPROJ_DIR/src/test/resources/pages.json could NOT been parsed, because { error : [%s] }\t\n", e.getMessage() ) );

        }

        return pageElementObject;

    }

    public static JsonObject getPageKeyJsonObject(JsonObject pageObject, JsonObject pageElementObject, String pageKey) {

        try {

            pageObject = pageElementObject.get( pageKey ).getAsJsonObject();
            LOGGER.info( String.format( "\tPage Key Element [%s] is get from pages.json\t\n", pageKey ) );


        } catch (Exception e) {

            LOGGER.info( String.format( "\tPage Key Element [%s] could NOT been get from pages.json because { error : [%s] }\t\n", pageKey, e.getMessage() ) );
            Assert.fail( String.format( "\tPage Key Element [%s] could NOT been get from pages.json because { error : [%s] }\t\n", pageKey, e.getMessage() ) );

        }

        return pageObject;

    }

    public static String getPageObjectURL(JsonObject pageObject) {

        String urlString = null;

        try {

            urlString = pageObject.get( "url" ).getAsString();
            LOGGER.info( String.format( "\tURL is get from Page Elements: [%s]\t\n", urlString ) );


        } catch (Exception e) {

            LOGGER.info( String.format( "\tURL could NOT been get from Page Elements, because { error : [%s] }\t\n", e.getMessage() ) );
            Assert.fail( String.format( "\tURL could NOT been get from Page Elements, because { error : [%s] }\t\n", e.getMessage() ) );

        }

        return urlString;

    }

    public static String getCurrentURL(WebDriver webDriver) {

        String currentUrl = null;

        try {

            currentUrl = webDriver.getCurrentUrl();
            LOGGER.info( String.format( "The current url is get: [%s]", currentUrl ) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe current url could NOT been get, because { error: [%s] }\t\n", e.getMessage() ) );
            Assert.fail( String.format( "\tThe current url could NOT been get, because { error: [%s] }\t\n", e.getMessage() ) );

        }

        return currentUrl;

    }

    public static String getCurrentPageTitle(WebDriver webDriver) {

        String currentTitle = null;

        try {

            currentTitle = webDriver.getTitle();
            LOGGER.info( String.format( "The current title is get: [%s]", currentTitle ) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe current title could NOT been get, because { error: [%s] }\t\n", e.getMessage() ) );
            Assert.fail( String.format( "\tThe current title could NOT been get, because { error: [%s] }\t\n", e.getMessage() ) );

        }

        return currentTitle;

    }

    public static String getElementString(JsonObject pageObject, String elementKey) {

        getElementJsonObject( pageObject );

        String elementValue = null;

        try {

            elementValue = elementsObject.get( elementKey ).getAsString();

            LOGGER.info( String.format( "\tElement Key Element [%s] is get from pages.json as: [%s]\t\n", elementKey, elementValue ) );

        } catch (NullPointerException npe){

            LOGGER.info( String.format( "\tElement Key Element [%s] could NOT been get from pages.json, because it is null\t\n", elementKey ) );
            Assert.fail( String.format( "\tElement Key Element [%s] could NOT been get from pages.json, because it is null\t\n", elementKey ) );
        } catch (Exception e) {

            LOGGER.info( String.format( "\tElement Key Element [%s] could NOT been get from pages.json\t\n", elementKey ) );
            Assert.fail( String.format( "\tElement Key Element [%s] could NOT been get from pages.json\t\n", elementKey ) );

        }

        return elementValue;

    }

    public static String getWebElementText(WebElement webElement, String elementKey, String elementValue) {

        String string = null;

        try {

            string = webElement.getText();
            LOGGER.info( String.format( "\tThe text is get from [%s] : [%s] as [%s]\t\n", elementKey, elementValue, string ) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe text could NOT been get from [%s] : [%s]\t\n", elementKey, elementValue ) );
            Assert.fail( String.format( "\tThe text could NOT been get from [%s] : [%s]\t\n", elementKey, elementValue ) );

        }

        return string;
    }

    public static By getSelectTypeBy(By by, String decisionVariable, String pageElement) {

        try {

            switch (decisionVariable) {
                case "id":
                    by = By.id(pageElement);
                    break;
                case "name":
                    by = By.name(pageElement);
                    break;
                case "xpath":
                    by = By.xpath(pageElement);
                    break;
                case "className":
                    by = By.className(pageElement);
                    break;
                case "cssSelector":
                    by = By.cssSelector(pageElement);
                    break;
                case "tagName":
                    by = By.tagName(pageElement);
                    break;
                case "linkText":
                    by = By.linkText(pageElement);
                    break;
                case "partialLinkText":
                    by = By.partialLinkText(pageElement);
                    break;
                default:
                    throw new Error("Not a valid selector type: %s" + decisionVariable);
            }

        } catch ( AssertionError e ) {
            e.printStackTrace();
        }

        return by;

    }

}
