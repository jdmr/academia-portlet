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
package mx.edu.um.academia.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
@Entity
@Table(name="aca_alumno_objeto")
public class AlumnoObjetoAprendizaje implements Serializable {
    @Id
    private AlumnoObjetoAprendizajePK id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date iniciado;
    @Temporal(TemporalType.TIMESTAMP)
    private Date terminado;

    public AlumnoObjetoAprendizaje() {
    }
    
    public AlumnoObjetoAprendizaje(Alumno alumno, ObjetoAprendizaje objeto) {
        id = new AlumnoObjetoAprendizajePK(alumno, objeto);
    }

    /**
     * @return the id
     */
    public AlumnoObjetoAprendizajePK getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(AlumnoObjetoAprendizajePK id) {
        this.id = id;
    }

    /**
     * @return the iniciado
     */
    public Date getIniciado() {
        return iniciado;
    }

    /**
     * @param iniciado the iniciado to set
     */
    public void setIniciado(Date iniciado) {
        this.iniciado = iniciado;
    }

    /**
     * @return the terminado
     */
    public Date getTerminado() {
        return terminado;
    }

    /**
     * @param terminado the terminado to set
     */
    public void setTerminado(Date terminado) {
        this.terminado = terminado;
    }
}
