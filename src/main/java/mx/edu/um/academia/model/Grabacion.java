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

import java.util.Date;
import java.util.Objects;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
public class Grabacion {

    private String id;
    private Date inicio;
    private Date termino;
    private Integer duracion;
    private String url;
    private String elimina;

    public Grabacion() {
    }

    public Grabacion(String id, Date inicio, Date termino, Integer duracion, String url) {
        this.id = id;
        this.inicio = inicio;
        this.termino = termino;
        this.duracion = duracion;
        this.url = url;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the inicio
     */
    public Date getInicio() {
        return inicio;
    }

    /**
     * @param inicio the inicio to set
     */
    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    /**
     * @return the termino
     */
    public Date getTermino() {
        return termino;
    }

    /**
     * @param termino the termino to set
     */
    public void setTermino(Date termino) {
        this.termino = termino;
    }

    /**
     * @return the duracion
     */
    public Integer getDuracion() {
        return duracion;
    }

    /**
     * @param duracion the duracion to set
     */
    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the elimina
     */
    public String getElimina() {
        return elimina;
    }

    /**
     * @param elimina the elimina to set
     */
    public void setElimina(String elimina) {
        this.elimina = elimina;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Grabacion other = (Grabacion) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Grabacion{" + "id=" + id + ", inicio=" + inicio + ", termino=" + termino + ", duracion=" + duracion + ", url=" + url + '}';
    }
}
