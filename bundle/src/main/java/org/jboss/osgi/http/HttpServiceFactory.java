
package org.jboss.osgi.http;

import java.util.HashMap;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Catalina;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
//import org.apache.tomcat.util.digester.Digester;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpService;


public class HttpServiceFactory implements ServiceFactory
{
   private Catalina catalinaInstance;
   private JBossWebWrapper server;
   private HashMap<String,JBossWebHttpServiceImpl> services;

   /*
    * Model code passes a server wrapper into this constructor
    */
   public HttpServiceFactory()
   {
      server = new JBossWebWrapper();
   }

   /* 
    * return a new service instance each time
    */
   public Object getService(
      final Bundle servicebndl, 
      final ServiceRegistration reg
      )
   {
      JBossWebHttpContextImpl ctxImpl = 
         new JBossWebHttpContextImpl(servicebndl, reg);
      JBossWebHttpServiceImpl srvcImpl = 
         new JBossWebHttpServiceImpl(ctxImpl, server);
      return srvcImpl;
   }

   public void ungetService(
      final Bundle servicebndl,
      final ServiceRegistration reg,
      Object obj
      )
   {
      // recall the service from a map and shut down the registrations
      // related to it. 
   }

   /*
    * Temporary driver
    */
   public static void main(String args[] )
   {
      HttpServiceFactory factory = null;

      factory = new HttpServiceFactory();
      
      JBossWebHttpServiceImpl httpsvc = 
         (JBossWebHttpServiceImpl)factory.getService(null, null);

   }

}
