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

import java.util.*;
import mx.edu.um.academia.model.ObjetoAprendizaje;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import static org.junit.Assert.*;
import org.junit.*;
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
public class ObjetoAprendizajeDaoTest {

    private static final Logger log = LoggerFactory.getLogger(ObjetoAprendizajeDaoTest.class);
    @Autowired
    private ObjetoAprendizajeDao instance;
    @Autowired
    private SessionFactory sessionFactory;

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    public ObjetoAprendizajeDaoTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of actualiza method, of class ObjetoAprendizajeDao.
     */
    @Test
    public void testActualiza() {
        log.debug("actualiza");
        ObjetoAprendizaje objetoAprendizaje = creaObjetoAprendizaje("TEST--1");
        assertNotNull(objetoAprendizaje.getId());

        objetoAprendizaje.setNombre("TEST--2");
        ObjetoAprendizaje result = instance.actualiza(objetoAprendizaje, null);
        assertEquals("TEST--2", result.getNombre());
    }

    /**
     * Test of crea method, of class ObjetoAprendizajeDao.
     */
    @Test
    public void testCrea() {
        log.debug("crea");
        ObjetoAprendizaje objetoAprendizaje = new ObjetoAprendizaje("TEST--1", "TEST--1", "TEST--1", 1l);
        ObjetoAprendizaje result = instance.crea(objetoAprendizaje, null);
        assertNotNull(result.getId());
        assertNotNull(result.getVersion());
    }

    /**
     * Test of elimina method, of class ObjetoAprendizajeDao.
     */
    @Test
    public void testElimina() {
        log.debug("elimina");
        ObjetoAprendizaje objetoAprendizaje = creaObjetoAprendizaje("TEST--1");
        assertNotNull(objetoAprendizaje.getId());
        Long objetoAprendizajeId = objetoAprendizaje.getId();
        String expResult = "TEST--1";
        String result = instance.elimina(objetoAprendizajeId, null);
        assertEquals(expResult, result);
    }

    /**
     * Test of lista method, of class ObjetoAprendizajeDao.
     */
    @Test
    public void testLista() {
        log.debug("lista");
        for (int i = 0; i < 20; i++) {
            ObjetoAprendizaje objetoAprendizaje = creaObjetoAprendizaje("TEST--" + i);
            assertNotNull(objetoAprendizaje.getId());
        }
        Map<String, Object> params = new HashMap<>();
        Set<Long> comunidades = new HashSet<>();
        comunidades.add(1L);
        params.put("comunidades", comunidades);
        Map<String, Object> result = instance.lista(params);
        assertNotNull(result);
        assertNotNull(result.get("objetos"));
        assertNotNull(result.get("cantidad"));
        List<ObjetoAprendizaje> objetoAprendizajes = (List<ObjetoAprendizaje>) result.get("objetos");
        Long cantidad = (Long) result.get("cantidad");
        assertEquals(10, objetoAprendizajes.size());
        assertTrue(20 <= cantidad);
    }

    /**
     * Test of obtiene method, of class ObjetoAprendizajeDao.
     */
    @Test
    public void testObtiene() {
        log.debug("obtiene");
        ObjetoAprendizaje objetoAprendizaje = creaObjetoAprendizaje("TEST--1");
        assertNotNull(objetoAprendizaje.getId());
        Long objetoAprendizajeId = objetoAprendizaje.getId();
        ObjetoAprendizaje result = instance.obtiene(objetoAprendizajeId);
        assertEquals("TEST--1", result.getNombre());
    }

    private ObjetoAprendizaje creaObjetoAprendizaje(String nombre) {
        ObjetoAprendizaje objetoAprendizaje = new ObjetoAprendizaje(nombre, nombre, nombre, 1l);
        Date fecha = new Date();
        objetoAprendizaje.setFechaCreacion(fecha);
        objetoAprendizaje.setFechaModificacion(fecha);
        objetoAprendizaje.setCreador("tests");
        currentSession().save(objetoAprendizaje);
        currentSession().flush();
        return objetoAprendizaje;
    }
}
