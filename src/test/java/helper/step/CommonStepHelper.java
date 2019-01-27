package helper.step;

import cucumber.api.DataTable;
import org.junit.Assert;
import step.OracleStepDefinitions;
import step.RestStepDefinitions;
import step.SeleniumStepDefinitions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static util.ContextMapUtil.context;
import static util.EnvironmentUtil.PROJECT_DIR;
import static util.EnvironmentUtil.SLASH;
import static util.LoggingUtil.LOGGER;

public class CommonStepHelper {

    public static String getStringValueOfSource(String key, String source) {

        switch ( source ) {

            case "text":
                return String.valueOf( key );

            case "file":

                try {

                    return new String( Files.readAllBytes( Paths.get( PROJECT_DIR + SLASH + "src" + SLASH + "test"
                            + SLASH + "resources" + SLASH + "config" + SLASH + "test-bases" + SLASH + key ) ) );

                } catch (IOException e) {

                    LOGGER.info( String.format( "\tThe file could NOT been found: [%s]\t\n", key ) );
                    Assert.fail( String.format( "\tThe file could NOT been found: [%s]\t\n", key ) );
                    return null;

                }

            case "context":
                return context.getValue( key );

            default:
                LOGGER.info( String.format( "\tThe source type could NOT been recognised: [%s]\t\n", source ) );
                Assert.fail( String.format( "\tThe source type could NOT been recognised: [%s]\t\n", source ) );
                return null;


        }

    }

    public static class getCommonElements {


        public static void getOracleElements(DataTable dataTable) {

            OracleStepDefinitions.saveValuesOfQueryReturn( dataTable );

        }

        public static void getRestElements(DataTable dataTable) {

            RestStepDefinitions.saveValuesOfResponse( dataTable );

        }

        public static void getJMeterElements(DataTable dataTable) {



        }

        public static void getSeleniumElements(DataTable dataTable) {

            SeleniumStepDefinitions.saveValuesOfElement( dataTable );

        }

    }

}
