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

// Dictionary is outdated but required by OSGi. 
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.osgi.framework.Bundle;

import org.apache.catalina.core.StandardContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

public class JBossWebHttpServiceImpl 
{
   private final Bundle bundle;
   private final JBossWebWrapper jbwWrapper;
   //private static ConcurrentHashMap<String, HttpContext> registrar;
   //private final JBossWebHttpContext httpCtx;
   private final String workDir;

   public JBossWebHttpServiceImpl(final Bundle bundle, 
                                  final JBossWebWrapper server, 
                                  String workDir
                                  )
   {
      this.bundle = bundle;
      this.jbwWrapper = server;
      this.workDir = workDir;
      //registrar = new ConcurrentHashMap<String, HttpContext>();
   }

   // This service returns the same context each time
   public HttpContext createDefaultHttpContext()
   {
      return new JBossWebHttpContextImpl(this.bundle);
   }

   // Find the servlet, delete old resources, add the new resources 
   public void registerResources(String alias, String name, HttpContext context) throws NamespaceException
   {
   }

   @SuppressWarnings("rawtypes")
   public void registerServlet(String alias, Servlet servlet, Dictionary initparams, HttpContext context) throws ServletException, NamespaceException
   {
      //should check the registrar to make sure it's not a duplicate
      StandardContext stdCtx = new StandardContext();
      stdCtx.setName(alias);
      stdCtx.setWorkDir(context.getResource("workDir").toString());
      ServletContext servletCtx = stdCtx.getServletContext();
      
      // addServlet should compile any annotations.
      ServletRegistration.Dynamic srvRegDyn = servletCtx.addServlet(alias, servlet);
      
      for (Enumeration e = initparams.keys(); e.hasMoreElements();)
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
      //registrar.put(alias, context);
   }

   //Unregister previously registered Resources
   public void unregister(String alias) throws IllegalArgumentException
   {
   }

}
