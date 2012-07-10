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
import java.util.*;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.validation.Valid;
import mx.edu.um.academia.dao.ExamenDao;
import mx.edu.um.academia.dao.PreguntaDao;
import mx.edu.um.academia.model.*;
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
public class ExamenPortlet extends BaseController {

    @Autowired
    private ExamenDao examenDao;
    @Autowired
    private PreguntaDao preguntaDao;
    @Autowired
    private TextoUtil textoUtil;
    @Autowired
    private ResourceBundleMessageSource messageSource;

    public ExamenPortlet() {
        log.info("Nueva instancia del Controlador de Examenes");
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
        log.debug("Lista de examenes");
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

        params = examenDao.lista(params);
        List<Examen> examenes = (List<Examen>) params.get("examenes");
        if (examenes != null && examenes.size() > 0) {
            modelo.addAttribute("examenes", examenes);
        }

        return "examen/lista";
    }

    @RequestMapping(params = "action=nuevo")
    public String nuevo(RenderRequest request, Model modelo) throws SystemException, PortalException {
        log.debug("Nuevo examen");
        Examen examen = new Examen();
        modelo.addAttribute("examen", examen);
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        return "examen/nuevo";
    }

    @RequestMapping(params = "action=nuevoError")
    public String nuevoError(RenderRequest request, Model modelo) throws SystemException, PortalException {
        log.debug("Nuevo examen despues de error");
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        return "examen/nuevo";
    }

    @RequestMapping(params = "action=crea")
    public void crea(ActionRequest request, ActionResponse response,
            @Valid Examen examen,
            BindingResult result) throws SystemException, PortalException {
        log.debug("Creando examen {}", examen);
        if (result.hasErrors()) {
            log.debug("Hubo algun error en la forma, regresando");
            response.setRenderParameter("action", "nuevoError");
        }

        User creador = PortalUtil.getUser(request);
        examenDao.crea(examen, creador);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", examen.getId().toString());
    }

    @RequestMapping(params = "action=ver")
    public String ver(RenderRequest request, @RequestParam Long id, Model modelo) throws PortalException, SystemException {
        log.debug("Mostrando examen {}", id);
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        Examen examen = examenDao.obtiene(id);
        if (examen.getContenido() != null) {
            JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(examen.getContenido());
            if (ja != null) {
                String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                modelo.addAttribute("texto", texto);
            }
        }
        modelo.addAttribute("examen", examen);

        List<Pregunta> preguntas = new ArrayList<>();
        for (Pregunta pregunta : examenDao.preguntas(id)) {
            for (Respuesta respuesta : pregunta.getRespuestas()) {
                JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(respuesta.getContenido());
                if (ja != null) {
                    String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                    respuesta.setTexto(texto);
                }
            }
            if (pregunta.getContenido() != null) {
                JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(pregunta.getContenido());
                if (ja != null) {
                    String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                    pregunta.setTexto(texto);
                }
                preguntas.add(pregunta);
            } else {
                pregunta.setTexto(messageSource.getMessage("pregunta.requiere.texto", new String[] {pregunta.getNombre()}, themeDisplay.getLocale()));
                preguntas.add(pregunta);
            }
        }
        if (preguntas.size() > 0) {
            for (Pregunta pregunta : preguntas) {
                log.debug("{} ||| {}", pregunta, pregunta.getTexto());
            }
            modelo.addAttribute("preguntas", preguntas);
        }

        return "examen/ver";
    }

    @RequestMapping(params = "action=edita")
    public String edita(RenderRequest request, Model modelo, @RequestParam Long id) throws SystemException, PortalException {
        log.debug("Edita examen");
        Examen examen = examenDao.obtiene(id);
        modelo.addAttribute("examen", examen);
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        return "examen/edita";
    }

    @RequestMapping(params = "action=editaError")
    public String editaError(RenderRequest request, Model modelo) throws SystemException, PortalException {
        log.debug("Edita examen despues de error");
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        return "examen/edita";
    }

    @RequestMapping(params = "action=actualiza")
    public void actualiza(ActionRequest request, ActionResponse response,
            @Valid Examen examen,
            BindingResult result) throws SystemException, PortalException {
        log.debug("Actualizando examen {}", examen);
        if (result.hasErrors()) {
            log.debug("Hubo algun error en la forma, regresando");
            response.setRenderParameter("action", "editaError");
        }

        User creador = PortalUtil.getUser(request);
        examenDao.actualiza(examen, creador);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", examen.getId().toString());
    }

    @RequestMapping(params = "action=elimina")
    public void elimina(ActionRequest request, @RequestParam Long id) throws PortalException, SystemException {
        log.debug("eliminando examen {}", id);

        Examen examen = examenDao.obtiene(id);
        if (examen.getContenido() != null) {
            JournalArticleLocalServiceUtil.deleteJournalArticle(examen.getContenido());
        }
        User creador = PortalUtil.getUser(request);
        examenDao.elimina(id, creador);
    }

    @RequestMapping(params = "action=nuevoTexto")
    public String nuevoTexto(RenderRequest request, Model modelo, @RequestParam Long id) throws SystemException, PortalException {
        log.debug("Nuevo texto para examen {}", id);
        Examen examen = examenDao.obtiene(id);
        modelo.addAttribute("examen", examen);
        return "examen/nuevoTexto";
    }

    @RequestMapping(params = "action=creaTexto")
    public void creaTexto(ActionRequest request, ActionResponse response,
            @ModelAttribute Examen examen, @RequestParam String texto) throws SystemException, PortalException {
        log.debug("Creando texto para examen {}", examen.getId());
        examen = examenDao.obtiene(examen.getId());
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
        if (examen.getContenido() != null) {
            JournalArticleLocalServiceUtil.deleteJournalArticle(examen.getContenido());
        }

        JournalArticle article = textoUtil.crea(
                examen.getNombre(),
                examen.getNombre(),
                texto,
                displayDate,
                creador.getUserId(),
                examen.getComunidadId(),
                serviceContext);
        examen.setContenido(article.getId());
        examen = examenDao.actualizaContenido(examen, creador);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", examen.getId().toString());
    }

    @RequestMapping(params = "action=editaTexto")
    public String editaTexto(RenderRequest request, Model modelo, @RequestParam Long id) throws SystemException, PortalException {
        log.debug("Edita texto para examen {}", id);
        Examen examen = examenDao.obtiene(id);
        modelo.addAttribute("examen", examen);
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        String texto = textoUtil.obtieneTexto(examen.getContenido(), themeDisplay);
        if (texto != null) {
            modelo.addAttribute("texto", texto);
            modelo.addAttribute("textoUnicode", UnicodeFormatter.toString(texto));
        }
        return "examen/editaTexto";
    }

    @RequestMapping(params = "action=actualizaTexto")
    public void actualizaTexto(ActionRequest request, ActionResponse response,
            @ModelAttribute Examen examen, @RequestParam String texto) throws SystemException, PortalException {
        log.debug("Actualizando texto para examen {}", examen.getId());
        examen = examenDao.obtiene(examen.getId());
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='UTF-8'?><root><static-content><![CDATA[");
        sb.append(texto);
        sb.append("]]></static-content></root>");
        texto = sb.toString();

        User creador = PortalUtil.getUser(request);
        JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(examen.getContenido());
        ja.setUserId(creador.getUserId());
        ja.setTitle(examen.getNombre());
        ja.setDescription(examen.getNombre());
        ja.setContent(texto);
        ja.setVersion(ja.getVersion() + 1);
        JournalArticleLocalServiceUtil.updateJournalArticle(ja);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", examen.getId().toString());
    }

    @RequestMapping(params = "action=pregunta")
    public String pregunta(RenderRequest request, Model modelo, @RequestParam Long id) throws SystemException, PortalException {
        log.debug("Pregunta para examen {}", id);
        Examen examen = examenDao.obtiene(id);
        modelo.addAttribute("examen", examen);

        List<Pregunta> preguntas = preguntaDao.todas(ComunidadUtil.obtieneComunidades(request).keySet());
        modelo.addAttribute("preguntas", preguntas);

        ExamenPreguntaPK examenPreguntaPK = new ExamenPreguntaPK(examen, new Pregunta());
        ExamenPregunta examenPregunta = new ExamenPregunta(examenPreguntaPK);
        modelo.addAttribute("examenPregunta", examenPregunta);
        return "examen/nuevaPregunta";
    }

    @RequestMapping(params = "action=asignaPregunta")
    public void asignaPregunta(ActionRequest request, ActionResponse response,
            @ModelAttribute ExamenPregunta examenPregunta) throws SystemException, PortalException {
        log.debug("Actualizando texto para examen {}", examenPregunta);
        User creador = PortalUtil.getUser(request);
        examenDao.asignaPregunta(
                examenPregunta.getId().getExamen().getId(),
                examenPregunta.getId().getPregunta().getId(),
                examenPregunta.getPuntos(),
                examenPregunta.getPorPregunta(),
                null,
                creador);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", examenPregunta.getId().getExamen().getId().toString());
    }

    @RequestMapping(params = "action=eliminaPregunta")
    public void eliminaPregunta(ActionRequest request, ActionResponse response, @RequestParam Long examenId, @RequestParam Long preguntaId) {
        log.debug("Eliminando pregunta {} del examen {}", preguntaId, examenId);
        examenDao.quitaPregunta(examenId, preguntaId);
        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", examenId.toString());
    }
}
