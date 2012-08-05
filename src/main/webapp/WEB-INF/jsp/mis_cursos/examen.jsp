<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<div class="row-fluid">
    <div class="span12">
        <a href="${inicio}"><i class="icon-backward"></i> <s:message code="regresar.mis.cursos" /></a>
    </div>
</div>
<h1><s:message code="resultado.examen" arguments="${examen.nombre}" /></h1>
<div class="alert alert-block ${messageType}">
    <h4 class="alert-header"><s:message code="${messageTitle}" /></h4>
    <s:message code="resultado.mensaje" arguments="${totales}" />
</div>
<c:if test="${incorrectas != null}">
    <div class="well">
        <h2><s:message code="incorrectas" /></h3>
        <c:forEach items="${incorrectas}" var="pregunta">
        <h5>${pregunta.texto}</h5>
            <c:forEach items="${pregunta.respuestas}" var="respuesta">
                <div class="alert alert-error">${respuesta.texto}</div>
            </c:forEach>
        </c:forEach>
    </div>
</c:if>
<portlet:renderURL var="verSiguienteUrl" >
    <portlet:param name="action" value="ver" />
    <portlet:param name="cursoId" value="${cursoId}" />
</portlet:renderURL>
<div style="margin-top: 20px;"><a href="${verSiguienteUrl}" class="btn btn-primary btn-large"><s:message code="siguiente" /> <i class="icon-chevron-right" ></i></a></div>
