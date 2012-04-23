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
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.validation.Valid;
import mx.edu.um.academia.dao.ContenidoDao;
import mx.edu.um.academia.model.Contenido;
import mx.edu.um.academia.utils.ComunidadUtil;
import mx.edu.um.academia.utils.Constantes;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String ver(RenderRequest request, @RequestParam Long id, Model modelo) {
        log.debug("Mostrando contenido {}", id);
        Contenido contenido = contenidoDao.obtiene(id);
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

    private Map<String, String> obtieneTiposDeContenido(Locale locale) {
        Map<String, String> tipos = new LinkedHashMap<>();
        tipos.put(Constantes.TEXTO, messages.getMessage(Constantes.TEXTO, null, locale));
        tipos.put(Constantes.VIDEO, messages.getMessage(Constantes.VIDEO, null, locale));
        tipos.put(Constantes.IMAGEN, messages.getMessage(Constantes.IMAGEN, null, locale));
        tipos.put(Constantes.URL, messages.getMessage(Constantes.URL, null, locale));
        tipos.put(Constantes.EXAMEN, messages.getMessage(Constantes.EXAMEN, null, locale));
        return tipos;
    }
}
