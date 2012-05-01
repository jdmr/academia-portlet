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
import java.util.Objects;
import javax.persistence.*;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
@Entity
@Table(name = "aca_examen_pregunta")
public class ExamenPregunta implements Serializable {

    @EmbeddedId
    private ExamenPreguntaPK id;
    @Version
    private Integer version;
    @Column(nullable = false)
    private Integer puntos = 0;
    @Column(name = "por_pregunta", nullable = false)
    private Boolean porPregunta = false;
    @Column(name = "comunidad_id", nullable = false)
    private Long comunidadId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_creacion", nullable = false)
    private Date fechaCreacion;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_modificacion", nullable = false)
    private Date fechaModificacion;
    @Column(nullable = false, length = 32)
    private String creador;

    public ExamenPregunta() {
    }

    public ExamenPregunta(Examen examen, Pregunta pregunta, Integer puntos, Boolean porPregunta, Long comunidadId) {
        this.id = new ExamenPreguntaPK(examen, pregunta);
        this.puntos = puntos;
        this.porPregunta = porPregunta;
        this.comunidadId = comunidadId;
    }

    public ExamenPregunta(ExamenPreguntaPK id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public ExamenPreguntaPK getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(ExamenPreguntaPK id) {
        this.id = id;
    }

    /**
     * @return the version
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * @return the puntos
     */
    public Integer getPuntos() {
        return puntos;
    }

    /**
     * @param puntos the puntos to set
     */
    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    /**
     * @return the porPregunta
     */
    public Boolean getPorPregunta() {
        return porPregunta;
    }

    /**
     * @param porPregunta the porPregunta to set
     */
    public void setPorPregunta(Boolean porPregunta) {
        this.porPregunta = porPregunta;
    }

    /**
     * @return the comunidadId
     */
    public Long getComunidadId() {
        return comunidadId;
    }

    /**
     * @param comunidadId the comunidadId to set
     */
    public void setComunidadId(Long comunidadId) {
        this.comunidadId = comunidadId;
    }

    /**
     * @return the fechaCreacion
     */
    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * @param fechaCreacion the fechaCreacion to set
     */
    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * @return the fechaModificacion
     */
    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    /**
     * @param fechaModificacion the fechaModificacion to set
     */
    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    /**
     * @return the creador
     */
    public String getCreador() {
        return creador;
    }

    /**
     * @param creador the creador to set
     */
    public void setCreador(String creador) {
        this.creador = creador;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExamenPregunta other = (ExamenPregunta) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.getId());
        hash = 13 * hash + Objects.hashCode(this.getPuntos());
        hash = 13 * hash + Objects.hashCode(this.getPorPregunta());
        return hash;
    }

    @Override
    public String toString() {
        return "ExamenPregunta{" + "id=" + getId() + ", puntos=" + getPuntos() + ", porPregunta=" + getPorPregunta() + '}';
    }
}
