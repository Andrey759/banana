package org.banananetwork;

import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishRuntime;

/**
 * @author karyakin dmitry
 *         date 21.07.15.
 */
public class Launcher {


    public static void main(String[] args) {
        try {
            GlassFish server = GlassFishRuntime.bootstrap().newGlassFish();
            server.start();
        } catch (GlassFishException e) {
            e.printStackTrace();
        }
    }


}
