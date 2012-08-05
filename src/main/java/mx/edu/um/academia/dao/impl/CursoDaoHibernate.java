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
package mx.edu.um.academia.dao.impl;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import java.util.*;
import mx.edu.um.academia.dao.CursoDao;
import mx.edu.um.academia.dao.ExamenDao;
import mx.edu.um.academia.model.*;
import mx.edu.um.academia.utils.Constantes;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
@Repository
@Transactional
public class CursoDaoHibernate implements CursoDao {

    private static final Logger log = LoggerFactory.getLogger(CursoDaoHibernate.class);
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private ExamenDao examenDao;

    public CursoDaoHibernate() {
        log.info("Nueva instancia del dao de cursos");
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Map<String, Object> lista(Map<String, Object> params) {
        log.debug("Buscando lista de cursos con params {}", params);
        if (params == null) {
            params = new HashMap<>();
        }

        if (!params.containsKey("max") || params.get("max") == null) {
            params.put("max", 5);
        } else {
            params.put("max", Math.min((Integer) params.get("max"), 100));
        }

        if (params.containsKey("pagina") && params.get("pagina") != null) {
            Long pagina = (Long) params.get("pagina");
            Long offset = (pagina - 1) * (Integer) params.get("max");
            params.put("offset", offset.intValue());
        }

        if (!params.containsKey("offset") || params.get("offset") == null) {
            params.put("offset", 0);
        }

        Criteria criteria = currentSession().createCriteria(Curso.class);
        Criteria countCriteria = currentSession().createCriteria(Curso.class);

        if (params.containsKey("comunidades")) {
            criteria.add(Restrictions.in("comunidadId", (Set<Integer>) params.get("comunidades")));
            countCriteria.add(Restrictions.in("comunidadId", (Set<Integer>) params.get("comunidades")));
        }

        if (params.containsKey("filtro")) {
            String filtro = (String) params.get("filtro");
            Disjunction propiedades = Restrictions.disjunction();
            propiedades.add(Restrictions.ilike("codigo", filtro, MatchMode.ANYWHERE));
            propiedades.add(Restrictions.ilike("nombre", filtro, MatchMode.ANYWHERE));
            criteria.add(propiedades);
            countCriteria.add(propiedades);
        }

        if (params.containsKey("order")) {
            String campo = (String) params.get("order");
            if (params.get("sort").equals("desc")) {
                criteria.addOrder(Order.desc(campo));
            } else {
                criteria.addOrder(Order.asc(campo));
            }
        }
        criteria.addOrder(Order.desc("fechaModificacion"));

        criteria.setFirstResult((Integer) params.get("offset"));
        criteria.setMaxResults((Integer) params.get("max"));
        params.put("cursos", criteria.list());

        countCriteria.setProjection(Projections.rowCount());
        List cantidades = countCriteria.list();
        if (cantidades != null) {
            params.put("cantidad", (Long) cantidades.get(0));
        } else {
            params.put("cantidad", 0L);
        }

        return params;
    }

    @Override
    public Curso obtiene(Long cursoId) {
        log.debug("Obteniendo curso {}", cursoId);
        return (Curso) currentSession().get(Curso.class, cursoId);
    }

    @Override
    public Curso crea(Curso curso, User creador) {
        log.debug("Creando curso {} por usuario", curso, creador);
        Date fecha = new Date();
        curso.setFechaCreacion(fecha);
        curso.setFechaModificacion(fecha);
        if (creador != null) {
            curso.setCreador(creador.getScreenName());
        } else {
            curso.setCreador("admin");
        }
        currentSession().save(curso);
        return curso;
    }

    @Override
    public Curso actualiza(Curso otro, User creador) {
        log.debug("Actualizando curso {} por usuario {}", otro, creador);
        Curso curso = (Curso) currentSession().get(Curso.class, otro.getId());
        curso.setVersion(otro.getVersion());
        curso.setCodigo(otro.getCodigo());
        curso.setNombre(otro.getNombre());
        curso.setTipo(otro.getTipo());
        curso.setPrecio(otro.getPrecio());
        curso.setComunidadId(otro.getComunidadId());
        if (otro.getIntro() != null) {
            curso.setIntro(otro.getIntro());
        }
        curso.setComercio(otro.getComercio());
        curso.setComercioId(otro.getComercioId());
        curso.setFechaModificacion(new Date());
        if (creador != null) {
            curso.setCreador(creador.getScreenName());
        } else {
            curso.setCreador("admin");
        }
        currentSession().update(curso);
        return curso;
    }

    @Override
    public String elimina(Long cursoId, User creador) {
        log.debug("Eliminando curso {} por usuario {}", cursoId, creador);
        Curso curso = (Curso) currentSession().get(Curso.class, cursoId);
        String nombre = curso.getNombre();
        currentSession().delete(curso);
        return nombre;
    }

    @Override
    public Map<String, Object> objetos(Long id, Set<Long> comunidades) {
        log.debug("Buscando los objetos del curso {}", id);
        Curso curso = (Curso) currentSession().get(Curso.class, id);
        List<ObjetoAprendizaje> objetos = curso.getObjetos();
        log.debug("Lista de seleccionados");
        for (ObjetoAprendizaje objeto : objetos) {
            log.debug("Seleccionado: " + objeto.getNombre());
        }
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("seleccionados", objetos);

        Criteria criteria = currentSession().createCriteria(ObjetoAprendizaje.class);
        criteria.add(Restrictions.in("comunidadId", (Set<Long>) comunidades));
        criteria.addOrder(Order.asc("codigo"));
        log.debug("Lista de disponibles");
        List<ObjetoAprendizaje> disponibles = criteria.list();
        disponibles.removeAll(objetos);
        for (ObjetoAprendizaje objeto : disponibles) {
            log.debug("Disponible: " + objeto.getNombre());
        }
        resultado.put("disponibles", disponibles);
        log.debug("regresando {}", resultado);
        return resultado;
    }

    @Override
    public List<ObjetoAprendizaje> objetos(Long id) {
        log.debug("Buscando los objetos del curso {}", id);
        Curso curso = (Curso) currentSession().get(Curso.class, id);
        List<ObjetoAprendizaje> objetos = curso.getObjetos();
        for (ObjetoAprendizaje objeto : objetos) {
            log.debug("Seleccionado: " + objeto.getNombre());
        }
        return objetos;
    }

    @Override
    public void agregaObjetos(Long cursoId, Long[] objetosArray) {
        log.debug("Agregando objetos {} a curso {}", objetosArray, cursoId);
        Curso curso = (Curso) currentSession().get(Curso.class, cursoId);
        curso.getObjetos().clear();
        for (Long objetoId : objetosArray) {
            curso.getObjetos().add((ObjetoAprendizaje) currentSession().load(ObjetoAprendizaje.class, objetoId));
        }
        log.debug("Actualizando curso {}", curso);
        currentSession().update(curso);
        currentSession().flush();
    }

    @Override
    public Map<String, Object> verContenido(Long cursoId) {
        Curso curso = (Curso) currentSession().get(Curso.class, cursoId);
        List<ObjetoAprendizaje> objetos = curso.getObjetos();
        for (ObjetoAprendizaje objeto : objetos) {
            for (Contenido contenido : objeto.getContenidos()) {
                log.debug("{} : {} : {}", new Object[]{curso.getCodigo(), objeto.getCodigo(), contenido.getCodigo()});
            }
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("objetos", objetos);
        return resultado;
    }

    @Override
    public List<Curso> todos(Set<Long> comunidades) {
        log.debug("Buscando lista de cursos en las comunidades {}", comunidades);
        Criteria criteria = currentSession().createCriteria(Curso.class);

        criteria.add(Restrictions.in("comunidadId", comunidades));

        criteria.addOrder(Order.desc("codigo"));

        return criteria.list();
    }

    @Override
    public PortletCurso guardaPortlet(Long cursoId, String portletId) {
        Curso curso = (Curso) currentSession().get(Curso.class, cursoId);
        PortletCurso portlet = (PortletCurso) currentSession().get(PortletCurso.class, portletId);
        if (portlet == null) {
            portlet = new PortletCurso(portletId, curso);
            currentSession().save(portlet);
        } else {
            portlet.setCurso(curso);
            currentSession().update(portlet);
        }
        return portlet;
    }

    @Override
    public PortletCurso obtienePortlet(String portletId) {
        return (PortletCurso) currentSession().get(PortletCurso.class, portletId);
    }

    @Override
    public Alumno obtieneAlumno(Long id) {
        return (Alumno) currentSession().get(Alumno.class, id);
    }

    @Override
    public void inscribe(Curso curso, Alumno alumno, Boolean creaUsuario, String estatus) {
        log.debug("Inscribiendo a alumno {} en curso {}", alumno, curso);
        if (creaUsuario) {
            log.debug("Creando alumno primero");
            alumno.setComunidad(curso.getComunidadId());
            currentSession().save(alumno);
        }

        log.debug("Inscribiendo...");
        AlumnoCursoPK pk = new AlumnoCursoPK(alumno, curso);
        AlumnoCurso alumnoCurso = (AlumnoCurso) currentSession().get(AlumnoCurso.class, pk);
        if (alumnoCurso == null) {
            alumnoCurso = new AlumnoCurso(alumno, curso, estatus);
            currentSession().save(alumnoCurso);
        } else {
            alumnoCurso.setEstatus(estatus);
            currentSession().update(alumnoCurso);
        }
        currentSession().flush();
    }

    @Override
    public Boolean estaInscrito(Long cursoId, Long alumnoId) {
        log.debug("Validando si el alumno {} esta inscrito en {}", alumnoId, cursoId);
        Curso curso = (Curso) currentSession().load(Curso.class, cursoId);
        Alumno alumno = (Alumno) currentSession().load(Alumno.class, alumnoId);
        AlumnoCursoPK pk = new AlumnoCursoPK(alumno, curso);
        AlumnoCurso alumnoCurso = (AlumnoCurso) currentSession().get(AlumnoCurso.class, pk);
        boolean resultado = false;
        if (alumnoCurso != null && (Constantes.INSCRITO.equals(alumnoCurso.getEstatus()) || Constantes.CONCLUIDO.equals(alumnoCurso.getEstatus()))) {
            resultado = true;
        }
        return resultado;
    }

    @Override
    public List<ObjetoAprendizaje> objetosAlumno(Long cursoId, Long alumnoId, ThemeDisplay themeDisplay) {
        log.debug("Obteniendo objetos de aprendizaje del curso {} para el alumno {}", cursoId, alumnoId);

        Curso curso = (Curso) currentSession().get(Curso.class, cursoId);
        log.debug("{}", curso);
        Alumno alumno = (Alumno) currentSession().load(Alumno.class, alumnoId);
        log.debug("{}", alumno);
        List<ObjetoAprendizaje> objetos = curso.getObjetos();
        boolean noAsignado = true;
        boolean activo = false;
        for (ObjetoAprendizaje objeto : objetos) {
            boolean bandera = true;
            for (Contenido contenido : objeto.getContenidos()) {
                log.debug("Cargando contenido {} del objeto {} : activo : {}", new Object[]{contenido, objeto, contenido.getActivo()});
                AlumnoContenidoPK pk = new AlumnoContenidoPK(alumno, contenido);
                AlumnoContenido alumnoContenido = (AlumnoContenido) currentSession().get(AlumnoContenido.class, pk);
                if (alumnoContenido == null) {
                    alumnoContenido = new AlumnoContenido(alumno, contenido);
                    currentSession().save(alumnoContenido);
                    currentSession().flush();
                }
                log.debug("Buscando {} : {}", bandera, alumnoContenido.getTerminado());
                if (bandera && alumnoContenido.getTerminado() == null && !activo) {
                    this.asignaContenido(cursoId, alumnoContenido, contenido, themeDisplay);
                    log.debug("Activando a {}", contenido.getNombre());
                    contenido.setActivo(bandera);
                    activo = true;
                    alumnoContenido.setIniciado(new Date());
                    currentSession().update(alumnoContenido);
                    currentSession().flush();
                    bandera = false;
                    noAsignado = false;
                }
                log.debug("Asignando el contenido {} : activo : {}", contenido.getNombre(), contenido.getActivo());
                contenido.setAlumno(alumnoContenido);
            }
        }
        if (noAsignado) {
            log.debug("No asignado >> asignando");
            for (ObjetoAprendizaje objeto : objetos) {
                boolean bandera = true;
                for (Contenido contenido : objeto.getContenidos()) {
                    log.debug("Cargando contenido {} del objeto {}", contenido, objeto);
                    AlumnoContenidoPK pk = new AlumnoContenidoPK(alumno, contenido);
                    AlumnoContenido alumnoContenido = (AlumnoContenido) currentSession().get(AlumnoContenido.class, pk);
                    if (alumnoContenido == null) {
                        alumnoContenido = new AlumnoContenido(alumno, contenido);
                        currentSession().save(alumnoContenido);
                        currentSession().flush();
                    }
                    if (bandera && !activo) {
                        this.asignaContenido(cursoId, alumnoContenido, contenido, themeDisplay);
                        log.debug("Activando a {}", contenido.getNombre());
                        contenido.setActivo(true);
                        activo = true;
                        alumnoContenido.setIniciado(new Date());
                        currentSession().update(alumnoContenido);
                        currentSession().flush();
                        bandera = false;
                    }
                    contenido.setAlumno(alumnoContenido);
                }
            }
        }
        return objetos;
    }

    @Override
    public List<ObjetoAprendizaje> objetosAlumno(Long cursoId, Long contenidoId, Long alumnoId, ThemeDisplay themeDisplay) {
        log.debug("Obteniendo objetos de aprendizaje del curso {} para el alumno {}", cursoId, alumnoId);

        Curso curso = (Curso) currentSession().get(Curso.class, cursoId);
        log.debug("{}", curso);
        Alumno alumno = (Alumno) currentSession().load(Alumno.class, alumnoId);
        log.debug("{}", alumno);
        List<ObjetoAprendizaje> objetos = curso.getObjetos();
        boolean terminado = true;
        boolean noAsignado = true;
        boolean activo = false;
        for (ObjetoAprendizaje objeto : objetos) {
            for (Contenido contenido : objeto.getContenidos()) {
                log.debug("Cargando contenido {} del objeto {}", contenido, objeto);
                AlumnoContenidoPK pk = new AlumnoContenidoPK(alumno, contenido);
                AlumnoContenido alumnoContenido = (AlumnoContenido) currentSession().get(AlumnoContenido.class, pk);
                if (alumnoContenido == null) {
                    alumnoContenido = new AlumnoContenido(alumno, contenido);
                    currentSession().save(alumnoContenido);
                    currentSession().flush();
                }
                if (contenidoId == contenido.getId() && terminado) {
                    this.asignaContenido(cursoId, alumnoContenido, contenido, themeDisplay);
                    contenido.setActivo(true);
                    noAsignado = false;
                    activo = true;
                    log.debug("Validando si ha sido iniciado {}", alumnoContenido.getIniciado());
                    if (alumnoContenido.getIniciado() == null) {
                        alumnoContenido.setIniciado(new Date());
                        currentSession().update(alumnoContenido);
                        currentSession().flush();
                    }
                }
                if (alumnoContenido.getTerminado() == null) {
                    terminado = false;
                }
                contenido.setAlumno(alumnoContenido);
            }
        }
        if (noAsignado) {
            for (ObjetoAprendizaje objeto : objetos) {
                boolean bandera = true;
                for (Contenido contenido : objeto.getContenidos()) {
                    AlumnoContenidoPK pk = new AlumnoContenidoPK(alumno, contenido);
                    AlumnoContenido alumnoContenido = (AlumnoContenido) currentSession().get(AlumnoContenido.class, pk);
                    if (alumnoContenido == null) {
                        alumnoContenido = new AlumnoContenido(alumno, contenido);
                        currentSession().save(alumnoContenido);
                        currentSession().flush();
                    }
                    if (bandera && alumnoContenido.getTerminado() == null && !activo) {
                        this.asignaContenido(cursoId, alumnoContenido, contenido, themeDisplay);
                        contenido.setActivo(bandera);
                        bandera = false;
                        activo = true;
                        noAsignado = false;
                        if (alumnoContenido.getIniciado() == null) {
                            alumnoContenido.setIniciado(new Date());
                            currentSession().update(alumnoContenido);
                            currentSession().flush();
                        }
                    }
                    contenido.setAlumno(alumnoContenido);
                }
            }
        }
        if (noAsignado) {
            for (ObjetoAprendizaje objeto : objetos) {
                boolean bandera = true;
                for (Contenido contenido : objeto.getContenidos()) {
                    AlumnoContenidoPK pk = new AlumnoContenidoPK(alumno, contenido);
                    AlumnoContenido alumnoContenido = (AlumnoContenido) currentSession().get(AlumnoContenido.class, pk);
                    if (alumnoContenido == null) {
                        alumnoContenido = new AlumnoContenido(alumno, contenido);
                        currentSession().save(alumnoContenido);
                        currentSession().flush();
                    }
                    if (bandera && !activo) {
                        this.asignaContenido(cursoId, alumnoContenido, contenido, themeDisplay);
                        contenido.setActivo(bandera);
                        alumnoContenido.setIniciado(new Date());
                        currentSession().update(alumnoContenido);
                        currentSession().flush();
                        bandera = false;
                        activo = true;
                    }
                    contenido.setAlumno(alumnoContenido);
                }
            }
        }
        return objetos;
    }

    @Override
    public List<ObjetoAprendizaje> objetosAlumnoSiguiente(Long cursoId, Long alumnoId, ThemeDisplay themeDisplay) {
        log.debug("Obteniendo siguiente contenido curso {} para el alumno {}", cursoId, alumnoId);

        Curso curso = (Curso) currentSession().get(Curso.class, cursoId);
        log.debug("{}", curso);
        Alumno alumno = (Alumno) currentSession().load(Alumno.class, alumnoId);
        log.debug("{}", alumno);
        List<ObjetoAprendizaje> objetos = curso.getObjetos();
        boolean noAsignado = true;
        boolean activo = false;
        for (ObjetoAprendizaje objeto : objetos) {
            boolean bandera = true;
            boolean bandera2 = false;
            for (Contenido contenido : objeto.getContenidos()) {
                log.debug("Cargando contenido {} del objeto {}", contenido, objeto);
                AlumnoContenidoPK pk = new AlumnoContenidoPK(alumno, contenido);
                AlumnoContenido alumnoContenido = (AlumnoContenido) currentSession().get(AlumnoContenido.class, pk);
                if (alumnoContenido == null) {
                    alumnoContenido = new AlumnoContenido(alumno, contenido);
                    currentSession().save(alumnoContenido);
                    currentSession().flush();
                }
                if (bandera && alumnoContenido.getTerminado() == null && !activo) {
                    if (bandera2) {
                        this.asignaContenido(cursoId, alumnoContenido, contenido, themeDisplay);
                        contenido.setActivo(bandera);
                        activo = true;
                        alumnoContenido.setIniciado(new Date());
                        currentSession().update(alumnoContenido);
                        currentSession().flush();
                        bandera = false;
                        bandera2 = false;
                        noAsignado = false;
                    } else {
                        alumnoContenido.setTerminado(new Date());
                        currentSession().update(alumnoContenido);
                        currentSession().flush();
                        bandera2 = true;
                    }
                }
                contenido.setAlumno(alumnoContenido);
            }
        }
        if (noAsignado) {
            for (ObjetoAprendizaje objeto : objetos) {
                boolean bandera = true;
                for (Contenido contenido : objeto.getContenidos()) {
                    AlumnoContenidoPK pk = new AlumnoContenidoPK(alumno, contenido);
                    AlumnoContenido alumnoContenido = (AlumnoContenido) currentSession().get(AlumnoContenido.class, pk);
                    if (alumnoContenido == null) {
                        alumnoContenido = new AlumnoContenido(alumno, contenido);
                        currentSession().save(alumnoContenido);
                        currentSession().flush();
                    }
                    if (bandera && !activo) {
                        this.asignaContenido(cursoId, alumnoContenido, contenido, themeDisplay);
                        contenido.setActivo(bandera);
                        activo = true;
                        if (alumnoContenido.getIniciado() == null) {
                            alumnoContenido.setIniciado(new Date());
                        } else {
                            AlumnoCursoPK pk2 = new AlumnoCursoPK(alumno, curso);
                            AlumnoCurso alumnoCurso = (AlumnoCurso) currentSession().get(AlumnoCurso.class, pk2);
                            alumnoCurso.setEstatus(Constantes.CONCLUIDO);
                            alumnoCurso.setFechaConclusion(new Date());
                            currentSession().update(alumnoCurso);
                            currentSession().flush();
                        }
                        currentSession().update(alumnoContenido);
                        currentSession().flush();
                        bandera = false;
                    }
                    contenido.setAlumno(alumnoContenido);
                }
            }
        }
        return objetos;
    }

    @Override
    public List<AlumnoCurso> alumnos(Long cursoId) {
        log.debug("Lista de alumnos del curso {}", cursoId);

        Query query = currentSession().createQuery("select a from AlumnoCurso a where a.id.curso.id = :cursoId");
        query.setLong("cursoId", cursoId);
        return query.list();
    }

    @Override
    public void inscribe(Long cursoId, Long alumnoId) {
        log.debug("Inscribe alumno {} a curso {}", alumnoId, cursoId);

        Curso curso = (Curso) currentSession().get(Curso.class, cursoId);
        Alumno alumno = (Alumno) currentSession().get(Alumno.class, alumnoId);
        if (alumno == null) {
            try {
                User usuario = UserLocalServiceUtil.getUser(alumnoId);
                alumno = new Alumno(usuario);
                alumno.setComunidad(curso.getComunidadId());
                currentSession().save(alumno);
                currentSession().flush();
            } catch (PortalException | SystemException ex) {
                log.error("No se pudo obtener el usuario", ex);
            }
        }
        AlumnoCursoPK pk = new AlumnoCursoPK(alumno, curso);
        AlumnoCurso alumnoCurso = (AlumnoCurso) currentSession().get(AlumnoCurso.class, pk);
        if (alumnoCurso == null) {
            alumnoCurso = new AlumnoCurso(pk, Constantes.INSCRITO);
            currentSession().save(alumnoCurso);
        } else {
            alumnoCurso.setEstatus(Constantes.INSCRITO);
            alumnoCurso.setFecha(new Date());
            currentSession().update(alumnoCurso);
        }
    }

    @Override
    public Map<String, Object> alumnos(Map<String, Object> params) {
        Long cursoId = (Long) params.get("cursoId");
        Query query = currentSession().createQuery("select a from AlumnoCurso a where a.id.curso.id = :cursoId");
        query.setLong("cursoId", cursoId);
        params.put("alumnos", query.list());

        Curso curso = (Curso) currentSession().get(Curso.class, cursoId);
        params.put("curso", curso);
        try {
            log.debug("Buscando usuarios en la empresa {}", params.get("companyId"));
            List<User> usuarios = UserLocalServiceUtil.getCompanyUsers((Long) params.get("companyId"), QueryUtil.ALL_POS, QueryUtil.ALL_POS);
            for (User user : usuarios) {
                if (user.isDefaultUser()) {
                    usuarios.remove(user);
                    break;
                }
            }
            params.put("disponibles", usuarios);
        } catch (SystemException e) {
            log.error("No se pudo obtener lista de usuarios", e);
        }

        return params;
    }

    private void asignaContenido(Long cursoId, AlumnoContenido alumnoContenido, Contenido contenido, ThemeDisplay themeDisplay) {
        try {
            JournalArticle ja;
            switch (contenido.getTipo()) {
                case Constantes.ARTICULATE:
                    StringBuilder sb = new StringBuilder();
                    sb.append("<iframe src='/academia-portlet");
                    sb.append("/contenido/player.html?contenidoId=").append(contenido.getId());
                    sb.append("&cursoId=").append(cursoId);
                    sb.append("&userId=").append(themeDisplay.getUserId());
                    sb.append("&admin=true");
                    sb.append("' style='width:100%;height:600px;'></iframe>");
                    contenido.setTexto(sb.toString());
                    break;
                case Constantes.TEXTO:
                    ja = JournalArticleLocalServiceUtil.getArticle(contenido.getContenidoId());
                    if (ja != null) {
                        String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                        contenido.setTexto(texto);
                    }
                    break;
                case Constantes.VIDEO:
                    log.debug("Buscando el video con el id {}", contenido.getContenidoId());
                    DLFileEntry fileEntry = DLFileEntryLocalServiceUtil.getDLFileEntry(contenido.getContenidoId());
                    if (fileEntry != null) {
                        StringBuilder videoLink = new StringBuilder();
                        videoLink.append("/documents/");
                        videoLink.append(fileEntry.getGroupId());
                        videoLink.append("/");
                        videoLink.append(fileEntry.getFolderId());
                        videoLink.append("/");
                        videoLink.append(fileEntry.getTitle());
                        contenido.setTexto(videoLink.toString());
                    }
                    break;
                case Constantes.EXAMEN:
                    Examen examen = contenido.getExamen();
                    if (examen.getContenido() != null) {
                        ja = JournalArticleLocalServiceUtil.getArticle(examen.getContenido());
                        if (ja != null) {
                            String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                            contenido.setTexto(texto);
                        }
                    }

                    List<Pregunta> preguntas = new ArrayList<>();
                    for (Pregunta pregunta : examenDao.preguntas(examen.getId())) {
                        for (Respuesta respuesta : pregunta.getRespuestas()) {
                            ja = JournalArticleLocalServiceUtil.getArticle(respuesta.getContenido());
                            if (ja != null) {
                                String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                                respuesta.setTexto(texto);
                            }
                        }
                        ja = JournalArticleLocalServiceUtil.getArticle(pregunta.getContenido());
                        if (ja != null) {
                            String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                            pregunta.setTexto(texto);
                        }
                        preguntas.add(pregunta);
                    }
                    if (preguntas.size() > 0) {
                        for (Pregunta pregunta : preguntas) {
                            log.debug("{} ||| {}", pregunta, pregunta.getTexto());
                        }
                        examen.setOtrasPreguntas(preguntas);
                    }
                    break;
            }
            log.debug("Validando si ha sido iniciado {}", alumnoContenido.getIniciado());
            if (alumnoContenido.getIniciado() == null) {
                alumnoContenido.setIniciado(new Date());
                currentSession().update(alumnoContenido);
                currentSession().flush();
            }
        } catch (PortalException | SystemException e) {
            log.error("No se pudo obtener el texto del contenido", e);
        }
    }

    @Override
    public Examen obtieneExamen(Long examenId) {
        Examen examen = (Examen) currentSession().get(Examen.class, examenId);
        return examen;
    }

    @Override
    public Map<String, Object> califica(Map<String, String[]> params, ThemeDisplay themeDisplay, User usuario) {
        try {
            Examen examen = (Examen) currentSession().get(Examen.class, new Long(params.get("examenId")[0]));
            Integer totalExamen = 0;
            Integer totalUsuario = 0;
            Set<Pregunta> incorrectas = new LinkedHashSet<>();
            for (ExamenPregunta examenPregunta : examen.getPreguntas()) {
                Pregunta pregunta = examenPregunta.getId().getPregunta();
                log.debug("{}({}:{}) > Multiple : {} || Por pregunta : {}", new Object[]{pregunta.getNombre(), pregunta.getId(), examen.getId(), pregunta.getEsMultiple(), examenPregunta.getPorPregunta()});
                if (pregunta.getEsMultiple() && examenPregunta.getPorPregunta()) {
                    // Cuando puede tener muchas respuestas y los puntos son por pregunta
                    log.debug("ENTRO 1");
                    totalExamen += examenPregunta.getPuntos();
                    String[] respuestas = params.get(pregunta.getId().toString());
                    List<String> correctas = new ArrayList<>();
                    if (respuestas.length == pregunta.getCorrectas().size()) {
                        boolean vaBien = true;
                        for (Respuesta correcta : pregunta.getCorrectas()) {
                            boolean encontre = false;
                            for (String respuesta : respuestas) {
                                if (respuesta.equals(correcta.getId().toString())) {
                                    encontre = true;
                                    correctas.add(respuesta);
                                    break;
                                }
                            }
                            if (!encontre) {
                                vaBien = false;
                            }
                        }
                        if (vaBien) {
                            totalUsuario += examenPregunta.getPuntos();
                        } else {
                            // pon respuesta incorrecta
                            for (String respuestaId : respuestas) {
                                if (!correctas.contains(respuestaId)) {
                                    Respuesta respuesta = (Respuesta) currentSession().get(Respuesta.class, new Long(respuestaId));
                                    JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(respuesta.getContenido());
                                    if (ja != null) {
                                        String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                                        respuesta.setTexto(texto);
                                    }
                                    pregunta.getRespuestas().add(respuesta);
                                }
                            }
                            JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(pregunta.getContenido());
                            if (ja != null) {
                                String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                                pregunta.setTexto(texto);
                            }
                            incorrectas.add(pregunta);
                        }
                    }
                } else {
                    // Cuando puede tener muchas respuestas pero los puntos son por respuesta
                    // Tambien cuando es una sola respuesta la correcta
                    log.debug("ENTRO 2");
                    String[] respuestas = params.get(pregunta.getId().toString());
                    List<String> correctas = new ArrayList<>();
                    if (respuestas.length <= pregunta.getCorrectas().size()) {
                        log.debug("ENTRO 3");
                        respuestasLoop:
                        for (Respuesta correcta : pregunta.getCorrectas()) {
                            log.debug("Sumando {} a {} para el total de puntos del examen", examenPregunta.getPuntos(), totalExamen);
                            totalExamen += examenPregunta.getPuntos();
                            for (String respuesta : respuestas) {
                                if (respuesta.equals(correcta.getId().toString())) {
                                    totalUsuario += examenPregunta.getPuntos();
                                    correctas.add(respuesta);
                                    continue respuestasLoop;
                                }
                            }
                        }
                        if (correctas.size() < pregunta.getCorrectas().size()) {
                            // pon respuesta incorrecta
                            for (String respuestaId : respuestas) {
                                if (!correctas.contains(respuestaId)) {
                                    Respuesta respuesta = (Respuesta) currentSession().get(Respuesta.class, new Long(respuestaId));
                                    JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(respuesta.getContenido());
                                    if (ja != null) {
                                        String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                                        respuesta.setTexto(texto);
                                    }
                                    pregunta.getRespuestas().add(respuesta);
                                }
                            }

                            JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(pregunta.getContenido());
                            if (ja != null) {
                                String texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
                                pregunta.setTexto(texto);
                            }
                            incorrectas.add(pregunta);
                        }
                    }
                }
                log.debug("Pregunta {} : Respuesta {} : Usuario {}", new Object[]{pregunta.getId(), pregunta.getCorrectas(), params.get(pregunta.getId().toString())});
            }

            Map<String, Object> resultados = new HashMap<>();
            resultados.put("examen", examen);
            resultados.put("totalExamen", totalExamen);
            resultados.put("totalUsuario", totalUsuario);
            resultados.put("totales", new String[]{totalUsuario.toString(), totalExamen.toString(), examen.getPuntos().toString()});
            if (examen.getPuntos() != null && totalUsuario < examen.getPuntos()) {
                resultados.put("messageTitle", "desaprobado");
                resultados.put("messageType", "alert-error");
                Long contenidoId = new Long(params.get("contenidoId")[0]);
                Alumno alumno = (Alumno) currentSession().load(Alumno.class, usuario.getUserId());
                Contenido contenido = (Contenido) currentSession().load(Contenido.class, contenidoId);
                AlumnoContenidoPK pk = new AlumnoContenidoPK(alumno, contenido);
                AlumnoContenido alumnoContenido = (AlumnoContenido) currentSession().get(AlumnoContenido.class, pk);
                if (alumnoContenido != null && alumnoContenido.getTerminado() != null) {
                    alumnoContenido.setTerminado(null);
                    currentSession().update(alumnoContenido);
                    currentSession().flush();
                }
            } else {
                resultados.put("messageTitle", "aprobado");
                resultados.put("messageType", "alert-success");
                for (String key : params.keySet()) {
                    log.debug("{} : {}", key, params.get(key));
                }
                Long contenidoId = new Long(params.get("contenidoId")[0]);
                Alumno alumno = (Alumno) currentSession().load(Alumno.class, usuario.getUserId());
                Contenido contenido = (Contenido) currentSession().load(Contenido.class, contenidoId);
                AlumnoContenidoPK pk = new AlumnoContenidoPK(alumno, contenido);
                AlumnoContenido alumnoContenido = (AlumnoContenido) currentSession().get(AlumnoContenido.class, pk);
                if (alumnoContenido != null) {
                    alumnoContenido.setTerminado(new Date());
                    currentSession().update(alumnoContenido);
                    currentSession().flush();
                }
            }
            if (incorrectas.size() > 0) {
                resultados.put("incorrectas", incorrectas);
            }
            return resultados;
        } catch (PortalException | SystemException e) {
            log.error("No se pudo calificar el examen", e);
        }
        return null;
    }

    @Override
    public Boolean haConcluido(Long alumnoId, Long cursoId) {
        Curso curso = (Curso) currentSession().load(Curso.class, cursoId);
        Alumno alumno = (Alumno) currentSession().load(Alumno.class, alumnoId);
        AlumnoCursoPK pk = new AlumnoCursoPK(alumno, curso);
        AlumnoCurso alumnoCurso = (AlumnoCurso) currentSession().get(AlumnoCurso.class, pk);
        boolean resultado = false;
        if (alumnoCurso.getEstatus().equals(Constantes.CONCLUIDO)) {
            resultado = true;
        }
        return resultado;
    }

    @Override
    public List<AlumnoCurso> obtieneCursos(Long alumnoId) {
        log.debug("Buscando los cursos del alumno {}", alumnoId);
        Query query = currentSession().createQuery("select ac from AlumnoCurso ac "
                + "join fetch ac.id.curso "
                + "where ac.id.alumno.id = :alumnoId "
                + "order by fecha desc");
        query.setLong("alumnoId", alumnoId);
        return query.list();
    }
    
    @Override
    public AlumnoCurso obtieneAlumnoCurso(Long alumnoId, Long cursoId) {
        log.debug("Buscando el curso con {} y {}", alumnoId, cursoId);
        Query query = currentSession().createQuery("select ac from AlumnoCurso ac "
                + "join fetch ac.id.alumno "
                + "join fetch ac.id.curso "
                + "where ac.id.alumno.id = :alumnoId "
                + "and ac.id.curso.id = :cursoId");
        query.setLong("alumnoId", alumnoId);
        query.setLong("cursoId", cursoId);
        AlumnoCurso alumnoCurso = (AlumnoCurso) query.uniqueResult();
        alumnoCurso.setUltimoAcceso(new Date());
        currentSession().save(alumnoCurso);
        currentSession().flush();
        
        return alumnoCurso;
    }
}
