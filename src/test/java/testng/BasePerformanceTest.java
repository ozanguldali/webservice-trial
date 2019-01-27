package testng;

import org.testng.annotations.Factory;

import java.io.File;
import java.util.Arrays;

import static util.BannerUtil.paintBanner;
import static util.SshTunnelUtil.openTunnel;
import static util.TestNGUtil.getCucumberTest;

public class BasePerformanceTest {

    private static final String FEATURE_DIR     =   "src/test/resources/features";
    private static String tags                  =   System.getProperty( "tag", "~@ignore&@performance" );
    // "@tag1,@tag2" == tag1 || tag2
    // "@tag1&@tag2" == tag1 && tag2

    public BasePerformanceTest() {

        paintBanner();

        openTunnel();

    }

    @Factory
    public Object[] createTest() {

        String[] featureFiles = ( new File( FEATURE_DIR ) ).list();

        assert featureFiles != null;
        return Arrays.stream( featureFiles ).map( this::createCucumberTest ).toArray();

    }

    private CucumberTest createCucumberTest( String featureFile ) {

        return getCucumberTest( featureFile, tags );

    }

}
