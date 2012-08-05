<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<portlet:renderURL var="verSiguienteUrl" >
    <portlet:param name="cursoId" value="${curso.id}" />
    <portlet:param name="action" value="verSiguiente" />
</portlet:renderURL>
<portlet:renderURL var="inicio" />

<div class="row-fluid">
    <div class="span12">
        <a href="${inicio}"><i class="icon-backward"></i> <s:message code="regresar.mis.cursos" /></a>
    </div>
</div>
<div class="row-fluid">
    <div class="span3">
        <div class="well" style='padding: 0; padding-bottom: 15px;'>
            <ul class="nav nav-list" style='margin-right: 0;'>
                <c:forEach items="${objetos}" var="objeto">
                    <li class="nav-header"><h5>${objeto.nombre}</h5></li>
                    <c:forEach items="${objeto.contenidos}" var="contenido">
                        <portlet:renderURL var="verContenidoUrl" >
                            <portlet:param name="action" value="verContenido" />
                            <portlet:param name="contenidoId" value="${contenido.id}" />
                            <portlet:param name="cursoId" value="${curso.id}" />
                        </portlet:renderURL>
                        <li<c:if test="${contenido.activo}"> class="active"</c:if> style="font-size: 0.8em;">
                            <a href="${verContenidoUrl}">
                                <c:choose>
                                    <c:when test="${contenido.alumno.iniciado != null && contenido.alumno.terminado == null}">
                                        <i class="icon-eye-open"></i>
                                    </c:when>
                                    <c:when test="${contenido.alumno.terminado == null}">
                                        <i class="icon-ban-circle"></i>
                                    </c:when>
                                    <c:otherwise>
                                        <i class="icon-ok-circle"></i>
                                    </c:otherwise>
                                </c:choose>
                                ${contenido.nombre}
                            </a>
                        </li>
                    </c:forEach>
                </c:forEach>
            </ul>
        </div>
    </div>
    <div class="span9">
        <c:choose>
            <c:when test="${concluido}">
                <h3><s:message code="concluido.titulo" /></h3>
                <h6><s:message code="concluido.mensaje" /></h6>
                <form name="<portlet:namespace />diplomaForm" action="<portlet:resourceURL id='diploma'/>" method="post">
                    <input type="hidden" name="<portlet:namespace />cursoId" id="<portlet:namespace />cursoId" value="${curso.id}" />
                    <div class="control-group">
                        <button type="submit" class="btn btn-primary btn-large"><i class="icon-print icon-white"></i> <s:message code="concluido.diploma" /></button>
                    </div>
                </form>
            </c:when>
            <c:when test="${not empty video}">
                <video id="<portlet:namespace />mediaspace" controls="controls">
                    <source src="${video}" />
                </video>
                <div style="margin-top: 20px;"><a href="${verSiguienteUrl}" class="btn btn-primary btn-large"><s:message code="siguiente" /> <i class="icon-chevron-right" ></i></a></div>
                <aui:script>
                    jwplayer('<portlet:namespace />mediaspace').setup({
                        modes : [
                            { type : 'html5' },
                            { type : 'flash', src : '${themeRoot}jwplayer/player.swf'}
                        ]        
                    });
                </aui:script>
            </c:when>
            <c:when test="${examen != null}">
                <div>${texto}</div>
                <portlet:renderURL var="enviaExamenUrl" >
                    <portlet:param name="action" value="enviaExamen" />
                </portlet:renderURL>
                <form name="<portlet:namespace />preguntasForm" action="${enviaExamenUrl}" method="post" class="well">
                    <input type="hidden" name="<portlet:namespace />examenId" id="<portlet:namespace />examenId" value="${examen.id}" />
                    <input type="hidden" name="<portlet:namespace />contenidoId" id="<portlet:namespace />contenidoId" value="${contenidoId}" />
                    <input type="hidden" name="<portlet:namespace />cursoId" id="<portlet:namespace />cursoId" value="${curso.id}" />
                    <c:forEach items="${preguntas}" var="pregunta">
                        <div class="control-group">
                            <h5>${pregunta.texto}</h5>
                            <c:forEach items="${pregunta.respuestas}" var="respuesta">
                                <c:choose>
                                    <c:when test="${pregunta.esMultiple}">
                                        <label class="checkbox">
                                            <input type="checkbox" name="<portlet:namespace/>${pregunta.id}" value="${respuesta.id}" />
                                            ${respuesta.texto}
                                        </label>
                                    </c:when>
                                    <c:otherwise>
                                        <label class="radio">
                                            <input type="radio" name="<portlet:namespace/>${pregunta.id}" value="${respuesta.id}" />
                                            ${respuesta.texto}
                                        </label>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </div>
                    </c:forEach>
                    <div class="control-group">
                        <button type="submit" class="btn btn-primary btn-large"><i class="icon-upload icon-white"></i> <s:message code="enviar.respuestas" /></button>
                    </div>
                </form>
            </c:when>
            <c:otherwise>
                ${texto}
                <div style="margin-top: 20px;"><a href="${verSiguienteUrl}" class="btn btn-primary btn-large"><s:message code="siguiente" /> <i class="icon-chevron-right" ></i></a></div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
