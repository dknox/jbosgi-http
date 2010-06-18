/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.osgi.http.internal;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpContext;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.core.StandardService;
import org.apache.catalina.startup.Catalina;

/*
 * Wraps the server instance. Responsible for server lifecycle events like
 * stop and start.
 */
public final class JBossWebWrapper implements LifecycleListener
{

   private String CATALINA_CONFIG = "conf" + java.io.File.separator + "server.xml";

   /**
    * The references to the Tomcat instance
    */
   private StandardServer server = null;
   private StandardService service = null;
   private Catalina catalinaInstance = null;
   private StandardEngine catalinaEngine = null;
   private final BundleAdapter metadata;

   private volatile boolean started = false;

   public JBossWebWrapper(final BundleAdapter bundle)
   {
      this.metadata = bundle;
   }

   /*
    * Prepare the server but do not start it
    * TBD: Requires a server.xml
    */
   public void init() throws Exception
   {
      try
      {
         System.setProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", "true");
         System.setProperty("catalina.useNaming", "false");

         // Initialize the Server and Service.
         initCatalinaServer();

         // Add Connectors
         initConnectors();

         // Instance the Engine
         initEngine();

         // Initialize the Hosts
         initHosts();

      }
      catch (Exception ex)
      {
         // TBD: Logging
         // Rethrow for now
         ex.printStackTrace();
         throw ex;
      }
   }

   /*
    * Start the server
    */
   public void startServer() throws Exception
   {
      if ( catalinaInstance == null )
      {
         throw new IllegalStateException("Can't start uninitialized server.");
      }
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

   /*
    * Stop the server
    */
   public void stopServer() throws Exception
   {
      if ( catalinaInstance == null )
      {
         throw new IllegalStateException("Server has not been started.");
      }
      catalinaInstance.stop();
   }

   /* 
    * Access the StandardServer
    * This will go away. All functional access to the server comes
    * via this Wrapper
    */
   public Server getServer()
   {
      return server;
   }

   /*
    * Access the state of the server
    */
   public boolean isStarted()
   {
      return started;
   }
   
  

   /*
    * Add a servlet to the running server
    */
   public void registerServlet(final String alias,
                               final Servlet servlet,
                               final Dictionary initparams,
                               final HttpContext context
                               ) throws Exception
   {
      if ( !isStarted())
      {
         throw new IllegalStateException("Server has not been started");
      }
      // Either need a way to identify the 'host' or everything
      // is deployed under separate contexts under one host
      StandardHost host = (StandardHost)catalinaEngine.findChild("localhost");
      StandardContext ctx = (StandardContext)host.map(alias);
      if (ctx == null)
      {
         String ctxclassname = host.getContextClass();
         Class clz = Class.forName(ctxclassname);
         ctx = (StandardContext)clz.newInstance();
         ctx.setName(alias);
         ctx.setPath(alias);
         ctx.setDocBase("webapps");
         ctx.start();
         //ctx.init();
      }
      // This will require more. It may not be required
      // It would be nice if the HttpContext would returun
      // all mimetypes. 
      // stdctx.setMimeType(context.getMimeType(null));

      ServletContext servletctx = ctx.getServletContext();
      ServletRegistration.Dynamic srvregdyn = servletctx.addServlet(alias, servlet);
      host.addChild(ctx);
      // We have to do the iteration ourselves. There's no direct translation
      // from a Dictionary to a Map
      for (Enumeration e = initparams.keys(); e.hasMoreElements();)
      {
         String key = (String)e.nextElement();
         String parm = (String)initparams.get(key);
         srvregdyn.setInitParameter(key, parm);
      }

      // Add the mapping
      srvregdyn.addMapping("/"+alias);
      // In the case of multiple hosts on the service, how do we find
      // the correct host?
      // For the purpose of prototyping assume (dangerously) that there
      // is only one host.
   }

   /*
    * Listen for lifecycle events from the server
    */
   @Override
   public void lifecycleEvent(LifecycleEvent event)
   {
      if (event.getType().equals(Lifecycle.AFTER_START_EVENT))
      {
         started = true;
      }
   }

   /*
    * Initialize the Catalina Server and Service
    * TBD: Metadata from server.xml
    * Each bundle have a unique service name?
    */
   private void initCatalinaServer() throws Exception
   {
      // TBD - goes away when the bundle is plugged in
      System.setProperty("catalina.home", System.getProperty("user.dir"));
      
      catalinaInstance = new Catalina();
      server = new StandardServer();
      catalinaInstance.setServer(server);
      service = new StandardService();
      // Hack a name for now
      service.setName("JBossWebOSGiHttpService");
      service.setServer(server);
      server.addService(service);
   }

   /*
    * Add Connectors
    * TBD: metadata from server.xml
    * Requres a port number at least. 
    */
   private void initConnectors() throws Exception
   {
      Connector connector = new Connector();
      connector.setPort(8091);
      connector.setService(service);
      service.addConnector(connector);
   }
   
   /*
    * Instance the Engine.
    * TBD: metadata from server.xml
    * Requries name, host.
    * Optional: DatabaseRealem
    */
   private void initEngine() throws Exception
   {
      catalinaEngine = new StandardEngine();
      catalinaEngine.setName("JBossWebOSGiHttpEngine");
      catalinaEngine.setDefaultHost("localhost");
      service.setContainer(catalinaEngine);
      //engine.setRealm(new org.apache.catalina.realm.UserDatabaseRealm());
   }

   /*
    * Initialize Hosts
    * TBD: metadata from server.xml. There will likely be multiple
    * Hosts
    * Requires: Hostname, AppBase, WorkDir
    * Shouldn't use the default 'localhost'. Each bundle doesn't require
    * unique host name.
    * 
    */
   private void initHosts() throws Exception
   {
      // Instance the Host
      StandardHost host = new StandardHost();
      // Hacked
      host.setName("localhost");
      host.setAppBase("webapps");
      host.setAutoDeploy(true);
      host.setUnpackWARs(true);
      host.setWorkDir("work");
      host.addLifecycleListener(this);
      catalinaEngine.addChild(host);
   }
}
