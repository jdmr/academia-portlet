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

import com.liferay.portal.model.User;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mx.edu.um.academia.dao.CursoDao;
import mx.edu.um.academia.model.Curso;
import org.hibernate.Criteria;
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

        if (!params.containsKey("max")) {
            params.put("max", 10);
        } else {
            params.put("max", Math.min((Integer) params.get("max"), 100));
        }

        Integer max = 0;
        if (params.containsKey("max")) {
            max = (Integer) params.get("max");
        }
        Integer offset = 0;
        if (params.containsKey("offset")) {
            offset = (Integer) params.get("offset");
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

        criteria.setFirstResult(offset);
        criteria.setMaxResults(max);
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
    public Curso actualiza(Curso curso, User creador) {
        log.debug("Actualizando curso {} por usuario {}", curso, creador);
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
}