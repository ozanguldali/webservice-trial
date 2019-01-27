package step;

import cucumber.api.DataTable;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import gherkin.formatter.model.DataTableRow;
import helper.step.CommonStepHelper;
import org.junit.Assert;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import static step.SeleniumStepDefinitions.webDriver;
import static util.ContextMapUtil.context;
import static util.EnvironmentUtil.PROJECT_DIR;
import static util.EnvironmentUtil.SLASH;
import static util.LoggingUtil.LOGGER;
import static util.step.CommonStepUtil.quitWebDriver;

public class CommonStepDefinitions {

    static boolean ifAnySaved = false;
    private static int scenariosCounter = 0;
    private static int failedScenariosCounter = 0;

    @Before
    public void beforeScenario(Scenario scenario) {

        webDriver = null;

        LOGGER.info( String.format( "\t[%d] > Scenario [%s] started\t\n", ++scenariosCounter, scenario.getName() ) );

    }

    @After
    public void afterScenario(Scenario scenario) {

        if ( scenario.isFailed() ) {

            ++failedScenariosCounter;

            if ( webDriver != null ) {

                try {

                    scenario.embed( ( ( TakesScreenshot ) webDriver ).getScreenshotAs( OutputType.BYTES ), "image/png" );
                    LOGGER.info(String.format("\tThe screenshot has been taken for scenario: [ %s ]\t\n", scenario.getName() ) );

                } catch (Exception e) {

                    LOGGER.info(String.format("\tThe screenshot could NOT been taken for scenario: [ %s ]\t\n", scenario.getName() ) );
                    Assert.fail(String.format("\tThe screenshot could NOT been taken for scenario: [ %s ]\t\n", scenario.getName() ) );

                }

                quitWebDriver( webDriver );

            }

        } else {

            if ( webDriver != null )
                quitWebDriver( webDriver );

        }

        String result = scenario.isFailed() ? "with errors" : "succesfully";

        LOGGER.info(String.format("\t[%d] > Scenario [%s] finished %s\n\t", scenariosCounter, scenario.getName(), result));
        LOGGER.info(String.format("\t%d of %d scenarios failed so far\n\t", failedScenariosCounter, scenariosCounter));

    }

    @Given("^I wait for (\\d+) seconds$")
    public void waitForNSeconds(long seconds) throws Exception {

        Thread.sleep( seconds * 1000L );

        LOGGER.info(String.format("\tWait for %d seconds\t\n", seconds));

    }

    @Given("^I save elements from (\\w+(?: \\w+)*)")
    public void saveElementsFrom(String selectKey, DataTable dataTable) {

        switch ( selectKey ) {

            case "selenium":
                CommonStepHelper.getCommonElements.getSeleniumElements( dataTable );
                break;

            case "rest":
                CommonStepHelper.getCommonElements.getRestElements( dataTable );
                break;

            case "oracle":
                CommonStepHelper.getCommonElements.getOracleElements( dataTable );
                break;

            case "jMeter":
                CommonStepHelper.getCommonElements.getJMeterElements( dataTable );
                break;

            default:
                LOGGER.info( ( String.format("The select key [ '%s' ] could not been matched with one of " +
                        "['selenium', 'rest', 'oracle', 'jMeter'] ", selectKey ) ) );
                Assert.fail( ( String.format("The select key [ '%s' ] could not been matched with one of " +
                        "['selenium', 'rest', 'oracle', 'jMeter'] ", selectKey ) ) );
                break;

        }

    }

    @Given("^I open the corresponding html by using (\\w+(?: \\w+)*) driver")
    public void iOpenHTML(String driver, DataTable table) {

        String htmlBasesDir = PROJECT_DIR + SLASH + "src" + SLASH + "test" + SLASH + "resources" + SLASH + "config" + SLASH + "html-bases";

        SeleniumStepDefinitions.useDriver( driver );

        for ( DataTableRow row : table.getGherkinRows() ) {

            String key      =   row.getCells().get( 0 );
            String value    =   row.getCells().get( 1 );

            switch ( key ) {

                case "htmlFile":

                    String htmlFile = htmlBasesDir + SLASH + value;

                    try {

                        webDriver.get( "file:" + SLASH + SLASH + SLASH + htmlFile + ".html" ) ;
                        LOGGER.info( String.format( "\tNavigated to the HTML file: %s\n\t", value ) );

                    } catch (Exception e) {

                        LOGGER.info( String.format( "\tCould NOT navigated to the HTML file: %s\n\t", value ) );
                        Assert.fail( String.format( "\tCould NOT navigated to the HTML file: %s\n\t", value ) );

                    }

                    break;

                case "htmlString":

                    try {

                        webDriver.get( "data:text/html;charset=utf-8," + value );
                        LOGGER.info( String.format( "\tNavigated to the HTML string: %s\n\t", value ) );

                    } catch (Exception e) {

                        LOGGER.info( String.format( "\tCould NOT navigated to the HTML string: %s\n\t", value ) );
                        Assert.fail( String.format( "\tCould NOT navigated to the HTML string: %s\n\t", value ) );

                    }

                    break;

                case "htmlSource":

                    String htmlSource = context.getValue( "htmlSource" );

                    try {

                        webDriver.get( "data:text/html;charset=utf-8," + htmlSource );
                        LOGGER.info( String.format( "\tNavigated to the HTML source: %s\n\t", htmlSource ) );

                    } catch (Exception e) {

                        LOGGER.info( String.format( "\tCould NOT navigated to the HTML source: %s\n\t", htmlSource ) );
                        Assert.fail( String.format( "\tCould NOT navigated to the HTML source: %s\n\t", htmlSource ) );

                    }

                    break;
            }

        }

    }

}