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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
@Entity
@Table(name = "aca_cursos", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"comunidad_id", "codigo"}),
    @UniqueConstraint(columnNames = {"comunidad_id", "nombre"})
})
public class Curso implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version;
    @NotBlank
    @Column(length = 32, nullable = false)
    private String codigo;
    @Column(length = 128, nullable = false)
    @NotBlank
    private String nombre;
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
    @ManyToMany
    @OrderColumn(name = "orden")
    private List<ObjetoAprendizaje> objetos = new ArrayList<>();
    private Long intro;
    @Column(nullable = false, length = 32)
    private String tipo = "PATROCINADO";
    @Column(scale = 2, precision = 8)
    private BigDecimal precio = BigDecimal.ZERO;
    @Column(length = 32)
    private String comercio = "PAYPAL";
    @Column(length = 64)
    private String comercioId;
    @Column(nullable = false)
    private Integer dias = 365;
    @Transient
    private Reporte reporte;
    @Email
    private String correo;
    @Email
    private String correo2;
    @Column(name="correo_id")
    private Long correoId;
    private Boolean usarServicioPostal = false;
    private Integer horas = 10;

    public Curso() {
    }

    public Curso(String codigo, String nombre, Long comunidadId) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.comunidadId = comunidadId;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
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
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    /**
     * @return the objetos
     */
    public List<ObjetoAprendizaje> getObjetos() {
        return objetos;
    }

    /**
     * @param objetos the objetos to set
     */
    public void setObjetos(List<ObjetoAprendizaje> objetos) {
        this.objetos = objetos;
    }

    /**
     * @return the intro
     */
    public Long getIntro() {
        return intro;
    }

    /**
     * @param intro the intro to set
     */
    public void setIntro(Long intro) {
        this.intro = intro;
    }

    /**
     * @return the tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the precio
     */
    public BigDecimal getPrecio() {
        return precio;
    }

    /**
     * @param precio the precio to set
     */
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    /**
     * @return the comercio
     */
    public String getComercio() {
        return comercio;
    }

    /**
     * @param comercio the comercio to set
     */
    public void setComercio(String comercio) {
        this.comercio = comercio;
    }

    /**
     * @return the comercioId
     */
    public String getComercioId() {
        return comercioId;
    }

    /**
     * @param comercioId the comercioId to set
     */
    public void setComercioId(String comercioId) {
        this.comercioId = comercioId;
    }

    /**
     * @return the dias
     */
    public Integer getDias() {
        return dias;
    }

    /**
     * @param dias the dias to set
     */
    public void setDias(Integer dias) {
        this.dias = dias;
    }

    /**
     * @return the reporte
     */
    public Reporte getReporte() {
        return reporte;
    }

    /**
     * @param reporte the reporte to set
     */
    public void setReporte(Reporte reporte) {
        this.reporte = reporte;
    }

    /**
     * @return the correo
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * @param correo the correo to set
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * @return the correo2
     */
    public String getCorreo2() {
        return correo2;
    }

    /**
     * @param correo2 the correo2 to set
     */
    public void setCorreo2(String correo2) {
        this.correo2 = correo2;
    }

    /**
     * @return the correoId
     */
    public Long getCorreoId() {
        return correoId;
    }

    /**
     * @param correoId the correoId to set
     */
    public void setCorreoId(Long correoId) {
        this.correoId = correoId;
    }

    /**
     * @return the usarServicioPostal
     */
    public Boolean getUsarServicioPostal() {
        return usarServicioPostal;
    }

    /**
     * @param usarServicioPostal the usarServicioPostal to set
     */
    public void setUsarServicioPostal(Boolean usarServicioPostal) {
        this.usarServicioPostal = usarServicioPostal;
    }

    /**
     * @return the horas
     */
    public Integer getHoras() {
        return horas;
    }

    /**
     * @param horas the horas to set
     */
    public void setHoras(Integer horas) {
        this.horas = horas;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Curso other = (Curso) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 97 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 97 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Curso{" + "id=" + id + ", codigo=" + codigo + ", nombre=" + nombre + ", comunidadId=" + comunidadId + '}';
    }
}
