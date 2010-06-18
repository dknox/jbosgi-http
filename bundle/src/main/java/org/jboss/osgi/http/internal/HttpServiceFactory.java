package org.jboss.osgi.http.internal;

import java.util.HashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

public class HttpServiceFactory implements ServiceFactory
{
   private HashMap<String, JBossWebHttpServiceImpl> services;

   /*
    * Model code passes a server wrapper into this constructor
    */
   public HttpServiceFactory()
   {
   }

   /* 
    * Return a new service instance each time for now. It may need
    * to be smarter later.
    * 
    */
   public Object getService(final Bundle bundle, final ServiceRegistration reg)
   {
      JBossWebHttpServiceImpl srvcImpl = new JBossWebHttpServiceImpl(bundle, reg);
      return srvcImpl;
   }

   public void ungetService(final Bundle servicebndl, final ServiceRegistration reg, Object obj)
   {
      // recall the service from a map and shut down the registrations
      // related to it. 
   }
}
