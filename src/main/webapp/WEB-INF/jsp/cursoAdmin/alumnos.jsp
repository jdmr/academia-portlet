<%@ include file="/WEB-INF/jsp/include.jsp" %>

<h1>${curso.nombre}</h1>

<portlet:actionURL var="inscribeUrl" >
    <portlet:param name="action" value="inscribeAlumno" />
</portlet:actionURL>
<portlet:renderURL var="verCursoUrl" >
    <portlet:param name="action" value="ver" />
    <portlet:param name="id" value="${curso.id}" />
</portlet:renderURL>

<form action="${inscribeUrl}" method="post" name="<portlet:namespace />inscribeAlumnoForm" id="<portlet:namespace />inscribeAlumnoForm" class="well form-horizontal">
    <input type="hidden" name="<portlet:namespace />cursoId" value="${curso.id}" />
    <label>
        <select name="<portlet:namespace />alumnoId" id="<portlet:namespace />alumnoId" >
            <option value=""><s:message code="curso.elija.alumno" /></option>
            <c:forEach items="${disponibles}" var="alumno">
                <option value="${alumno.userId}">${alumno.fullName}</option>
            </c:forEach>
        </select>
        <button type="submit" class="btn btn-primary"><i class="icon-plus icon-white"></i> <s:message code="curso.inscribe" /></button>
        <a href="${verCursoUrl}" class="btn btn-primary"><i class="icon-list icon-white"></i> <s:message code="curso.lista" /></a>
    </label>
</form>

<c:if test="${alumnos != null}">
    <table id="<portlet:namespace />alumnos" class="table table-striped table-hover">
        <thead>
            <tr>

                <th><s:message code="usuario" /></th>

                <th><s:message code="nombre" /></th>
                
                <th><s:message code="correo" /></th>
                
                <th><s:message code="fecha" /></th>
                
                <th><s:message code="estatus" /></th>
                
                <th style="text-align: right;"><s:message code="saldo" /></th>
                
                <th><s:message code="acciones" /></th>
                
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${alumnos}" var="alumnoCurso">
                <portlet:actionURL var="eliminaUrl" >
                    <portlet:param name="action" value="bajaAlumno" />
                    <portlet:param name="cursoId" value="${curso.id}" />
                    <portlet:param name="alumnoId" value="${alumnoCurso.alumno.id}" />
                </portlet:actionURL>
                <portlet:renderURL var="alumnoContenidoUrl" >
                    <portlet:param name="action" value="alumnoContenido" />
                    <portlet:param name="cursoId" value="${curso.id}" />
                    <portlet:param name="alumnoId" value="${alumnoCurso.alumno.id}" />
                </portlet:renderURL>
                <tr>

                    <td>${alumnoCurso.alumno.usuario}</td>

                    <td>${alumnoCurso.alumno.nombreCompleto}</td>
                    
                    <td>${alumnoCurso.alumno.correo}</td>

                    <td>${alumnoCurso.fecha}</td>

                    <td><s:message code="${alumnoCurso.estatus}" /></td>

                    <td style="text-align: right;"><fmt:formatNumber value="${alumnoCurso.saldo}" type="currency" currencySymbol="$" /></td>
                    
                    <td>
                        <a href="${alumnoContenidoUrl}" class="btn btn-success btn-mini"><i class="icon-eye-open icon-white"></i></a>
                        <a href="${eliminaUrl}" class="btn btn-danger btn-mini" onclick="return confirm('<s:message code="confirma.baja.alumno" />')"><i class="icon-remove icon-white"></i></a>
                    </td>
                    
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>
<script type="text/javascript">
    $(document).ready(function() {
        $('select#<portlet:namespace />alumnoId').chosen();
    });
</script>
