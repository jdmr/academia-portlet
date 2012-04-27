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
package mx.edu.um.academia.dao;

import java.util.ArrayList;
import java.util.List;
import mx.edu.um.academia.model.Examen;
import mx.edu.um.academia.model.ExamenPregunta;
import mx.edu.um.academia.model.Pregunta;
import mx.edu.um.academia.model.Respuesta;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:context/academia.xml"})
@Transactional
public class ExamenDaoTest {

    private static final Logger log = LoggerFactory.getLogger(ExamenDaoTest.class);
    @Autowired
    private ExamenDao examenDao;
    @Autowired
    private PreguntaDao preguntaDao;
    @Autowired
    private RespuestaDao respuestaDao;
    @Autowired
    private SessionFactory sessionFactory;

    public Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Test
    public void debieraCrearExamen() {
        log.debug("Debiera crear examen");

        log.debug("Creando respuestas");
        List<Respuesta> respuestas = creaRespuestas();

        log.debug("Creando preguntas");
        List<Pregunta> preguntas = creaPreguntas();

        log.debug("Asigna respuestas a preguntas");
        for (int i = 0; i < 10; i++) {
            asignaRespuestas(respuestas, preguntas, i);
            Pregunta pregunta = preguntas.get(i);
            currentSession().refresh(pregunta);
            if (i < 7) {
                assertFalse(pregunta.getEsMultiple());
                assertEquals(1, pregunta.getCorrectas().size());
                assertEquals(3, pregunta.getIncorrectas().size());
            } else {
                assertTrue(pregunta.getEsMultiple());
                assertEquals(2, pregunta.getCorrectas().size());
                assertEquals(2, pregunta.getIncorrectas().size());
            }
        }

        log.debug("Crea examen");
        Examen examen = new Examen("EXAMEN--1", 1l);
        examen = examenDao.crea(examen, null);
        assertNotNull(examen.getId());

        log.debug("Asigna preguntas a examen");

        boolean bandera = true;
        for(Pregunta pregunta : preguntas) {
            if (pregunta.getEsMultiple() && bandera) {
                asignaPreguntas(examen, pregunta, 1, true);
                bandera = false;
            } else {
                asignaPreguntas(examen, pregunta, 1, false);
            }
        }
        
        currentSession().refresh(examen);
        assertEquals(10, examen.getPreguntas().size());
        for(ExamenPregunta examenPregunta : examen.getPreguntas()) {
            log.debug("{} | {}", examenPregunta.getId().getExamen(), examenPregunta.getId().getPregunta());
            log.debug("{}", examenPregunta);
        }
        
        log.debug("Elimina una de las preguntas del examen");
        List<String> nombres = examenDao.quitaPregunta(examen.getId(), preguntas.get(9).getId());
        assertNotNull(nombres);
        assertEquals(2, nombres.size());
        assertEquals("EXAMEN--1", nombres.get(0));
        assertEquals("PREGUNTA-9", nombres.get(1));
        currentSession().refresh(examen);
        assertEquals(9, examen.getPreguntas().size());
        for(ExamenPregunta examenPregunta : examen.getPreguntas()) {
            log.debug("{}", examenPregunta);
        }
        
    }

    private void asignaPreguntas(Examen examen, Pregunta pregunta, Integer puntos, Boolean porPregunta) {
        log.debug("Asignando {} a {}", pregunta, examen);
        
        ExamenPregunta examenPregunta = examenDao.asignaPregunta(examen.getId(), pregunta.getId(), puntos, porPregunta, 1l, null);
        assertNotNull(examenPregunta);
    }

    private void asignaRespuestas(List<Respuesta> respuestas, List<Pregunta> preguntas, int pos) {
        log.debug("Asignando respuestas de pregunta {}", pos);
        if (pos < 7) {
            Long[] correctas = new Long[]{respuestas.get(pos).getId()};
            Long[] incorrectas = new Long[]{
                respuestas.get(pos + 1).getId(),
                respuestas.get(pos + 2).getId(),
                respuestas.get(pos + 3).getId()
            };
            preguntaDao.asignaRespuestas(preguntas.get(pos).getId(), correctas, incorrectas);
        } else {
            Pregunta pregunta = preguntas.get(pos);
            pregunta.setEsMultiple(true);
            preguntaDao.actualiza(pregunta, null);

            Long[] correctas = new Long[]{
                respuestas.get(pos).getId(),
                respuestas.get(pos - 1).getId()
            };
            Long[] incorrectas = new Long[]{
                respuestas.get(pos - 2).getId(),
                respuestas.get(pos - 3).getId()
            };
            preguntaDao.asignaRespuestas(preguntas.get(pos).getId(), correctas, incorrectas);
        }
    }

    private List<Respuesta> creaRespuestas() {
        List<Respuesta> respuestas = new ArrayList<>();
        Respuesta respuesta0 = new Respuesta("RESPUESTA-0", 1l);
        respuesta0 = respuestaDao.crea(respuesta0, null);
        assertNotNull(respuesta0.getId());
        respuestas.add(respuesta0);
        Respuesta respuesta1 = new Respuesta("RESPUESTA-1", 1l);
        respuesta1 = respuestaDao.crea(respuesta1, null);
        assertNotNull(respuesta1.getId());
        respuestas.add(respuesta1);
        Respuesta respuesta2 = new Respuesta("RESPUESTA-2", 1l);
        respuestaDao.crea(respuesta2, null);
        assertNotNull(respuesta2.getId());
        respuestas.add(respuesta2);
        Respuesta respuesta3 = new Respuesta("RESPUESTA-3", 1l);
        respuestaDao.crea(respuesta3, null);
        assertNotNull(respuesta3.getId());
        respuestas.add(respuesta3);
        Respuesta respuesta4 = new Respuesta("RESPUESTA-4", 1l);
        respuestaDao.crea(respuesta4, null);
        assertNotNull(respuesta4.getId());
        respuestas.add(respuesta4);
        Respuesta respuesta5 = new Respuesta("RESPUESTA-5", 1l);
        respuestaDao.crea(respuesta5, null);
        assertNotNull(respuesta5.getId());
        respuestas.add(respuesta5);
        Respuesta respuesta6 = new Respuesta("RESPUESTA-6", 1l);
        respuestaDao.crea(respuesta6, null);
        assertNotNull(respuesta6.getId());
        respuestas.add(respuesta6);
        Respuesta respuesta7 = new Respuesta("RESPUESTA-7", 1l);
        respuestaDao.crea(respuesta7, null);
        assertNotNull(respuesta7.getId());
        respuestas.add(respuesta7);
        Respuesta respuesta8 = new Respuesta("RESPUESTA-8", 1l);
        respuestaDao.crea(respuesta8, null);
        assertNotNull(respuesta8.getId());
        respuestas.add(respuesta8);
        Respuesta respuesta9 = new Respuesta("RESPUESTA-9", 1l);
        respuestaDao.crea(respuesta9, null);
        assertNotNull(respuesta9.getId());
        respuestas.add(respuesta9);
        return respuestas;
    }

    public List<Pregunta> creaPreguntas() {
        log.debug("Creando Preguntas");
        List<Pregunta> preguntas = new ArrayList<>();
        Pregunta pregunta0 = new Pregunta("PREGUNTA-0", 1l);
        pregunta0 = preguntaDao.crea(pregunta0, null);
        assertNotNull(pregunta0.getId());
        preguntas.add(pregunta0);
        Pregunta pregunta1 = new Pregunta("PREGUNTA-1", 1l);
        pregunta1 = preguntaDao.crea(pregunta1, null);
        assertNotNull(pregunta1.getId());
        preguntas.add(pregunta1);
        Pregunta pregunta2 = new Pregunta("PREGUNTA-2", 1l);
        preguntaDao.crea(pregunta2, null);
        assertNotNull(pregunta2.getId());
        preguntas.add(pregunta2);
        Pregunta pregunta3 = new Pregunta("PREGUNTA-3", 1l);
        preguntaDao.crea(pregunta3, null);
        assertNotNull(pregunta3.getId());
        preguntas.add(pregunta3);
        Pregunta pregunta4 = new Pregunta("PREGUNTA-4", 1l);
        preguntaDao.crea(pregunta4, null);
        assertNotNull(pregunta4.getId());
        preguntas.add(pregunta4);
        Pregunta pregunta5 = new Pregunta("PREGUNTA-5", 1l);
        preguntaDao.crea(pregunta5, null);
        assertNotNull(pregunta5.getId());
        preguntas.add(pregunta5);
        Pregunta pregunta6 = new Pregunta("PREGUNTA-6", 1l);
        preguntaDao.crea(pregunta6, null);
        assertNotNull(pregunta6.getId());
        preguntas.add(pregunta6);
        Pregunta pregunta7 = new Pregunta("PREGUNTA-7", 1l);
        preguntaDao.crea(pregunta7, null);
        assertNotNull(pregunta7.getId());
        preguntas.add(pregunta7);
        Pregunta pregunta8 = new Pregunta("PREGUNTA-8", 1l);
        preguntaDao.crea(pregunta8, null);
        assertNotNull(pregunta8.getId());
        preguntas.add(pregunta8);
        Pregunta pregunta9 = new Pregunta("PREGUNTA-9", 1l);
        preguntaDao.crea(pregunta9, null);
        assertNotNull(pregunta9.getId());
        preguntas.add(pregunta9);

        return preguntas;
    }
}
