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
import com.liferay.portal.kernel.util.UnicodeFormatter;
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
import java.util.*;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.validation.Valid;
import mx.edu.um.academia.dao.ContenidoDao;
import mx.edu.um.academia.dao.ExamenDao;
import mx.edu.um.academia.model.Contenido;
import mx.edu.um.academia.model.Examen;
import mx.edu.um.academia.utils.ComunidadUtil;
import mx.edu.um.academia.utils.Constantes;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
@Controller
@RequestMapping("VIEW")
public class ContenidoPortlet extends BaseController {

    @Autowired
    private ContenidoDao contenidoDao;
    @Autowired
    private ExamenDao examenDao;
    @Autowired
    private ResourceBundleMessageSource messages;

    public ContenidoPortlet() {
        log.info("Nueva instancia de Contenido Portlet");
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
        log.debug("Lista de contenidos");
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

        params = contenidoDao.lista(params);
        List<Contenido> contenidos = (List<Contenido>) params.get("contenidos");
        if (contenidos != null && contenidos.size() > 0) {
            modelo.addAttribute("contenidos", contenidos);
        }

        return "contenido/lista";
    }

    @RequestMapping(params = "action=nuevo")
    public String nuevo(RenderRequest request, Model modelo) throws SystemException, PortalException {
        log.debug("Nuevo contenido");
        Contenido contenido = new Contenido();
        modelo.addAttribute("contenido", contenido);
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        modelo.addAttribute("tipos", obtieneTiposDeContenido(getThemeDisplay(request).getLocale()));
        return "contenido/nuevo";
    }

    @RequestMapping(params = "action=nuevoError")
    public String nuevoError(RenderRequest request, Model modelo) throws SystemException, PortalException {
        log.debug("Nuevo contenido despues de error");
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        modelo.addAttribute("tipos", obtieneTiposDeContenido(getThemeDisplay(request).getLocale()));
        return "contenido/nuevo";
    }

    @RequestMapping(params = "action=crea")
    public void crea(ActionRequest request, ActionResponse response,
            @Valid Contenido contenido,
            BindingResult result) throws SystemException, PortalException {
        log.debug("Creando contenido {}", contenido);
        if (result.hasErrors()) {
            log.debug("Hubo algun error en la forma, regresando");
            response.setRenderParameter("action", "nuevoError");
        }

        User creador = PortalUtil.getUser(request);
        contenidoDao.crea(contenido, creador);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", contenido.getId().toString());
    }

    @RequestMapping(params = "action=ver")
    public String ver(RenderRequest request, @RequestParam Long id, Model modelo) throws PortalException, SystemException {
        log.debug("Mostrando contenido {}", id);
        Contenido contenido = contenidoDao.obtiene(id);
        switch (contenido.getTipo()) {
            case Constantes.TEXTO:
                if (contenido.getContenidoId() != null) {
                    ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
                    JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(contenido.getContenidoId());
                    if (ja != null) {
                        String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                        modelo.addAttribute("texto", texto);
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
                    modelo.addAttribute("video", videoLink);
                }
                break;
            case Constantes.EXAMEN:
                List<Examen> examenes = examenDao.todos(ComunidadUtil.obtieneComunidades(request).keySet());
                modelo.addAttribute("examenes", examenes);
                if (contenido.getExamen() != null) {
                    modelo.addAttribute("examen", contenido.getExamen());
                }
        }
        modelo.addAttribute("contenido", contenido);
        return "contenido/ver";
    }

    @RequestMapping(params = "action=edita")
    public String edita(RenderRequest request, Model modelo, @RequestParam Long id) throws SystemException, PortalException {
        log.debug("Edita contenido");
        Contenido contenido = contenidoDao.obtiene(id);
        modelo.addAttribute("contenido", contenido);
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        modelo.addAttribute("tipos", obtieneTiposDeContenido(getThemeDisplay(request).getLocale()));
        return "contenido/edita";
    }

    @RequestMapping(params = "action=editaError")
    public String editaError(RenderRequest request, Model modelo) throws SystemException, PortalException {
        log.debug("Edita contenido despues de error");
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        modelo.addAttribute("tipos", obtieneTiposDeContenido(getThemeDisplay(request).getLocale()));
        return "contenido/edita";
    }

    @RequestMapping(params = "action=actualiza")
    public void actualiza(ActionRequest request, ActionResponse response,
            @Valid Contenido contenido,
            BindingResult result) throws SystemException, PortalException {
        log.debug("Actualizando contenido {}", contenido);
        if (result.hasErrors()) {
            log.debug("Hubo algun error en la forma, regresando");
            response.setRenderParameter("action", "editaError");
        }

        User creador = PortalUtil.getUser(request);
        contenidoDao.actualiza(contenido, creador);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", contenido.getId().toString());
    }

    @RequestMapping(params = "action=elimina")
    public void elimina(ActionRequest request, @RequestParam Long id) throws PortalException, SystemException {
        log.debug("eliminando contenido {}", id);

        Contenido contenido = contenidoDao.obtiene(id);
        switch (contenido.getTipo()) {
            case Constantes.TEXTO:
                if (contenido.getContenidoId() != null) {
                    JournalArticleLocalServiceUtil.deleteJournalArticle(contenido.getContenidoId());
                }
                break;

        }
        User creador = PortalUtil.getUser(request);
        contenidoDao.elimina(id, creador);
    }

    @RequestMapping(params = "action=nuevoTexto")
    public String nuevoTexto(RenderRequest request, Model modelo, @RequestParam Long id) throws SystemException, PortalException {
        log.debug("Nuevo texto para contenido {}", id);
        Contenido contenido = contenidoDao.obtiene(id);
        modelo.addAttribute("contenido", contenido);
        return "contenido/nuevoTexto";
    }

    @RequestMapping(params = "action=creaTexto")
    public void creaTexto(ActionRequest request, ActionResponse response,
            @ModelAttribute Contenido contenido, @RequestParam String texto) throws SystemException, PortalException {
        log.debug("Creando texto para contenido {}", contenido.getId());
        contenido = contenidoDao.obtiene(contenido.getId());
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='UTF-8'?><root><static-content><![CDATA[");
        sb.append(texto);
        sb.append("]]></static-content></root>");
        texto = sb.toString();

        ThemeDisplay themeDisplay = getThemeDisplay(request);
        Calendar displayDate;
        if (themeDisplay != null) {
            displayDate = CalendarFactoryUtil.getCalendar(themeDisplay.getTimeZone(), themeDisplay.getLocale());
        } else {
            displayDate = CalendarFactoryUtil.getCalendar();
        }
        User creador = PortalUtil.getUser(request);
        ServiceContext serviceContext = ServiceContextFactory.getInstance(JournalArticle.class.getName(), request);
        if (contenido.getContenidoId() != null) {
            JournalArticleLocalServiceUtil.deleteJournalArticle(contenido.getContenidoId());
        }
        JournalArticle article = JournalArticleLocalServiceUtil.addArticle(
                creador.getUserId(), // UserId
                contenido.getComunidadId(), // GroupId
                "", // ArticleId
                true, // AutoArticleId
                JournalArticleConstants.DEFAULT_VERSION, // Version
                contenido.getNombre(), // Titulo
                contenido.getNombre(), // Descripcion
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
        contenido.setContenidoId(article.getId());
        contenido = contenidoDao.actualizaContenidoId(contenido, creador);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", contenido.getId().toString());
    }

    @RequestMapping(params = "action=editaTexto")
    public String editaTexto(RenderRequest request, Model modelo, @RequestParam Long id) throws SystemException, PortalException {
        log.debug("Nuevo texto para contenido {}", id);
        Contenido contenido = contenidoDao.obtiene(id);
        modelo.addAttribute("contenido", contenido);
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(contenido.getContenidoId());
        if (ja != null) {
            String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
            modelo.addAttribute("texto", texto);
            modelo.addAttribute("textoUnicode", UnicodeFormatter.toString(texto));
        }
        return "contenido/editaTexto";
    }

    @RequestMapping(params = "action=actualizaTexto")
    public void actualizaTexto(ActionRequest request, ActionResponse response,
            @ModelAttribute Contenido contenido, @RequestParam String texto) throws SystemException, PortalException {
        log.debug("Actualizando texto para contenido {}", contenido.getId());
        contenido = contenidoDao.obtiene(contenido.getId());
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='UTF-8'?><root><static-content><![CDATA[");
        sb.append(texto);
        sb.append("]]></static-content></root>");
        texto = sb.toString();

        User creador = PortalUtil.getUser(request);
        JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(contenido.getContenidoId());
        ja.setUserId(creador.getUserId());
        ja.setTitle(contenido.getNombre());
        ja.setDescription(contenido.getNombre());
        ja.setContent(texto);
        ja.setVersion(ja.getVersion() + 1);
        JournalArticleLocalServiceUtil.updateJournalArticle(ja);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", contenido.getId().toString());
    }

    @RequestMapping(params = "action=nuevoVideo")
    public String nuevoVideo(RenderRequest request, Model modelo, @RequestParam Long id) throws SystemException, PortalException {
        log.debug("Nuevo documento para contenido {}", id);
        Contenido contenido = contenidoDao.obtiene(id);
        modelo.addAttribute("contenido", contenido);
        return "contenido/nuevoVideo";
    }

    @RequestMapping(params = "action=creaVideo")
    public void creaVideo(ActionRequest request, ActionResponse response,
            @ModelAttribute Contenido contenido, @RequestParam MultipartFile archivo) throws SystemException, PortalException, IOException {
        log.debug("Creando documento para contenido {}", contenido.getId());
        contenido = contenidoDao.obtiene(contenido.getId());

        User creador = PortalUtil.getUser(request);
        if (contenido.getContenidoId() != null) {
            DLFileEntryLocalServiceUtil.deleteDLFileEntry(contenido.getContenidoId());
        }
        ServiceContext serviceContext = ServiceContextFactory.getInstance(DLFileEntry.class.getName(), request);
        DLFileEntry fileEntry = DLFileEntryLocalServiceUtil.addFileEntry(creador.getUserId(), contenido.getComunidadId(), 0, archivo.getOriginalFilename(), archivo.getOriginalFilename(), contenido.getNombre(), "", "", archivo.getBytes(), serviceContext);
        contenido.setContenidoId(fileEntry.getPrimaryKey());
        contenido = contenidoDao.actualizaContenidoId(contenido, creador);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", contenido.getId().toString());
    }

    private Map<String, String> obtieneTiposDeContenido(Locale locale) {
        Map<String, String> tipos = new LinkedHashMap<>();
        tipos.put(Constantes.TEXTO, messages.getMessage(Constantes.TEXTO, null, locale));
        tipos.put(Constantes.VIDEO, messages.getMessage(Constantes.VIDEO, null, locale));
        tipos.put(Constantes.IMAGEN, messages.getMessage(Constantes.IMAGEN, null, locale));
        tipos.put(Constantes.URL, messages.getMessage(Constantes.URL, null, locale));
        tipos.put(Constantes.EXAMEN, messages.getMessage(Constantes.EXAMEN, null, locale));
        return tipos;
    }
    
    @RequestMapping(params = "action=asignaExamen")
    public void asignaExamen(ActionRequest request, ActionResponse response, 
            @RequestParam Long contenidoId,
            @RequestParam Long examenId) throws SystemException, PortalException {
        log.debug("Asignando examen {} a contenido {}", examenId, contenidoId);
        contenidoDao.asignaExamen(examenId, contenidoId);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", contenidoId.toString());
    }

}
