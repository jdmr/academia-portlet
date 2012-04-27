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
import mx.edu.um.academia.dao.RespuestaDao;
import mx.edu.um.academia.model.Respuesta;
import mx.edu.um.academia.utils.ComunidadUtil;
import mx.edu.um.academia.utils.TextoUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class RespuestaPortlet extends BaseController {
    
    @Autowired
    private RespuestaDao respuestaDao;
    @Autowired
    private TextoUtil textoUtil;
    
    public RespuestaPortlet() {
        log.info("Nueva instancia del Controlador de Respuestas");
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
        log.debug("Lista de respuestas");
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

        params = respuestaDao.lista(params);
        List<Respuesta> respuestas = (List<Respuesta>) params.get("respuestas");
        if (respuestas != null && respuestas.size() > 0) {
            modelo.addAttribute("respuestas", respuestas);
        }

        return "respuesta/lista";
    }

    @RequestMapping(params = "action=nuevo")
    public String nuevo(RenderRequest request, Model modelo) throws SystemException, PortalException {
        log.debug("Nuevo respuesta");
        Respuesta respuesta = new Respuesta();
        modelo.addAttribute("respuesta", respuesta);
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        return "respuesta/nuevo";
    }

    @RequestMapping(params = "action=nuevoError")
    public String nuevoError(RenderRequest request, Model modelo) throws SystemException, PortalException {
        log.debug("Nuevo respuesta despues de error");
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        return "respuesta/nuevo";
    }

    @RequestMapping(params = "action=crea")
    public void crea(ActionRequest request, ActionResponse response,
            @Valid Respuesta respuesta,
            BindingResult result) throws SystemException, PortalException {
        log.debug("Creando respuesta {}", respuesta);
        if (result.hasErrors()) {
            log.debug("Hubo algun error en la forma, regresando");
            response.setRenderParameter("action", "nuevoError");
        }

        User creador = PortalUtil.getUser(request);
        respuestaDao.crea(respuesta, creador);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", respuesta.getId().toString());
    }

    @RequestMapping(params = "action=ver")
    public String ver(RenderRequest request, @RequestParam Long id, Model modelo) throws PortalException, SystemException {
        log.debug("Mostrando respuesta {}", id);
        Respuesta respuesta = respuestaDao.obtiene(id);
        String texto = textoUtil.obtieneTexto(respuesta.getContenido(), this.getThemeDisplay(request));
        if (texto != null) {
            modelo.addAttribute("texto", texto);
        }
        modelo.addAttribute("respuesta", respuesta);
        return "respuesta/ver";
    }

    @RequestMapping(params = "action=edita")
    public String edita(RenderRequest request, Model modelo, @RequestParam Long id) throws SystemException, PortalException {
        log.debug("Edita respuesta");
        Respuesta respuesta = respuestaDao.obtiene(id);
        modelo.addAttribute("respuesta", respuesta);
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        return "respuesta/edita";
    }

    @RequestMapping(params = "action=editaError")
    public String editaError(RenderRequest request, Model modelo) throws SystemException, PortalException {
        log.debug("Edita respuesta despues de error");
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        return "respuesta/edita";
    }

    @RequestMapping(params = "action=actualiza")
    public void actualiza(ActionRequest request, ActionResponse response,
            @Valid Respuesta respuesta,
            BindingResult result) throws SystemException, PortalException {
        log.debug("Actualizando respuesta {}", respuesta);
        if (result.hasErrors()) {
            log.debug("Hubo algun error en la forma, regresando");
            response.setRenderParameter("action", "editaError");
        }

        User creador = PortalUtil.getUser(request);
        respuestaDao.actualiza(respuesta, creador);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", respuesta.getId().toString());
    }

    @RequestMapping(params = "action=elimina")
    public void elimina(ActionRequest request, @RequestParam Long id) throws PortalException, SystemException {
        log.debug("eliminando respuesta {}", id);

        Respuesta respuesta = respuestaDao.obtiene(id);
        if (respuesta.getContenido() != null) {
            JournalArticleLocalServiceUtil.deleteJournalArticle(respuesta.getContenido());
        }
        User creador = PortalUtil.getUser(request);
        respuestaDao.elimina(id, creador);
    }
    
    @RequestMapping(params = "action=nuevoTexto")
    public String nuevoTexto(RenderRequest request, Model modelo, @RequestParam Long id) throws SystemException, PortalException {
        log.debug("Nuevo texto para respuesta {}", id);
        Respuesta respuesta = respuestaDao.obtiene(id);
        modelo.addAttribute("respuesta", respuesta);
        return "respuesta/nuevoTexto";
    }

    @RequestMapping(params = "action=creaTexto")
    public void creaTexto(ActionRequest request, ActionResponse response,
            @ModelAttribute Respuesta respuesta, @RequestParam String texto) throws SystemException, PortalException {
        log.debug("Creando texto para respuesta {}", respuesta.getId());
        respuesta = respuestaDao.obtiene(respuesta.getId());
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
        if (respuesta.getContenido() != null) {
            JournalArticleLocalServiceUtil.deleteJournalArticle(respuesta.getContenido());
        }

        JournalArticle article = textoUtil.crea(
                respuesta.getNombre(),
                respuesta.getNombre(),
                texto,
                displayDate,
                creador.getUserId(),
                respuesta.getComunidadId(),
                serviceContext);
        respuesta.setContenido(article.getId());
        respuesta = respuestaDao.actualizaContenido(respuesta, creador);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", respuesta.getId().toString());
    }

    @RequestMapping(params = "action=editaTexto")
    public String editaTexto(RenderRequest request, Model modelo, @RequestParam Long id) throws SystemException, PortalException {
        log.debug("Edita texto para respuesta {}", id);
        Respuesta respuesta = respuestaDao.obtiene(id);
        modelo.addAttribute("respuesta", respuesta);
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        String texto = textoUtil.obtieneTexto(respuesta.getContenido(), themeDisplay);
        if (texto != null) {
            modelo.addAttribute("texto", texto);
            modelo.addAttribute("textoUnicode", UnicodeFormatter.toString(texto));
        }
        return "respuesta/editaTexto";
    }

    @RequestMapping(params = "action=actualizaTexto")
    public void actualizaTexto(ActionRequest request, ActionResponse response,
            @ModelAttribute Respuesta respuesta, @RequestParam String texto) throws SystemException, PortalException {
        log.debug("Actualizando texto para respuesta {}", respuesta.getId());
        respuesta = respuestaDao.obtiene(respuesta.getId());
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='UTF-8'?><root><static-content><![CDATA[");
        sb.append(texto);
        sb.append("]]></static-content></root>");
        texto = sb.toString();

        User creador = PortalUtil.getUser(request);
        JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(respuesta.getContenido());
        ja.setUserId(creador.getUserId());
        ja.setTitle(respuesta.getNombre());
        ja.setDescription(respuesta.getNombre());
        ja.setContent(texto);
        ja.setVersion(ja.getVersion() + 1);
        JournalArticleLocalServiceUtil.updateJournalArticle(ja);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", respuesta.getId().toString());
    }

}
