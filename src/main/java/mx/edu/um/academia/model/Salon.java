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
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
@Entity
@Table(name = "aca_salones")
public class Salon implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Version
    private Integer version;
    private String name;
    private String meetingID;
    private String attendeePW;
    private String moderatorPW;
    private String createTime;
    @ManyToOne
    private Curso curso;
    private String subject;
    private Long contentId;
    @Transient
    private String texto;
    @Transient
    private String ligaAcceso;

    public Salon() {
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the meetingID
     */
    public String getMeetingID() {
        return meetingID;
    }

    /**
     * @param meetingID the meetingID to set
     */
    public void setMeetingID(String meetingID) {
        this.meetingID = meetingID;
    }

    /**
     * @return the attendeePW
     */
    public String getAttendeePW() {
        return attendeePW;
    }

    /**
     * @param attendeePW the attendeePW to set
     */
    public void setAttendeePW(String attendeePW) {
        this.attendeePW = attendeePW;
    }

    /**
     * @return the moderatorPW
     */
    public String getModeratorPW() {
        return moderatorPW;
    }

    /**
     * @param moderatorPW the moderatorPW to set
     */
    public void setModeratorPW(String moderatorPW) {
        this.moderatorPW = moderatorPW;
    }

    /**
     * @return the createTime
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime the createTime to set
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the curso
     */
    public Curso getCurso() {
        return curso;
    }

    /**
     * @param curso the curso to set
     */
    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the contentId
     */
    public Long getContentId() {
        return contentId;
    }

    /**
     * @param contentId the contentId to set
     */
    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    /**
     * @return the texto
     */
    public String getTexto() {
        return texto;
    }

    /**
     * @param texto the texto to set
     */
    public void setTexto(String texto) {
        this.texto = texto;
    }

    /**
     * @return the ligaAcceso
     */
    public String getLigaAcceso() {
        return ligaAcceso;
    }

    /**
     * @param ligaAcceso the ligaAcceso to set
     */
    public void setLigaAcceso(String ligaAcceso) {
        this.ligaAcceso = ligaAcceso;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + Objects.hashCode(this.id);
        hash = 31 * hash + Objects.hashCode(this.version);
        hash = 31 * hash + Objects.hashCode(this.meetingID);
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
        final Salon other = (Salon) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Salon{" + "id=" + id + ", version=" + version + ", name=" + name + ", meetingID=" + meetingID + ", attendeePW=" + attendeePW + ", moderatorPW=" + moderatorPW + ", createTime=" + createTime + '}';
    }
}
