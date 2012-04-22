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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mx.edu.um.academia.model.Contenido;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.*;
import static org.junit.Assert.*;
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
public class ContenidoDaoTest {

    private static final Logger log = LoggerFactory.getLogger(ContenidoDaoTest.class);
    @Autowired
    private ContenidoDao instance;
    @Autowired
    private SessionFactory sessionFactory;

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    public ContenidoDaoTest() {
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
     * Test of actualiza method, of class ContenidoDao.
     */
    @Test
    public void testActualiza() {
        log.debug("actualiza");
        Contenido contenido = creaContenido("TEST--1");
        assertNotNull(contenido.getId());

        contenido.setNombre("TEST--2");
        Contenido result = instance.actualiza(contenido, null);
        assertEquals("TEST--2", result.getNombre());
    }

    /**
     * Test of crea method, of class ContenidoDao.
     */
    @Test
    public void testCrea() {
        log.debug("crea");
        Contenido contenido = new Contenido("TEST--1", "TEST--1", 1l);
        contenido.setComunidadId(1l);
        Contenido result = instance.crea(contenido, null);
        assertNotNull(result.getId());
        assertNotNull(result.getVersion());
    }

    /**
     * Test of elimina method, of class ContenidoDao.
     */
    @Test
    public void testElimina() {
        log.debug("elimina");
        Contenido contenido = creaContenido("TEST--1");
        assertNotNull(contenido.getId());
        Long contenidoId = contenido.getId();
        String expResult = "TEST--1";
        String result = instance.elimina(contenidoId, null);
        assertEquals(expResult, result);
    }

    /**
     * Test of lista method, of class ContenidoDao.
     */
    @Test
    public void testLista() {
        log.debug("lista");
        for (int i = 0; i < 20; i++) {
            Contenido contenido = creaContenido("TEST--" + i);
            assertNotNull(contenido.getId());
        }
        Map<String, Object> params = new HashMap<>();
        Set<Long> comunidades = new HashSet<>();
        comunidades.add(1L);
        params.put("comunidades", comunidades);
        Map<String, Object> result = instance.lista(params);
        assertNotNull(result);
        assertNotNull(result.get("contenidos"));
        assertNotNull(result.get("cantidad"));
        List<Contenido> contenidos = (List<Contenido>) result.get("contenidos");
        Long cantidad = (Long) result.get("cantidad");
        assertEquals(10, contenidos.size());
        assertTrue(20 <= cantidad);
    }

    /**
     * Test of obtiene method, of class ContenidoDao.
     */
    @Test
    public void testObtiene() {
        log.debug("obtiene");
        Contenido contenido = creaContenido("TEST--1");
        assertNotNull(contenido.getId());
        Long contenidoId = contenido.getId();
        Contenido result = instance.obtiene(contenidoId);
        assertEquals("TEST--1", result.getNombre());
    }

    private Contenido creaContenido(String nombre) {
        Contenido contenido = new Contenido(nombre, nombre, 1l);
        Date fecha = new Date();
        contenido.setFechaCreacion(fecha);
        contenido.setFechaModificacion(fecha);
        contenido.setComunidadId(1l);
        contenido.setCreador("tests");
        currentSession().save(contenido);
        currentSession().flush();
        return contenido;
    }
}
