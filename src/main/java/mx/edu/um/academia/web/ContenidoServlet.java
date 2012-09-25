/*
 * The MIT License
 *
 * Copyright 2012 Universidad de Montemorelos A. C.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package mx.edu.um.academia.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.edu.um.academia.dao.ContenidoDao;
import mx.edu.um.academia.dao.CursoDao;
import mx.edu.um.academia.model.Contenido;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.FileCopyUtils;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
public class ContenidoServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(ContenidoServlet.class);
    private ApplicationContext context = null;
    private CursoDao cursoDao = null;
    private ContenidoDao contenidoDao = null;

    @Override
    public void init() throws ServletException {
        super.init();
        log.info("Cargando contexto de spring en ContenidoServlet...");
        context = new ClassPathXmlApplicationContext("/context/academia.xml");
        cursoDao = context.getBean(CursoDao.class);
        contenidoDao = context.getBean(ContenidoDao.class);
        log.info("Contexto cargado.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        log.debug("Buscando {}", request.getPathInfo());
        InputStream in = null;
        // Set to expire far in the past.
        response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");
        try {
            String userIdString = request.getParameter("userId");
            String cursoIdString = request.getParameter("cursoId");
            String esAdmin = request.getParameter("admin");
            String contenidoIdString = request.getParameter("contenidoId");
            if (StringUtils.isNotBlank(userIdString)
                    && StringUtils.isNotBlank(cursoIdString)) {
                log.trace("Buscando por usuario");
                Long userId = new Long(userIdString);
                Long cursoId = new Long(cursoIdString);
                boolean estaInscrito = cursoDao.estaInscrito(cursoId, userId);
                if (estaInscrito || StringUtils.isNotBlank(esAdmin)) {
                    log.trace("ContenidoId: {}", contenidoIdString);
                    if (StringUtils.isNotBlank(contenidoIdString)) {
                        Long contenidoId = new Long(contenidoIdString);
                        Contenido contenido = contenidoDao.obtiene(contenidoId);
                        if (StringUtils.isNotBlank(contenido.getRuta())) {
                            log.trace("Subiendo ruta {}", contenido.getRuta());
                            request.getSession().setAttribute("ruta", contenido.getRuta());
                            in = new FileInputStream(contenido.getRuta());
                            OutputStream out = response.getOutputStream();
                            FileCopyUtils.copy(in, out);
                        }
                    }
                }
            } else if (StringUtils.isNotBlank(esAdmin) && StringUtils.isNotBlank(contenidoIdString)) {
                log.trace("Buscando por admin");
                Long contenidoId = new Long(contenidoIdString);
                Contenido contenido = contenidoDao.obtiene(contenidoId);
                if (StringUtils.isNotBlank(contenido.getRuta())) {
                    log.debug("Subiendo ruta {}", contenido.getRuta());
                    request.getSession().setAttribute("ruta", contenido.getRuta());
                    in = new FileInputStream(contenido.getRuta());
                    OutputStream out = response.getOutputStream();
                    FileCopyUtils.copy(in, out);
                }
            } else {
                String ruta = (String) request.getSession().getAttribute("ruta");
                log.trace("RUTA {}", ruta);
                if (StringUtils.isNotBlank(ruta)) {
                    File file = new File(ruta);
                    if (file.exists()) {
                        String padre = file.getParent();
                        String archivo = padre + request.getPathInfo();
                        log.trace("Buscando {}", archivo);
                        File file2 = new File(archivo);
                        if (file2.exists() && !file2.isDirectory()) {
                            log.trace("Cargando: {}", file2.getName());
                            if (StringUtils.endsWithAny(file2.getName(), new String[]{".html", ".js", ".css"})) {
                                if (file2.getName().endsWith(".html")) {
                                    response.setContentType("text/html");
                                } else if (file2.getName().endsWith(".js")) {
                                    response.setContentType("application/x-javascript");
                                } else if (file2.getName().endsWith(".css")) {
                                    response.setContentType("text/css");
                                }
                                OutputStream out = response.getOutputStream();
                                in = new FileInputStream(file2);
                                FileCopyUtils.copy(in, out);
                            } else {
                                if (file2.getName().endsWith(".swf")) {
                                    response.setContentType("application/x-shockwave-flash");
                                }
                                OutputStream out = response.getOutputStream();
                                in = new FileInputStream(file2);
                                FileCopyUtils.copy(in, out);
                            }
                        }
                    }
                } else {
                    log.error("No encontre una ruta en la session");
                }
            }
        } catch (IOException e) {
            log.error("Hubo un problema al intentar obtener la ruta " + request.getPathInfo(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    log.error("No se pudo cerrar el inputstream", ex);
                }
            }
        }
    }
}
