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
import mx.edu.um.academia.dao.PreguntaDao;
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
public class PreguntaDaoHibernate implements PreguntaDao {

    private static final Logger log = LoggerFactory.getLogger(PreguntaDaoHibernate.class);
    @Autowired
    private SessionFactory sessionFactory;

    public PreguntaDaoHibernate() {
        log.info("Nueva instancia del DAO de Preguntas");
    }
    
    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Pregunta actualiza(Pregunta otra, User creador) {
        log.debug("Actualizando pregunta {}", otra);
        Pregunta pregunta = (Pregunta) currentSession().get(Pregunta.class, otra.getId());
        pregunta.setVersion(otra.getVersion());
        pregunta.setNombre(otra.getNombre());
        pregunta.setComunidadId(otra.getComunidadId());
        pregunta.setContenido(otra.getContenido());
        pregunta.setFechaModificacion(new Date());
        log.debug("{} : {} : {}", new Object[] {pregunta.getEsMultiple(), otra.getEsMultiple(), (pregunta.getEsMultiple() != otra.getEsMultiple())});
        if (pregunta.getEsMultiple() != otra.getEsMultiple()) {
            pregunta.getCorrectas().clear();
            pregunta.getIncorrectas().clear();
            pregunta.setEsMultiple(otra.getEsMultiple());
        }
        if (creador != null) {
            pregunta.setCreador(creador.getScreenName());
        } else {
            pregunta.setCreador("admin");
        }
        currentSession().update(pregunta);
        currentSession().flush();
        return pregunta;
    }

    @Override
    public Pregunta crea(Pregunta pregunta, User creador) {
        log.debug("Creando pregunta {} por usuario {}", pregunta, creador);
        Date fecha = new Date();
        pregunta.setFechaCreacion(fecha);
        pregunta.setFechaModificacion(fecha);
        if (creador != null) {
            pregunta.setCreador(creador.getScreenName());
        } else {
            pregunta.setCreador("admin");
        }
        currentSession().save(pregunta);
        currentSession().flush();
        return pregunta;
    }

    @Override
    public String elimina(Long preguntaId, User creador) {
        log.debug("Eliminando pregunta {}", preguntaId);
        Pregunta pregunta = (Pregunta) currentSession().get(Pregunta.class, preguntaId);
        String nombre = pregunta.getNombre();
        currentSession().delete(pregunta);
        return nombre;
    }

    @Override
    public Map<String, Object> lista(Map<String, Object> params) {
        log.debug("Buscando lista de preguntas con params {}", params);
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

        Criteria criteria = currentSession().createCriteria(Pregunta.class);
        Criteria countCriteria = currentSession().createCriteria(Pregunta.class);

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
        params.put("preguntas", criteria.list());

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
    public Pregunta obtiene(Long preguntaId) {
        log.debug("Obteniendo pregunta {}", preguntaId);
        Pregunta pregunta = (Pregunta) currentSession().get(Pregunta.class, preguntaId);
        return pregunta;
    }

    @Override
    public void asignaRespuestas(Long preguntaId, Long[] correctas, Long[] incorrectas) {
        log.debug("Asignando respuestas {} - {} a pregunta {}", new Object[] {correctas, incorrectas, preguntaId});
        Pregunta pregunta = (Pregunta) currentSession().get(Pregunta.class, preguntaId);
        pregunta.getCorrectas().clear();
        pregunta.getIncorrectas().clear();
        for(Long respuestaId : correctas) {
            pregunta.getCorrectas().add((Respuesta) currentSession().get(Respuesta.class, respuestaId));
        }
        for(Long respuestaId : incorrectas) {
            pregunta.getIncorrectas().add((Respuesta) currentSession().get(Respuesta.class, respuestaId));
        }
        currentSession().update(pregunta);
        currentSession().flush();
    }

    @Override
    public Pregunta actualizaContenido(Pregunta otro, User creador) {
        Pregunta pregunta = (Pregunta) currentSession().get(Pregunta.class, otro.getId());
        pregunta.setVersion(otro.getVersion());
        pregunta.setContenido(otro.getContenido());
        pregunta.setFechaModificacion(new Date());
        if (creador != null) {
            pregunta.setCreador(creador.getScreenName());
        } else {
            pregunta.setCreador("admin");
        }
        currentSession().update(pregunta);
        return pregunta;
    }

    @Override
    public Map<String, Object> respuestas(Long preguntaId, Set<Long> comunidades) {
        Pregunta pregunta = (Pregunta) currentSession().get(Pregunta.class, preguntaId);
        Set<Respuesta> correctas = pregunta.getCorrectas();
        Set<Respuesta> incorrectas = pregunta.getIncorrectas();
        
        Criteria criteria = currentSession().createCriteria(Respuesta.class);
        criteria.add(Restrictions.in("comunidadId", (Set<Long>) comunidades));
        criteria.addOrder(Order.asc("nombre"));
        List<Respuesta> disponibles = criteria.list();
        disponibles.removeAll(correctas);
        disponibles.removeAll(incorrectas);
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("correctas", correctas);
        resultado.put("incorrectas", incorrectas);
        resultado.put("disponibles", disponibles);
        return resultado;
    }
    
    @Override
    public List<Pregunta> todas(Set<Long> comunidades) {
        Criteria criteria = currentSession().createCriteria(Pregunta.class);
        criteria.add(Restrictions.in("comunidadId", (Set<Long>) comunidades));
        criteria.addOrder(Order.asc("nombre"));
        return criteria.list();
    }
    
}
