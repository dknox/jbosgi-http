package org.jboss.osgi.http.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URL;
import java.io.IOException;
import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;

public class JBossWebHttpContextImpl implements HttpContext
{
   private Bundle bundle;

   public JBossWebHttpContextImpl(final Bundle bundle)
   {
      this.bundle = bundle;
   }

   public String getMimeType(final String name)
   {
      // We want to let JBossWeb determine the MIME type itself
      return null;
   }

   public URL getResource(final String name)
   {
      return bundle.getResource(name);
   }

   public boolean handleSecurity(final HttpServletRequest req, final HttpServletResponse resp) throws IOException
   {
      // for now just return true
      return true;
   }
}
