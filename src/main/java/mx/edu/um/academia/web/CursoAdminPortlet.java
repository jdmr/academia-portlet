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
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalArticleConstants;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import javax.portlet.*;
import javax.validation.Valid;
import mx.edu.um.academia.dao.ContenidoDao;
import mx.edu.um.academia.dao.CursoDao;
import mx.edu.um.academia.model.Contenido;
import mx.edu.um.academia.model.Curso;
import mx.edu.um.academia.model.ObjetoAprendizaje;
import mx.edu.um.academia.utils.ComunidadUtil;
import mx.edu.um.academia.utils.Constantes;
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
            Model modelo) throws SystemException, PortalException {
        log.debug("Lista de cursos");
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
        if (max == null) {
            max = new Integer(5);
        }
        if (offset == null) {
            offset = new Integer(0);
        } else if (direccion != null && direccion == 1) {
            offset = offset + max;
        } else if ((direccion != null && direccion == 0) && offset > 0) {
            offset = offset - max;
        }
        params.put("max", max);
        params.put("offset", offset);

        params = cursoDao.lista(params);
        List<Curso> cursos = (List<Curso>) params.get("cursos");
        if (cursos != null && cursos.size() > 0) {
            modelo.addAttribute("cursos", params.get("cursos"));
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
            BindingResult result) throws SystemException, PortalException {
        log.debug("Creando curso {}", curso);
        if (result.hasErrors()) {
            log.debug("Hubo algun error en la forma, regresando");
            response.setRenderParameter("action", "nuevoError");
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
            BindingResult result) throws SystemException, PortalException {
        log.debug("Actualizando curso {}", curso);
        if (result.hasErrors()) {
            log.debug("Hubo algun error en la forma, regresando");
            response.setRenderParameter("action", "editaError");
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

            JournalArticle article = JournalArticleLocalServiceUtil.addArticle(
                    creador.getUserId(), // UserId
                    curso.getComunidadId(), // GroupId
                    "", // ArticleId
                    true, // AutoArticleId
                    JournalArticleConstants.DEFAULT_VERSION, // Version
                    curso.getNombre() + " - INTRO", // Titulo
                    "", // Descripcion
                    texto, // Contenido
                    "general", // Tipo
                    "", // Estructura
                    "", // Template
                    displayDate.get(Calendar.MONTH), // displayDateMonth,
                    displayDate.get(Calendar.DAY_OF_MONTH), // displayDateDay,
                    displayDate.get(Calendar.YEAR), // displayDateYear,
                    displayDate.get(Calendar.HOUR_OF_DAY), // displayDateHour,
                    displayDate.get(Calendar.MINUTE), // displayDateMinute,
                    0, // expirationDateMonth, 
                    0, // expirationDateDay, 
                    0, // expirationDateYear, 
                    0, // expirationDateHour, 
                    0, // expirationDateMinute, 
                    true, // neverExpire
                    0, // reviewDateMonth, 
                    0, // reviewDateDay, 
                    0, // reviewDateYear, 
                    0, // reviewDateHour, 
                    0, // reviewDateMinute, 
                    true, // neverReview
                    true, // indexable
                    false, // SmallImage
                    "", // SmallImageUrl
                    null, // SmallFile
                    null, // Images
                    "", // articleURL 
                    serviceContext // serviceContext
                    );

            log.debug("Asignando intro {} a curso {}", article.getId(), curso);
            curso.setIntro(article.getId());
            cursoDao.actualiza(curso, creador);

        } catch (PortalException | SystemException | DataAccessException e) {
            log.error("No se pudo crear la intro", e);
        }

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
        switch (contenido.getTipo()) {
            case Constantes.TEXTO:
                if (contenido.getContenidoId() != null) {
                    ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
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
                ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
                sb.append("<iframe src='").append(request.getContextPath());
                sb.append("/contenido/player.html?contenidoId=").append(contenido.getId());
                sb.append("&cursoId=").append(cursoId);
                sb.append("&userId=").append(themeDisplay.getUserId());
                sb.append("&admin=true");
                sb.append("' style='width:100%;height:600px;'></iframe>");
        }
        sb.append("</div>");
        PrintWriter writer = response.getWriter();
        writer.write(sb.toString());
    }

    @RequestMapping(params = "action=vistaPreviaIntro")
    public void vistaPreviaIntro(ResourceRequest request, ResourceResponse response, @RequestParam Long cursoId) throws IOException, PortalException, SystemException {
        log.debug("Vista previa del contenido del curso {} ", cursoId);
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
        comercios.add("DINEROMAIL");
        return comercios;
    }
}
