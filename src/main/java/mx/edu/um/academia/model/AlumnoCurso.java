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
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import javax.persistence.*;
import mx.edu.um.academia.utils.Constantes;
import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
@Entity
@Table(name = "aca_alumno_curso")
public class AlumnoCurso implements Serializable {

    @Id
    private AlumnoCursoPK id;
    @Version
    private Integer version;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date fecha = new Date();
    @Column(scale = 3, precision = 8)
    private BigDecimal evaluacion;
    @Column(name = "cantidad_evaluaciones")
    private Integer cantidadEvaluaciones;
    @Column(scale = 3, precision = 8)
    private BigDecimal calificacion;
    @Column(nullable = false, length = 32)
    private String estatus = Constantes.PENDIENTE;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_conclusion")
    private Date fechaConclusion;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ultimo_acceso")
    private Date ultimoAcceso;
    @Transient
    private Integer diasDisponibles;
    @Transient
    private String cursos;
    @Transient
    private BigDecimal saldo = BigDecimal.ZERO;

    public AlumnoCurso() {
    }

    public AlumnoCurso(Alumno alumno, Curso curso, String estatus) {
        this.id = new AlumnoCursoPK(alumno, curso);
        this.estatus = estatus;
    }

    public AlumnoCurso(AlumnoCursoPK id, String estatus) {
        this.id = id;
        this.estatus = estatus;
    }

    /**
     * @return the id
     */
    public AlumnoCursoPK getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(AlumnoCursoPK id) {
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
     * @return the alumno
     */
    public Alumno getAlumno() {
        return id.getAlumno();
    }

    /**
     * @param alumno the alumno to set
     */
    public void setAlumno(Alumno alumno) {
        this.id.setAlumno(alumno);
    }

    /**
     * @return the curso
     */
    public Curso getCurso() {
        return this.id.getCurso();
    }

    /**
     * @param curso the curso to set
     */
    public void setCurso(Curso curso) {
        this.id.setCurso(curso);
    }

    /**
     * @return the fecha
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * @return the evaluacion
     */
    public BigDecimal getEvaluacion() {
        return evaluacion;
    }

    /**
     * @param evaluacion the evaluacion to set
     */
    public void setEvaluacion(BigDecimal evaluacion) {
        this.evaluacion = evaluacion;
    }

    /**
     * @return the cantidadEvaluaciones
     */
    public Integer getCantidadEvaluaciones() {
        return cantidadEvaluaciones;
    }

    /**
     * @param cantidadEvaluaciones the cantidadEvaluaciones to set
     */
    public void setCantidadEvaluaciones(Integer cantidadEvaluaciones) {
        this.cantidadEvaluaciones = cantidadEvaluaciones;
    }

    /**
     * @return the calificacion
     */
    public BigDecimal getCalificacion() {
        return calificacion;
    }

    /**
     * @param calificacion the calificacion to set
     */
    public void setCalificacion(BigDecimal calificacion) {
        this.calificacion = calificacion;
    }

    /**
     * @return the estatus
     */
    public String getEstatus() {
        return estatus;
    }

    /**
     * @param estatus the estatus to set
     */
    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    /**
     * @return the fechaConclusion
     */
    public Date getFechaConclusion() {
        return fechaConclusion;
    }

    /**
     * @param fechaConclusion the fechaConclusion to set
     */
    public void setFechaConclusion(Date fechaConclusion) {
        this.fechaConclusion = fechaConclusion;
    }

    /**
     * @return the ultimoAcceso
     */
    public Date getUltimoAcceso() {
        return ultimoAcceso;
    }

    /**
     * @param ultimoAcceso the ultimoAcceso to set
     */
    public void setUltimoAcceso(Date ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    /**
     * @return the diasDisponibles
     */
    public Integer getDiasDisponibles() {
        if (diasDisponibles == null) {
            DateTime inicio = new DateTime(fecha);
            DateTime ahora = new DateTime();
            int diasTranscurridos = Days.daysBetween(inicio, ahora).getDays();
            diasDisponibles = id.getCurso().getDias() - diasTranscurridos;
            if (diasDisponibles < 0) {
                diasDisponibles = 0;
            }
        }
        return diasDisponibles;
    }

    /**
     * @return the cursos
     */
    public String getCursos() {
        return cursos;
    }

    /**
     * @param cursos the cursos to set
     */
    public void setCursos(String cursos) {
        this.cursos = cursos;
    }

    /**
     * @return the saldo
     */
    public BigDecimal getSaldo() {
        return saldo;
    }

    /**
     * @param saldo the saldo to set
     */
    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AlumnoCurso other = (AlumnoCurso) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.id);
        hash = 43 * hash + Objects.hashCode(this.version);
        return hash;
    }

    @Override
    public String toString() {
        return "AlumnoCurso{" + "id=" + id + ", fecha=" + fecha + ", evaluacion=" + evaluacion + ", cantidadEvaluaciones=" + cantidadEvaluaciones + ", calificacion=" + calificacion + ", estatus=" + estatus + '}';
    }
}
