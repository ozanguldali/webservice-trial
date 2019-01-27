package helper;

import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import static util.LoggingUtil.LOGGER;

public class DriverHelper {

    public static WebDriver setDriverOptions( String driverSelect, boolean isHeadless ) {

        switch ( driverSelect ) {

            case "chrome": {

                ChromeOptions options = new ChromeOptions();

                try {

                    options.addArguments( "test-type" );
                    options.addArguments( "start-fullscreen" );
                    options.addArguments( "incognito" );
                    options.addArguments( "--no-sandbox" );
                    options.setHeadless( isHeadless );

                    LOGGER.info( String.format( "\t[%s] option is set.\t\n", driverSelect ) );

                } catch (Exception e) {

                    LOGGER.info( String.format( "\t[%s] option could NOT been set.\t\n", driverSelect ) );
                    Assert.fail( String.format( "\t[%s] option could NOT been set.\t\n", driverSelect ) );

                }

                return (new ChromeDriver( options ) );

            }

            case "firefox": {

                FirefoxOptions options = new FirefoxOptions();


                try {

                    options.addArguments( "test-type" );
                    options.setHeadless( true );

                } catch (Exception e) {

                    LOGGER.info( String.format( "\t[%s] option could NOT been set.\t\n", driverSelect ) );
                    Assert.fail( String.format( "\t[%s] option could NOT been set.\t\n", driverSelect ) );

                }

                return ( new FirefoxDriver( options ) );

            }

            case "opera": {

                OperaOptions options = new OperaOptions();

                try {

                    options.addArguments("test-type");

                    LOGGER.info( String.format( "\t[%s] option is set.\t\n", driverSelect ) );

                } catch (Exception e) {

                    LOGGER.info( String.format( "\t[%s] option could NOT been set.\t\n", driverSelect ) );
                    Assert.fail( String.format( "\t[%s] option could NOT been set.\t\n", driverSelect ) );

                }

                return ( new OperaDriver( options ) );

            }

            case "safari": {

                //@Before := /usr/bin/safaridriver --enable
                SafariOptions options = new SafariOptions();

                return ( new SafariDriver( options ) );

            }

            case "edge": {

                EdgeOptions options;

                options = new EdgeOptions();

                return ( new EdgeDriver( options ) );

            }

            default:
                return null;
        }

    }

}
