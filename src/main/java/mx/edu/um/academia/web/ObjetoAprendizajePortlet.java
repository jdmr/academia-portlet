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
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.validation.Valid;
import mx.edu.um.academia.dao.ObjetoAprendizajeDao;
import mx.edu.um.academia.model.Contenido;
import mx.edu.um.academia.model.ObjetoAprendizaje;
import mx.edu.um.academia.utils.ComunidadUtil;
import mx.edu.um.academia.utils.Constantes;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
public class ObjetoAprendizajePortlet extends BaseController {

    @Autowired
    private ObjetoAprendizajeDao objetoAprendizajeDao;

    public ObjetoAprendizajePortlet() {
        log.info("Nueva instancia del portlet de objetos de aprendizaje");
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
        log.debug("Lista de objetos [filtro: {}, offset: {}, max: {}, direccion: {}, order: {}, sort: {}, pagina: {}]", new Object[]{filtro, offset, max, direccion, order, sort, pagina});
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

        params = objetoAprendizajeDao.lista(params);
        List<ObjetoAprendizaje> objetos = (List<ObjetoAprendizaje>) params.get("objetos");
        if (objetos != null && objetos.size() > 0) {
            modelo.addAttribute("objetos", objetos);
            this.pagina(params, modelo, "objetos", pagina);
        }

        return "objeto/lista";
    }

    @RequestMapping(params = "action=nuevo")
    public String nuevo(RenderRequest request, Model modelo) throws SystemException, PortalException {
        log.debug("Nuevo objeto de aprendizaje");
        ObjetoAprendizaje objeto = new ObjetoAprendizaje();
        modelo.addAttribute("objeto", objeto);
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        return "objeto/nuevo";
    }

    @RequestMapping(params = "action=nuevoError")
    public String nuevoError(RenderRequest request, Model modelo) throws SystemException, PortalException {
        log.debug("Nuevo objeto de aprendizaje");
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        return "objeto/nuevo";
    }

    @RequestMapping(params = "action=crea")
    public void crea(ActionRequest request, ActionResponse response,
            @Valid ObjetoAprendizaje objeto,
            BindingResult result,
            @RequestParam(required = false) MultipartFile archivo) throws SystemException, PortalException, IOException {
        log.debug("Creando objeto de aprendizaje {}", objeto);
        if (result.hasErrors()) {
            log.debug("Hubo algun error en la forma, regresando");
            response.setRenderParameter("action", "nuevoError");
        }

        User creador = PortalUtil.getUser(request);

        objetoAprendizajeDao.crea(objeto, archivo, creador);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", objeto.getId().toString());
    }

    @RequestMapping(params = "action=ver")
    public String ver(RenderRequest request, @RequestParam Long id, Model modelo) throws PortalException, SystemException {
        log.debug("Mostrando objeto de aprendizaje {}", id);
        ObjetoAprendizaje objeto = objetoAprendizajeDao.obtiene(id);
        modelo.addAttribute("objeto", objeto);

        Map<Long, String> comunidades = ComunidadUtil.obtieneComunidades(request);
        Map<String, Object> contenidos = objetoAprendizajeDao.contenidos(id, comunidades.keySet());
        List<Contenido> seleccionados = (List<Contenido>) contenidos.get("seleccionados");
        if (seleccionados != null) {
            cicloContenidos:
            for (Contenido contenido : seleccionados) {
                StringBuilder sb = new StringBuilder();
                sb.append(request.getScheme())
                        .append("://")
                        .append(request.getServerName());
                if (request.getServerPort() != 80) {
                    sb.append(":").append(request.getServerPort());
                }
                sb.append(request.getContextPath());
                switch (contenido.getTipo()) {
                    case Constantes.ARTICULATE:
                        sb.append("/contenido/player.html?contenidoId=").append(contenido.getId());
                        sb.append("&admin=true");
                        log.debug("vistaPrevia: {}", sb.toString());
                        modelo.addAttribute("vistaPrevia", sb.toString());
                        break cicloContenidos;
                    case Constantes.STORYLINE:
                        sb.append("/contenido/story.html?contenidoId=").append(contenido.getId());
                        sb.append("&admin=true");
                        log.debug("vistaPrevia: {}", sb.toString());
                        modelo.addAttribute("vistaPrevia", sb.toString());
                        break cicloContenidos;
                }
            }
            modelo.addAttribute("seleccionados", seleccionados);
        }
        modelo.addAttribute("disponibles", contenidos.get("disponibles"));

        return "objeto/ver";
    }

    @RequestMapping(params = "action=edita")
    public String edita(RenderRequest request, Model modelo, @RequestParam Long id) throws SystemException, PortalException {
        log.debug("Edita objeto de aprendizaje");
        ObjetoAprendizaje objeto = objetoAprendizajeDao.obtiene(id);
        modelo.addAttribute("objeto", objeto);
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        return "objeto/edita";
    }

    @RequestMapping(params = "action=editaError")
    public String editaError(RenderRequest request, Model modelo) throws SystemException, PortalException {
        log.debug("Edita objeto de aprendizaje despues de error");
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        return "objeto/edita";
    }

    @RequestMapping(params = "action=actualiza")
    public void actualiza(ActionRequest request, ActionResponse response,
            @Valid ObjetoAprendizaje objeto,
            BindingResult result) throws SystemException, PortalException {
        log.debug("Actualizando objeto de aprendizaje {}", objeto);
        if (result.hasErrors()) {
            log.debug("Hubo algun error en la forma, regresando");
            response.setRenderParameter("action", "editaError");
        }

        User creador = PortalUtil.getUser(request);
        objetoAprendizajeDao.actualiza(objeto, creador);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", objeto.getId().toString());
    }

    @RequestMapping(params = "action=elimina")
    public void elimina(ActionRequest request, @RequestParam Long id) throws PortalException, SystemException {
        log.debug("eliminando objeto de aprendizaje {}", id);

        User creador = PortalUtil.getUser(request);
        objetoAprendizajeDao.elimina(id, creador);
    }

    @RequestMapping(params = "action=agregaContenido")
    public void agregaContenido(ActionRequest request, ActionResponse response, @RequestParam Long objetoId, @RequestParam(required = false) Long[] contenidos) {
        log.debug("Agregando contenido {} a {}", contenidos, objetoId);
        for(String key : request.getParameterMap().keySet()) {
            log.debug("{} : {}", key, request.getParameterMap().get(key));
        }

        objetoAprendizajeDao.agregaContenido(objetoId, contenidos);

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", objetoId.toString());
    }

    @ResourceMapping(value = "actualizaContenido")
    public void actualizaContenido(ResourceRequest request, ResourceResponse response, @RequestParam Long id, @RequestParam(value="contenidos[]", required = false) Long[] contenidos) {
        log.debug("Actualizando contenidos {} para el objeto {}", contenidos, id);

        objetoAprendizajeDao.agregaContenido(id, contenidos);
    }
}
