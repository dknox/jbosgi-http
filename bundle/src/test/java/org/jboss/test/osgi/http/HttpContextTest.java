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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.URL;

import org.jboss.osgi.http.internal.JBossWebHttpContextImpl;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;

/**
 * Test the HttpContext.
 * 
 * @author thomas.diesler@jboss.com
 * @since 10-Jun-2010
 */
public class HttpContextTest 
{
   @Test
   public void testGetMimeType() throws Exception
   {
      HttpContext context = new JBossWebHttpContextImpl(null);
      assertNull("MimeType null", context.getMimeType("any-name"));
   }

   @Test
   public void testGetResource() throws Exception
   {
      String resname = "resource-name";
      URL resurl = new URL("http://resource-name");
      
      Bundle bundle = Mockito.mock(Bundle.class);
      Mockito.stub(bundle.getResource(resname)).toReturn(resurl);
      
      HttpContext context = new JBossWebHttpContextImpl(bundle);
      assertEquals("Resource not null", resurl, context.getResource(resname));
   }
}
