<%@ include file="/WEB-INF/jsp/include.jsp" %>

<div class="well">
    <portlet:renderURL var="nuevoUrl" >
        <portlet:param name="action" value="nuevo" />
    </portlet:renderURL>
    <portlet:renderURL var="editaUrl" >
        <portlet:param name="action" value="edita" />
        <portlet:param name="id" value="${pregunta.id}" />
    </portlet:renderURL>
    <portlet:actionURL var="eliminaUrl" >
        <portlet:param name="action" value="elimina" />
        <portlet:param name="id" value="${pregunta.id}" />
    </portlet:actionURL>

    <a class="btn btn-primary" href="<portlet:renderURL portletMode='view'/>"><i class="icon-list icon-white"></i> <s:message code="pregunta.lista" /></a>
    <a class="btn btn-primary" href="${nuevoUrl}"><i class="icon-file icon-white"></i> <s:message code="pregunta.nuevo" /></a>
    <a class="btn btn-primary" href="${editaUrl}"><i class="icon-edit icon-white"></i> <s:message code="pregunta.edita" /></a>
    <c:choose>
        <c:when test="${not empty pregunta.contenido}">
            <portlet:renderURL var="editaTexto" >
                <portlet:param name="action" value="editaTexto" />
                <portlet:param name="id" value="${pregunta.id}" />
            </portlet:renderURL>
            <a class="btn btn-primary" href="${editaTexto}"><i class="icon-file icon-white"></i> <s:message code="edita.texto" /></a>
        </c:when>
        <c:otherwise>
            <portlet:renderURL var="nuevoTexto" >
                <portlet:param name="action" value="nuevoTexto" />
                <portlet:param name="id" value="${pregunta.id}" />
            </portlet:renderURL>
            <a class="btn btn-primary" href="${nuevoTexto}"><i class="icon-file icon-white"></i> <s:message code="nuevo.texto" /></a>
        </c:otherwise>
    </c:choose>
    <a class="btn btn-danger"  href="${eliminaUrl}" onclick="return confirm('<s:message code="pregunta.elimina.confirma"/>')"><i class="icon-ban-circle icon-white"></i> <s:message code="pregunta.elimina" /></a>
    
</div>
<div class="row-fluid">
    <div class="span6">
        <h5><s:message code="nombre" /></h5>
        <h3>${pregunta.nombre}</h3>
    </div>
    <div class="span6">
        <h5><s:message code="creador" /></h5>
        <h3>${pregunta.creador}</h3>
    </div>
</div>
<div class="row-fluid">
    <div class="span6">
        <h5><s:message code="es.multiple" /></h5>
        <h3><input type="checkbox" disabled="true" <c:if test="${pregunta.esMultiple}">checked="checked"</c:if> /></h3>
    </div>
</div>
<div class="row-fluid">
    <div class="span6">
        <h5><s:message code="fechaCreacion" /></h5>
        <h3>${pregunta.fechaCreacion}</h3>
    </div>
    <div class="span6">
        <h5><s:message code="fechaModificacion" /></h5>
        <h3>${pregunta.fechaModificacion}</h3>
    </div>
</div>
<c:if test="${not empty texto}">
    <div class="row-fluid">
        <div>${texto}</div>
    </div>
</c:if>
<portlet:actionURL var="agregaRespuestaURL">
    <portlet:param name="action" value="agregaRespuestas"/>
</portlet:actionURL>
<form id="<portlet:namespace />agregaRespuestaForm" action="${agregaRespuestaURL}" method="post" class="form-vertical">
    <input type="hidden" id="<portlet:namespace />preguntaId" name="<portlet:namespace />preguntaId" value="${pregunta.id}"/>
    <div class="row-fluid">
        <div class="span6">
            <select 
                id  ="<portlet:namespace />correctas" 
                name="<portlet:namespace />correctas" 
                <c:choose>
                    <c:when test="${pregunta.esMultiple}">
                        multiple="multiple" 
                        data-placeholder="<s:message code="pregunta.elija.respuestas.correctas" />" 
                    </c:when>
                    <c:otherwise>
                        data-placeholder="<s:message code="pregunta.elija.respuesta.correcta" />" 
                    </c:otherwise>
                </c:choose>
                class="span12">
                <c:forEach items="${correctas}" var="respuesta">
                    <option value="${respuesta.id}" selected="selected">${respuesta.nombre}</option>
                </c:forEach>
                <c:forEach items="${disponibles}" var="respuesta">
                    <option value="${respuesta.id}">${respuesta.nombre}</option>
                </c:forEach>
                <c:forEach items="${incorrectas}" var="respuesta">
                    <option value="${respuesta.id}">${respuesta.nombre}</option>
                </c:forEach>
            </select>
        </div>
        <div class="span6">
            <select 
                id  ="<portlet:namespace />incorrectas" 
                name="<portlet:namespace />incorrectas" 
                multiple="multiple" 
                data-placeholder="<s:message code="pregunta.elija.respuestas.incorrectas" />" 
                class="span12">
                <c:forEach items="${incorrectas}" var="respuesta">
                    <option value="${respuesta.id}" selected="selected">${respuesta.nombre}</option>
                </c:forEach>
                <c:forEach items="${disponibles}" var="respuesta">
                    <option value="${respuesta.id}">${respuesta.nombre}</option>
                </c:forEach>
                <c:forEach items="${correctas}" var="respuesta">
                    <option value="${respuesta.id}">${respuesta.nombre}</option>
                </c:forEach>
            </select>
        </div>
    </div>
    <div class="row-fluid" style="text-align: center;">
        <button type="submit" class="btn btn-primary"><i class="icon-file icon-white"></i> <s:message code="pregunta.agrega.respuestas" /></button>
    </div>
</form>
<script type="text/javascript">
    $(document).ready(function() {
        $("select#<portlet:namespace />correctas").chosen();
        $("select#<portlet:namespace />incorrectas").chosen();
    });
</script>
