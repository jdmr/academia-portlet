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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("Buscando {}", req.getPathInfo());
        String userIdString = req.getParameter("userId");
        String cursoIdString = req.getParameter("cursoId");
        String esAdmin = req.getParameter("admin");
        String contenidoIdString = req.getParameter("contenidoId");
        log.debug("CursoId: {}", cursoIdString);
        if (StringUtils.isNotBlank(userIdString)
                && StringUtils.isNotBlank(cursoIdString)) {
            Long userId = new Long(userIdString);
            Long cursoId = new Long(cursoIdString);
            boolean estaInscrito = cursoDao.estaInscrito(cursoId, userId);
            if (estaInscrito || StringUtils.isNotBlank(esAdmin)) {
                log.debug("ContenidoId: {}", contenidoIdString);
                if (StringUtils.isNotBlank(contenidoIdString)) {
                    Long contenidoId = new Long(contenidoIdString);
                    Contenido contenido = contenidoDao.obtiene(contenidoId);
                    if (StringUtils.isNotBlank(contenido.getRuta())) {
                        log.debug("Subiendo ruta {}", contenido.getRuta());
                        req.getSession().setAttribute("ruta", contenido.getRuta());
                        BufferedOutputStream out = new BufferedOutputStream(resp.getOutputStream());
                        try (InputStream in = new FileInputStream(contenido.getRuta())) {
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }
                            out.flush();
                        }
                    }
                }
            }
        } else if (StringUtils.isNotBlank(esAdmin) && StringUtils.isNotBlank(contenidoIdString)) {
            Long contenidoId = new Long(contenidoIdString);
            Contenido contenido = contenidoDao.obtiene(contenidoId);
            if (StringUtils.isNotBlank(contenido.getRuta())) {
                log.debug("Subiendo ruta {}", contenido.getRuta());
                req.getSession().setAttribute("ruta", contenido.getRuta());
                BufferedOutputStream out = new BufferedOutputStream(resp.getOutputStream());
                try (InputStream in = new FileInputStream(contenido.getRuta())) {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.flush();
                }
            }
        } else {
            String ruta = (String) req.getSession().getAttribute("ruta");
            if (StringUtils.isNotBlank(ruta)) {
                File file = new File(ruta);
                if (file.exists()) {
                    String padre = file.getParent();
                    String archivo = padre + req.getPathInfo();
                    BufferedOutputStream out = new BufferedOutputStream(resp.getOutputStream());
                    try (InputStream in = new FileInputStream(archivo)) {
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                        out.flush();
                    }
                }
            } else {
                log.debug("No encontre una ruta en la session");
            }
        }
    }
}
