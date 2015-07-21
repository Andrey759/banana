package org.banananetwork;

import org.glassfish.embeddable.*;

/**
 * @author karyakin dmitry
 *         date 21.07.15.
 */
public class Launcher {


    public static void main(String[] args) {
        try {

            GlassFishProperties glassfishProperties = new GlassFishProperties();
            glassfishProperties.setPort("http-listener", 8080);
            glassfishProperties.setPort("https-listener", 8181);
            GlassFish server = GlassFishRuntime.bootstrap().newGlassFish(glassfishProperties);
            server.start();
        } catch (GlassFishException e) {
            e.printStackTrace();
        }
    }


}
