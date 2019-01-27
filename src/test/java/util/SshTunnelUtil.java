package util;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.junit.Assert;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import static util.EnvironmentUtil.*;
import static util.LoggingUtil.LOGGER;

public class SshTunnelUtil {

    private static Session session = null;
    private static Channel channel = null;

    public static void openTunnel() {

        JSch jSch = new JSch();

        try {

            jSch.addIdentity( PROJECT_DIR + SLASH + "src" + SLASH + "test" + SLASH +
                    "resources" + SLASH + "config" + SLASH + "key-bases" + SLASH + KEY_FILE + "_id_rsa" );

//            jSch.addIdentity( "/Users/ozanguldali/.ssh/ozan_id_rsa" );

            LOGGER.info( String.format( "\tThe identity [%s] is successfully added.\t\n", KEY_FILE ) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe identity <private_key> could NOT been added, because { error: [%s] }\t\n", e.getMessage() ) );
            Assert.fail( String.format( "\tThe identity <private_key> could NOT been added, because { error: [%s] }\t\n", e.getMessage() ) );

        }

        try {

            session = jSch.getSession( SSH_USER, SSH_HOST );

            LOGGER.info( String.format( "\tThe session is created with user: [%s] and host: [%s]\t\n", SSH_USER, SSH_HOST) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe session could NOT been created, because { error: [%s] }\t\n", e.getMessage() ) );
            Assert.fail( String.format( "\tThe session could NOT been created, because { error: [%s] }\t\n", e.getMessage() ) );

        }

        java.util.Properties config = new java.util.Properties();
        config.put( "StrictHostKeyChecking", "no" );
        session.setConfig( config );

        try {

            session.connect();

            LOGGER.info( "\tThe session is connected.\t\n" );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe session could NOT been connected, because { error: [%s] }\t\n", e.getMessage() ) );
            Assert.fail( String.format( "\tThe session could NOT been connected, because { error: [%s] }\t\n", e.getMessage() ) );

        }

        try {

            int port = Integer.parseInt( SSH_PORT );

            while ( !isPortAvailable( port ) ) {

                port = port + 1;
                LOGGER.info( String.format( "\tThe new port is: [%d], but this can be cause some troubles...\t\n", port ) );

            }

            session.setPortForwardingL( port, SSH_HOST, port );

            LOGGER.info( String.format( "\tPort Forwarding is set to the tunnel for [%s] @ [%s] : [%d]\t\n", SSH_USER, SSH_HOST, port ) );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tPort Forwarding could NOT been set to the tunnel, because { error: [%s] }\t\n", e.getMessage() ) );
            Assert.fail( String.format( "\tPort Forwarding could NOT been set to the tunnel, because { error: [%s] }\t\n", e.getMessage() ) );

        }

        try {

            channel = session.openChannel( "direct-tcpip" );
            LOGGER.info( "\tThe tunnel channel is successfully opened.\t\n" );

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe tunnel channel could NOT been opened, because { error: [%s] }\t\n", e.getMessage() ) );
            Assert.fail( String.format( "\tThe tunnel channel could NOT been opened, because { error: [%s] }\t\n", e.getMessage() ) );

        }

    }

    public static void closeTunnel() {

        if ( channel.isConnected() ) {

            try {

                channel.disconnect();
                LOGGER.info( "\tThe tunnel channel is successfully closed.\t\n" );

            } catch (Exception e) {

                LOGGER.info( String.format( "\tThe tunnel channel could NOT been closed, because { error: [%s] }\t\n", e.getMessage() ) );
                Assert.fail( String.format( "\tThe tunnel channel could NOT been closed, because { error: [%s] }\t\n", e.getMessage() ) );

            }

        }

        if ( session.isConnected() ) {

            try {


                session.disconnect();
                LOGGER.info( "\tThe session is disconnected.\t\n" );

            } catch (Exception e) {

                LOGGER.info( String.format( "\tThe session could NOT been disconnected, because { error: [%s] }\t\n", e.getMessage() ) );
                Assert.fail( String.format( "\tThe session could NOT been disconnected, because { error: [%s] }\t\n", e.getMessage() ) );

            }

        }



    }

    private static boolean isPortAvailable(int port) {

        try ( ServerSocket serverSocket = new ServerSocket() ) {

            if ( OS_VALUE.toLowerCase().contains( "mac" ) )
                serverSocket.setReuseAddress( false );

            serverSocket.bind( new InetSocketAddress( InetAddress.getByName( "localhost" ), port ), 1 );
            LOGGER.info( String.format( "\tThe port is available: [%d]\t\n", port ) );
            return true;

        } catch (Exception e) {

            LOGGER.info( String.format( "\tThe port is NOT available: [%d]\t\n", port ) );

//            try {
//
//                if ( serverSocket != null ) {
//
//                    serverSocket.close();
//                    LOGGER.info( String.format( "\tThe port is closed: [%d]\t\n", port ) );
//                    isPortAvailable( port );
//
//                }
//
//            } catch (Exception ex) {
//
//                ex.printStackTrace();
//
//            }

            return false;

        }

    }

}
