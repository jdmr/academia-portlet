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
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mx.edu.um.academia.model.Contenido;
import mx.edu.um.academia.model.ObjetoAprendizaje;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
public interface ObjetoAprendizajeDao {

    public ObjetoAprendizaje actualiza(ObjetoAprendizaje objetoAprendizaje, User creador);

    public ObjetoAprendizaje crea(ObjetoAprendizaje objetoAprendizaje, MultipartFile archivo, User creador) throws IOException;

    public String elimina(Long objetoAprendizajeId, User creador);

    public Map<String, Object> lista(Map<String, Object> params);

    public ObjetoAprendizaje obtiene(Long objetoAprendizajeId);

    public Map<String, Object> contenidos(Long id, Set<Long> comunidades);

    public void agregaContenido(Long objetoId, Long[] contenidos);
    
    public List<Contenido> buscaContenidos(Long objetoId, String filtro);
}
