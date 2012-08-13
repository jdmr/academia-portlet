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
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.util.mail.MailEngine;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.internet.InternetAddress;
import javax.portlet.*;
import mx.edu.um.academia.dao.CursoDao;
import mx.edu.um.academia.model.*;
import mx.edu.um.academia.utils.ComunidadUtil;
import mx.edu.um.academia.utils.Constantes;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSender;
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
public class CursoPortlet extends BaseController {

    @Autowired
    private CursoDao cursoDao;
    @Autowired
    private ResourceBundleMessageSource messageSource;
    @Autowired
    protected JavaMailSender mailSender;

    public CursoPortlet() {
        log.info("Nueva instancia de Curso Portlet");
    }

    @RequestMapping(value = "VIEW")
    public String ver(RenderRequest request, Model model) throws SystemException, PortalException {
        log.debug("Ver curso");

        PortletCurso portletCurso = cursoDao.obtienePortlet(PortalUtil.getPortletId(request));

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        model.addAttribute("themeRoot", themeDisplay.getPathThemeRoot());
        if (portletCurso != null) {
            Curso curso = portletCurso.getCurso();
            model.addAttribute("curso", curso);
            User usuario = PortalUtil.getUser(request);
            if (usuario == null) {
                // Necesitamos firmar al usuario
                model.addAttribute("sign_in", Boolean.TRUE);
                model.addAttribute("sign_in_url", themeDisplay.getURLSignIn());

                if (curso.getIntro() != null) {
                    JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(curso.getIntro());
                    if (ja != null) {
                        String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                        model.addAttribute("texto", texto);
                    }
                } else {
                    String texto = messageSource.getMessage("curso.necesita.intro", null, themeDisplay.getLocale());
                    model.addAttribute("texto", texto);
                }

            } else {
                AlumnoCurso alumnoCurso = cursoDao.obtieneAlumnoCurso(usuario.getUserId(), curso.getId());
                // Valida si el usuario se acaba de inscribir
                if (alumnoCurso == null && curso.getComercio().equals("UM")) {
                    log.debug("Buscando si el alumno {} esta inscrito", usuario.getScreenName());
                    MathContext mc = new MathContext(16, RoundingMode.HALF_UP);
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DAY_OF_YEAR, -7);
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection conn = DriverManager.getConnection("jdbc:mysql://rigel.um.edu.mx/store", "tomcat", "tomcat00");
                        PreparedStatement ps = conn.prepareStatement("select autorizacion, comentario, cantidad from movimientos where producto = 'umvirtual' and fecha > ? and email = ? and autorizacion is not null order by fecha desc");
                        ps.setTimestamp(1, new java.sql.Timestamp(cal.getTimeInMillis()));
                        ps.setString(2, usuario.getEmailAddress());
                        ResultSet rs = ps.executeQuery();
                        BigDecimal total = BigDecimal.ZERO;
                        log.debug("Entrando a iteracion");
                        while (rs.next()) {
                            String autorizacion = rs.getString("autorizacion");
                            if (StringUtils.startsWith(autorizacion, "\"Y")) {
                                String comentario = rs.getString("comentario");
                                log.debug("Comentario: {} : {}", comentario, StringUtils.contains(comentario, usuario.getScreenName()));
                                if (StringUtils.contains(comentario, usuario.getScreenName())) {
                                    log.debug("Codigo : {} : {}", curso.getCodigo(),  StringUtils.contains(comentario, curso.getCodigo()));
                                    if (StringUtils.contains(comentario, curso.getCodigo())) {
                                        BigDecimal cantidad = rs.getBigDecimal("cantidad");
                                        log.debug("Cantidad: {}", cantidad);
                                        if (cantidad != null) {
                                            total = total.add(cantidad, mc).setScale(2, RoundingMode.HALF_UP);
                                        }
                                    }
                                }
                            }
                        }
                        log.debug("Total: {}, {} , {}", new Object[]{total, curso.getPrecio(), total.compareTo(curso.getPrecio())});
                        if (total.doubleValue() > 0 && total.compareTo(curso.getPrecio()) >= 0) {
                            log.debug("Inscribiendo alumno");
                            Alumno alumno = cursoDao.obtieneAlumno(usuario.getUserId());
                            boolean creaUsuario = false;
                            if (alumno == null) {
                                alumno = new Alumno(usuario);
                                creaUsuario = true;
                            }
                            cursoDao.inscribe(curso, alumno, creaUsuario, Constantes.INSCRITO);
                            alumnoCurso = cursoDao.obtieneAlumnoCurso(usuario.getUserId(), curso.getId());
                        }
                    } catch (ClassNotFoundException | SQLException e) {
                        log.error("No pude validar si esta inscrito contra la base de datos de la UM", e);
                    }
                }
                // Termina validacion de alumno recien inscrito

                if (alumnoCurso != null
                        && (alumnoCurso.getEstatus().equals(Constantes.INSCRITO) || alumnoCurso.getEstatus().equals(Constantes.CONCLUIDO))
                        && alumnoCurso.getDiasDisponibles() > 0) {
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
                    log.debug("Asignando valores para Pago UM {} - {} - {}", new String[] {usuario.getScreenName(), usuario.getEmailAddress(), usuario.getFullName()});
                    model.addAttribute("username", usuario.getScreenName());
                    model.addAttribute("correo", usuario.getEmailAddress());
                    model.addAttribute("nombreAlumno", usuario.getFullName());
                    if (curso.getIntro() != null) {
                        JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(curso.getIntro());
                        if (ja != null) {
                            String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                            model.addAttribute("texto", texto);
                        }
                    } else {
                        String texto = messageSource.getMessage("curso.necesita.intro", null, themeDisplay.getLocale());
                        model.addAttribute("texto", texto);
                    }
                }
            }
        } else {
            log.warn("Preferencias no encontradas");
            model.addAttribute("message", "curso.no.configurado");
        }

        return "curso/ver";
    }

    @RequestMapping(value = "VIEW", params = "action=verContenido")
    public String verContenido(RenderRequest request, Model model, @RequestParam Long contenidoId) throws SystemException, PortalException {
        log.debug("Ver contenido {}", contenidoId);
        PortletCurso portletCurso = cursoDao.obtienePortlet(PortalUtil.getPortletId(request));

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        if (portletCurso != null) {
            Curso curso = portletCurso.getCurso();
            model.addAttribute("curso", curso);
            User usuario = PortalUtil.getUser(request);
            if (usuario == null) {
                // Necesitamos firmar al usuario
                model.addAttribute("sign_in", Boolean.TRUE);
                model.addAttribute("sign_in_url", themeDisplay.getURLSignIn());

                if (curso.getIntro() != null) {
                    JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(curso.getIntro());
                    if (ja != null) {
                        String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                        model.addAttribute("texto", texto);
                    }
                } else {
                    String texto = messageSource.getMessage("curso.necesita.intro", null, themeDisplay.getLocale());
                    model.addAttribute("texto", texto);
                }

            } else {
                AlumnoCurso alumnoCurso = cursoDao.obtieneAlumnoCurso(usuario.getUserId(), curso.getId());
                if (alumnoCurso != null
                        && (alumnoCurso.getEstatus().equals(Constantes.INSCRITO) || alumnoCurso.getEstatus().equals(Constantes.CONCLUIDO))
                        && alumnoCurso.getDiasDisponibles() > 0) {
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
                } else {
                    if (curso.getIntro() != null) {
                        JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(curso.getIntro());
                        if (ja != null) {
                            String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                            model.addAttribute("texto", texto);
                        }
                    } else {
                        String texto = messageSource.getMessage("curso.necesita.intro", null, themeDisplay.getLocale());
                        model.addAttribute("texto", texto);
                    }
                }
            }
        } else {
            log.warn("Preferencias no encontradas");
            model.addAttribute("message", "curso.no.configurado");
        }

        return "curso/ver";
    }

    @RequestMapping(value = "VIEW", params = "action=verSiguiente")
    public String verSiguiente(RenderRequest request, Model model) throws SystemException, PortalException {
        log.debug("Ver siguiente contenido");
        PortletCurso portletCurso = cursoDao.obtienePortlet(PortalUtil.getPortletId(request));

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        if (portletCurso != null) {
            Curso curso = portletCurso.getCurso();
            model.addAttribute("curso", curso);
            User usuario = PortalUtil.getUser(request);
            if (usuario == null) {
                // Necesitamos firmar al usuario
                model.addAttribute("sign_in", Boolean.TRUE);
                model.addAttribute("sign_in_url", themeDisplay.getURLSignIn());

                if (curso.getIntro() != null) {
                    JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(curso.getIntro());
                    if (ja != null) {
                        String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                        model.addAttribute("texto", texto);
                    }
                } else {
                    String texto = messageSource.getMessage("curso.necesita.intro", null, themeDisplay.getLocale());
                    model.addAttribute("texto", texto);
                }

            } else {
                AlumnoCurso alumnoCurso = cursoDao.obtieneAlumnoCurso(usuario.getUserId(), curso.getId());
                if (alumnoCurso != null
                        && (alumnoCurso.getEstatus().equals(Constantes.INSCRITO) || alumnoCurso.getEstatus().equals(Constantes.CONCLUIDO))
                        && alumnoCurso.getDiasDisponibles() > 0) {
                    List<ObjetoAprendizaje> objetos = cursoDao.objetosAlumnoSiguiente(curso.getId(), usuario.getUserId(), themeDisplay);
                    if (cursoDao.haConcluido(usuario.getUserId(), curso.getId())) {
                        model.addAttribute("concluido", true);
//                                try {
//                                    InternetAddress destinatario = new InternetAddress(usuario.getEmailAddress(), usuario.getFullName());
//                                    MailEngine.send(destinatario, destinatario, null, null);
//
//            //                        try {
//            //                            MimeMessage message = mailSender.createMimeMessage();
//            //                            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            //                            helper.setTo("lneria@um.edu.mx");
//            //                            String titulo = usuario.getFullName() + " ha concluido el curso "+ curso.getNombre();
//            //                            helper.setSubject(titulo);
//            //                            helper.setText(titulo);
//            //                            //helper.addAttachment("Diploma-"+curso.getCodigo()+".pdf", new ByteArrayDataSource(archivo, tipoContenido));
//            //                            mailSender.send(message);
//            //                        } catch(MessagingException e) {
//            //                            log.error("Hubo un error al intentar enviar el correo", e);
//            //                        }
//                                } catch (UnsupportedEncodingException e) {
//                                    log.error("Hubo un problema al intentar enviar correo", e);
//                                }

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
                } else {
                    if (curso.getIntro() != null) {
                        JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(curso.getIntro());
                        if (ja != null) {
                            String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                            model.addAttribute("texto", texto);
                        }
                    } else {
                        String texto = messageSource.getMessage("curso.necesita.intro", null, themeDisplay.getLocale());
                        model.addAttribute("texto", texto);
                    }
                }
            }
        } else {
            log.warn("Preferencias no encontradas");
            model.addAttribute("message", "curso.no.configurado");
        }

        return "curso/ver";
    }

    @RequestMapping(value = "EDIT")
    public String edita(RenderRequest request, Model model) throws SystemException, PortalException {
        log.debug("Configurar curso");

        PortletCurso portletCurso = cursoDao.obtienePortlet(PortalUtil.getPortletId(request));
        if (portletCurso != null) {
            model.addAttribute("cursoId", portletCurso.getCurso().getId());
        }

        Map<Long, String> comunidades = ComunidadUtil.obtieneComunidades(request);

        List<Curso> cursos = cursoDao.todos(comunidades.keySet());
        model.addAttribute("cursos", cursos);

        return "curso/edita";
    }

    @RequestMapping(value = "EDIT", params = "action=configuraCurso")
    public void configuraCurso(ActionRequest request, ActionResponse response, @RequestParam Long cursoId) throws SystemException, ReadOnlyException, IOException, ValidatorException {
        log.debug("Configurando curso {}", cursoId);

        String portletId = PortalUtil.getPortletId(request);
        cursoDao.guardaPortlet(cursoId, portletId);
    }

    @RequestMapping(value = "VIEW", params = "action=inscribeAlumno")
    public void inscribeAlumno(ActionRequest request, ActionResponse response, @RequestParam Long cursoId) throws SystemException, PortalException {
        log.debug("Inscribiendo alumno a curso {}", cursoId);
        for (String key : request.getParameterMap().keySet()) {
            log.debug("{} : {}", key, request.getParameterMap().get(key));
        }

        Curso curso = cursoDao.obtiene(cursoId);
        User usuario = PortalUtil.getUser(request);
        if (usuario != null) {
            Alumno alumno = cursoDao.obtieneAlumno(usuario.getUserId());
            boolean creaUsuario = false;
            if (alumno == null) {
                alumno = new Alumno(usuario);
                creaUsuario = true;
            }
            switch (curso.getTipo()) {
                case Constantes.PATROCINADO:
                    cursoDao.inscribe(curso, alumno, creaUsuario, Constantes.INSCRITO);
                    break;
                case Constantes.PAGADO:
                    cursoDao.inscribe(curso, alumno, creaUsuario, Constantes.PENDIENTE);

                    break;
            }
        }
    }

    @RequestMapping(value = "VIEW", params = "action=pagoAprobado")
    public String pagoAprobado(RenderRequest request, RenderResponse response, @RequestParam Long cursoId) throws SystemException, PortalException {
        log.debug("Pago aprobado para alumno a curso {}", cursoId);
        for (String key : request.getParameterMap().keySet()) {
            log.debug("{} : {}", key, request.getParameterMap().get(key));
        }

        Curso curso = cursoDao.obtiene(cursoId);
        User usuario = PortalUtil.getUser(request);
        if (usuario != null) {
            Alumno alumno = cursoDao.obtieneAlumno(usuario.getUserId());
            boolean creaUsuario = false;
            if (alumno == null) {
                alumno = new Alumno(usuario);
                creaUsuario = true;
            }
            cursoDao.inscribe(curso, alumno, creaUsuario, Constantes.INSCRITO);
            return "curso/inscrito";
        }
        return null;
    }

    @RequestMapping(value = "VIEW", params = "action=pagoDenegado")
    public String pagoDenegado(RenderRequest request, RenderResponse response, @RequestParam Long cursoId) throws SystemException, PortalException {
        log.debug("Alumno no pago curso {}", cursoId);
        for (String key : request.getParameterMap().keySet()) {
            log.debug("{} : {}", key, request.getParameterMap().get(key));
        }

        Curso curso = cursoDao.obtiene(cursoId);
        User usuario = PortalUtil.getUser(request);
        if (usuario != null) {
            Alumno alumno = cursoDao.obtieneAlumno(usuario.getUserId());
            boolean creaUsuario = false;
            if (alumno == null) {
                alumno = new Alumno(usuario);
                creaUsuario = true;
            }
            cursoDao.inscribe(curso, alumno, creaUsuario, Constantes.PENDIENTE);
            return "curso/denegado";
        }
        return null;
    }

    @RequestMapping(value = "VIEW", params = "action=enviaExamen")
    public String enviaExamen(RenderRequest request, RenderResponse response, Model model, @RequestParam Long examenId) throws SystemException, PortalException {
        log.debug("Recibiendo respuestas de examen {}", examenId);
        for (String key : request.getParameterMap().keySet()) {
            log.debug("{} : {}", key, request.getParameterMap().get(key));
        }
        User usuario = PortalUtil.getUser(request);
        Map<String, Object> resultados = cursoDao.califica(request.getParameterMap(), this.getThemeDisplay(request), usuario);
        model.addAllAttributes(resultados);


        return "curso/examen";
    }

    @ResourceMapping(value = "diploma")
    public void diploma(ResourceRequest request, ResourceResponse response) throws SystemException, PortalException {
        log.debug("Obteniendo diploma");

        PortletCurso portletCurso = cursoDao.obtienePortlet(PortalUtil.getPortletId(request));
        if (portletCurso != null) {
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM yyyy", themeDisplay.getLocale());
            try {
                Curso curso = portletCurso.getCurso();
                if (curso != null) {
                    User usuario = PortalUtil.getUser(request);
                    AlumnoCurso alumnoCurso = cursoDao.obtieneAlumnoCurso(usuario.getUserId(), curso.getId());
                    if (alumnoCurso != null) {
                        log.debug("Imprimiendo diploma de {} para el curso {}", usuario.getScreenName(), curso.getNombre());
                        JasperReport jr = cursoDao.obtieneReporte(curso.getId());
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
                    }
                }

            } catch (JRException | IOException e) {
                log.error("Hubo un problema al general el reporte", e);
            }
        }
    }
}
