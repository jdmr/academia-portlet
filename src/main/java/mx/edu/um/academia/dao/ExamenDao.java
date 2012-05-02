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
import java.util.List;
import java.util.Map;
import java.util.Set;
import mx.edu.um.academia.model.Examen;
import mx.edu.um.academia.model.ExamenPregunta;
import mx.edu.um.academia.model.Pregunta;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
public interface ExamenDao {

    public Examen actualiza(Examen examen, User creador);

    public Examen crea(Examen examen, User creador);

    public String elimina(Long examenId, User creador);

    public Map<String, Object> lista(Map<String, Object> params);

    public Examen obtiene(Long examenId);

    public ExamenPregunta asignaPregunta(Long examenId, Long preguntaId, Integer puntos, Boolean porPregunta, Long comunidadId, User creador);

    public List<String> quitaPregunta(Long examenId, Long preguntaId);

    public Examen actualizaContenido(Examen examen, User creador);

    public List<Pregunta> preguntas(Long examenId);

    public List<Examen> todos(Set<Long> comunidades);
}
