<%@ include file="/WEB-INF/jsp/include.jsp" %>
<portlet:renderURL var="inscribeUrl" >
    <portlet:param name="action" value="inscribeAlumno" />
</portlet:renderURL>

<c:if test="${alumnos != null}">
    <table id="<portlet:namespace />alumnos" class="table table-striped">
        <thead>
            <tr>

                <th><s:message code="usuario" /></th>

                <th><s:message code="nombre" /></th>
                
                <th><s:message code="correo" /></th>
                
                <th><s:message code="fecha" /></th>
                
                <th><s:message code="estatus" /></th>
                
                <th><s:message code="curso" /></th>
                
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${alumnos}" var="alumnoCurso">
                <tr>

                    <td>${alumnoCurso.alumno.usuario}</td>

                    <td>${alumnoCurso.alumno.nombreCompleto}</td>
                    
                    <td>${alumnoCurso.alumno.correo}</td>

                    <td>${alumnoCurso.fecha}</td>

                    <td><s:message code="${alumnoCurso.estatus}" /></td>
                    
                    <td>${alumnoCurso.curso.nombre}</td>

                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>
