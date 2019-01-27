package util;

import testng.CucumberTest;

import java.util.ArrayList;

public class TestNGUtil {

    public static CucumberTest getCucumberTest(String featureFile, String tags) {

        String feature = featureFile.replaceFirst( ".feature$", "" );

        ArrayList< String > options = new ArrayList<>();

        String[] splittedTags = tags.split( "&" );

        for (String tag : splittedTags) {

            if (tag.length() > 0) {

                options.add( "-t" );
                options.add( tag );

            }

        }

        options.add( "-s" );
        options.add( "-m" );
        options.add( "-g" );
        options.add( "src/test/resources/features/" );
        options.add( "-g" );
        options.add( "step" );
        //options.add( "-p" );
        //options.add( "html:src/test/resources/reports/" + feature );
        options.add( "-p" );
        options.add( "html:target/site/cucumber-pretty/" + feature );
        //options.add( "-p" );
        //options.add( "json:src/test/resources/reports/" + feature + "/" + feature + ".json" );
        options.add( "-p" );
        options.add( "json:target/site/cucumber-files/" + feature + ".json" );
        //options.add( "-p" );
        //options.add( "json:target/site/cucumber-jvm-reports/" + feature + ".json" );
        //options.add( "-p" );
        //options.add( "pretty:src/test/resources/reports/" + feature + "/" + feature + ".txt" );
        //options.add( "-p" );
        //options.add( "junit:src/test/resources/reports/" + feature + "/" + feature + ".xml" );
        options.add( "-p" );
        options.add( "rerun:build/rerun/" + feature + ".rerun" );
        options.add( "src/test/resources/features/" + featureFile );

        return new CucumberTest( options );

    }

}
