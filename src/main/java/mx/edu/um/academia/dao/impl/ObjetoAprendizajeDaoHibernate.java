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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import mx.edu.um.academia.dao.ContenidoDao;
import mx.edu.um.academia.dao.ObjetoAprendizajeDao;
import mx.edu.um.academia.model.Contenido;
import mx.edu.um.academia.model.ObjetoAprendizaje;
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
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
@Repository
@Transactional
public class ObjetoAprendizajeDaoHibernate implements ObjetoAprendizajeDao {

    private static final Logger log = LoggerFactory.getLogger(ObjetoAprendizajeDaoHibernate.class);
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private ContenidoDao contenidoDao;

    public ObjetoAprendizajeDaoHibernate() {
        log.info("Nueva instancia de ObjetoAprendizajeDaoHibernate");
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public ObjetoAprendizaje actualiza(ObjetoAprendizaje otro, User creador) {
        log.debug("Actualizando objetoAprendizaje {} por usuario {}", otro, creador);
        ObjetoAprendizaje objetoAprendizaje = (ObjetoAprendizaje) currentSession().get(ObjetoAprendizaje.class, otro.getId());
        objetoAprendizaje.setVersion(otro.getVersion());
        objetoAprendizaje.setCodigo(otro.getCodigo());
        objetoAprendizaje.setNombre(otro.getNombre());
        objetoAprendizaje.setDescripcion(otro.getDescripcion());
        objetoAprendizaje.setComunidadId(otro.getComunidadId());
        objetoAprendizaje.setFechaModificacion(new Date());
        if (creador != null) {
            objetoAprendizaje.setCreador(creador.getScreenName());
        } else {
            objetoAprendizaje.setCreador("admin");
        }
        currentSession().update(objetoAprendizaje);
        return objetoAprendizaje;
    }

    @Override
    public ObjetoAprendizaje crea(ObjetoAprendizaje objetoAprendizaje, MultipartFile archivo, User creador) throws IOException {
        log.debug("Creando ObjetoAprendizaje {} por usuario", objetoAprendizaje, creador);
        Date fecha = new Date();
        Contenido contenido = null;

        if (archivo != null) {
            int size = 1024;
            byte[] buf = new byte[size];
            ZipInputStream zinstream = new ZipInputStream(archivo.getInputStream());
            ZipEntry zentry = zinstream.getNextEntry();
            while (zentry != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("/opt/portal/liferay-portal-6.0.6/data/cursos");
                sb.append("/").append(objetoAprendizaje.getComunidadId());
                sb.append("/").append(objetoAprendizaje.getCodigo());
                sb.append("/").append(zentry.getName());
                String entryName = sb.toString();
                log.debug("Obtuve archivo {}", entryName);
                File file = new File(entryName);
                if (!file.getParentFile().exists()) {
                    boolean creoDirectorios = file.mkdirs();
                    log.debug("Se crearon los directorios: {}", creoDirectorios);
                }

                if (zentry.isDirectory()) {
                    if (!file.exists()) {
                        boolean creoDirectorios = file.mkdirs();
                        log.debug("Se crearon los directorios: {}", creoDirectorios);
                    }
                } else {
                    log.debug("Creando archivo {}", entryName);
                    try (FileOutputStream outstream = new FileOutputStream(file)) {
                        int n;

                        while ((n = zinstream.read(buf, 0, size)) > -1) {
                            outstream.write(buf, 0, n);
                        }
                    }
                }

                if (entryName.endsWith("player.html")) {
                    contenido = new Contenido(objetoAprendizaje.getCodigo(), objetoAprendizaje.getNombre(), null);
                    contenido.setRuta(entryName);
                    contenido.setComunidadId(objetoAprendizaje.getComunidadId());
                    contenido.setTipo(Constantes.ARTICULATE);
                    contenido = contenidoDao.crea(contenido, creador);
                } else if (entryName.endsWith("story.html")) {
                    contenido = new Contenido(objetoAprendizaje.getCodigo(), objetoAprendizaje.getNombre(), null);
                    contenido.setRuta(entryName);
                    contenido.setComunidadId(objetoAprendizaje.getComunidadId());
                    contenido.setTipo(Constantes.STORYLINE);
                    contenido = contenidoDao.crea(contenido, creador);
                }

                zinstream.closeEntry();
                zentry = zinstream.getNextEntry();
            }
        }

        objetoAprendizaje.setFechaCreacion(fecha);
        objetoAprendizaje.setFechaModificacion(fecha);
        if (creador != null) {
            objetoAprendizaje.setCreador(creador.getScreenName());
        } else {
            objetoAprendizaje.setCreador("admin");
        }
        if (contenido != null) {
            List<Contenido> contenidos = new ArrayList<>();
            contenidos.add(contenido);
            objetoAprendizaje.setContenidos(contenidos);
        }
        currentSession().save(objetoAprendizaje);
        return objetoAprendizaje;
    }

    @Override
    public String elimina(Long objetoAprendizajeId, User creador) {
        log.debug("Eliminando objetoAprendizaje {} por usuario {}", objetoAprendizajeId, creador);
        ObjetoAprendizaje objetoAprendizaje = (ObjetoAprendizaje) currentSession().get(ObjetoAprendizaje.class, objetoAprendizajeId);
        String nombre = objetoAprendizaje.getNombre();
        currentSession().delete(objetoAprendizaje);
        return nombre;
    }

    @Override
    public Map<String, Object> lista(Map<String, Object> params) {
        log.debug("Buscando lista de objetoAprendizajes con params {}", params);
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

        Criteria criteria = currentSession().createCriteria(ObjetoAprendizaje.class);
        Criteria countCriteria = currentSession().createCriteria(ObjetoAprendizaje.class);

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
        params.put("objetos", criteria.list());

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
    public ObjetoAprendizaje obtiene(Long objetoAprendizajeId) {
        log.debug("Obteniendo objetoAprendizaje {}", objetoAprendizajeId);
        return (ObjetoAprendizaje) currentSession().get(ObjetoAprendizaje.class, objetoAprendizajeId);
    }

    @Override
    public Map<String, Object> contenidos(Long id, Set<Long> comunidades) {
        log.debug("Buscando los contenidos del objeto de aprendizaje {}", id);
        Query query = currentSession().createQuery("select oa from ObjetoAprendizaje oa inner join fetch oa.contenidos where oa.id = :objetoId");
        query.setLong("objetoId", id);
        Map<String, Object> resultado = new HashMap<>();
        ObjetoAprendizaje objetoAprendizaje = (ObjetoAprendizaje) query.uniqueResult();
        List<Contenido> contenidos = null;
        if (objetoAprendizaje != null) {
            contenidos = objetoAprendizaje.getContenidos();
            log.debug("Lista de seleccionados");
            for (Contenido contenido : contenidos) {
                log.debug("Seleccionado: " + contenido.getNombre());
            }
            resultado.put("seleccionados", contenidos);
        }

        Criteria criteria = currentSession().createCriteria(Contenido.class);
        criteria.add(Restrictions.in("comunidadId", (Set<Long>) comunidades));
        criteria.addOrder(Order.asc("codigo"));
        log.debug("Lista de disponibles");
        List<Contenido> disponibles = criteria.list();
        if (contenidos != null) {
            disponibles.removeAll(contenidos);
        }
        for (Contenido contenido : disponibles) {
            log.debug("Disponible: " + contenido.getNombre());
        }
        resultado.put("disponibles", disponibles);
        log.debug("regresando {}", resultado);
        return resultado;
    }

    @Override
    public void agregaContenido(Long objetoId, Long[] contenidosArray) {
        log.debug("Agregando contenido a objeto {}", objetoId);
//        SQLQuery query = currentSession().createSQLQuery("delete from aca_objetos_aca_contenidos where objetos_id = :objetoId");
//        query.setLong("objetoId", objetoId);
//        query.executeUpdate();
//        currentSession().flush();
        ObjetoAprendizaje objeto = (ObjetoAprendizaje) currentSession().get(ObjetoAprendizaje.class, objetoId);
        objeto.getContenidos().clear();
        currentSession().update(objeto);
        currentSession().flush();
        if (contenidosArray != null) {
            for (Long contenidoId : contenidosArray) {
                objeto.getContenidos().add((Contenido) currentSession().load(Contenido.class, contenidoId));
            }
            currentSession().update(objeto);
            currentSession().flush();
        }
    }
    
    @Override
    public List<Contenido> buscaContenidos(Long objetoId, String filtro) {
        Query query = currentSession().createQuery("select comunidadId from ObjetoAprendizaje where id = :objetoId");
        query.setLong("objetoId", objetoId);
        Long comunidadId = (Long) query.uniqueResult();
        query = currentSession().createQuery ("select c.id from ObjetoAprendizaje oa inner join oa.contenidos as c where oa.id = :objetoId");
        query.setLong("objetoId", objetoId);
        List<Long> idsDeContenido = query.list();
        Criteria criteria = currentSession().createCriteria(Contenido.class);
        criteria.add(Restrictions.eq("comunidadId", comunidadId));
        if (idsDeContenido != null && idsDeContenido.size() > 0) {
            criteria.add(Restrictions.not(Restrictions.in("id", idsDeContenido)));
        }
        Disjunction propiedades = Restrictions.disjunction();
        propiedades.add(Restrictions.ilike("codigo", filtro, MatchMode.ANYWHERE));
        propiedades.add(Restrictions.ilike("nombre", filtro, MatchMode.ANYWHERE));
        criteria.add(propiedades);
        criteria.addOrder(Order.asc("codigo"));
        return criteria.list();
    }
}
