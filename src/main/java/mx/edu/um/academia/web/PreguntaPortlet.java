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
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.validation.Valid;
import mx.edu.um.academia.dao.PreguntaDao;
import mx.edu.um.academia.model.Pregunta;
import mx.edu.um.academia.utils.ComunidadUtil;
import mx.edu.um.academia.utils.TextoUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
@Controller
@RequestMapping("VIEW")
public class PreguntaPortlet extends BaseController {

    @Autowired
    private PreguntaDao preguntaDao;
    @Autowired
    private TextoUtil textoUtil;
    @Autowired
    private ResourceBundleMessageSource messageSource;

    public PreguntaPortlet() {
        log.info("Nueva instancia del Controlador de Preguntas");
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
        log.debug("Lista de preguntas");
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

        params = preguntaDao.lista(params);
        List<Pregunta> preguntas = (List<Pregunta>) params.get("preguntas");
        if (preguntas != null && preguntas.size() > 0) {
            modelo.addAttribute("preguntas", preguntas);
        }

        return "pregunta/lista";
    }

    @RequestMapping(params = "action=nuevo")
    public String nuevo(RenderRequest request, Model modelo) throws SystemException, PortalException {
        log.debug("Nuevo pregunta");
        Pregunta pregunta = new Pregunta();
        modelo.addAttribute("pregunta", pregunta);
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        return "pregunta/nuevo";
    }

    @RequestMapping(params = "action=nuevoError")
    public String nuevoError(RenderRequest request, Model modelo) throws SystemException, PortalException {
        log.debug("Nuevo pregunta despues de error");
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        return "pregunta/nuevo";
    }

    @RequestMapping(params = "action=crea")
    public void crea(ActionRequest request, ActionResponse response,
            @Valid Pregunta pregunta,
            BindingResult result) throws SystemException, PortalException {
        log.debug("Creando pregunta {}", pregunta);
        if (result.hasErrors()) {
            log.debug("Hubo algun error en la forma, regresando");
            response.setRenderParameter("action", "nuevoError");
        }

        User creador = PortalUtil.getUser(request);
        preguntaDao.crea(pregunta, creador);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", pregunta.getId().toString());
    }

    @RequestMapping(params = "action=ver")
    public String ver(RenderRequest request, @RequestParam Long id, Model modelo) throws PortalException, SystemException {
        log.debug("Mostrando pregunta {}", id);
        Pregunta pregunta = preguntaDao.obtiene(id);
        if (pregunta.getContenido() != null) {
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            try {
                JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(pregunta.getContenido());
                if (ja != null) {
                    String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                    modelo.addAttribute("texto", texto);
                }
            } catch (com.liferay.portlet.journal.NoSuchArticleException e) {
                log.error("No encontre el contenido", e);
                modelo.addAttribute("texto", messageSource.getMessage("no.encontre.articulo", new String[]{pregunta.getNombre()}, themeDisplay.getLocale()));
            }
        }
        modelo.addAttribute("pregunta", pregunta);

        Map<Long, String> comunidades = ComunidadUtil.obtieneComunidades(request);
        Map<String, Object> respuestas = preguntaDao.respuestas(id, comunidades.keySet());
        modelo.addAttribute("correctas", respuestas.get("correctas"));
        modelo.addAttribute("incorrectas", respuestas.get("incorrectas"));
        modelo.addAttribute("disponibles", respuestas.get("disponibles"));

        return "pregunta/ver";
    }

    @RequestMapping(params = "action=edita")
    public String edita(RenderRequest request, Model modelo, @RequestParam Long id) throws SystemException, PortalException {
        log.debug("Edita pregunta");
        Pregunta pregunta = preguntaDao.obtiene(id);
        modelo.addAttribute("pregunta", pregunta);
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        return "pregunta/edita";
    }

    @RequestMapping(params = "action=editaError")
    public String editaError(RenderRequest request, Model modelo) throws SystemException, PortalException {
        log.debug("Edita pregunta despues de error");
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        return "pregunta/edita";
    }

    @RequestMapping(params = "action=actualiza")
    public void actualiza(ActionRequest request, ActionResponse response,
            @Valid Pregunta pregunta,
            BindingResult result) throws SystemException, PortalException {
        log.debug("Actualizando pregunta {}", pregunta);
        if (result.hasErrors()) {
            log.debug("Hubo algun error en la forma, regresando");
            response.setRenderParameter("action", "editaError");
        }

        User creador = PortalUtil.getUser(request);
        preguntaDao.actualiza(pregunta, creador);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", pregunta.getId().toString());
    }

    @RequestMapping(params = "action=elimina")
    public void elimina(ActionRequest request, @RequestParam Long id) throws PortalException, SystemException {
        log.debug("eliminando pregunta {}", id);

        Pregunta pregunta = preguntaDao.obtiene(id);
        if (pregunta.getContenido() != null) {
            try {
                JournalArticleLocalServiceUtil.deleteJournalArticle(pregunta.getContenido());
            } catch (com.liferay.portlet.journal.NoSuchArticleException e) {
                log.error("No se encontro el articulo para borrarlo, borrando contenido de todas formas", e);
            }
        }
        User creador = PortalUtil.getUser(request);
        preguntaDao.elimina(id, creador);
    }

    @RequestMapping(params = "action=nuevoTexto")
    public String nuevoTexto(RenderRequest request, Model modelo, @RequestParam Long id) throws SystemException, PortalException {
        log.debug("Nuevo texto para pregunta {}", id);
        Pregunta pregunta = preguntaDao.obtiene(id);
        modelo.addAttribute("pregunta", pregunta);
        return "pregunta/nuevoTexto";
    }

    @RequestMapping(params = "action=creaTexto")
    public void creaTexto(ActionRequest request, ActionResponse response,
            @ModelAttribute Pregunta pregunta, @RequestParam String texto) throws SystemException, PortalException {
        log.debug("Creando texto para pregunta {}", pregunta.getId());
        pregunta = preguntaDao.obtiene(pregunta.getId());
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
        if (pregunta.getContenido() != null) {
            JournalArticleLocalServiceUtil.deleteJournalArticle(pregunta.getContenido());
        }

        JournalArticle article = textoUtil.crea(
                pregunta.getNombre(),
                pregunta.getNombre(),
                texto,
                displayDate,
                creador.getUserId(),
                pregunta.getComunidadId(),
                serviceContext);
        
        pregunta.setContenido(article.getId());
        pregunta = preguntaDao.actualizaContenido(pregunta, creador);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", pregunta.getId().toString());
    }

    @RequestMapping(params = "action=editaTexto")
    public String editaTexto(RenderRequest request, Model modelo, @RequestParam Long id) throws SystemException, PortalException {
        log.debug("Edita texto para pregunta {}", id);
        Pregunta pregunta = preguntaDao.obtiene(id);
        modelo.addAttribute("pregunta", pregunta);
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        String texto = textoUtil.obtieneTexto(pregunta.getContenido(), themeDisplay);
        if (texto != null) {
            modelo.addAttribute("texto", texto);
            modelo.addAttribute("textoUnicode", UnicodeFormatter.toString(texto));
        }
        return "pregunta/editaTexto";
    }

    @RequestMapping(params = "action=actualizaTexto")
    public void actualizaTexto(ActionRequest request, ActionResponse response,
            @ModelAttribute Pregunta pregunta, @RequestParam String texto) throws SystemException, PortalException {
        log.debug("Actualizando texto para pregunta {}", pregunta.getId());
        pregunta = preguntaDao.obtiene(pregunta.getId());
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='UTF-8'?><root><static-content><![CDATA[");
        sb.append(texto);
        sb.append("]]></static-content></root>");
        texto = sb.toString();

        User creador = PortalUtil.getUser(request);
        JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(pregunta.getContenido());
        ja.setUserId(creador.getUserId());
        ja.setTitle(pregunta.getNombre());
        ja.setDescription(pregunta.getNombre());
        ja.setContent(texto);
        ja.setVersion(ja.getVersion() + 1);
        JournalArticleLocalServiceUtil.updateJournalArticle(ja);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", pregunta.getId().toString());
    }

    @RequestMapping(params = "action=agregaRespuestas")
    public void agregaRespuestas(ActionRequest request, ActionResponse response, @RequestParam Long preguntaId, @RequestParam Long[] correctas, @RequestParam Long[] incorrectas) {
        log.debug("Agregando respuestas correctas {} e incorrectas {} a {}", new Object[]{correctas, incorrectas, preguntaId});

        preguntaDao.asignaRespuestas(preguntaId, correctas, incorrectas);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", preguntaId.toString());
    }
}
