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

import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mx.edu.um.academia.model.*;
import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
public interface CursoDao {

    public Curso actualiza(Curso curso, User creador);

    public Curso crea(Curso curso, User creador);

    public String elimina(Long cursoId, User creador);

    public Map<String, Object> lista(Map<String, Object> params);

    public Curso obtiene(Long cursoId);

    public Map<String, Object> objetos(Long id, Set<Long> comunidades);

    public List<ObjetoAprendizaje> objetos(Long id);

    public void agregaObjetos(Long cursoId, Long[] objetos);

    public Map<String, Object> verContenido(Long cursoId);

    public List<Curso> todos(Set<Long> comunidades);

    public PortletCurso guardaPortlet(Long cursoId, String portletId);

    public PortletCurso obtienePortlet(String portletId);

    public Alumno obtieneAlumno(Long id);

    public void inscribe(Curso curso, Alumno alumno, Boolean creaUsuario, String estatus);

    public Boolean estaInscrito(Long cursoId, Long alumnoId);

    public List<ObjetoAprendizaje> objetosAlumno(Long cursoId, Long alumnoId, ThemeDisplay themeDisplay);

    public List<ObjetoAprendizaje> objetosAlumno(Long cursoId, Long contenidoId, Long alumnoId, ThemeDisplay themeDisplay);

    public List<ObjetoAprendizaje> objetosAlumnoSiguiente(Long id, Long alumnoId, ThemeDisplay themeDisplay);

    public void inscribe(Long cursoId, Long alumnoId);

    public List<AlumnoCurso> alumnos(Long cursoId);

    public Map<String, Object> alumnos(Map<String, Object> params);

    public Examen obtieneExamen(Long examenId);

    public Map<String, Object> califica(Map<String, String[]> params, ThemeDisplay themeDisplay, User usuario);

    public Boolean haConcluido(Long alumnoId, Long cursoId);

    public List<AlumnoCurso> obtieneCursos(Long alumnoId);

    public AlumnoCurso obtieneAlumnoCurso(Long alumnoId, Long cursoId);

    public JasperReport obtieneReporte(Long cursoId);

    public Map<String, Object> todosAlumnos(Map<String, Object> params);

    public void bajaAlumno(Long alumnoId, Long cursoId);

    public Curso obtiene(String codigo, Long comunidadId);

    public void asignaIntro(Curso curso);

    public void asignaCorreo(Curso curso);

    public Salon obtieneSalon(Long cursoId);
    
    public Salon obtieneSalonPorId(Long salonId);
    
    public Salon creaSalon(Salon salon);
    
    public Salon actualizaSalon(Salon salon);
    
    public void eliminaSalon(Salon salon);

    public void actualizaObjetos(Long cursoId, Long[] objetos);

    public List<ObjetoAprendizaje> buscaObjetos(Long cursoId, String filtro);
    
    public List<Map> contenidos(Long alumnoId, Long cursoId);
}
