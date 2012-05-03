<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:choose>
    <c:when test="${not empty message}">
        <div class="alert alert-block alert-error fade in" role="status">
            <a class="close" data-dismiss="alert">×</a>
            <s:message code="${message}" arguments="${messageAttrs}" />
        </div>
    </c:when>
    <c:when test="${objetos != null}">
        <div class="row-fluid">
            <div class="span3">
                <div class="well" style='padding: 0; padding-bottom: 15px;'>
                    <ul class="nav nav-list" style='margin-right: 0;'>
                        <c:forEach items="${objetos}" var="objeto">
                            <li class="nav-header"><h5>${objeto.nombre}</h5></li>
                            <c:forEach items="${objeto.contenidos}" var="contenido">
                                <portlet:actionURL var="verContenidoUrl" >
                                    <portlet:param name="action" value="inscribeAlumno" />
                                    <portlet:param name="cursoId" value="${contenido.id}" />
                                </portlet:actionURL>
                                <li<c:if test="${contenido.activo}"> class="active"</c:if> style="font-size: 0.8em;"><a href="${verContenidoUrl}">${contenido.nombre}</a></li>
                            </c:forEach>
                        </c:forEach>
                    </ul>
                </div>
            </div>
            <div class="span9">${texto}</div>
        </div>
    </c:when>
    <c:otherwise>
        <div>${texto}</div>
        <c:choose>
            <c:when test="${sign_in}">
                <a href="${sign_in_url}" class="btn btn-primary btn-large"><i class="icon-key icon-white"></i> <s:message code="curso.registrar.primero" /></a>
            </c:when>
            <c:otherwise>
                <portlet:actionURL var="inscribeAlumnoUrl" >
                    <portlet:param name="action" value="inscribeAlumno" />
                    <portlet:param name="cursoId" value="${cursoId}" />
                </portlet:actionURL>
                <a href="${inscribeAlumnoUrl}" class="btn btn-primary btn-large"><i class="icon-plus icon-white"></i> <s:message code="curso.inscribe" /></a>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
