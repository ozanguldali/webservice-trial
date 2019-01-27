package step;

import com.google.gson.JsonObject;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import gherkin.formatter.model.DataTableRow;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.step.CommonStepUtil;

import java.util.concurrent.TimeUnit;

import static helper.step.SeleniumStepHelper.*;
import static step.CommonStepDefinitions.ifAnySaved;
import static util.DriverUtil.setDriver;
import static util.LoggingUtil.LOGGER;
import static util.step.CommonStepUtil.quitWebDriver;
import static util.step.CommonStepUtil.saveValues;
import static util.step.SeleniumStepUtil.*;

public class SeleniumStepDefinitions {

    static WebDriver webDriver = null;

    private static JsonObject pageObject;

    private static By by;

    @Given("^I use (\\w+(?: \\w+)*) driver$")
    public static void useDriver(String driverSelect) {

        try {

            webDriver = setDriver( driverSelect );

            if( webDriver != null )
                LOGGER.info(String.format("\tDriver has been selected as: [%s]\t\n", driverSelect ) );

            else{

                LOGGER.info( String.format( "\tDriver could NOT been selected as: [%s]\t\n", driverSelect ) );
                Assert.fail( String.format( "\tDriver could NOT been selected as: [%s]\t\n", driverSelect ) );

            }

        } catch ( Exception e ) {

            LOGGER.info( String.format( "\tDriver could NOT been selected as: [%s], because { error: [%s] }\t\n", driverSelect, e.getMessage() ) );
            Assert.fail( String.format( "\tDriver could NOT been selected as: [%s], because { error: [%s] }\t\n", driverSelect, e.getMessage() ) );

        }

    }

    @When("^I open (\\w+(?: \\w+)*) page$")
    public void openPage(String pageKey) {

        usePage( pageKey );

        String urlString = getPageObjectURL( pageObject );

        navigateToURL( webDriver, urlString );

    }

    @When("^I use (\\w+(?: \\w+)*) page$")
    public void usePage(String pageKey) {

        JsonObject pageElementObject = getPageElementJsonObject();

        pageObject = getPageKeyJsonObject( pageObject, pageElementObject, pageKey );

    }

    @Then("^I refresh the page$")
    public void refreshPage() {

        try {

            webDriver.navigate().refresh();

            LOGGER.info( String.format( "\t The page having URL: [%s] has been refreshed.\t\n ", webDriver.getCurrentUrl() ) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe page having URL: [%s] could NOT been refreshed.\t\n ", webDriver.getCurrentUrl() ) );
            Assert.fail( String.format( "\tThe page having URL: [%s] could NOT been refreshed.\t\n ", webDriver.getCurrentUrl() ) );

        }

    }

    @Then("^I open new tab$")
    public void openNewTab() {

        try {

            ( ( JavascriptExecutor ) webDriver ).executeScript( "window.open()" );

            LOGGER.info( "\tThe new tab is opened\t\n" );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe new tab could NOT been opened, because { error: [%s] }.\t\n ", e.getMessage() ) );
            Assert.fail( String.format( "\tThe new tab could NOT been opened, because { error: [%s] }.\t\n ", e.getMessage() ) );

        }

        switchToTabNumber( webDriver, 1 );

    }

    @When("^I open the page (\\w+(?: \\w+)*) in new tab$")
    public void iOpenUrlNewTab(String pageKey) {

        openNewTab();

        openPage( pageKey );

        LOGGER.info("\tThe URL [" + pageKey + "] \t opened in new tab\t\n");

    }

    @Then("^I switch to the tab of (\\d+)$")
    public void iSwitchToTab(int tabNumber) {

        switchToTabNumber( webDriver, tabNumber );

    }

    @Then("^I close the tab$")
    public void closeTab() {

        try {

            ( ( JavascriptExecutor ) webDriver ).executeScript( "window.close()" );

            LOGGER.info( "\tThe tab is closed\t\n" );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe new tab could NOT been closed, because { error: [%s] }.\t\n ", e.getMessage() ) );
            Assert.fail( String.format( "\tThe new tab could NOT been closed, because { error: [%s] }.\t\n ", e.getMessage() ) );

        }

        switchToLastTab( webDriver );

    }

    @Then("^I quit the driver$")
    public void quitDriver() {

        quitWebDriver( webDriver );

        LOGGER.info( "\tThe driver is quited\t\n" );

    }

    @Then("^I see the url is \"([^\"]*)\"$")
    public void iSeeTheUrlIs(String expectedUrl) {

        String currentUrl = getCurrentURL( webDriver );

        if ( CommonStepUtil.doesEqualTwoStrings( currentUrl, expectedUrl ) )
            LOGGER.info( String.format( "\tThe url: [%s] equals to expected: [%s]\t\n", currentUrl, expectedUrl ) );

        else {

            LOGGER.info( String.format( "\tThe url: [%s] does NOT equal to expected: [%s]\t\n", currentUrl, expectedUrl ) );
            Assert.fail( String.format( "\tThe url: [%s] does NOT equal to expected: [%s]\t\n", currentUrl, expectedUrl ) );

        }


    }

    @Then("^I see the url contains \"([^\"]*)\"$")
    public void iSeeTheUrlContains(String expectedUrl) {

        String currentUrl = getCurrentURL( webDriver );

        if ( CommonStepUtil.doesContainsTwoStrings( currentUrl, expectedUrl ) )
            LOGGER.info( String.format( "\tThe url: [%s] contains the expected: [%s]\t\n", currentUrl, expectedUrl ) );

        else {

            LOGGER.info( String.format( "\tThe url: [%s] does NOT contain the expected: [%s]\t\n", currentUrl, expectedUrl ) );
            Assert.fail( String.format( "\tThe url: [%s] does NOT contain the expected: [%s]\t\n", currentUrl, expectedUrl ) );

        }

    }

    @Then("^I see webPage title as \"([^\"]*)\"$")
    public void iSeeWebPageTitleAs(String expectedTitle) {

        String currentTitle = getCurrentPageTitle( webDriver );

        if ( CommonStepUtil.doesEqualTwoStrings( currentTitle, expectedTitle ) )
            LOGGER.info( String.format( "\tThe title: [%s] equals to expected: [%s]\t\n", currentTitle, expectedTitle ) );

        else {

            LOGGER.info( String.format( "\tThe title: [%s] does NOT equal to expected: [%s]\t\n", currentTitle, expectedTitle ) );
            Assert.fail( String.format( "\tThe title: [%s] does NOT equal to expected: [%s]\t\n", currentTitle, expectedTitle ) );

        }

    }

    @Then("^I see webPage title contains \"([^\"]*)\"$")
    public void iSeeWebPageContainsTitle(String expectedTitle) {

        String currentTitle = getCurrentPageTitle( webDriver );

        if ( CommonStepUtil.doesContainsTwoStrings( currentTitle, expectedTitle ) )
            LOGGER.info( String.format( "\tThe title: [%s] contains the expected: [%s]\t\n", currentTitle, expectedTitle ) );

        else {

            LOGGER.info( String.format( "\tThe title: [%s] does NOT contain the expected: [%s]\t\n", currentTitle, expectedTitle ) );
            Assert.fail( String.format( "\tThe title: [%s] does NOT contain the expected: [%s]\t\n", currentTitle, expectedTitle ) );

        }

    }

    @When("^I wait for page$")
    public void iWaitForPage() {

        try {

            webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            LOGGER.info( "\tWaiting for the page...\t\n");

        } catch (Exception e){

            LOGGER.info( String.format( "\tFailed during waiting the page, because { error : [%s] }...\t\n", e.getMessage() ) );
            Assert.fail( String.format( "\tFailed during waiting the page, because { error : [%s] }...\t\n", e.getMessage() ) );

        }

    }

    @Then("^I switch to (\\w+(?: \\w+)*) iframe by (\\w+(?: \\w+)*)$")
    public void iSwitchToFrame(String iframeKey, String selectKey) {

        String iFrameValue = getElementString( pageObject, iframeKey );

        WebElement iFrame = findWebElement(
                webDriver, getSelectTypeBy( by, selectKey, iFrameValue ),
                iframeKey, iFrameValue
        );

        try {

            webDriver.switchTo().frame( iFrame );
            LOGGER.info( String.format( "\tSwitching to the iFrame [%s] : [%s]...\t\n", iframeKey, iFrameValue ) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tFailed during switching to the iFrame [%s] : [%s], " +
                    "because { error : [%s] }\t\n", iframeKey, iFrameValue, e.getMessage() ) );
            Assert.fail( String.format( "\tFailed during switching to the iFrame [%s] : [%s], " +
                    "because { error : [%s] }\t\n", iframeKey, iFrameValue, e.getMessage() ) );

        }

        //WebElement body = webDriver.findElement(By.tagName("body"));
        WebElement body = findWebElement(
                webDriver, getSelectTypeBy( by, "tagName", "body" ),
                "body", "body"
        );

        try {

            body.click();
            LOGGER.info(String.format("\tSwitched to the iFrame [%s] : [%s]\n\t", iframeKey, selectKey));

        } catch (Exception e) {

            LOGGER.info( String.format( "\tCould NOT been switched to the iFrame [%s] : [%s], " +
                    "because { error : [%s] }\t\n", iframeKey, iFrameValue, e.getMessage() ) );
            Assert.fail( String.format( "\tCould NOT been switched to the iFrame [%s] : [%s], " +
                    "because { error : [%s] }\t\n", iframeKey, iFrameValue, e.getMessage() ) );

        }

    }

    @Then("^I see elements do not contain texts:")
    public void iSeeElementsNotContainsTexts(DataTable dataTable) {
        
        for ( DataTableRow row : dataTable.getGherkinRows() ) {

            String pageKey      =   row.getCells().get( 0 );
            String valueKey     =   row.getCells().get( 1 );
            String selectKey    =   row.getCells().get( 2 );

            String pageElement = getElementString( pageObject, pageKey );

            String pageElementValue = getWebElementText(
                    findWebElement(
                    webDriver, getSelectTypeBy( by, selectKey, pageElement ), pageKey, pageElement ),
                    pageKey,
                    pageElement
            );

            if ( !CommonStepUtil.doesContainsTwoStrings( pageElementValue, valueKey ) )
                LOGGER.info( String.format( "\tThe element: [%s] does not contain : [%s] as expected\t\n", pageElementValue, valueKey ) );

            else {

                LOGGER.info( String.format( "\tThe element: [%s] contains: [%s] unexpectedly\t\n", pageElementValue, valueKey ) );
                Assert.fail( String.format( "\tThe element: [%s] contains: [%s] unexpectedly\t\n", pageElementValue, valueKey ) );

            }

        }

    }

    @Then("^I see elements contains texts:")
    public void iSeeElementsContainsTexts(DataTable dataTable) {

        for ( DataTableRow row : dataTable.getGherkinRows() ) {

            String pageKey      =   row.getCells().get( 0 );
            String valueKey     =   row.getCells().get( 1 );
            String selectKey    =   row.getCells().get( 2 );

            String pageElement = getElementString( pageObject, pageKey );

            String pageElementValue = getWebElementText(
                    findWebElement(
                            webDriver, getSelectTypeBy( by, selectKey, pageElement ), pageKey, pageElement ),
                    pageKey,
                    pageElement

            );

            if ( CommonStepUtil.doesContainsTwoStrings( pageElementValue, valueKey ) )
                LOGGER.info( String.format( "\tThe element: [%s] contains : [%s] as expected\t\n", pageElementValue, valueKey ) );

            else {

                LOGGER.info( String.format( "\tThe element: [%s] does NOT contain: [%s] unexpectedly\t\n", pageElementValue, valueKey ) );
                Assert.fail( String.format( "\tThe element: [%s] does NOT contain: [%s] unexpectedly\t\n", pageElementValue, valueKey ) );

            }

        }

    }

    @Then("^I see text:$")
    public void iSeeText(DataTable table) {

        String pageElement = getElementString( pageObject, "mainPanel" );

        for ( DataTableRow row : table.getGherkinRows() ) {

            String key = row.getCells().get( 0 );

            String pageElementValue = getWebElementText(
                    findWebElement(
                            webDriver, getSelectTypeBy( by, "xpath", pageElement ), "mainPanel", pageElement ),
                    "mainPanel",
                    pageElement

            );

            if ( CommonStepUtil.doesContainsTwoStrings( pageElementValue, key ) )
                LOGGER.info( String.format( "\tThe text: [%s] is contained as expected\t\n", key ) );

            else {

                LOGGER.info( String.format( "\tThe text: [%s] is NOT contained unexpectedly\t\n", key ) );
                Assert.fail( String.format( "\tThe text: [%s] is NOT contained unexpectedly\t\n", key ) );

            }

        }

    }

    @Then("^I do not see text:$")
    public void iNotSeeText(DataTable table) {

        String pageElement = getElementString( pageObject, "mainPanel" );

        for ( DataTableRow row : table.getGherkinRows() ) {

            String key = row.getCells().get( 0 );

            String pageElementValue = getWebElementText(
                    findWebElement(
                            webDriver, getSelectTypeBy( by, "xpath", pageElement ), "mainPanel", pageElement ),
                    "mainPanel",
                    pageElement

            );

            if ( !CommonStepUtil.doesContainsTwoStrings( pageElementValue, key ) )
                LOGGER.info( String.format( "\tThe text: [%s] is not contained as expected\t\n", key ) );

            else {

                LOGGER.info( String.format( "\tThe text: [%s] is contained unexpectedly\t\n", key ) );
                Assert.fail( String.format( "\tThe text: [%s] is contained unexpectedly\t\n", key ) );

            }

        }

    }

    @Then("^I see (\\w+(?: \\w+)*) element by (\\w+(?: \\w+)*)$")
    public void iSeeElement(String pageKey, String selectKey) {

        String pageElement = getElementString( pageObject, pageKey );

        boolean existElement = findWebElements(
                webDriver, getSelectTypeBy( by, selectKey, pageElement ),
                pageKey, pageElement
        ).size() != 0;

        if ( existElement )
            LOGGER.info( String.format( "\tThe WebElement [%s] : [%s] is in the page as expected\t\n", pageKey, pageElement ) );

        else {

            LOGGER.info( String.format( "\tThe WebElement [%s] : [%s] is NOT in the page unexpectedly\t\n", pageKey, pageElement ) );
            Assert.fail( String.format( "\tThe WebElement [%s] : [%s] is NOT in the page unexpectedly\t\n", pageKey, pageElement ) );

        }

    }

    @Then("^I do not see (\\w+(?: \\w+)*) element by (\\w+(?: \\w+)*)$")
    public void iNotSeeElement(String pageKey, String selectKey) {

        String pageElement = getElementString( pageObject, pageKey );

        boolean existElement = findWebElements(
                webDriver, getSelectTypeBy( by, selectKey, pageElement ),
                pageKey, pageElement
        ).size() != 0;

        if ( !existElement )
            LOGGER.info( String.format( "\tThe WebElement [%s] : [%s] is not in the page as expected\t\n", pageKey, pageElement ) );

        else {

            LOGGER.info( String.format( "\tThe WebElement [%s] : [%s] is in the page unexpectedly\t\n", pageKey, pageElement ) );
            Assert.fail( String.format( "\tThe WebElement [%s] : [%s] is in the page unexpectedly\t\n", pageKey, pageElement ) );

        }

    }

    @Then("^I fill by (\\w+(?: \\w+)*)$")
    public void iFillBy(String selectKey, DataTable table) {

        for ( DataTableRow row : table.getGherkinRows() ) {

            String pageKey = row.getCells().get(0);
            String pageValue = row.getCells().get(1);

            String pageElement = getElementString( pageObject, pageKey );

            findWebElements(
                    webDriver, getSelectTypeBy( by, selectKey, pageElement ),
                    pageKey, pageElement
            ).clear();

            sendKeyToWebElement( webDriver, getSelectTypeBy( by, selectKey, pageElement ), pageKey, pageElement, pageValue );

        }

    }

    @Then("^I mouse hover on (\\w+(?: \\w+)*) element by (\\w+(?: \\w+)*)$")
    public void iMouseHover(String pageKey, String selectKey) {

        String pageElement = getElementString( pageObject, pageKey );

        by = getSelectTypeBy( by, selectKey, pageElement);

        WebDriverWait wait  =   new WebDriverWait( webDriver, 10 );
        WebElement webElement1 = null;

        try {

            webElement1 = wait.until( ExpectedConditions.elementToBeClickable( by ) );
            LOGGER.info( String.format( "\tThe WebElement [%s] is waiting to be clickable...\t\n", pageKey ) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe WebElement [%s] is NOT clickable, because { error : [%s] }\t\n", pageKey, e.getMessage() ) );
            Assert.fail( String.format( "\tThe WebElement [%s] is NOT clickable, because { error : [%s] }\t\n", pageKey, e.getMessage() ) );

        }

        Actions actions     =   new Actions( webDriver );

        try {

            WebElement webElement2 = findWebElement(
                    webDriver, getSelectTypeBy( by, selectKey, pageElement ),
                    pageKey, pageElement
            );

            actions.moveToElement( webElement1 ).moveToElement( webElement2 ).build().perform();

            LOGGER.info( String.format( "\tMouse Hovered on [%s] : [%s]\t\n", pageKey, pageElement ) );

        } catch ( Exception e ) {

            LOGGER.info( String.format( "\tFailed during Mouse Hovering on [%s] : [%s], because { error : [%s] }\t\n", pageKey, pageElement, e.getMessage() ) );
            Assert.fail( String.format( "\tFailed during Mouse Hovering on [%s] : [%s], because { error : [%s] }\t\n", pageKey, pageElement, e.getMessage() ) );

        }

    }

    @Then("^I click (\\w+(?: \\w+)*) element by (\\w+(?: \\w+)*)$")
    public void iClickElement(String pageKey, String selectKey) {

        String pageElement = getElementString( pageObject, pageKey );

//        by = getSelectTypeBy( by, selectKey, pageElement );
//        WebDriverWait wait = new WebDriverWait( webDriver, 10 );
//        wait.until( ExpectedConditions.invisibilityOfElementLocated( by ) );
//        WebElement webElement = wait.until( ExpectedConditions.elementToBeClickable( by ) );

        WebElement webElement = findWebElement(
                webDriver, getSelectTypeBy( by, selectKey, pageElement ),
                pageKey, pageElement
        );

        Actions actions = new Actions( webDriver );

        try {

            actions.moveToElement( webElement ).click().perform();
            LOGGER.info(String.format( "\tClicked to the WebElement [%s] : [%s]\t\n", pageKey, pageElement ) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tFailed during Clicking to the [%s] : [%s], because { error : [%s] }\t\n", pageKey, pageElement, e.getMessage() ) );
            Assert.fail( String.format( "\tFailed during Clicking to the [%s] : [%s], because { error : [%s] }\t\n", pageKey, pageElement, e.getMessage() ) );

        }

    }

    @Then("^I open (\\w+(?: \\w+)*) element by (\\w+(?: \\w+)*) on a new tab$")
    public void iOpenElementNewTab(String pageKey, String selectKey) {

        String pageElement = getElementString( pageObject, pageKey );

        WebElement webElement = findWebElement(
                webDriver, getSelectTypeBy( by, selectKey, pageElement ),
                pageKey, pageElement
        );

        Actions actions = new Actions( webDriver );

        try {

            rightClickToElement( actions );

            actions.click( webElement ).build().perform();

            LOGGER.info(String.format( "\tOpened the WebElement [%s] : [%s] on the new tab\t\n", pageKey, pageElement ) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tFailed during Openning the [%s] : [%s] on the new tab, because { error : [%s] }\t\n", pageKey, pageElement, e.getMessage() ) );
            Assert.fail( String.format( "\tFailed during Openning the [%s] : [%s] on the new tab, because { error : [%s] }\t\n", pageKey, pageElement, e.getMessage() ) );

        }

        switchToLastTab( webDriver );

    }

    @Then("^I scroll the page to x-axis (-?\\d+) and y-axis (-?\\d+)")
    public void iScrollToCoords(String x_axis, String y_axis) {

        int x = Integer.valueOf( x_axis );
        int y = Integer.valueOf( y_axis );

        try {

            ( (JavascriptExecutor) webDriver ).executeScript( "window.scrollBy(" + x + "," + y + ")" );
            LOGGER.info( String.format( "\tThe page is scrolled to x: [%d] and y: [%d]\t\n", x, y ) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe page could NOT been scrolled to x: [%d] and y: [%d]\t\n", x, y ) );
            Assert.fail( String.format( "\tThe page could NOT been scrolled to x: [%d] and y: [%d]\t\n", x, y ) );

        }

    }

    @Then("^I scroll the page to (\\w+(?: \\w+)*) element by (\\w+(?: \\w+)*)")
    public void iScrollToElement(String pageKey, String selectKey) {

        String pageElement = getElementString( pageObject, pageKey );
        WebElement webElement = findWebElement(
                webDriver, getSelectTypeBy( by, selectKey, pageElement ),
                pageKey, pageElement
        );


        try {

            ( (JavascriptExecutor) webDriver ).executeScript( "arguments[0].scrollIntoView(true);", webElement );
            LOGGER.info( String.format( "\tThe page is scrolled to element: [%s] : [%s]\t\n", pageKey, pageElement ) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe page could NOT been scrolled to element: [%s] : [%s]\t\n", pageKey, pageElement ) );
            Assert.fail( String.format( "\tThe page could NOT been scrolled to element: [%s] : [%s]\t\n", pageKey, pageElement ) );

        }

    }

    @Then("^I drag from (\\w+(?: \\w+)*) element by (\\w+(?: \\w+)*) to (\\w+(?: \\w+)*) element by (\\w+(?: \\w+)*)")
    public void iDragFromTo(String pageKey_1, String selectKey_1, String pageKey_2, String selectKey_2 ) {

        String pageElement_1 = getElementString( pageObject, pageKey_1 );

        WebElement webElement_1 = findWebElement(
                webDriver, getSelectTypeBy( by, selectKey_1, pageElement_1 ),
                pageKey_1, pageElement_1
        );

        String pageElement_2 = getElementString( pageObject, pageKey_2 );

        WebElement webElement_2 = findWebElement(
                webDriver, getSelectTypeBy( by, selectKey_2, pageElement_2 ),
                pageKey_2, pageElement_2
        );

        Actions actions = new Actions( webDriver );

        try {

//            actions.moveToElement( webElement_1 ).clickAndHold().moveToElement( webElement_2 ).release().perform();
            actions.dragAndDrop( webElement_1 , webElement_2 ).perform();
            LOGGER.info( String.format( "\tDragged from [%s] : [%s] to [%s] : [%s]\t\n", pageKey_1, pageElement_1, pageKey_2, pageElement_2 ) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tFailed during dragging from [%s] : [%s] to [%s] : [%s], " +
                    "because { error : [%s] }\t\n", pageKey_1, pageElement_1, pageKey_2, pageElement_2, e.getMessage() ) );
            Assert.fail( String.format( "\tFailed during dragging from [%s] : [%s] to [%s] : [%s], " +
                    "because { error : [%s] }\t\n", pageKey_1, pageElement_1, pageKey_2, pageElement_2, e.getMessage() ) );

        }

    }

    @Then("^I click and hold from (\\w+(?: \\w+)*) element at (\\d+),(\\d+) by (\\w+(?: \\w+)*) to (\\w+(?: \\w+)*) element at (\\d+),(\\d+) by (\\w+(?: \\w+)*)")
    public void iClickAndHold(String pageKey_1, int x_axis_1, int y_axis_1, String selectKey_1, String pageKey_2, int x_axis_2, int y_axis_2, String selectKey_2 ) {

        String pageElement_1 = getElementString( pageObject, pageKey_1 );

        WebElement webElement_1 = findWebElement(
                webDriver, getSelectTypeBy( by, selectKey_1, pageElement_1 ),
                pageKey_1, pageElement_1
        );

        String pageElement_2 = getElementString( pageObject, pageKey_2 );

        WebElement webElement_2 = findWebElement(
                webDriver, getSelectTypeBy( by, selectKey_2, pageElement_2 ),
                pageKey_2, pageElement_2
        );

        Actions actions = new Actions( webDriver );

        try {

            actions.moveToElement( webElement_1, x_axis_1, y_axis_1 ).clickAndHold().moveToElement( webElement_2, x_axis_2, y_axis_2 ).release().perform();
//            actions.dragAndDrop( webElement_1 , webElement_2 ).perform();
            LOGGER.info( String.format( "\tDragged from [%s] : [%s] to [%s] : [%s]\t\n", pageKey_1, pageElement_1, pageKey_2, pageElement_2 ) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tFailed during dragging from [%s] : [%s] to [%s] : [%s], " +
                    "because { error : [%s] }\t\n", pageKey_1, pageElement_1, pageKey_2, pageElement_2, e.getMessage() ) );
            Assert.fail( String.format( "\tFailed during dragging from [%s] : [%s] to [%s] : [%s], " +
                    "because { error : [%s] }\t\n", pageKey_1, pageElement_1, pageKey_2, pageElement_2, e.getMessage() ) );

        }

    }

    @Then("^I save the values of element by (\\w+(?: \\w+)*)$")
    public static void saveValuesOfElement(DataTable dataTable ) {

        for ( DataTableRow row : dataTable.getGherkinRows() ) {

            String leftKey      =   row.getCells().get( 0 );
            String rightKey     =   row.getCells().get( 1 );
            String pageKey      =   row.getCells().get( 2 );

            String pageElement = getElementString( pageObject, pageKey );

            String value = getWebElementText(
                    findWebElement(
                            webDriver, getSelectTypeBy( by, rightKey, pageElement ), pageKey, pageElement ),
                    pageKey,
                    pageElement
            );

            saveValues( leftKey, rightKey, value );

        }

        if ( !ifAnySaved )
            ifAnySaved = true;

    }

}
