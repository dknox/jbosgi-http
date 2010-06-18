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

// Dictionary is deprecated but required by OSGi. 
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.catalina.core.StandardContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

public class JBossWebHttpServiceImpl implements HttpService
{
   private final JBossWebWrapper jbwWrapper;
   private final Bundle bundle;

   public JBossWebHttpServiceImpl(final Bundle bundle, ServiceRegistration reg)
   {
      this.bundle = bundle;
      jbwWrapper = new JBossWebWrapper(new BundleAdapter(bundle, reg));
      try
      {
         // initialize but don't start
         jbwWrapper.init();
      } catch (Exception ex)
      {
         // TBD: Logging
         ex.printStackTrace();
      }
   }

   // This service returns the same context each time
   @Override
   public HttpContext createDefaultHttpContext()
   {
      return new JBossWebHttpContextImpl(this.bundle);
   }

   // Find the servlet, delete old resources, add the new resources 
   @Override
   public void registerResources(String alias, String name, HttpContext context) throws NamespaceException
   {
   }

   @SuppressWarnings("rawtypes")
   public void registerServlet(String alias, Servlet servlet, Dictionary initparams, HttpContext context) throws ServletException, NamespaceException
   {
      
      try
      {
         jbwWrapper.registerServlet(alias, servlet, initparams, context);
      } catch (Exception ex)
      {
         // TBD logging
         ex.printStackTrace();
      }
      
      // Call the Wrapper
      //should check the registrar to make sure it's not a duplicate

      //do this last
      //registrar.put(alias, context);
   }

   //Unregister previously registered Resources
   public void unregister(String alias) throws IllegalArgumentException
   {
   }

}
