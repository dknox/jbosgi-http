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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URL;
import java.io.IOException;
import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;
import org.osgi.framework.ServiceRegistration;

public class JBossWebHttpContextImpl implements HttpContext
{
   private Bundle bundle;

   public JBossWebHttpContextImpl(final Bundle bundle)
   {
      this.bundle = bundle;
   }

   public String getMimeType(final String name)
   {
      // stubbed for now
      return null;
   }

   public URL getResource(final String name)
   {
      return bundle.getResource(name);
   }

   public boolean handleSecurity(final HttpServletRequest req, final HttpServletResponse resp) throws IOException
   {
         return true;
   }
}
