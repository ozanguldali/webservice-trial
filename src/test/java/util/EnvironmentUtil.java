package util;

import com.google.gson.JsonObject;
import helper.EnvironmentHelper;

public final class EnvironmentUtil extends EnvironmentHelper {

    public static final String OS_VALUE = System.getProperty( "os.name" );

    public static final String PROJECT_DIR = System.getProperty( "user.dir" );

    public static final String SLASH = System.getProperty( "file.separator" );

    private static final JsonObject environmentObject = ParserUtil.jsonFileParsing( "environment" );

    public static final String REST_HOST = getJsonElement( environmentObject, "restHost_" +
            getJsonElement( environmentObject, "restHost_env" ).getAsString() ).getAsString();

    static final String SSH_USER = getJsonElement( environmentObject, "sshUser" ).getAsString();
    static final String SSH_HOST = getJsonElement( environmentObject, "sshHost" ).getAsString();
    static final String SSH_PORT = getJsonElement( environmentObject, "sshPort" ).getAsString();
    static final String KEY_FILE = getJsonElement( environmentObject, "keyFile" ).getAsString();

    static final String DB_USERNAME = getJsonElement( environmentObject, "dbUsername" ).getAsString();
    static final String DB_PASSWORD = getJsonElement( environmentObject, "dbPassword" ).getAsString();
    static final String DB_HOST = getJsonElement( environmentObject, "dbHost" ).getAsString();
    static final String DB_DRIVER_CLASS = getJsonElement( environmentObject, "dbDriverClass" ).getAsString();

    public static final String CACHE_CONTROL = getJsonElement( environmentObject, "Cache-Control" ).getAsString();

}
