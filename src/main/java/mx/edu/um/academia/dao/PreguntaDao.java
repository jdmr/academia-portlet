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
import java.util.Map;
import java.util.Set;
import mx.edu.um.academia.model.Pregunta;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
public interface PreguntaDao {

    public Pregunta actualiza(Pregunta pregunta, User creador);

    public Pregunta crea(Pregunta pregunta, User creador);

    public String elimina(Long preguntaId, User creador);

    public Map<String, Object> lista(Map<String, Object> params);

    public Pregunta obtiene(Long preguntaId);
    
    public void asignaRespuestas(Long preguntaId, Long[] correctas, Long[] incorrectas);
    
    public Pregunta actualizaContenido(Pregunta pregunta, User creador);
    
    public Map<String, Object> respuestas(Long preguntaId, Set<Long> comunidades);
}
