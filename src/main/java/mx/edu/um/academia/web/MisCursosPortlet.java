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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.portlet.*;
import mx.edu.um.academia.dao.CursoDao;
import mx.edu.um.academia.model.Alumno;
import mx.edu.um.academia.model.AlumnoCurso;
import mx.edu.um.academia.model.Contenido;
import mx.edu.um.academia.model.Curso;
import mx.edu.um.academia.model.ObjetoAprendizaje;
import mx.edu.um.academia.utils.Constantes;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
@Controller
@RequestMapping(value = "VIEW")
public class MisCursosPortlet extends BaseController {

    @Autowired
    private CursoDao cursoDao;

    public MisCursosPortlet() {
        log.info("Nueva instancia de Mis Curso Portlet");
    }

    @RequestMapping
    public String lista(RenderRequest request, Model model) throws SystemException, PortalException {
        log.debug("Lista de mis cursos");
        User usuario = PortalUtil.getUser(request);
        if (usuario != null) {
            ThemeDisplay themeDisplay = this.getThemeDisplay(request);
            // Valida si el usuario se acaba de inscribir
            log.debug("Buscando si el alumno {} esta inscrito", usuario.getScreenName());
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -7);
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://rigel.um.edu.mx/store", "tomcat", "tomcat00");
                PreparedStatement ps = conn.prepareStatement("select autorizacion, comentario, cantidad from movimientos where producto = 'umvirtual' and fecha > ? and email = ? and autorizacion is not null order by fecha desc");
                ps.setTimestamp(1, new java.sql.Timestamp(cal.getTimeInMillis()));
                ps.setString(2, usuario.getEmailAddress());
                ResultSet rs = ps.executeQuery();
                Map<String, BigDecimal> totales = new HashMap<>();
                Map<String, Curso> mapa = new HashMap<>();
                log.debug("Entrando a iteracion");
                while (rs.next()) {
                    String autorizacion = rs.getString("autorizacion");
                    if (StringUtils.startsWith(autorizacion, "\"Y")) {
                        String comentario = rs.getString("comentario");
                        String[] tokens = StringUtils.splitByWholeSeparator(comentario, ", ");
                        String codigo = tokens[4];
                        Curso curso = mapa.get(codigo);
                        if (curso == null) {
                            curso = cursoDao.obtiene(codigo, themeDisplay.getScopeGroupId());
                            mapa.put(codigo, curso);
                        }
                        AlumnoCurso alumnoCurso = cursoDao.obtieneAlumnoCurso(usuario.getUserId(), curso.getId());
                        if (alumnoCurso == null && tokens[0].equals(usuario.getScreenName())) {
                            BigDecimal cantidad = rs.getBigDecimal("cantidad");
                            log.debug("Cantidad: {}", cantidad);
                            if (cantidad != null) {
                                totales.put(codigo, cantidad.setScale(2, RoundingMode.HALF_UP));
                            }
                        }
                    }
                }
                for (Curso curso : mapa.values()) {
                    BigDecimal total = totales.get(curso.getCodigo());
                    if (total != null && total.doubleValue() > 0 && total.compareTo(curso.getPrecio()) >= 0) {
                        log.debug("Inscribiendo alumno");
                        Alumno alumno = cursoDao.obtieneAlumno(usuario.getUserId());
                        boolean creaUsuario = false;
                        if (alumno == null) {
                            alumno = new Alumno(usuario);
                            creaUsuario = true;
                        }
                        cursoDao.inscribe(curso, alumno, creaUsuario, Constantes.INSCRITO);
                        cursoDao.obtieneAlumnoCurso(usuario.getUserId(), curso.getId());
                    }
                }
            } catch (ClassNotFoundException | SQLException e) {
                log.error("No pude validar si esta inscrito contra la base de datos de la UM", e);
            }
            // Termina validacion de alumno recien inscrito


            List<AlumnoCurso> cursos = cursoDao.obtieneCursos(usuario.getUserId());
            model.addAttribute("cursos", cursos);
        }
        return "mis_cursos/lista";
    }

    @RequestMapping(params = "action=ver")
    public String ver(RenderRequest request, Model model, @RequestParam Long cursoId) throws PortalException, SystemException {
        User usuario = PortalUtil.getUser(request);
        AlumnoCurso alumnoCurso = cursoDao.obtieneAlumnoCurso(usuario.getUserId(), cursoId);
        if (alumnoCurso.getDiasDisponibles() > 0) {
            Curso curso = alumnoCurso.getCurso();
            model.addAttribute("curso", curso);

            ThemeDisplay themeDisplay = this.getThemeDisplay(request);
            List<ObjetoAprendizaje> objetos = cursoDao.objetosAlumno(curso.getId(), usuario.getUserId(), themeDisplay);
            if (cursoDao.haConcluido(usuario.getUserId(), curso.getId())) {
                model.addAttribute("concluido", true);
            } else {
                cicloObjetos:
                for (ObjetoAprendizaje objeto : objetos) {
                    log.debug("Viendo contenido de objeto {}", objeto);
                    for (Contenido contenido : objeto.getContenidos()) {
                        log.debug("Contenido : {} : Activo : {}", contenido, contenido.getActivo());
                        if (contenido.getActivo()) {
                            model.addAttribute("contenidoId", contenido.getId());
                            switch (contenido.getTipo()) {
                                case Constantes.TEXTO:
                                    model.addAttribute("texto", contenido.getTexto());
                                    break;
                                case Constantes.VIDEO:
                                    model.addAttribute("video", contenido.getTexto());
                                    break;
                                case Constantes.EXAMEN:
                                    model.addAttribute("texto", contenido.getTexto());
                                    model.addAttribute("examen", contenido.getExamen());
                                    model.addAttribute("preguntas", contenido.getExamen().getOtrasPreguntas());
                                    break;
                                case Constantes.ARTICULATE:
                                    model.addAttribute("texto", contenido.getTexto());
                                    break;
                            }
                            break cicloObjetos;
                        }
                    }
                }
            }
            model.addAttribute("objetos", objetos);
        } else {
            List<AlumnoCurso> cursos = cursoDao.obtieneCursos(usuario.getUserId());
            model.addAttribute("cursos", cursos);
            return "mis_cursos/lista";
        }

        return "mis_cursos/ver";
    }

    @RequestMapping(params = "action=verContenido")
    public String verContenido(RenderRequest request, Model model, @RequestParam Long cursoId, @RequestParam Long contenidoId) throws SystemException, PortalException {
        log.debug("Ver contenido {}", contenidoId);
        User usuario = PortalUtil.getUser(request);
        AlumnoCurso alumnoCurso = cursoDao.obtieneAlumnoCurso(usuario.getUserId(), cursoId);
        if (alumnoCurso.getDiasDisponibles() > 0) {

            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            if (alumnoCurso != null) {
                Curso curso = alumnoCurso.getCurso();
                model.addAttribute("curso", curso);
                List<ObjetoAprendizaje> objetos = cursoDao.objetosAlumno(curso.getId(), contenidoId, usuario.getUserId(), themeDisplay);
                cicloObjetos:
                for (ObjetoAprendizaje objeto : objetos) {
                    log.debug("Viendo contenido de objeto {}", objeto);
                    for (Contenido contenido : objeto.getContenidos()) {
                        log.debug("Contenido : {} : Activo : {}", contenido, contenido.getActivo());
                        if (contenido.getActivo()) {
                            log.debug("Encontre el contenido activo {} y el texto {}", contenido, contenido.getTexto());
                            model.addAttribute("contenidoId", contenido.getId());
                            switch (contenido.getTipo()) {
                                case Constantes.TEXTO:
                                    model.addAttribute("texto", contenido.getTexto());
                                    break cicloObjetos;
                                case Constantes.VIDEO:
                                    model.addAttribute("video", contenido.getTexto());
                                    break cicloObjetos;
                                case Constantes.EXAMEN:
                                    model.addAttribute("texto", contenido.getTexto());
                                    model.addAttribute("examen", contenido.getExamen());
                                    model.addAttribute("preguntas", contenido.getExamen().getOtrasPreguntas());
                                    break cicloObjetos;
                                case Constantes.ARTICULATE:
                                    model.addAttribute("texto", contenido.getTexto());
                                    break cicloObjetos;
                            }
                        }
                    }
                }
                model.addAttribute("objetos", objetos);
            }
        } else {
            List<AlumnoCurso> cursos = cursoDao.obtieneCursos(usuario.getUserId());
            model.addAttribute("cursos", cursos);
            return "mis_cursos/lista";
        }

        return "mis_cursos/ver";
    }

    @RequestMapping(params = "action=verSiguiente")
    public String verSiguiente(RenderRequest request, Model model, @RequestParam Long cursoId) throws SystemException, PortalException {
        log.debug("Ver siguiente contenido");
        User usuario = PortalUtil.getUser(request);
        AlumnoCurso alumnoCurso = cursoDao.obtieneAlumnoCurso(usuario.getUserId(), cursoId);
        if (alumnoCurso.getDiasDisponibles() > 0) {

            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            if (alumnoCurso != null) {
                Curso curso = alumnoCurso.getCurso();
                model.addAttribute("curso", curso);
                List<ObjetoAprendizaje> objetos = cursoDao.objetosAlumnoSiguiente(curso.getId(), usuario.getUserId(), themeDisplay);
                if (cursoDao.haConcluido(usuario.getUserId(), curso.getId())) {
                    model.addAttribute("concluido", true);

                    //                        try {
                    //                            MimeMessage message = mailSender.createMimeMessage();
                    //                            MimeMessageHelper helper = new MimeMessageHelper(message, true);
                    //                            helper.setTo("lneria@um.edu.mx");
                    //                            String titulo = usuario.getFullName() + " ha concluido el curso "+ curso.getNombre();
                    //                            helper.setSubject(titulo);
                    //                            helper.setText(titulo);
                    //                            //helper.addAttachment("Diploma-"+curso.getCodigo()+".pdf", new ByteArrayDataSource(archivo, tipoContenido));
                    //                            mailSender.send(message);
                    //                        } catch(MessagingException e) {
                    //                            log.error("Hubo un error al intentar enviar el correo", e);
                    //                        }

                } else {
                    cicloObjetos:
                    for (ObjetoAprendizaje objeto : objetos) {
                        log.debug("Viendo contenido de objeto {}", objeto);
                        for (Contenido contenido : objeto.getContenidos()) {
                            log.debug("Contenido : {} : Activo : {}", contenido, contenido.getActivo());
                            if (contenido.getActivo()) {
                                log.debug("Encontre el contenido activo {} y el texto {}", contenido, contenido.getTexto());
                                model.addAttribute("contenidoId", contenido.getId());
                                switch (contenido.getTipo()) {
                                    case Constantes.TEXTO:
                                        model.addAttribute("texto", contenido.getTexto());
                                        break cicloObjetos;
                                    case Constantes.VIDEO:
                                        model.addAttribute("video", contenido.getTexto());
                                        break cicloObjetos;
                                    case Constantes.EXAMEN:
                                        model.addAttribute("texto", contenido.getTexto());
                                        model.addAttribute("examen", contenido.getExamen());
                                        model.addAttribute("preguntas", contenido.getExamen().getOtrasPreguntas());
                                        break cicloObjetos;
                                    case Constantes.ARTICULATE:
                                        model.addAttribute("texto", contenido.getTexto());
                                        break cicloObjetos;
                                }
                            }
                        }
                    }
                }
                model.addAttribute("objetos", objetos);
            }
        } else {
            List<AlumnoCurso> cursos = cursoDao.obtieneCursos(usuario.getUserId());
            model.addAttribute("cursos", cursos);
            return "mis_cursos/lista";
        }

        return "mis_cursos/ver";
    }

    @RequestMapping(params = "action=enviaExamen")
    public String enviaExamen(RenderRequest request, RenderResponse response, Model model, @RequestParam Long examenId, @RequestParam Long cursoId) throws SystemException, PortalException {
        log.debug("Recibiendo respuestas de examen {}", examenId);
        User usuario = PortalUtil.getUser(request);
        AlumnoCurso alumnoCurso = cursoDao.obtieneAlumnoCurso(usuario.getUserId(), cursoId);
        if (alumnoCurso != null) {
            Map<String, Object> resultados = cursoDao.califica(request.getParameterMap(), this.getThemeDisplay(request), usuario);
            model.addAllAttributes(resultados);
            model.addAttribute("curso", alumnoCurso.getCurso());
        }

        return "mis_cursos/examen";
    }

    @ResourceMapping(value = "diploma")
    public void diploma(ResourceRequest request, ResourceResponse response, @RequestParam Long cursoId) throws SystemException, PortalException {
        log.debug("Obteniendo diploma para curso {}", cursoId);

        User usuario = PortalUtil.getUser(request);
        AlumnoCurso alumnoCurso = cursoDao.obtieneAlumnoCurso(usuario.getUserId(), cursoId);
        if (alumnoCurso != null) {
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM yyyy", themeDisplay.getLocale());
            try {
                Curso curso = alumnoCurso.getCurso();
                log.debug("Imprimiendo diploma de {} para el curso {}", usuario.getScreenName(), curso.getNombre());
                JasperReport jr = cursoDao.obtieneReporte(cursoId);
                Map<String, Object> params = new HashMap<>();
                params.put("alumno", usuario.getFullName());
                params.put("curso", curso.getNombre());
                params.put("fecha", sdf.format(alumnoCurso.getFechaConclusion()));
                log.debug("PARAMS: {}", params);
                JasperPrint jasperPrint = JasperFillManager.fillReport(jr, params, new JREmptyDataSource());
                byte[] archivo = JasperExportManager.exportReportToPdf(jasperPrint);
                if (archivo != null) {
                    response.setContentType("application/pdf");
                    response.setContentLength(archivo.length);
                    try (BufferedOutputStream bos = new BufferedOutputStream(response.getPortletOutputStream())) {
                        bos.write(archivo);
                        bos.flush();
                    }
                }

            } catch (JRException | IOException e) {
                log.error("Hubo un problema al general el reporte", e);
            }
        }
    }
}
