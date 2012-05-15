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
import mx.edu.um.academia.dao.ExamenDao;
import mx.edu.um.academia.model.*;
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
public class ExamenDaoHibernate implements ExamenDao {

    private static final Logger log = LoggerFactory.getLogger(ExamenDaoHibernate.class);
    @Autowired
    private SessionFactory sessionFactory;

    public ExamenDaoHibernate() {
        log.info("Nueva instancia del DAO de Examenes");
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Examen actualiza(Examen otra, User creador) {
        log.debug("Actualizando examen {}", otra);
        Examen examen = (Examen) currentSession().get(Examen.class, otra.getId());
        examen.setVersion(otra.getVersion());
        examen.setNombre(otra.getNombre());
        examen.setPuntos(otra.getPuntos());
        examen.setComunidadId(otra.getComunidadId());
        examen.setFechaModificacion(new Date());
        if (creador != null) {
            examen.setCreador(creador.getScreenName());
        } else {
            examen.setCreador("admin");
        }
        currentSession().update(examen);
        currentSession().flush();
        return examen;
    }

    @Override
    public Examen crea(Examen examen, User creador) {
        log.debug("Creando examen {} por usuario {}", examen, creador);
        Date fecha = new Date();
        examen.setFechaCreacion(fecha);
        examen.setFechaModificacion(fecha);
        if (creador != null) {
            examen.setCreador(creador.getScreenName());
        } else {
            examen.setCreador("admin");
        }
        currentSession().save(examen);
        currentSession().flush();
        return examen;
    }

    @Override
    public String elimina(Long examenId, User creador) {
        log.debug("Eliminando examen {}", examenId);
        Examen examen = (Examen) currentSession().get(Examen.class, examenId);
        String nombre = examen.getNombre();
        currentSession().delete(examen);
        return nombre;
    }

    @Override
    public Map<String, Object> lista(Map<String, Object> params) {
        log.debug("Buscando lista de examenes con params {}", params);
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

        Criteria criteria = currentSession().createCriteria(Examen.class);
        Criteria countCriteria = currentSession().createCriteria(Examen.class);

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
        params.put("examenes", criteria.list());

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
    public Examen obtiene(Long examenId) {
        log.debug("Obteniendo examen {}", examenId);
        Examen examen = (Examen) currentSession().get(Examen.class, examenId);
        return examen;
    }

    @Override
    public ExamenPregunta asignaPregunta(Long examenId, Long preguntaId, Integer puntos, Boolean porPregunta, Long comunidadId, User creador) {
        log.debug("Asignando pregunta {} a examen {}", preguntaId, examenId);
        Examen examen = (Examen) currentSession().load(Examen.class, examenId);
        Pregunta pregunta = (Pregunta) currentSession().load(Pregunta.class, preguntaId);
        ExamenPregunta examenPregunta = new ExamenPregunta(examen, pregunta, puntos, porPregunta, examen.getComunidadId());
        Date fecha = new Date();
        examenPregunta.setFechaCreacion(fecha);
        examenPregunta.setFechaModificacion(fecha);
        if (creador != null) {
            examenPregunta.setCreador(creador.getScreenName());
        } else {
            examenPregunta.setCreador("admin");
        }
        currentSession().save(examenPregunta);
        currentSession().flush();
        return examenPregunta;
    }

    @Override
    public List<String> quitaPregunta(Long examenId, Long preguntaId) {
        log.debug("Quitando pregunta {} del examen {}", preguntaId, examenId);
        Examen examen = (Examen) currentSession().get(Examen.class, examenId);
        Pregunta pregunta = (Pregunta) currentSession().get(Pregunta.class, preguntaId);
        List<String> nombres = new ArrayList<>();
        nombres.add(examen.getNombre());
        nombres.add(pregunta.getNombre());
        ExamenPreguntaPK pk = new ExamenPreguntaPK(examen, pregunta);
        ExamenPregunta examenPregunta = (ExamenPregunta) currentSession().load(ExamenPregunta.class, pk);
        currentSession().delete(examenPregunta);
        currentSession().flush();
        return nombres;
    }

    @Override
    public Examen actualizaContenido(Examen otro, User creador) {
        Examen examen = (Examen) currentSession().get(Examen.class, otro.getId());
        examen.setVersion(otro.getVersion());
        examen.setContenido(otro.getContenido());
        examen.setFechaModificacion(new Date());
        if (creador != null) {
            examen.setCreador(creador.getScreenName());
        } else {
            examen.setCreador("admin");
        }
        currentSession().update(examen);
        return examen;
    }

    @Override
    public List<Pregunta> preguntas(Long examenId) {
        Examen examen = (Examen) currentSession().get(Examen.class, examenId);
        List<Pregunta> preguntas = new ArrayList<>();
        for(ExamenPregunta examenPregunta : examen.getPreguntas()) {
            Map<Integer, Respuesta> respuestas = new TreeMap<>();
            Pregunta pregunta  = examenPregunta.getId().getPregunta();
            pregunta.setPuntos(examenPregunta.getPuntos());
            pregunta.setPorPregunta(examenPregunta.getPorPregunta());
            for(Respuesta respuesta : pregunta.getCorrectas()) {
                log.debug("Agregando correcta {} a pregunta {}", respuesta, pregunta);
                respuestas.put(new Double(Math.random() * 100000).intValue(), respuesta);
            }
            for(Respuesta respuesta : pregunta.getIncorrectas()) {
                log.debug("Agregando incorrecta {} a pregunta {}", respuesta, pregunta);
                respuestas.put(new Double(Math.random() * 100000).intValue(), respuesta);
            }
            pregunta.getRespuestas().clear();
            pregunta.getRespuestas().addAll(respuestas.values());
            preguntas.add(pregunta);
        }
        return preguntas;
    }

    @Override
    public List<Examen> todos(Set<Long> comunidades) {
        Criteria criteria = currentSession().createCriteria(Examen.class);
        criteria.add(Restrictions.in("comunidadId", comunidades));
        criteria.addOrder(Order.asc("nombre"));
        return criteria.list();
    }
}
