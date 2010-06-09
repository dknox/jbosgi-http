
package org.jboss.osgi.http;

import java.lang.reflect.Field;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Catalina;
import org.apache.catalina.Server;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public final class JBossWebWrapper
{

   private String CATALINA_CONFIG = 
      "conf"+ java.io.File.separator + "server.xml";

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
         System.getProperties().setProperty(
            "org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", "true"
            );
         System.getProperties().setProperty(
            "catalina.useNaming", "false"
            );

         catalinaInstance = new Catalina();
         catalinaInstance.load();
         server = catalinaInstance.getServer();
         catalinaInstance.start();
         Thread.currentThread().join();
      } catch( Exception ex)
      {
         ex.printStackTrace();
      }
   }

   public Server getServer()
   {
      return server;
   }

}
