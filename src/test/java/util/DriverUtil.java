package util;

import org.junit.Assert;
import org.openqa.selenium.WebDriver;

import java.io.File;

import static helper.DriverHelper.setDriverOptions;
import static util.EnvironmentUtil.OS_VALUE;
import static util.EnvironmentUtil.SLASH;
import static util.LoggingUtil.LOGGER;

public class DriverUtil {

    public static WebDriver setDriver(String driverSelect) {

        String osLower = OS_VALUE.toLowerCase();
        boolean isHeadless = Boolean.FALSE;

        try {

            if ( osLower.contains( "win" ) ) {

                final String winPath = "tools" + SLASH + "drivers" + SLASH + "win";

                try {

                    if ( driverSelect.equals( "chrome" ) )
                        System.setProperty( "webdriver.chrome.driver", ( new File( winPath + SLASH + "chromedriver.exe" ) ).getAbsolutePath() );
                    else if ( driverSelect.contains( "firefox" ) )
                        System.setProperty( "webdriver.gecko.driver", ( new File( winPath + SLASH + "geckodriver.exe" ) ).getAbsolutePath() );
                    else if ( driverSelect.contains("edge" ) )
                        System.setProperty( "webdriver.edge.driver", ( new File( winPath + SLASH + "MicrosoftWebDriver.exe" ) ).getAbsolutePath() );
                    else {

                        LOGGER.info( "\tBrowser Type Could NOT Been Found !!!" );
                        Assert.fail( "\tBrowser Type Could NOT Been Found !!!" );

                    }

                } catch (Exception e) {

                    LOGGER.info( "\tBrowser Type Could NOT Been Found !!!" );
                    Assert.fail( "\tBrowser Type Could NOT Been Found !!!" );

                }

            } else if ( osLower.contains( "mac" ) ) {

                final String macPath = "tools" + SLASH + "drivers" + SLASH + "mac";

                try {

                    if ( driverSelect.equals( "chrome" ) )
                        System.setProperty( "webdriver.chrome.driver", ( new File( macPath + SLASH + "chromedriver" ) ).getAbsolutePath() );
                    else if ( driverSelect.contains( "firefox" ) )
                        System.setProperty( "webdriver.gecko.driver", ( new File( macPath + SLASH + "geckodriver" ) ).getAbsolutePath() );
                    else if ( driverSelect.contains( "opera" ) )
                        System.setProperty( "webdriver.opera.driver", ( new File( macPath + SLASH + "operadriver" ) ).getAbsolutePath() );
                    else if ( driverSelect.contains( "safari" ) )
                        System.setProperty( "webdriver.safari.driver", ( new File( macPath + SLASH + "safaridriver" ) ).getAbsolutePath() );
                    else {

                        LOGGER.info( "\tBrowser Type Could NOT Been Found !!!" );
                        Assert.fail( "\tBrowser Type Could NOT Been Found !!!" );

                    }

                } catch (Exception e) {

                    LOGGER.info( "\tBrowser Type Could NOT Been Found !!!" );
                    Assert.fail( "\tBrowser Type Could NOT Been Found !!!" );

                }

            } else if ( osLower.contains( "sunos" )
                    || osLower.contains( "nix" )
                    || osLower.contains( "nux" )
                    || osLower.contains( "aix" ) ) {

                final String linuxPath = "tools" + SLASH + "drivers" + SLASH + "linux";

                isHeadless = true;

                try {

                    if ( driverSelect.equals( "chrome" ) )
                        System.setProperty( "webdriver.chrome.driver", ( new File( linuxPath + SLASH + "chromedriver" ) ).getAbsolutePath() );
                    else if ( driverSelect.contains("firefox" ) )
                        System.setProperty( "webdriver.gecko.driver", ( new File( linuxPath + SLASH + "/geckodriver" ) ).getAbsolutePath() );
                    else if ( driverSelect.contains( "opera" ) )
                        System.setProperty( "webdriver.opera.driver", ( new File( linuxPath + SLASH + "operadriver" ) ).getAbsolutePath() );
                    else {

                        LOGGER.info( "\tBrowser Type Could NOT Been Found !!!" );
                        Assert.fail( "\tBrowser Type Could NOT Been Found !!!" );

                    }

                } catch (Exception e) {

                    LOGGER.info( "\tBrowser Type Could NOT Been Found !!!" );
                    Assert.fail( "\tBrowser Type Could NOT Been Found !!!" );

                }


            } else {

                LOGGER.info( "\tOperating System Could NOT Been Found !!!\t\n" );
                Assert.fail( "\tOperating System Could NOT Been Found !!!\t\n" );

            }

        } catch (Exception e) {

            LOGGER.info( "\tOperating System Could NOT Been Found !!!" );
            Assert.fail( "\tOperating System Could NOT Been Found !!!" );

        }

        return ( setDriverOptions( driverSelect, isHeadless ) );

    }

}
