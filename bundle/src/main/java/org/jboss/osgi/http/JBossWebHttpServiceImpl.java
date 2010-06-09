

package org.jboss.osgi.http;

// Dictionary is outdated but required by OSGi. 
import java.util.Dictionary;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.ContextConfig;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;


public class JBossWebHttpServiceImpl implements HttpService
{
   // private final Bundle bundle;
   private final JBossWebWrapper tcWrapper;
   private static ConcurrentHashMap<String, StandardContext> registrar;
   private final JBossWebHttpContextImpl ctxImpl;

   public JBossWebHttpServiceImpl(
      final JBossWebHttpContextImpl httpCtx, final JBossWebWrapper server
      )
   {
      ctxImpl = httpCtx;
      tcWrapper = server;
      registrar = new ConcurrentHashMap<String,StandardContext>();
   }

   // This service returns the same context each time
   public HttpContext createDefaultHttpContext()
   {
      return ctxImpl;
   }

   // Find the servlet, delete old resources, add the new resources 
   public void registerResources(String alias, 
                  String name,
                  HttpContext context
                  ) throws NamespaceException
   {
   }

   public void registerServlet(
                  String alias,
                  Servlet servlet,
                  Dictionary initparams,
                  HttpContext context
                  ) throws ServletException, NamespaceException
   {
      //should check the registrar to make sure it's not a duplicate
      StandardContext stdCtx = new StandardContext();
      stdCtx.setName(alias);
      stdCtx.setWorkDir(context.getResource("workDir").toString());
      ServletContext servletCtx = stdCtx.getServletContext();
      // addServlet should compile any annotations.
      ServletRegistration.Dynamic srvRegDyn = servletCtx.addServlet(
         alias, servlet
         );
      // Dictionary is deprecated. Using ye-olde enumeration.. 
      java.util.Enumeration parmKeys = initparams.keys();
      for (java.util.Enumeration e = initparams.keys(); e.hasMoreElements();)
      {
         String key = (String)e.nextElement();
         String parm = (String)initparams.get(key);
         srvRegDyn.setInitParameter(key, parm);
      }
      // A servlet is defined by it's annotations or a web.xml fragment
      // Does a fragment exist in the bundle?
      // For now assume the bundle has a file structure and we can find
      // the web fragment or a web.xml under the servlet's name

      //do this last
      registrar.put(alias, stdCtx);
   }

   //Unregister previously registered Resources
   public void unregister(String alias)
      throws IllegalArgumentException
   {
   }

}
