package runner;

import static util.SshTunnelUtil.closeTunnel;
import static util.SshTunnelUtil.openTunnel;

public class SshTunnelRunner {

    static class open {

        public static void main(String[] args) {

            openTunnel();

//            System.exit( 0 );

        }

    }

    static class close {

        public static void main(String[] args) {

            closeTunnel();

//            System.exit( 0 );

        }

    }

}
