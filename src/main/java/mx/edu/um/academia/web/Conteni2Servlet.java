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
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import mx.edu.um.academia.dao.ContenidoDao;
import mx.edu.um.academia.dao.CursoDao;
import mx.edu.um.academia.model.Contenido;
import mx.edu.um.academia.utils.Constantes;
import org.apache.commons.codec.digest.DigestUtils;
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
public class Conteni2Servlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(Conteni2Servlet.class);
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
        String[] params = StringUtils.split(request.getPathInfo(), "/");
        InputStream in = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(params[0]);
            sb.append(params[1]);
            sb.append(params[2]);
            sb.append(Constantes.SALT);
            String checksum = params[3];
            if (checksum.equals(DigestUtils.shaHex(sb.toString()))) {
//                // Set to expire far in the past.
//                response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
//                // Set standard HTTP/1.1 no-cache headers.
//                response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
//                // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
//                response.addHeader("Cache-Control", "post-check=0, pre-check=0");
//                // Set standard HTTP/1.0 no-cache header.
//                response.setHeader("Pragma", "no-cache");
                if (params[0].equals("admin")) {
                    log.trace("ADMIN");
                    procesa(params, request, response, in);
                } else {
                    Long userId = new Long(params[0]);
                    Long cursoId = new Long(params[1]);

                    log.trace("USUARIO: {} | CURSO: {}", userId, cursoId);
                    boolean estaInscrito = cursoDao.estaInscrito(cursoId, userId);
                    if (estaInscrito) {
                        procesa(params, request, response, in);
                    }
                }
                
            } else {
                log.warn("EL CHECKSUM NO COINCIDE! Le voy a mostrar el 404");
                OutputStream out = response.getOutputStream();
                FileCopyUtils.copy(new FileInputStream(request.getSession().getServletContext().getRealPath("/404.html")), out);
            }
            
        } catch (IOException e) {
            log.error("Hubo un problema al intentar cargar el contenido", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    log.error("Hubo problemas al intentar cerrar el inputstream", ex);
                }
            }
        }
    }

    private void procesa(String[] params, HttpServletRequest request, HttpServletResponse response, InputStream in) throws IOException {
        Long contenidoId = new Long(params[2]);
        String ruta = (String) request.getSession().getAttribute("r" + contenidoId);
        if (StringUtils.isBlank(ruta)) {
            log.debug("Buscando ruta para contenido {}", contenidoId);
            Contenido contenido = contenidoDao.obtiene(contenidoId);
            ruta = contenido.getRuta();
            request.getSession().setAttribute("r" + contenidoId, ruta);
        }
        String nombreArchivo = params[params.length - 1];
        if (StringUtils.endsWithAny(nombreArchivo, new String[]{".html", ".css", ".js"})) {
            if (nombreArchivo.endsWith(".html")) {
                response.setContentType("text/html");
            } else if (nombreArchivo.endsWith(".css")) {
                response.setContentType("text/css");
            } else if (nombreArchivo.endsWith(".js")) {
                response.setContentType("application/x-javascript");
            }
            File file = new File(ruta);
            StringBuilder sb = new StringBuilder();
            sb.append(file.getParent());
            for (int i = 4; i < params.length; i++) {
                sb.append("/").append(params[i]);
            }
            log.trace("Buscando: {}", sb.toString());
            File file2 = new File(sb.toString());
            if (file2.exists()) {
                OutputStream out = response.getOutputStream();
                FileCopyUtils.copy(new FileInputStream(file2), out);
            }
        } else {
            File file = new File(ruta);
            StringBuilder sb = new StringBuilder();
            sb.append(file.getParent());
            for (int i = 4; i < params.length; i++) {
                sb.append("/").append(params[i]);
            }
            log.trace("Buscando: {}", sb.toString());
            File file2 = new File(sb.toString());
            if (file2.exists() && !file2.isDirectory()) {
                OutputStream out = response.getOutputStream();
                FileCopyUtils.copy(new FileInputStream(file2), out);
            } else {
                log.warn("No encontre el archivo {}", sb.toString());
            }
        }
    }
}
