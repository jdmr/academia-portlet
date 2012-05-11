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
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.portlet.*;
import mx.edu.um.academia.dao.CursoDao;
import mx.edu.um.academia.model.*;
import mx.edu.um.academia.utils.ComunidadUtil;
import mx.edu.um.academia.utils.Constantes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
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
    @Autowired
    private ResourceBundleMessageSource messageSource;

    public CursoPortlet() {
        log.info("Nueva instancia de Curso Portlet");
    }

    @RequestMapping(value = "VIEW")
    public String ver(RenderRequest request, Model model) throws SystemException, PortalException {
        log.debug("Ver curso");

        PortletCurso portletCurso = cursoDao.obtienePortlet(PortalUtil.getPortletId(request));

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        if (portletCurso != null) {
            Curso curso = portletCurso.getCurso();
            model.addAttribute("curso", curso);
            User usuario = PortalUtil.getUser(request);
            if (usuario == null) {
                // Necesitamos firmar al usuario
                model.addAttribute("sign_in", Boolean.TRUE);
                model.addAttribute("sign_in_url", themeDisplay.getURLSignIn());

                if (curso.getIntro() != null) {
                    JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(curso.getIntro());
                    if (ja != null) {
                        String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                        model.addAttribute("texto", texto);
                    }
                } else {
                    String texto = messageSource.getMessage("curso.necesita.intro", null, themeDisplay.getLocale());
                    model.addAttribute("texto", texto);
                }

            } else {
                if (cursoDao.estaInscrito(curso.getId(), usuario.getUserId())) {
                    List<ObjetoAprendizaje> objetos = cursoDao.objetosAlumno(curso.getId(), usuario.getUserId(), themeDisplay);
                    cicloObjetos:
                    for (ObjetoAprendizaje objeto : objetos) {
                        log.debug("Viendo contenido de objeto {}", objeto);
                        for (Contenido contenido : objeto.getContenidos()) {
                            log.debug("Contenido : {} : Activo : {}", contenido, contenido.getActivo());
                            if (contenido.getActivo()) {
                                switch(contenido.getTipo()) {
                                    case Constantes.TEXTO :
                                        model.addAttribute("texto", contenido.getTexto());
                                        break;
                                    case Constantes.VIDEO :
                                        model.addAttribute("video", contenido.getTexto());
                                        break;
                                }
                                break cicloObjetos;
                            }
                        }
                    }
                    model.addAttribute("objetos", objetos);
                } else {
                    if (curso.getIntro() != null) {
                        JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(curso.getIntro());
                        if (ja != null) {
                            String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                            model.addAttribute("texto", texto);
                        }
                    } else {
                        String texto = messageSource.getMessage("curso.necesita.intro", null, themeDisplay.getLocale());
                        model.addAttribute("texto", texto);
                    }
                }
            }
        } else {
            log.warn("Preferencias no encontradas");
            model.addAttribute("message", "curso.no.configurado");
        }

        return "curso/ver";
    }

    @RequestMapping(value = "VIEW", params="action=verContenido")
    public String verContenido(RenderRequest request, Model model, @RequestParam Long contenidoId) throws SystemException, PortalException {
        log.debug("Ver contenido {}", contenidoId);
        PortletCurso portletCurso = cursoDao.obtienePortlet(PortalUtil.getPortletId(request));

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        if (portletCurso != null) {
            Curso curso = portletCurso.getCurso();
            model.addAttribute("curso", curso);
            User usuario = PortalUtil.getUser(request);
            if (usuario == null) {
                // Necesitamos firmar al usuario
                model.addAttribute("sign_in", Boolean.TRUE);
                model.addAttribute("sign_in_url", themeDisplay.getURLSignIn());

                if (curso.getIntro() != null) {
                    JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(curso.getIntro());
                    if (ja != null) {
                        String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                        model.addAttribute("texto", texto);
                    }
                } else {
                    String texto = messageSource.getMessage("curso.necesita.intro", null, themeDisplay.getLocale());
                    model.addAttribute("texto", texto);
                }

            } else {
                if (cursoDao.estaInscrito(curso.getId(), usuario.getUserId())) {
                    List<ObjetoAprendizaje> objetos = cursoDao.objetosAlumno(curso.getId(), contenidoId, usuario.getUserId(), themeDisplay);
                    cicloObjetos:
                    for (ObjetoAprendizaje objeto : objetos) {
                        log.debug("Viendo contenido de objeto {}", objeto);
                        for (Contenido contenido : objeto.getContenidos()) {
                            log.debug("Contenido : {} : Activo : {}", contenido, contenido.getActivo());
                            if (contenido.getActivo()) {
                                log.debug("Encontre el contenido activo {} y el texto {}", contenido, contenido.getTexto());
                                switch(contenido.getTipo()) {
                                    case Constantes.TEXTO :
                                        model.addAttribute("texto", contenido.getTexto());
                                        break;
                                    case Constantes.VIDEO :
                                        model.addAttribute("video", contenido.getTexto());
                                        break;
                                }
                                break cicloObjetos;
                            }
                        }
                    }
                    model.addAttribute("objetos", objetos);
                } else {
                    if (curso.getIntro() != null) {
                        JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(curso.getIntro());
                        if (ja != null) {
                            String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                            model.addAttribute("texto", texto);
                        }
                    } else {
                        String texto = messageSource.getMessage("curso.necesita.intro", null, themeDisplay.getLocale());
                        model.addAttribute("texto", texto);
                    }
                }
            }
        } else {
            log.warn("Preferencias no encontradas");
            model.addAttribute("message", "curso.no.configurado");
        }
        
        return "curso/ver";
    }

    @RequestMapping(value = "VIEW", params="action=verSiguiente")
    public String verSiguiente(RenderRequest request, Model model) throws SystemException, PortalException {
        log.debug("Ver siguiente contenido");
        PortletCurso portletCurso = cursoDao.obtienePortlet(PortalUtil.getPortletId(request));

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        if (portletCurso != null) {
            Curso curso = portletCurso.getCurso();
            model.addAttribute("curso", curso);
            User usuario = PortalUtil.getUser(request);
            if (usuario == null) {
                // Necesitamos firmar al usuario
                model.addAttribute("sign_in", Boolean.TRUE);
                model.addAttribute("sign_in_url", themeDisplay.getURLSignIn());

                if (curso.getIntro() != null) {
                    JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(curso.getIntro());
                    if (ja != null) {
                        String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                        model.addAttribute("texto", texto);
                    }
                } else {
                    String texto = messageSource.getMessage("curso.necesita.intro", null, themeDisplay.getLocale());
                    model.addAttribute("texto", texto);
                }

            } else {
                if (cursoDao.estaInscrito(curso.getId(), usuario.getUserId())) {
                    List<ObjetoAprendizaje> objetos = cursoDao.objetosAlumnoSiguiente(curso.getId(), usuario.getUserId(), themeDisplay);
                    cicloObjetos:
                    for (ObjetoAprendizaje objeto : objetos) {
                        log.debug("Viendo contenido de objeto {}", objeto);
                        for (Contenido contenido : objeto.getContenidos()) {
                            log.debug("Contenido : {} : Activo : {}", contenido, contenido.getActivo());
                            if (contenido.getActivo()) {
                                log.debug("Encontre el contenido activo {} y el texto {}", contenido, contenido.getTexto());
                                switch(contenido.getTipo()) {
                                    case Constantes.TEXTO :
                                        model.addAttribute("texto", contenido.getTexto());
                                        break;
                                    case Constantes.VIDEO :
                                        model.addAttribute("video", contenido.getTexto());
                                        break;
                                }
                                break cicloObjetos;
                            }
                        }
                    }
                    model.addAttribute("objetos", objetos);
                } else {
                    if (curso.getIntro() != null) {
                        JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(curso.getIntro());
                        if (ja != null) {
                            String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                            model.addAttribute("texto", texto);
                        }
                    } else {
                        String texto = messageSource.getMessage("curso.necesita.intro", null, themeDisplay.getLocale());
                        model.addAttribute("texto", texto);
                    }
                }
            }
        } else {
            log.warn("Preferencias no encontradas");
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

    @RequestMapping(value = "VIEW", params = "action=inscribeAlumno")
    public void inscribeAlumno(ActionRequest request, ActionResponse response, @RequestParam Long cursoId) throws SystemException, PortalException {
        log.debug("Inscribiendo alumno a curso {}", cursoId);
        for (String key : request.getParameterMap().keySet()) {
            log.debug("{} : {}", key, request.getParameterMap().get(key));
        }

        Curso curso = cursoDao.obtiene(cursoId);
        User usuario = PortalUtil.getUser(request);
        if (usuario != null) {
            Alumno alumno = cursoDao.obtieneAlumno(usuario.getUserId());
            boolean creaUsuario = false;
            if (alumno == null) {
                alumno = new Alumno(usuario);
                creaUsuario = true;
            }
            switch (curso.getTipo()) {
                case Constantes.PATROCINADO:
                    cursoDao.inscribe(curso, alumno, creaUsuario, Constantes.INSCRITO);
                    break;
                case Constantes.PAGADO:
                    cursoDao.inscribe(curso, alumno, creaUsuario, Constantes.PENDIENTE);

                    break;
            }
        }
    }

    @RequestMapping(value = "VIEW", params = "action=pagoAprobado")
    public String pagoAprobado(RenderRequest request, RenderResponse response, @RequestParam Long cursoId) throws SystemException, PortalException {
        log.debug("Pago aprobado para alumno a curso {}", cursoId);
        for (String key : request.getParameterMap().keySet()) {
            log.debug("{} : {}", key, request.getParameterMap().get(key));
        }

        Curso curso = cursoDao.obtiene(cursoId);
        User usuario = PortalUtil.getUser(request);
        if (usuario != null) {
            Alumno alumno = cursoDao.obtieneAlumno(usuario.getUserId());
            boolean creaUsuario = false;
            if (alumno == null) {
                alumno = new Alumno(usuario);
                creaUsuario = true;
            }
            cursoDao.inscribe(curso, alumno, creaUsuario, Constantes.INSCRITO);
            return "curso/inscrito";
        }
        return null;
    }

    @RequestMapping(value = "VIEW", params = "action=pagoDenegado")
    public String pagoDenegado(RenderRequest request, RenderResponse response, @RequestParam Long cursoId) throws SystemException, PortalException {
        log.debug("Alumno no pago curso {}", cursoId);
        for (String key : request.getParameterMap().keySet()) {
            log.debug("{} : {}", key, request.getParameterMap().get(key));
        }

        Curso curso = cursoDao.obtiene(cursoId);
        User usuario = PortalUtil.getUser(request);
        if (usuario != null) {
            Alumno alumno = cursoDao.obtieneAlumno(usuario.getUserId());
            boolean creaUsuario = false;
            if (alumno == null) {
                alumno = new Alumno(usuario);
                creaUsuario = true;
            }
            cursoDao.inscribe(curso, alumno, creaUsuario, Constantes.PENDIENTE);
            return "curso/denegado";
        }
        return null;
    }
}
