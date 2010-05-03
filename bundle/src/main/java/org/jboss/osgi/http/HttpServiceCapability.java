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
package org.jboss.osgi.http;

//$Id$

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.jboss.osgi.spi.capability.Capability;
import org.osgi.service.http.HttpService;

/**
 * Adds the {@link HttpService} capability to the OSGiRuntime
 * under test. 
 * 
 * It is ignored if the {@link HttpService} is already registered.
 * 
 * Installed bundles: jboss-osgi-http.jar
 * 
 * Default properties set by this capability
 * 
 * <table>
 * <tr><th>Property</th><th>Value</th></tr> 
 * <tr><td>org.osgi.service.http.port</td><td>8090</td></tr> 
 * </table> 
 * 
 * @author thomas.diesler@jboss.com
 * @since 05-May-2009
 */
public class HttpServiceCapability extends Capability
{
   /** The default HttpService port: 8090 */
   public static final int DEFAULT_HTTP_SERVICE_PORT = 8090;
   
   public HttpServiceCapability()
   {
      super(HttpService.class.getName());
      addSystemProperty("org.osgi.service.http.port", new Integer(DEFAULT_HTTP_SERVICE_PORT).toString());
      
      addBundle("bundles/jboss-osgi-http.jar");
   }

   public static String getHttpResponse(String host, int port, String reqPath, int timeout) throws IOException 
   {
      int fraction = 200;
      
      String line = null;
      IOException lastException = null;
      while (line == null && 0 < (timeout -= fraction))
      {
         try
         {
            URL url = new URL("http://" + host + ":" + port + reqPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            line = br.readLine();
            br.close();
         }
         catch (IOException ex)
         {
            lastException = ex;
            try
            {
               Thread.sleep(fraction);
            }
            catch (InterruptedException ie)
            {
               // ignore
            }
         }
      }

      if (line == null && lastException != null)
         throw lastException;

      return line;
   }
}