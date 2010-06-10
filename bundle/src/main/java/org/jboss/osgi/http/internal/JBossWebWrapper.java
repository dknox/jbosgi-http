package org.jboss.osgi.http.internal;

import org.apache.catalina.Server;
import org.apache.catalina.startup.Catalina;

public final class JBossWebWrapper
{

   private String CATALINA_CONFIG = "conf" + java.io.File.separator + "server.xml";

   /**
    * The reference to the Tomcat instance
    */
   private Server server = null;
   private Catalina catalinaInstance = null;

   public JBossWebWrapper()
   {
      init();
   }

   private void init()
   {
      try
      {
         System.getProperties().setProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", "true");
         System.getProperties().setProperty("catalina.useNaming", "false");

         catalinaInstance = new Catalina();
         catalinaInstance.load();
         server = catalinaInstance.getServer();
         catalinaInstance.start();
         Thread.currentThread().join();
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }
   }

   public Server getServer()
   {
      return server;
   }

}
