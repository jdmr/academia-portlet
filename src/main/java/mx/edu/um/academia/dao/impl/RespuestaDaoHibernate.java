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
import java.util.*;
import mx.edu.um.academia.dao.RespuestaDao;
import mx.edu.um.academia.model.Pregunta;
import mx.edu.um.academia.model.Respuesta;
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
public class RespuestaDaoHibernate implements RespuestaDao {

    private static final Logger log = LoggerFactory.getLogger(RespuestaDaoHibernate.class);
    @Autowired
    private SessionFactory sessionFactory;

    public RespuestaDaoHibernate() {
        log.info("Nueva instancia del DAO de Respuestas");
    }
    
    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Respuesta actualiza(Respuesta otra, User creador) {
        log.debug("Actualizando respuesta {}", otra);
        Respuesta respuesta = (Respuesta) currentSession().get(Respuesta.class, otra.getId());
        respuesta.setVersion(otra.getVersion());
        respuesta.setNombre(otra.getNombre());
        respuesta.setComunidadId(otra.getComunidadId());
        respuesta.setContenido(otra.getContenido());
        respuesta.setFechaModificacion(new Date());
        if (creador != null) {
            respuesta.setCreador(creador.getScreenName());
        } else {
            respuesta.setCreador("admin");
        }
        currentSession().update(respuesta);
        currentSession().flush();
        return respuesta;
    }

    @Override
    public Respuesta crea(Respuesta respuesta, User creador) {
        log.debug("Creando respuesta {} por usuario {}", respuesta, creador);
        Date fecha = new Date();
        respuesta.setFechaCreacion(fecha);
        respuesta.setFechaModificacion(fecha);
        if (creador != null) {
            respuesta.setCreador(creador.getScreenName());
        } else {
            respuesta.setCreador("admin");
        }
        currentSession().save(respuesta);
        currentSession().flush();
        return respuesta;
    }

    @Override
    public String elimina(Long respuestaId, User creador) {
        log.debug("Eliminando respuesta {}", respuestaId);
        Respuesta respuesta = (Respuesta) currentSession().get(Respuesta.class, respuestaId);
        String nombre = respuesta.getNombre();
        currentSession().delete(respuesta);
        return nombre;
    }

    @Override
    public Map<String, Object> lista(Map<String, Object> params) {
        log.debug("Buscando lista de respuestas con params {}", params);
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

        Criteria criteria = currentSession().createCriteria(Respuesta.class);
        Criteria countCriteria = currentSession().createCriteria(Respuesta.class);

        if (params.containsKey("comunidades")) {
            criteria.add(Restrictions.in("comunidadId", (Set<Integer>) params.get("comunidades")));
            countCriteria.add(Restrictions.in("comunidadId", (Set<Integer>) params.get("comunidades")));
        }

        if (params.containsKey("filtro")) {
            String filtro = (String) params.get("filtro");
            Disjunction propiedades = Restrictions.disjunction();
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
        params.put("respuestas", criteria.list());

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
    public Respuesta obtiene(Long respuestaId) {
        log.debug("Obteniendo respuesta {}", respuestaId);
        Respuesta respuesta = (Respuesta) currentSession().get(Respuesta.class, respuestaId);
        return respuesta;
    }

    @Override
    public Respuesta actualizaContenido(Respuesta otro, User creador) {
        Respuesta respuesta = (Respuesta) currentSession().get(Respuesta.class, otro.getId());
        respuesta.setVersion(otro.getVersion());
        respuesta.setContenido(otro.getContenido());
        respuesta.setFechaModificacion(new Date());
        if (creador != null) {
            respuesta.setCreador(creador.getScreenName());
        } else {
            respuesta.setCreador("admin");
        }
        currentSession().update(respuesta);
        return respuesta;
    }
}
