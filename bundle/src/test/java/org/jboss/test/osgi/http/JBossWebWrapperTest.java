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


//$Id$
package org.jboss.test.osgi.http;

import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;

import org.jboss.osgi.http.internal.BundleAdapter;
import org.jboss.osgi.http.internal.JBossWebWrapper;
import org.jboss.osgi.http.internal.JBossWebHttpContextImpl;
import org.jboss.test.osgi.http.servlet.SimpleServlet;


/**
 * Test the JBossWeb wrapper.
 * 
 * @author thomas.diesler@jboss.com
 * @since 10-Jun-2010
 */

public class JBossWebWrapperTest 
{
   @Test
   public void testStartServer() throws Exception
   {
      Bundle bundle = Mockito.mock(Bundle.class);
      ServiceRegistration svcreg = Mockito.mock(ServiceRegistration.class);
      JBossWebWrapper wrapper = new JBossWebWrapper(new BundleAdapter(bundle,svcreg));
      wrapper.init();
      wrapper.startServer();
      assertNotNull("Server is null", wrapper.getServer());
      assertTrue("Server is not started", wrapper.isStarted());
      wrapper.stopServer();
   }

   @Test
   @Ignore("Not working yet")
   public void testServletAccess() throws Exception
   {
      // Need to mock the Bundle and ServiceRegistration instances
      Bundle bundle = Mockito.mock(Bundle.class);
      ServiceRegistration svcreg = Mockito.mock(ServiceRegistration.class);
      JBossWebWrapper wrapper = new JBossWebWrapper(new BundleAdapter(bundle,svcreg));
      wrapper.init();
      wrapper.startServer();
      assertTrue("Server is not started.", wrapper.isStarted());

      // We need String alias, Servlet, Dictionary, HttpContext
      String alias = "testservlet";
      Hashtable dic = new Hashtable();
      // Add initparams as needed
      HttpContext httpctx = new JBossWebHttpContextImpl(bundle);
      
      wrapper.registerServlet(alias, new SimpleServlet(), dic, httpctx);
      
      //Next step is to ping the Servlet. It should return "testservlet".


      wrapper.stopServer();
   }
}
