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
package org.jboss.test.osgi.http;

//$Id$

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.jboss.osgi.testing.OSGiFrameworkTest;
import org.jboss.osgi.testing.OSGiManifestBuilder;
import org.jboss.shrinkwrap.api.Archives;
import org.jboss.shrinkwrap.api.Asset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.test.osgi.http.bundle.EndpointServlet;
import org.jboss.test.osgi.http.bundle.HttpServiceTestActivator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;

/**
 * An HttpService test case.
 * 
 * @author thomas.diesler@jboss.com
 * @since 19-Apr-2010
 */
public class HttpServiceTestCase extends OSGiFrameworkTest
{
   private static Bundle testBundle;
   
   @BeforeClass
   public static void beforeClass() throws Exception
   {
      // Install/Start the jboss-osgi-http bundle
      String bundleName = "jboss-osgi-http-" + System.getProperty("project.version");
      URL bundleURL = new File("../bundle/target/" + bundleName + ".jar").toURI().toURL();
      Bundle bundle = systemContext.installBundle(bundleURL.toExternalForm());
      bundle.start();
   }
   
   @Before
   public void setUp() throws Exception
   {
      super.setUp();
      
      if (testBundle == null)
      {
         // Build a test bundle with shrinkwrap
         final JavaArchive archive = Archives.create("http-service-test", JavaArchive.class);
         archive.addClass(HttpServiceTestActivator.class);
         archive.addClass(EndpointServlet.class);
         archive.addResource(getResourceFile("http/message.txt"), "res/message.txt");
         archive.setManifest(new Asset()
         {
            public InputStream openStream()
            {
               OSGiManifestBuilder builder = OSGiManifestBuilder.newInstance();
               builder.addBundleSymbolicName(archive.getName());
               builder.addBundleManifestVersion(2);
               builder.addBundleActivator(HttpServiceTestActivator.class.getName());
               builder.addImportPackages("org.osgi.framework", "org.osgi.service.http", "org.osgi.util.tracker");
               builder.addImportPackages("javax.servlet", "javax.servlet.http");
               return builder.openStream();
            }
         });
         
         // Install/Start the test bundle
         testBundle = installBundle(archive);
         testBundle.start();
         assertBundleState(Bundle.ACTIVE, testBundle.getState());
         
         // Allow 10s for the HttpService to become available
         ServiceReference sref = getServiceReference(HttpService.class.getName(), 10000);
         assertNotNull("HttpService available", sref);
      }
   }
   
   @Test
   public void testServletAccess() throws Exception
   {
      String line = getHttpResponse("/servlet?test=plain");
      assertEquals("Hello from Servlet", line);
   }

   @Test
   public void testServletInitProps() throws Exception
   {
      String line = getHttpResponse("/servlet?test=initProp");
      assertEquals("initProp=SomeValue", line);
   }

   @Test
   public void testServletBundleContext() throws Exception
   {
      String line = getHttpResponse("/servlet?test=context");
      assertEquals("http-service-test", line);
   }

   @Test
   public void testResourceAccess() throws Exception
   {
      String line = getHttpResponse("/file/message.txt");
      assertEquals("Hello from Resource", line);
   }

   private String getHttpResponse(String reqPath) throws Exception
   {
      URL url = new URL("http://" + getServerHost() + ":8090" + reqPath);
      BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
      return br.readLine();
   }
}