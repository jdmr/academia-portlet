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
                    <th><s:message code="fecha" /></th>
                    <th><s:message code="diasDisponibles" /></th>
                    <th><s:message code="fechaConclusion" /></th>
                    <th><s:message code="ultimo.acceso" /></th>
                    <th><s:message code="acciones" /></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${cursos}" var="alumnoCurso">
                    <portlet:renderURL var="actionUrl" >
                        <portlet:param name="action" value="ver" />
                        <portlet:param name="cursoId" value="${alumnoCurso.curso.id}" />
                    </portlet:renderURL>

                    <tr>
                        <td>
                            <c:choose>
                                <c:when test="${alumnoCurso.diasDisponibles > 0}">
                                    <a href="${actionUrl}">${alumnoCurso.curso.nombre}</a>
                                </c:when>
                                <c:otherwise>
                                    ${alumnoCurso.curso.nombre}
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td><s:message code="${alumnoCurso.estatus}" /></td>
                        <td><fmt:formatDate value="${alumnoCurso.fecha}" timeZone="${timeZone}" pattern="yyyy/MM/dd HH:mm" /></td>
                        <td>${alumnoCurso.diasDisponibles}</td>
                        <td><fmt:formatDate value="${alumnoCurso.fechaConclusion}" timeZone="${timeZone}" pattern="yyyy/MM/dd HH:mm" /></td>
                        <td><fmt:formatDate value="${alumnoCurso.ultimoAcceso}" timeZone="${timeZone}" pattern="yyyy/MM/dd HH:mm" /></td>
                        <td>
                            <c:if test="${alumnoCurso.fechaConclusion != null}">
                                <form name="<portlet:namespace />curso${alumno.curso.id}" action="<portlet:resourceURL id='diploma'/>" method="post" class="form form-search">
                                    <input type="hidden" name="<portlet:namespace />cursoId" id="<portlet:namespace />cursoId" value="${alumnoCurso.curso.id}" />
                                    <div class="control-group">
                                        <button type="submit" class="btn btn-success"><i class="icon-print icon-white"></i> <s:message code="diploma" /></button>
                                    </div>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>