<%@ include file="/WEB-INF/jsp/include.jsp" %>

<div class="well">
    <portlet:renderURL var="nuevoUrl" >
        <portlet:param name="action" value="nuevo" />
    </portlet:renderURL>
    <portlet:renderURL var="editaUrl" >
        <portlet:param name="action" value="edita" />
        <portlet:param name="id" value="${examen.id}" />
    </portlet:renderURL>
    <portlet:renderURL var="preguntaUrl" >
        <portlet:param name="action" value="pregunta" />
        <portlet:param name="id" value="${examen.id}" />
    </portlet:renderURL>
    <portlet:actionURL var="eliminaUrl" >
        <portlet:param name="action" value="elimina" />
        <portlet:param name="id" value="${examen.id}" />
    </portlet:actionURL>

    <a class="btn btn-primary" href="<portlet:renderURL portletMode='view'/>"><i class="icon-list icon-white"></i> <s:message code="examen.lista" /></a>
    <a class="btn btn-primary" href="${nuevoUrl}"><i class="icon-file icon-white"></i> <s:message code="examen.nuevo" /></a>
    <a class="btn btn-primary" href="${editaUrl}"><i class="icon-edit icon-white"></i> <s:message code="examen.edita" /></a>
    <a class="btn btn-primary" href="${preguntaUrl}"><i class="icon-edit icon-white"></i> <s:message code="examen.pregunta.nueva" /></a>
    <c:choose>
        <c:when test="${not empty examen.contenido}">
            <portlet:renderURL var="editaTexto" >
                <portlet:param name="action" value="editaTexto" />
                <portlet:param name="id" value="${examen.id}" />
            </portlet:renderURL>
            <a class="btn btn-primary" href="${editaTexto}"><i class="icon-file icon-white"></i> <s:message code="edita.texto" /></a>
        </c:when>
        <c:otherwise>
            <portlet:renderURL var="nuevoTexto" >
                <portlet:param name="action" value="nuevoTexto" />
                <portlet:param name="id" value="${examen.id}" />
            </portlet:renderURL>
            <a class="btn btn-primary" href="${nuevoTexto}"><i class="icon-file icon-white"></i> <s:message code="nuevo.texto" /></a>
        </c:otherwise>
    </c:choose>
    <a class="btn btn-danger"  href="${eliminaUrl}" onclick="return confirm('<s:message code="examen.elimina.confirma"/>')"><i class="icon-ban-circle icon-white"></i> <s:message code="examen.elimina" /></a>
    
</div>
<div class="row-fluid">
    <div class="span6">
        <h5><s:message code="nombre" /></h5>
        <h3>${examen.nombre}</h3>
    </div>
    <div class="span6">
        <h5><s:message code="creador" /></h5>
        <h3>${examen.creador}</h3>
    </div>
</div>
<div class="row-fluid">
    <div class="span6">
        <h5><s:message code="fechaCreacion" /></h5>
        <h3>${examen.fechaCreacion}</h3>
    </div>
    <div class="span6">
        <h5><s:message code="fechaModificacion" /></h5>
        <h3>${examen.fechaModificacion}</h3>
    </div>
</div>
<div class="row-fluid">
    <div class="span6">
        <h5><s:message code="puntos" /></h5>
        <h3>${examen.puntos}</h3>
    </div>
</div>
<c:if test="${not empty texto}">
    <div class="row-fluid">
        <div>${texto}</div>
    </div>
</c:if>
<c:if test="${preguntas != null}">
    <form name="preguntasForm" action="#" class="well">
        <c:forEach items="${preguntas}" var="pregunta">
            <portlet:actionURL var="eliminaPreguntaUrl" >
                <portlet:param name="action" value="eliminaPregunta" />
                <portlet:param name="examenId" value="${examen.id}" />
                <portlet:param name="preguntaId" value="${pregunta.id}" />
            </portlet:actionURL>
            <div class="control-group" style="margin-bottom: 10px;">
                <h3><a href="${eliminaPreguntaUrl}" class="btn btn-danger btn-mini" onclick="return confirm('<s:message code="confirma.elimina.pregunta" arguments="${pregunta.nombre}" />')"><i class="icon-remove icon-white"></i></a> ${pregunta.nombre}</h3>
                <h5>${pregunta.texto}</h5>
                <c:forEach items="${pregunta.respuestas}" var="respuesta">
                    <c:choose>
                        <c:when test="${pregunta.esMultiple}">
                            <label class="checkbox">
                                <input type="checkbox" name="${pregunta.id}" value="${respuesta.id}" />
                                ${respuesta.texto}
                            </label>
                        </c:when>
                        <c:otherwise>
                            <label class="radio">
                                <input type="radio" name="${pregunta.id}" value="${respuesta.id}" />
                                ${respuesta.texto}
                            </label>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </div>
        </c:forEach>
    </form>
</c:if>
