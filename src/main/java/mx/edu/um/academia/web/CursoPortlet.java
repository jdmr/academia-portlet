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
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import javax.portlet.*;
import mx.edu.um.academia.dao.CursoDao;
import mx.edu.um.academia.model.Curso;
import mx.edu.um.academia.model.PortletCurso;
import mx.edu.um.academia.utils.ComunidadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
@Controller
public class CursoPortlet extends BaseController {

    @Autowired
    private CursoDao cursoDao;

    public CursoPortlet() {
        log.info("Nueva instancia de Curso Portlet");
    }

    @RequestMapping(value = "VIEW")
    public String ver(RenderRequest request, Model model) throws SystemException, PortalException {
        log.debug("Ver curso");

        PortletCurso portletCurso = cursoDao.obtienePortlet(PortalUtil.getPortletId(request));

        if (portletCurso != null) {
            Curso curso = portletCurso.getCurso();
            if (curso.getIntro() != null) {
                ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
                JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(curso.getIntro());
                if (ja != null) {
                    String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                    model.addAttribute("texto", texto);
                }
            }

        } else {
            log.debug("Preferencias no encontradas");
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
}
