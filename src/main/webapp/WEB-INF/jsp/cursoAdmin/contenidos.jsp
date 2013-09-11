<%@ include file="/WEB-INF/jsp/include.jsp" %>

<h1>${curso.nombre}</h1>

<portlet:renderURL var="verCursoUrl" >
    <portlet:param name="action" value="ver" />
    <portlet:param name="id" value="${curso.id}" />
</portlet:renderURL>
<portlet:renderURL var="alumnosUrl" >
    <portlet:param name="action" value="alumnos" />
    <portlet:param name="cursoId" value="${curso.id}" />
</portlet:renderURL>

<div class="well">
    <a class="btn btn-primary" href="${alumnosUrl}"><i class="icon-edit icon-white"></i> <s:message code="curso.alumnos" /></a>
    <a href="${verCursoUrl}" class="btn btn-primary"><i class="icon-list icon-white"></i> <s:message code="curso.lista" /></a>
</div>

<c:if test="${contenidos != null}">
    <table id="<portlet:namespace />contenidos" class="table table-striped table-hover">
        <thead>
            <tr>

                <th><s:message code="objeto" /></th>

                <th><s:message code="iniciado" /></th>
                
                <th><s:message code="terminado" /></th>
                
                <th><s:message code="contenido" /></th>
                
                <th><s:message code="iniciado" /></th>
                
                <th><s:message code="terminado" /></th>
                
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${contenidos}" var="contenido">
                <tr>

                    <td>${contenido.objetoNombre}</td>

                    <td>${contenido.objetoIniciado}</td>
                    
                    <td>${contenido.objetoTerminado}</td>

                    <td>${contenido.contenidoNombre}</td>

                    <td>${contenido.contenidoIniciado}</td>

                    <td>${contenido.contenidoTerminado}</td>
                    
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>
