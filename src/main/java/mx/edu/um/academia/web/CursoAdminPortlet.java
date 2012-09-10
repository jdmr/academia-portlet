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
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.UnicodeFormatter;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.util.mail.MailEngine;
import com.liferay.util.mail.MailEngineException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.portlet.*;
import javax.validation.Valid;
import mx.edu.um.academia.dao.ContenidoDao;
import mx.edu.um.academia.dao.CursoDao;
import mx.edu.um.academia.model.AlumnoCurso;
import mx.edu.um.academia.model.Contenido;
import mx.edu.um.academia.model.Curso;
import mx.edu.um.academia.model.Grabacion;
import mx.edu.um.academia.model.ObjetoAprendizaje;
import mx.edu.um.academia.model.Reporte;
import mx.edu.um.academia.model.Salon;
import mx.edu.um.academia.utils.ComunidadUtil;
import mx.edu.um.academia.utils.Constantes;
import mx.edu.um.academia.utils.TextoUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
@Controller
@RequestMapping("VIEW")
public class CursoAdminPortlet extends BaseController {

    @Autowired
    private CursoDao cursoDao;
    @Autowired
    private ResourceBundleMessageSource messages;
    @Autowired
    private ContenidoDao contenidoDao;
    @Autowired
    private TextoUtil textoUtil;

    public CursoAdminPortlet() {
        log.info("Nueva instancia de Curso Admin Portlet creada");
    }

    @RequestMapping
    public String lista(RenderRequest request,
            @RequestParam(required = false) String filtro,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer max,
            @RequestParam(required = false) Integer direccion,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Long pagina,
            Model modelo) throws SystemException, PortalException {
        log.debug("Lista de cursos [filtro: {}, offset: {}, max: {}, direccion: {}, order: {}, sort: {}, pagina: {}]", new Object[]{filtro, offset, max, direccion, order, sort, pagina});
        Map<Long, String> comunidades = ComunidadUtil.obtieneComunidades(request);
        Map<String, Object> params = new HashMap<>();
        params.put("comunidades", comunidades.keySet());
        if (StringUtils.isNotBlank(filtro)) {
            params.put("filtro", filtro);
        }
        if (StringUtils.isNotBlank(order)) {
            params.put("order", order);
            params.put("sort", sort);
        }
        params.put("max", max);
        params.put("offset", offset);
        params.put("pagina", pagina);

        params = cursoDao.lista(params);
        List<Curso> cursos = (List<Curso>) params.get("cursos");
        if (cursos != null && cursos.size() > 0) {
            modelo.addAttribute("cursos", params.get("cursos"));
            this.pagina(params, modelo, "cursos", pagina);
        }

        return "cursoAdmin/lista";
    }

    @RequestMapping(params = "action=nuevo")
    public String nuevo(RenderRequest request, Model modelo) throws SystemException, PortalException {
        log.debug("Nuevo curso");
        Curso curso = new Curso();
        modelo.addAttribute("curso", curso);
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        modelo.addAttribute("tipos", obtieneTiposDeCurso(getThemeDisplay(request).getLocale()));
        modelo.addAttribute("comercios", obtieneTiposDeComercio());
        return "cursoAdmin/nuevo";
    }

    @RequestMapping(params = "action=nuevoError")
    public String nuevoError(RenderRequest request, Model modelo) throws SystemException, PortalException {
        log.debug("Nuevo curso despues de error");
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        modelo.addAttribute("tipos", obtieneTiposDeCurso(getThemeDisplay(request).getLocale()));
        modelo.addAttribute("comercios", obtieneTiposDeComercio());
        return "cursoAdmin/nuevo";
    }

    @RequestMapping(params = "action=crea")
    public void crea(ActionRequest request, ActionResponse response,
            @Valid Curso curso,
            BindingResult result,
            @RequestParam(required = false) MultipartFile archivo) throws SystemException, PortalException, IOException {
        log.debug("Creando curso {}", curso);
        if (result.hasErrors()) {
            log.debug("Hubo algun error en la forma, regresando");
            response.setRenderParameter("action", "nuevoError");
        }

        if (archivo != null && !archivo.isEmpty()) {
            Reporte reporte = new Reporte();
            reporte.setCompilado(archivo.getBytes());
            curso.setReporte(reporte);
        }

        User creador = PortalUtil.getUser(request);
        curso = cursoDao.crea(curso, creador);


        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", curso.getId().toString());
    }

    @RequestMapping(params = "action=ver")
    public String ver(RenderRequest request, @RequestParam Long id, Model modelo) throws SystemException, PortalException {
        log.debug("Mostrando curso {}", id);
        Curso curso = cursoDao.obtiene(id);
        modelo.addAttribute("curso", curso);

        Map<Long, String> comunidades = ComunidadUtil.obtieneComunidades(request);
        Map<String, Object> objetos = cursoDao.objetos(id, comunidades.keySet());
        modelo.addAttribute("disponibles", objetos.get("disponibles"));
        modelo.addAttribute("seleccionados", objetos.get("seleccionados"));

        if (curso.getIntro() != null) {
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(curso.getIntro());
            if (ja != null) {
                String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                modelo.addAttribute("texto", texto);
            }
        } else {
            if (curso.getCorreoId() == null) {
                modelo.addAttribute("message", "curso.sin.intro.sin.correo.mensaje");
            } else {
                modelo.addAttribute("message", "curso.sin.intro.mensaje");
            }
        }

        if (curso.getCorreoId() == null) {
            modelo.addAttribute("message", "curso.sin.correo.mensaje");
        }

        return "cursoAdmin/ver";
    }

    @RequestMapping(params = "action=edita")
    public String edita(RenderRequest request, Model modelo, @RequestParam Long id) throws SystemException, PortalException {
        log.debug("Edita objeto de aprendizaje");
        Curso curso = cursoDao.obtiene(id);
        modelo.addAttribute("curso", curso);
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        modelo.addAttribute("tipos", obtieneTiposDeCurso(getThemeDisplay(request).getLocale()));
        modelo.addAttribute("comercios", obtieneTiposDeComercio());
        return "cursoAdmin/edita";
    }

    @RequestMapping(params = "action=editaError")
    public String editaError(RenderRequest request, Model modelo) throws SystemException, PortalException {
        log.debug("Edita curso despues de error");
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        modelo.addAttribute("tipos", obtieneTiposDeCurso(getThemeDisplay(request).getLocale()));
        modelo.addAttribute("comercios", obtieneTiposDeComercio());
        return "cursoAdmin/edita";
    }

    @RequestMapping(params = "action=actualiza")
    public void actualiza(ActionRequest request, ActionResponse response,
            @Valid Curso curso,
            BindingResult result,
            @RequestParam(required = false) MultipartFile archivo) throws SystemException, PortalException, IOException {
        log.debug("Actualizando curso {}", curso);
        if (result.hasErrors()) {
            log.debug("Hubo algun error en la forma, regresando");
            response.setRenderParameter("action", "editaError");
        }

        if (archivo != null && !archivo.isEmpty()) {
            Reporte reporte = new Reporte();
            reporte.setCompilado(archivo.getBytes());
            curso.setReporte(reporte);
        }

        User creador = PortalUtil.getUser(request);
        curso = cursoDao.actualiza(curso, creador);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", curso.getId().toString());
    }

    @RequestMapping(params = "action=elimina")
    public void elimina(ActionRequest request, @RequestParam Long id) throws PortalException, SystemException {
        log.debug("eliminando curso {}", id);

        User creador = PortalUtil.getUser(request);
        cursoDao.elimina(id, creador);
    }

    @RequestMapping(params = "action=intro")
    public String intro(RenderRequest request, @RequestParam Long id, Model modelo) {
        log.debug("Intro");
        Curso curso = cursoDao.obtiene(id);
        modelo.addAttribute("curso", curso);
        return "cursoAdmin/intro";
    }

    @RequestMapping(params = "action=creaIntro")
    public void creaIntro(ActionRequest request, ActionResponse response, @ModelAttribute Curso curso, @RequestParam String texto) {
        curso = cursoDao.obtiene(curso.getId());

        log.debug("Creando intro");
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='UTF-8'?><root><static-content><![CDATA[");
        sb.append(texto);
        sb.append("]]></static-content></root>");
        texto = sb.toString();

        try {
            User creador = PortalUtil.getUser(request);
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            Calendar displayDate;
            if (themeDisplay != null) {
                displayDate = CalendarFactoryUtil.getCalendar(themeDisplay.getTimeZone(), themeDisplay.getLocale());
            } else {
                displayDate = CalendarFactoryUtil.getCalendar();
            }

            if (curso.getIntro() != null) {
                JournalArticleLocalServiceUtil.deleteJournalArticle(curso.getIntro());
            }

            ServiceContext serviceContext = ServiceContextFactory.getInstance(JournalArticle.class.getName(), request);

            JournalArticle article = textoUtil.crea(
                    curso.getNombre() + " - INTRO",
                    StringUtils.EMPTY,
                    texto,
                    displayDate,
                    creador.getUserId(),
                    curso.getComunidadId(),
                    serviceContext);

            log.debug("Asignando intro {} a curso {}", article.getId(), curso);
            curso.setIntro(article.getId());
            cursoDao.asignaIntro(curso);

        } catch (PortalException | SystemException | DataAccessException e) {
            log.error("No se pudo crear la intro", e);
        }

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", curso.getId().toString());
    }

    @RequestMapping(params = "action=editaIntro")
    public String editaIntro(RenderRequest request, Model modelo, @RequestParam Long id) throws SystemException, PortalException {
        log.debug("Edita intro para curso {}", id);
        Curso curso = cursoDao.obtiene(id);
        modelo.addAttribute("curso", curso);
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(curso.getIntro());
        if (ja != null) {
            String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
            modelo.addAttribute("texto", texto);
            modelo.addAttribute("textoUnicode", UnicodeFormatter.toString(texto));
        }
        return "cursoAdmin/editaIntro";
    }

    @RequestMapping(params = "action=actualizaIntro")
    public void actualizaIntro(ActionRequest request, ActionResponse response,
            @ModelAttribute Curso curso, @RequestParam String texto) throws SystemException, PortalException {
        log.debug("Actualizando intro para curso {}", curso.getId());
        curso = cursoDao.obtiene(curso.getId());
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='UTF-8'?><root><static-content><![CDATA[");
        sb.append(texto);
        sb.append("]]></static-content></root>");
        texto = sb.toString();

        User creador = PortalUtil.getUser(request);
        JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(curso.getIntro());
        ja.setUserId(creador.getUserId());
        ja.setTitle(curso.getNombre());
        ja.setDescription(curso.getNombre());
        ja.setContent(texto);
        ja.setVersion(ja.getVersion() + 1);
        JournalArticleLocalServiceUtil.updateJournalArticle(ja);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", curso.getId().toString());
    }

    @RequestMapping(params = "action=correo")
    public String correo(RenderRequest request, @RequestParam Long id, Model modelo) {
        log.debug("Correo");
        Curso curso = cursoDao.obtiene(id);
        modelo.addAttribute("curso", curso);
        return "cursoAdmin/correo";
    }

    @RequestMapping(params = "action=creaCorreo")
    public void creaCorreo(ActionRequest request, ActionResponse response, @ModelAttribute Curso curso, @RequestParam String texto) {
        curso = cursoDao.obtiene(curso.getId());

        log.debug("Creando correo");
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='UTF-8'?><root><static-content><![CDATA[");
        sb.append(texto);
        sb.append("]]></static-content></root>");
        texto = sb.toString();

        try {
            User creador = PortalUtil.getUser(request);
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            Calendar displayDate;
            if (themeDisplay != null) {
                displayDate = CalendarFactoryUtil.getCalendar(themeDisplay.getTimeZone(), themeDisplay.getLocale());
            } else {
                displayDate = CalendarFactoryUtil.getCalendar();
            }

            if (curso.getCorreoId() != null) {
                JournalArticleLocalServiceUtil.deleteJournalArticle(curso.getCorreoId());
            }

            ServiceContext serviceContext = ServiceContextFactory.getInstance(JournalArticle.class.getName(), request);

            JournalArticle article = textoUtil.crea(
                    curso.getNombre() + " - EMAIL",
                    StringUtils.EMPTY,
                    texto,
                    displayDate,
                    creador.getUserId(),
                    curso.getComunidadId(),
                    serviceContext);

            log.debug("Asignando correo {} a curso {}", article.getId(), curso);
            curso.setCorreoId(article.getId());
            cursoDao.asignaCorreo(curso);

        } catch (PortalException | SystemException | DataAccessException e) {
            log.error("No se pudo crear la intro", e);
        }

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", curso.getId().toString());
    }

    @RequestMapping(params = "action=editaCorreo")
    public String editaCorreo(RenderRequest request, Model modelo, @RequestParam Long id) throws SystemException, PortalException {
        log.debug("Edita correo para curso {}", id);
        Curso curso = cursoDao.obtiene(id);
        modelo.addAttribute("curso", curso);
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(curso.getCorreoId());
        if (ja != null) {
            String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
            modelo.addAttribute("texto", texto);
            modelo.addAttribute("textoUnicode", UnicodeFormatter.toString(texto));
        }
        return "cursoAdmin/editaCorreo";
    }

    @RequestMapping(params = "action=actualizaCorreo")
    public void actualizaCorreo(ActionRequest request, ActionResponse response,
            @ModelAttribute Curso curso, @RequestParam String texto) throws SystemException, PortalException {
        log.debug("Actualizando correo para curso {}", curso.getId());
        curso = cursoDao.obtiene(curso.getId());
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='UTF-8'?><root><static-content><![CDATA[");
        sb.append(texto);
        sb.append("]]></static-content></root>");
        texto = sb.toString();

        User creador = PortalUtil.getUser(request);
        JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(curso.getCorreoId());
        ja.setUserId(creador.getUserId());
        ja.setTitle(curso.getNombre());
        ja.setDescription(curso.getNombre());
        ja.setContent(texto);
        ja.setVersion(ja.getVersion() + 1);
        JournalArticleLocalServiceUtil.updateJournalArticle(ja);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", curso.getId().toString());
    }

    @RequestMapping(params = "action=agregaObjetos")
    public void agregaObjetos(ActionRequest request, ActionResponse response, @RequestParam Long cursoId, @RequestParam Long[] objetos) {
        log.debug("Agregando objetos {} a {}", objetos, cursoId);

        cursoDao.agregaObjetos(cursoId, objetos);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", cursoId.toString());
    }

    @RequestMapping(params = "action=bajaAlumno")
    public void bajaAlumno(ActionRequest request, ActionResponse response, @RequestParam Long cursoId, @RequestParam Long alumnoId) {
        log.debug("Baja a alumno {} de curso {}", alumnoId, cursoId);

        cursoDao.bajaAlumno(alumnoId, cursoId);

        response.setRenderParameter("action", "alumnos");
        response.setRenderParameter("cursoId", cursoId.toString());
    }

    @RequestMapping(params = "action=alumnos")
    public String alumnos(RenderRequest request, RenderResponse response, Model modelo, @RequestParam Long cursoId) {
        log.debug("List de alumnos del curso {}", cursoId);

        Map<String, Object> params = new HashMap<>();
        params.put("cursoId", cursoId);
        params.put("companyId", getThemeDisplay(request).getCompanyId());
        params = cursoDao.alumnos(params);

        modelo.addAllAttributes(params);

        return "cursoAdmin/alumnos";
    }

    @RequestMapping(params = "action=todos")
    public String todos(RenderRequest request, Model modelo) {
        Map<String, Object> params = new HashMap<>();
        params.put("comunidadId", getThemeDisplay(request).getScopeGroupId());
        params = cursoDao.todosAlumnos(params);

        modelo.addAllAttributes(params);

        return "cursoAdmin/todos";
    }

    @RequestMapping(params = "action=inscribeAlumno")
    public void inscribeAlumno(ActionRequest request, ActionResponse response, @RequestParam Long cursoId, @RequestParam Long alumnoId) {
        log.debug("Inscribe alumno {} a {}", alumnoId, cursoId);

        cursoDao.inscribe(cursoId, alumnoId);

        response.setRenderParameter("action", "alumnos");
        response.setRenderParameter("cursoId", cursoId.toString());
    }

    @RequestMapping(params = "action=vistaPrevia")
    public void vistaPrevia(ResourceRequest request, ResourceResponse response, @RequestParam Long cursoId, @RequestParam Integer posicionObjeto, @RequestParam Integer posicionContenido, @RequestParam String url) throws IOException, PortalException, SystemException {
        log.debug("Vista previa del contenido del curso {} - {} - {} - {}", new Object[]{cursoId, posicionObjeto, posicionContenido, url});
        Map<String, Object> resultado = cursoDao.verContenido(cursoId);
        List<ObjetoAprendizaje> objetos = (List<ObjetoAprendizaje>) resultado.get("objetos");

        StringBuilder sb = new StringBuilder();
        sb.append("<div class='span3'>");
        sb.append("<div class='well' style='padding: 0; padding-bottom: 15px;'>");
        sb.append("<ul class='nav nav-list' style='margin-right: 0;'>");
        for (int i = 0; i < objetos.size(); i++) {
            ObjetoAprendizaje objeto = objetos.get(i);
            sb.append("<li class='nav-header'>");
            sb.append(objeto.getNombre());
            sb.append("</li>");
            for (int j = 0; j < objeto.getContenidos().size(); j++) {
                sb.append("<li");
                if (j == posicionContenido) {
                    sb.append(" class='active'");
                }
                sb.append(">");
                Contenido contenido = objeto.getContenidos().get(j);
                sb.append("<a href='#' onclick='cargaContenido(\"");
                StringBuilder urlsb = new StringBuilder(url);
                int pos = urlsb.indexOf("posicionObjeto=");
                urlsb.replace(pos + 16, pos + 16, new Integer(i).toString());
                pos = urlsb.indexOf("posicionContenido=");
                urlsb.replace(pos + 19, pos + 19, new Integer(j).toString());
                sb.append(urlsb.toString());
                sb.append("\");return false;'><i class='icon-ok-circle icon-white'></i> ");
                sb.append(contenido.getNombre());
                sb.append("</a>");
                sb.append("</li>");
            }
        }
        sb.append("</ul>");
        sb.append("</div>");
        sb.append("</div>");
        Contenido contenido = objetos.get(posicionObjeto).getContenidos().get(posicionContenido);
        sb.append("<div class='span9'>");
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        switch (contenido.getTipo()) {
            case Constantes.TEXTO:
                if (contenido.getContenidoId() != null) {
                    JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(contenido.getContenidoId());
                    if (ja != null) {
                        String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                        sb.append(texto);
                    }
                }
                break;
            case Constantes.VIDEO:
                if (contenido.getContenidoId() != null) {
                    DLFileEntry fileEntry = DLFileEntryLocalServiceUtil.getDLFileEntry(contenido.getContenidoId());
                    StringBuilder videoLink = new StringBuilder();
                    videoLink.append("/documents/");
                    videoLink.append(fileEntry.getGroupId());
                    videoLink.append("/");
                    videoLink.append(fileEntry.getFolderId());
                    videoLink.append("/");
                    videoLink.append(fileEntry.getTitle());
                    sb.append("<video controls='controls' src='").append(videoLink.toString()).append("' />");
                }
                break;
            case Constantes.ARTICULATE:
                sb.append("<iframe src='").append(request.getContextPath());
                sb.append("/contenido/player.html?contenidoId=").append(contenido.getId());
                sb.append("&cursoId=").append(cursoId);
                sb.append("&userId=").append(themeDisplay.getUserId());
                sb.append("&admin=true");
                sb.append("' style='width:100%;height:650px;'></iframe>");
                break;
            case Constantes.STORYLINE:
                sb.append("<iframe src='").append(request.getContextPath());
                sb.append("/contenido/story.html?contenidoId=").append(contenido.getId());
                sb.append("&cursoId=").append(cursoId);
                sb.append("&userId=").append(themeDisplay.getUserId());
                sb.append("&admin=true");
                sb.append("' style='width:100%;height:650px;'></iframe>");
        }
        sb.append("</div>");
        PrintWriter writer = response.getWriter();
        writer.write(sb.toString());
    }

    @RequestMapping(params = "action=vistaPreviaIntro")
    public void vistaPreviaIntro(ResourceRequest request, ResourceResponse response, @RequestParam Long cursoId) throws IOException, PortalException, SystemException {
        log.debug("Vista previa del intro del curso {} ", cursoId);
        Curso curso = cursoDao.obtiene(cursoId);
        if (curso.getIntro() != null) {
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(curso.getIntro());
            if (ja != null) {
                String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                StringBuilder sb = new StringBuilder();
                sb.append("<div>").append(texto).append("</div>");
                PrintWriter writer = response.getWriter();
                writer.write(sb.toString());
            }
        }
    }

    @RequestMapping(params = "action=vistaPreviaCorreo")
    public void vistaPreviaCorreo(ResourceRequest request, ResourceResponse response, @RequestParam Long cursoId) throws IOException, PortalException, SystemException {
        log.debug("Vista previa del correo del curso {} ", cursoId);
        Curso curso = cursoDao.obtiene(cursoId);
        if (curso.getIntro() != null) {
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(curso.getCorreoId());
            if (ja != null) {
                String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                StringBuilder sb = new StringBuilder();
                sb.append("<div>").append(texto).append("</div>");
                PrintWriter writer = response.getWriter();
                writer.write(sb.toString());
            }
        }
    }

    @ResourceMapping(value = "contenido")
    public void buscaContenido(@RequestParam Long contenidoId, ResourceRequest request, ResourceResponse response) throws IOException, SystemException {
        log.debug("Buscando contenido {}", contenidoId);
        Contenido contenido = contenidoDao.obtiene(contenidoId);
        log.debug("{}", contenido.getRuta());
        PrintWriter writer = response.getWriter();
        writer.println();
    }

    private Map<String, String> obtieneTiposDeCurso(Locale locale) {
        Map<String, String> tipos = new LinkedHashMap<>();
        tipos.put(Constantes.PATROCINADO, messages.getMessage(Constantes.PATROCINADO, null, locale));
        tipos.put(Constantes.PAGADO, messages.getMessage(Constantes.PAGADO, null, locale));
        return tipos;
    }

    private List<String> obtieneTiposDeComercio() {
        List<String> comercios = new ArrayList<>();
        comercios.add("PAYPAL");
        comercios.add("UM");
        return comercios;
    }

    @RequestMapping(params = "action=salon")
    public String salon(RenderRequest request, Model modelo, @RequestParam Long cursoId) {
        Curso curso = cursoDao.obtiene(cursoId);
        modelo.addAttribute("curso", curso);

        Salon salon = cursoDao.obtieneSalon(cursoId);
        if (salon == null) {
            try {
                Properties props = new Properties();
                String home = System.getProperty("user.home");
                File propsFile = new File(home, "portal-ext.properties");
                props.load(new FileInputStream(propsFile));
                StringBuilder params = new StringBuilder();
                params.append("name=").append(java.net.URLEncoder.encode(curso.getNombre(), "UTF-8"));
                params.append("&meetingID=").append(java.net.URLEncoder.encode(curso.getCodigo() + "-" + curso.getComunidadId(), "UTF-8"));
                params.append("&record=").append(java.net.URLEncoder.encode("true", "UTF-8"));
                String checksum = DigestUtils.shaHex("create" + params.toString() + props.getProperty("bbb.salt"));
                params.append("&checksum=").append(checksum);
                String bbb = props.getProperty("bbb.url") + "/create?" + params.toString();
                log.debug("URL: {}", bbb);
                URL url = new URL(bbb);
                URLConnection urlConn = url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                String linea = in.readLine();
                while (StringUtils.isNotBlank(linea)) {
                    log.debug("out: {}", linea);
                    String returnCode = StringUtils.substringBetween(linea, "<returncode>", "</returncode>");
                    if (returnCode.equals("SUCCESS")) {
                        String meetingID = StringUtils.substringBetween(linea, "<meetingID>", "</meetingID>");
                        String attendeePW = StringUtils.substringBetween(linea, "<attendeePW>", "</attendeePW>");
                        String moderatorPW = StringUtils.substringBetween(linea, "<moderatorPW>", "</moderatorPW>");
                        String createTime = StringUtils.substringBetween(linea, "<createTime>", "</createTime>");
                        salon = new Salon();
                        salon.setName(curso.getNombre());
                        salon.setMeetingID(meetingID);
                        salon.setAttendeePW(attendeePW);
                        salon.setModeratorPW(moderatorPW);
                        salon.setCreateTime(createTime);
                        salon.setCurso(curso);
                        salon = cursoDao.creaSalon(salon);
                    }
                    linea = in.readLine();
                }
            } catch (IOException ex) {
                log.error("No se pudo crear el salon", ex);
            }
        } else {
            try {
                Properties props = new Properties();
                String home = System.getProperty("user.home");
                File propsFile = new File(home, "portal-ext.properties");
                props.load(new FileInputStream(propsFile));
                StringBuilder params = new StringBuilder();
                params.append("meetingID=").append(java.net.URLEncoder.encode(curso.getCodigo() + "-" + curso.getComunidadId(), "UTF-8"));
                String checksum = DigestUtils.shaHex("getRecordings" + params.toString() + props.getProperty("bbb.salt"));
                params.append("&checksum=").append(checksum);
                String bbb = props.getProperty("bbb.url") + "/getRecordings?" + params.toString();
                log.debug("URL: {}", bbb);
                URL url = new URL(bbb);
                URLConnection urlConn = url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                String linea = in.readLine();
                while (StringUtils.isNotBlank(linea)) {
                    log.debug("out: {}", linea);
                    String returnCode = StringUtils.substringBetween(linea, "<returncode>", "</returncode>");
                    if (returnCode.equals("SUCCESS")) {
                        String messageKey = StringUtils.substringBetween(linea, "<messageKey>", "</messageKey>");
                        if (StringUtils.isBlank(messageKey) || !messageKey.equals("noRecordings")) {
                            String[] ids = StringUtils.substringsBetween(linea, "<recordID>", "</recordID>");
                            String[] urls = StringUtils.substringsBetween(linea, "<url>", "</url>");
                            String[] starts = StringUtils.substringsBetween(linea, "<startTime>", "</startTime>");
                            String[] ends = StringUtils.substringsBetween(linea, "<endTime>", "</endTime>");
                            String[] lengths = StringUtils.substringsBetween(linea, "<length>", "</length>");
                            List<Grabacion> grabaciones = new ArrayList<>();
                            for (int i = 0; i < ids.length; i++) {
                                Grabacion grabacion = new Grabacion(ids[i], new Date(new Long(starts[i])), new Date(new Long(ends[i])), new Integer(lengths[i]), urls[i]);
                                params = new StringBuilder();
                                params.append("recordID=").append(java.net.URLEncoder.encode(grabacion.getId(), "UTF-8"));
                                checksum = DigestUtils.shaHex("deleteRecordings" + params.toString() + props.getProperty("bbb.salt"));
                                params.append("&checksum=").append(checksum);
                                bbb = props.getProperty("bbb.url") + "/deleteRecordings?" + params.toString();
                                grabacion.setElimina(bbb);
                                grabaciones.add(grabacion);
                            }
                            modelo.addAttribute("grabaciones", grabaciones);
                            modelo.addAttribute("timeZone", getThemeDisplay(request).getTimeZone().getDisplayName());
                        }
                    }
                    linea = in.readLine();
                }
            } catch (IOException ex) {
                log.error("No se pudo crear el salon", ex);
            }
        }

        modelo.addAttribute("salon", salon);

        try {
            User creador = PortalUtil.getUser(request);
            Properties props = new Properties();
            String home = System.getProperty("user.home");
            File propsFile = new File(home, "portal-ext.properties");
            props.load(new FileInputStream(propsFile));
            StringBuilder params = new StringBuilder();
            params.append("fullName=").append(java.net.URLEncoder.encode(creador.getFullName(), "UTF-8"));
            params.append("&meetingID=").append(java.net.URLEncoder.encode(curso.getCodigo() + "-" + curso.getComunidadId(), "UTF-8"));
            params.append("&password=").append(java.net.URLEncoder.encode(salon.getModeratorPW(), "UTF-8"));
            params.append("&createTime=").append(java.net.URLEncoder.encode(salon.getCreateTime(), "UTF-8"));
            String checksum = DigestUtils.shaHex("join" + params.toString() + props.getProperty("bbb.salt"));
            params.append("&checksum=").append(checksum);
            String bbb = props.getProperty("bbb.url") + "/join?" + params.toString();
            salon.setLigaAcceso(bbb);
        } catch (PortalException | SystemException | IOException ex) {
            log.error("No se pudo crear la liga del maestro", ex);
        }

        return "cursoAdmin/salon";
    }

    @RequestMapping(params = "action=invitacion")
    public String invitacion(RenderRequest request, Model modelo, @RequestParam Long salonId) throws PortalException, SystemException {
        Salon salon = cursoDao.obtieneSalonPorId(salonId);
        modelo.addAttribute("salon", salon);
        if (salon.getContentId() != null) {
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(salon.getContentId());
            if (ja != null) {
                String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                modelo.addAttribute("texto", texto);
                modelo.addAttribute("textoUnicode", UnicodeFormatter.toString(texto));
            }
        }
        return "cursoAdmin/salonCorreo";
    }

    @RequestMapping(params = "action=enviaInvitacion")
    public void enviaInvitacion(ActionRequest request, ActionResponse response,
            @ModelAttribute Salon salon) throws SystemException, PortalException {

        String asunto = salon.getSubject();
        String texto = salon.getTexto();
        log.debug("ASUNTO: {}", asunto);
        log.debug("TEXTO: {}", texto);
        String mensaje = salon.getTexto();

        salon = cursoDao.obtieneSalonPorId(salon.getId());
        User creador = PortalUtil.getUser(request);
        ThemeDisplay themeDisplay = getThemeDisplay(request);
        if (salon.getContentId() == null) {
            Calendar displayDate;
            if (themeDisplay != null) {
                displayDate = CalendarFactoryUtil.getCalendar(themeDisplay.getTimeZone(), themeDisplay.getLocale());
            } else {
                displayDate = CalendarFactoryUtil.getCalendar();
            }
            ServiceContext serviceContext = ServiceContextFactory.getInstance(JournalArticle.class.getName(), request);

            JournalArticle article = textoUtil.crea(
                    salon.getCurso().getNombre() + " - CLASSROOM EMAIL",
                    salon.getCurso().getNombre() + " - CLASSROOM EMAIL",
                    texto,
                    displayDate,
                    creador.getUserId(),
                    salon.getCurso().getComunidadId(),
                    serviceContext);

            salon.setContentId(article.getId());
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("<?xml version='1.0' encoding='UTF-8'?><root><static-content><![CDATA[");
            sb.append(texto);
            sb.append("]]></static-content></root>");
            texto = sb.toString();

            JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(salon.getContentId());
            ja.setUserId(creador.getUserId());
            ja.setContent(texto);
            ja.setVersion(ja.getVersion() + 1);
            JournalArticleLocalServiceUtil.updateJournalArticle(ja);
        }

        cursoDao.actualizaSalon(salon);
        log.debug("Enviando correo");
        List<AlumnoCurso> alumnos = cursoDao.alumnos(salon.getCurso().getId());
        for (AlumnoCurso alumnoCurso : alumnos) {
            User usuario = UserLocalServiceUtil.getUser(alumnoCurso.getAlumno().getId());
            try {
                Properties props = new Properties();
                String home = System.getProperty("user.home");
                File propsFile = new File(home, "portal-ext.properties");
                props.load(new FileInputStream(propsFile));
                StringBuilder params = new StringBuilder();
                params.append("fullName=").append(java.net.URLEncoder.encode(usuario.getFullName(), "UTF-8"));
                params.append("&meetingID=").append(java.net.URLEncoder.encode(salon.getCurso().getCodigo() + "-" + salon.getCurso().getComunidadId(), "UTF-8"));
                params.append("&password=").append(java.net.URLEncoder.encode(salon.getAttendeePW(), "UTF-8"));
                params.append("&createTime=").append(java.net.URLEncoder.encode(salon.getCreateTime(), "UTF-8"));
                String checksum = DigestUtils.shaHex("join" + params.toString() + props.getProperty("bbb.salt"));
                params.append("&checksum=").append(checksum);
                String bbb = props.getProperty("bbb.url") + "/join?" + params.toString();
                String correo = mensaje + "<h1 style='font-size:1.5em;'><a href='" + bbb + "' target='_blank'>" + messages.getMessage("salon.entrar.clic", null, themeDisplay.getLocale()) + "</a></h1>";
                InternetAddress from = new InternetAddress(alumnoCurso.getCurso().getCorreo());
                InternetAddress destinatario = new InternetAddress(usuario.getEmailAddress(), usuario.getFullName());
                log.info("Enviando invitacion a {} para salon {}", usuario.getFullName(), salon);
                MailEngine.send(from, destinatario, asunto, correo, true);
            } catch (MailEngineException | IOException | AddressException e) {
                log.error("No se le pudo enviar el correo a " + alumnoCurso.getAlumno(), e);
            }
        }

        response.setRenderParameter("action", "salon");
        response.setRenderParameter("cursoId", salon.getCurso().getId().toString());
    }

    @RequestMapping(params = "action=eliminaSalon")
    public void eliminaSalon(ActionRequest request, ActionResponse response,
            @RequestParam Long salonId) throws SystemException, PortalException {

        Salon salon = cursoDao.obtieneSalonPorId(salonId);
        if (salon != null) {
            try {
                Properties props = new Properties();
                String home = System.getProperty("user.home");
                File propsFile = new File(home, "portal-ext.properties");
                props.load(new FileInputStream(propsFile));
                StringBuilder params = new StringBuilder();
                params.append("meetingID=").append(java.net.URLEncoder.encode(salon.getMeetingID(), "UTF-8"));
                params.append("&password=").append(java.net.URLEncoder.encode(salon.getModeratorPW(), "UTF-8"));
                String checksum = DigestUtils.shaHex("end" + params.toString() + props.getProperty("bbb.salt"));
                params.append("&checksum=").append(checksum);
                String bbb = props.getProperty("bbb.url") + "/end?" + params.toString();
                log.debug("URL: {}", bbb);
                URL url = new URL(bbb);
                URLConnection urlConn = url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                String linea = in.readLine();
                while (StringUtils.isNotBlank(linea)) {
                    log.debug("out: {}", linea);
                    String returnCode = StringUtils.substringBetween(linea, "<returncode>", "</returncode>");
                    if (returnCode.equals("SUCCESS")) {
                        log.info("Se ha enviado la peticion al servidor para cerrar el salon {}", salonId);
                    }
                    linea = in.readLine();
                }
            } catch (IOException ex) {
                log.error("No se pudo crear el salon", ex);
            }

            cursoDao.eliminaSalon(salon);

            response.setRenderParameter("action", "ver");
            response.setRenderParameter("id", salon.getCurso().getId().toString());

        }
    }

    @ResourceMapping(value = "actualizaObjetos")
    public void actualizaObjetos(ResourceRequest request, ResourceResponse response, @RequestParam Long id, @RequestParam(value = "objetos[]", required = false) String[] objetos) {
        log.debug("Actualizando objetos {} para el curso {}", objetos, id);

        List<Long> objetosArray = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < objetos.length; i++) {
            log.debug("Objeto: {}", objetos[i]);
            String[] x = StringUtils.split(objetos[i], ",");
            if (x != null) {
                for (String y : x) {
                    objetosArray.add(new Long(y));
                }
            } else {
                objetosArray.add(new Long(objetos[i]));
            }
        }

        Long[] ids = objetosArray.toArray(new Long[0]);
        cursoDao.actualizaObjetos(id, ids);
    }

    @ResourceMapping(value = "buscaObjetos")
    public void buscaObjetos(ResourceRequest request, ResourceResponse response, @RequestParam Long id, @RequestParam(value = "term", required = false) String filtro) throws IOException {
        log.debug("Busca objetos para curso {} con filtro {}", id, filtro);
        JSONArray results = JSONFactoryUtil.createJSONArray();

        List<ObjetoAprendizaje> objetos = cursoDao.buscaObjetos(id, filtro);
        for (ObjetoAprendizaje objeto: objetos) {
            JSONObject listEntry = JSONFactoryUtil.createJSONObject();

            listEntry.put("id", objeto.getId());
            listEntry.put("value", objeto.getCodigo() + " | " + objeto.getNombre());

            results.put(listEntry);
        }

        PrintWriter writer = response.getWriter();
        writer.println(results.toString());
    }
}
