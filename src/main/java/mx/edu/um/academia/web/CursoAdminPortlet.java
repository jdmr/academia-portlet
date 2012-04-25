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
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalArticleConstants;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.validation.Valid;
import mx.edu.um.academia.dao.CursoDao;
import mx.edu.um.academia.model.Curso;
import mx.edu.um.academia.utils.ComunidadUtil;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
public class CursoAdminPortlet extends BaseController {

    @Autowired
    private CursoDao cursoDao;
    @Autowired
    private SessionFactory sessionFactory;

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
        return "cursoAdmin/nuevo";
    }

    @RequestMapping(params = "action=nuevoError")
    public String nuevoError(RenderRequest request, Model modelo) throws SystemException, PortalException {
        log.debug("Nuevo curso despues de error");
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
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
    public String ver(RenderRequest request, @RequestParam Long id, Model modelo) {
        log.debug("Mostrando curso {}", id);
        Curso curso = cursoDao.obtiene(id);
        modelo.addAttribute("curso", curso);
        return "cursoAdmin/ver";
    }
    
    @RequestMapping(params = "action=edita")
    public String edita(RenderRequest request, Model modelo, @RequestParam Long id) throws SystemException, PortalException {
        log.debug("Edita objeto de aprendizaje");
        Curso curso = cursoDao.obtiene(id);
        modelo.addAttribute("curso", curso);
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
        return "cursoAdmin/edita";
    }

    @RequestMapping(params = "action=editaError")
    public String editaError(RenderRequest request, Model modelo) throws SystemException, PortalException {
        log.debug("Edita curso despues de error");
        modelo.addAttribute("comunidades", ComunidadUtil.obtieneComunidades(request));
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
    public String intro(RenderRequest request, @RequestParam Long cursoId, Model modelo) {
        log.debug("Intro");
        modelo.addAttribute("cursoId", cursoId);
        return "cursoAdmin/intro";
    }

    @RequestMapping(params = "action=creaIntro")
    public void creaIntro(ActionRequest request, ActionResponse response, @RequestParam Long cursoId, @RequestParam String texto) {
        Curso curso = cursoDao.obtiene(cursoId);

        log.debug("Creando intro");
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='UTF-8'?><root><static-content><![CDATA[");
        sb.append(texto);
        sb.append("]]></static-content></root>");
        texto = sb.toString();

        Transaction tx = null;
        try {
            User creador = PortalUtil.getUser(request);
            ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
            Calendar displayDate;
            if (themeDisplay != null) {
                displayDate = CalendarFactoryUtil.getCalendar(themeDisplay.getTimeZone(), themeDisplay.getLocale());
            } else {
                displayDate = CalendarFactoryUtil.getCalendar();
            }

            tx = currentSession().beginTransaction();
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

            curso.setIntro(article.getId());
            cursoDao.actualiza(curso, creador);
            
            tx.commit();
        } catch (PortalException | SystemException | DataAccessException e) {
            log.error("No se pudo crear la intro", e);
            tx.rollback();
        }

        response.setRenderParameter("action", "ver");
        response.setRenderParameter("id", cursoId.toString());
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }
}
