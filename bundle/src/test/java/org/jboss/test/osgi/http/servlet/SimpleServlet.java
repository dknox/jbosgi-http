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
package org.jboss.test.osgi.http.servlet;


import java.io.PrintWriter;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.annotation.*;

@WebServlet(name="testservlet", urlPatterns={"/osgihttp"},
      initParams={ @WebInitParam(name="parm1", value="foo") } )

/**
 * A simple servlet for testing purposes
 * 
 * @author dknox@jboss.com
 * @since 18-Jun-2010
 */

public class SimpleServlet extends HttpServlet
{
   protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException
   {
      PrintWriter out = resp.getWriter();
      String param = getServletConfig().getInitParameter("parm1");
      out.println(param);
      out.close();
   }

   protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException
   {
      doGet(req, resp);
   }
}
