package step;

import com.google.gson.JsonObject;
import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import gherkin.formatter.model.DataTableRow;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.junit.Assert;
import util.ParserUtil;
import util.step.JMeterUtil;

import java.io.File;
import java.io.FileOutputStream;

import static util.EnvironmentUtil.*;
import static util.LoggingUtil.LOGGER;

public class JMeterStepDefinitions {

    private static final File jMeterHome        =   new File( PROJECT_DIR + "/tools/jMeter" );
    private static final File jMeterProperties  =   new File(jMeterHome.getPath() + SLASH + "bin" + SLASH + "jmeter.properties" );

    private static StandardJMeterEngine jMeterEngine;
    private static HashTree testPlanTree;
    private static HTTPSamplerProxy httpSampler;
    private static HeaderManager headerManager;
    private static ThreadGroup threadGroup;

    @Given( "^I do a sample performance test$" )
    public void iDoSamplePerformance() {

        //JMeter Engine
        StandardJMeterEngine jmeter = new StandardJMeterEngine();

        //JMeter initialization (properties, log levels, locale, etc)
        JMeterUtils.loadJMeterProperties(PROJECT_DIR + "/tools/jMeter/bin/jmeter.properties" );
        //JMeterUtils.initLogging();// you can comment this line out to see extra log messages of i.e. DEBUG level
        JMeterUtils.initLocale();

        // JMeter Test Plan, basic all u JOrphan HashTree
        HashTree testPlanTree = new HashTree();

        // HTTP Sampler
        HTTPSampler httpSampler = new HTTPSampler();
        httpSampler.setDomain("example.com");
        httpSampler.setPort(80);
        httpSampler.setPath("/");
        httpSampler.setMethod("GET");

        // Loop Controller
        LoopController loopController = new LoopController();
        loopController.setLoops(1);
        loopController.addTestElement(httpSampler);
        loopController.setFirst(true);
        loopController.initialize();

        // Thread Group
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setNumThreads(1);
        threadGroup.setRampUp(1);
        threadGroup.setSamplerController(loopController);

        // Test Plan
        TestPlan testPlan = new TestPlan("Create JMeter Script From Java Code");

        // Construct Test Plan from previously initialized elements
        testPlanTree.add("testPlan", testPlan);
        testPlanTree.add("loopController", loopController);
        testPlanTree.add("threadGroup", threadGroup);
        testPlanTree.add("httpSampler", httpSampler);

        // Run Test Plan
        jmeter.configure(testPlanTree);
        jmeter.run();


    }

    @Given( "^I start a performance test$" )
    public void iStartPerformanceTest() {

        try {

            if ( jMeterHome.exists() ) {

                jMeterEngine = new StandardJMeterEngine();

                JMeterUtils.setJMeterHome( jMeterHome.getPath() );
                JMeterUtils.loadJMeterProperties( jMeterProperties.getPath() );
                //JMeterUtils.initLogging();
                JMeterUtils.initLocale();

                testPlanTree = new HashTree();

                httpSampler = new HTTPSamplerProxy();
                httpSampler.setProperty( TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName() );
                httpSampler.setProperty( TestElement.GUI_CLASS, HttpTestSampleGui.class.getName() );
                httpSampler.setProperty( TestElement.NAME, "HTTP Header Manager" );
                httpSampler.setEnabled( true );

                headerManager = new HeaderManager();
                headerManager.setProperty( TestElement.TEST_CLASS, HeaderManager.class.getName() );
                headerManager.setProperty( TestElement.GUI_CLASS, HeaderPanel.class.getName() );
                headerManager.setEnabled( true );

                LOGGER.info( "\tPerformance test has been preparing...\t\n" );

            }

        } catch ( AssertionError ae ) {

            LOGGER.info( "\tPerformance test could NOT been prepared.\t\n" );
            Assert.fail( "\tPerformance test could NOT been prepared.\t\n" );

        }



    }

    @When( "^I use (\\w+(?: \\w+)*) data set$" )
    public void iUseDataSet (String dataSetFile) {



    }

    @Then( "^I use (\\w+(?: \\w+)*) rest file$" )
    public void iUseRestFile (String restFile) {

        JsonObject restObject = ParserUtil.jsonFileParsing( "rest-bases/" + restFile );

        httpSampler.setProperty( TestElement.NAME, restFile );

        JMeterUtil.getJMeterRequestMethod( restFile, REST_HOST, restObject, httpSampler, headerManager );

    }

    @And( "^I use following thread properties$" )
    public void iUseFollowingThreadProperties (DataTable table) {

        threadGroup = new ThreadGroup();

        threadGroup.setProperty( TestElement.TEST_CLASS, ThreadGroup.class.getName() );
        threadGroup.setProperty( TestElement.GUI_CLASS, ThreadGroupGui.class.getName() );
        threadGroup.setProperty( TestElement.NAME, "Thread Group" );
        threadGroup.setEnabled( true );

        String threadNumber = null;
        String rampUp = null;
        String loop = null;

        for ( DataTableRow row : table.getGherkinRows() ) {

            String propertyKey      =   row.getCells().get( 0 );
            String propertyValue    =   row.getCells().get( 1 );

            switch ( propertyKey ) {

                case "threadNumber" :
                    threadNumber = propertyValue;
                    break;
                case "rampUp" :
                    rampUp = propertyValue;
                    break;
                case "loop" :
                    loop = propertyValue;
                    break;

            }

        }

        JMeterUtil.setJMeterThreadProperties( threadNumber, rampUp, loop, threadGroup, httpSampler );

    }

    @And( "^I run the corresponding performance test$" )
    public void iRunPerformanceTest() {

        TestPlan testPlan = new TestPlan("Automated JMeter Script From Java Code");

        try {

            testPlan.setProperty( TestElement.TEST_CLASS, TestPlan.class.getName() );
            testPlan.setProperty( TestElement.GUI_CLASS, TestPlanGui.class.getName() );
            testPlan.setUserDefinedVariables( (Arguments) new ArgumentsPanel().createTestElement());
            testPlan.setEnabled( true );

            testPlanTree.add(testPlan);

            HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup );
            threadGroupHashTree.add( headerManager );
            threadGroupHashTree.add( httpSampler );
            SaveService.saveTree( testPlanTree, new FileOutputStream( jMeterHome + SLASH + "example.jmx" ) );

            Summariser summer = null;
            String summariserName = JMeterUtils.getPropDefault( "summariser.name", "summary" );
            if (summariserName.length() > 0) {
                summer = new Summariser( summariserName );
            }

            String logFile = jMeterHome + SLASH + "example.jtl";
            ResultCollector logger = new ResultCollector( summer );
            logger.setFilename( logFile );
            testPlanTree.add( testPlanTree.getArray()[0], logger );

            jMeterEngine.configure( testPlanTree );
            jMeterEngine.run();

            LOGGER.info( "\tPerformance test has been started.\t\n" );

        } catch ( Exception e ) {

            LOGGER.info( "\tPerformance test has been aborted.\t\n" );
            Assert.fail( "\tPerformance test has been aborted.\t\n" );

        }

    }

}
