package org.jboss.osgi.http.internal;

import java.util.HashMap;

import org.apache.catalina.startup.Catalina;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

public class HttpServiceFactory implements ServiceFactory
{
   private Catalina catalinaInstance;
   private JBossWebWrapper wrapper;
   private HashMap<String, JBossWebHttpServiceImpl> services;

   /*
    * Model code passes a server wrapper into this constructor
    */
   public HttpServiceFactory()
   {
      try
      {
         wrapper = new JBossWebWrapper();
         wrapper.startServer();
      } catch (Exception ex)
      {
         //TBD connect up with logging
         ex.printStackTrace();
      }
   }

   /* 
    * return a new service instance each time
    */
   public Object getService(final Bundle bundle, final ServiceRegistration reg)
   {
      //get the workdir from the ServiceRegistration ?
      JBossWebHttpServiceImpl srvcImpl = new JBossWebHttpServiceImpl(bundle, wrapper, "/home/dknox/test" );
      return srvcImpl;
   }

   public void ungetService(final Bundle servicebndl, final ServiceRegistration reg, Object obj)
   {
      // recall the service from a map and shut down the registrations
      // related to it. 
   }
}
