<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="row-fluid">
    <h1><s:message code="mis.cursos.label" /></h1>
    <c:if test="${cursos != null}" >
        <table class="table table-striped">
            <thead>
                <tr>
                    <th><s:message code="curso" /></th>
                    <th><s:message code="estatus" /></th>
                    <th><s:message code="fechaConclusion" /></th>
                    <th><s:message code="ultimo.acceso" /></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${cursos}" var="alumnoCurso">
                    <portlet:renderURL var="actionUrl" >
                        <portlet:param name="action" value="ver" />
                        <portlet:param name="cursoId" value="${alumnoCurso.curso.id}" />
                    </portlet:renderURL>

                    <tr>
                        <td><a href="${actionUrl}">${alumnoCurso.curso.nombre}</a></td>
                        <td><s:message code="${alumnoCurso.estatus}" /></td>
                        <td>${alumnoCurso.fechaConclusion}</td>
                        <td>${alumnoCurso.ultimoAcceso}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>