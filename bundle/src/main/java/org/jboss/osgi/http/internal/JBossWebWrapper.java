package org.jboss.osgi.http.internal;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.startup.Catalina;

public final class JBossWebWrapper implements LifecycleListener
{

   private String CATALINA_CONFIG = "conf" + java.io.File.separator + "server.xml";

   /**
    * The reference to the Tomcat instance
    */
   private StandardServer server = null;
   private StandardService service = null;
   private Catalina catalinaInstance = null;
   private StandardEngine catalinaEngine = null;

   private volatile boolean started = false;


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

         // Set CATALINA_HOME. Hack it for now.
         catalinaInstance.setCatalinaHome( System.getProperty("user.dir"));
         catalinaInstance.setCatalinaBase( System.getProperty("user.dir"));
         server = new StandardServer();
         catalinaInstance.setServer(server);
         service = new StandardService();
         // Hack a name for now
         service.setName("JBossWebOSGiHttpService");
         service.setServer(server);
         server.addService(service);

         // Add a HTTP connector
         Connector connector = new Connector();
         connector.setPort(8091);
         connector.setService(service);
         service.addConnector(connector);

         // Instance the Engine
         catalinaEngine = new StandardEngine();
         catalinaEngine.setName("JBossWebOSGiHttpEngine");
         catalinaEngine.setDefaultHost("localhost");
         service.setContainer(catalinaEngine);
         //engine.setRealm(new org.apache.catalina.realm.UserDatabaseRealm());

         // Instance the Host
         StandardHost host = new StandardHost();
         // Hacked
         host.setName("localhost");
         host.setAppBase("webapps");
         host.setAutoDeploy(true);
         host.setUnpackWARs(true);
         host.setWorkDir("work");
         catalinaEngine.addChild(host);
      
         // Add ourself as a listener. Be greedy!
         server.addLifecycleListener(this);
         service.addLifecycleListener(this);
         catalinaEngine.addLifecycleListener(this);
         host.addLifecycleListener(this);
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

   public void startServer() throws Exception
   {
      try 
      {
        // Start Tomcat
        catalinaInstance.create();
        server.initialize();
        catalinaInstance.start();
      } catch (Exception ex)
      {
         ex.printStackTrace();
         throw ex;
      }
   }

   public void stopServer() throws Exception
   {
      catalinaInstance.stop();
   }

   public boolean isStarted()
   {
      return started;
   }

   public void lifecycleEvent(LifecycleEvent event)
   {
      if (event.getType().equals(Lifecycle.AFTER_START_EVENT))
      {
         started = true;
      }
   }

}
