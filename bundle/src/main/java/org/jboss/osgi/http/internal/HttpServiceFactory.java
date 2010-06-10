package org.jboss.osgi.http.internal;

import java.util.HashMap;

import org.apache.catalina.startup.Catalina;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

public class HttpServiceFactory implements ServiceFactory
{
   private Catalina catalinaInstance;
   private JBossWebWrapper server;
   private HashMap<String, JBossWebHttpServiceImpl> services;

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
   public Object getService(final Bundle servicebndl, final ServiceRegistration reg)
   {
      JBossWebHttpContextImpl ctxImpl = new JBossWebHttpContextImpl(servicebndl);
      JBossWebHttpServiceImpl srvcImpl = new JBossWebHttpServiceImpl(ctxImpl, server);
      return srvcImpl;
   }

   public void ungetService(final Bundle servicebndl, final ServiceRegistration reg, Object obj)
   {
      // recall the service from a map and shut down the registrations
      // related to it. 
   }
}
