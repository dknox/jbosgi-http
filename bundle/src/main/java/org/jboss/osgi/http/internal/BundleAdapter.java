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

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceRegistration;

import org.w3c.dom.Document;

/*
 * Centralize access to the Bundle and ServiceRegistration
 */

public class BundleAdapter
{
   private final Bundle bundle;
   private final ServiceRegistration serviceReg;

   public BundleAdapter(Bundle bundle, ServiceRegistration svcReg)
   {
      this.bundle = bundle;
      this.serviceReg = svcReg;
   }

   // Change this to return a structure versus a URL
   public Document getServerXml()
   {
      Document doc = null;
      try 
      {
         URL srvxmlurl = bundle.getResource("server.xml");
         InputStream is = srvxmlurl.openStream();
         // Use Digester?
      } catch (IOException iox)
      {
         // TBD: log
         iox.printStackTrace();
      }
      return doc;
   }

   /*
    * Find catalina.properties in the bundle. It may not be there,
    * but the operation will always return a non-null reference.
    */
   public Properties getCatalinaProperties()
   {
      Properties props = new Properties();
      try
      {
         URL propurl = bundle.getResource("catalina.properties");
         InputStream is = propurl.openStream();
         props.load(is);
      } catch (IOException iox)
      {
         // TBD: log
         iox.printStackTrace();
      }
      return props;
   }

}


