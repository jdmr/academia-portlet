<%@ include file="/WEB-INF/jsp/include.jsp" %>

<c:if test="${not empty message}">
    <div class="alert alert-block <c:choose><c:when test='${not empty messageStyle}'>${messageStyle}</c:when><c:otherwise>alert-success</c:otherwise></c:choose> fade in">
        <a class="close" data-dismiss="alert">�</a>
        <p>${message}</p>
    </div>
</c:if>

<div class="well">
    <portlet:renderURL var="nuevoUrl" >
        <portlet:param name="action" value="nuevo" />
    </portlet:renderURL>
    <portlet:renderURL var="editaUrl" >
        <portlet:param name="action" value="edita" />
        <portlet:param name="id" value="${respuesta.id}" />
    </portlet:renderURL>
    <portlet:actionURL var="eliminaUrl" >
        <portlet:param name="action" value="elimina" />
        <portlet:param name="id" value="${respuesta.id}" />
    </portlet:actionURL>

    <a class="btn btn-primary" href="<portlet:renderURL portletMode='view'/>"><i class="icon-list icon-white"></i> <s:message code="respuesta.lista" /></a>
    <a class="btn btn-primary" href="${nuevoUrl}"><i class="icon-file icon-white"></i> <s:message code="respuesta.nuevo" /></a>
    <a class="btn btn-primary" href="${editaUrl}"><i class="icon-edit icon-white"></i> <s:message code="respuesta.edita" /></a>
    <c:choose>
        <c:when test="${not empty respuesta.contenido}">
            <portlet:renderURL var="editaTexto" >
                <portlet:param name="action" value="editaTexto" />
                <portlet:param name="id" value="${respuesta.id}" />
            </portlet:renderURL>
            <a class="btn btn-primary" href="${editaTexto}"><i class="icon-file icon-white"></i> <s:message code="edita.texto" /></a>
        </c:when>
        <c:otherwise>
            <portlet:renderURL var="nuevoTexto" >
                <portlet:param name="action" value="nuevoTexto" />
                <portlet:param name="id" value="${respuesta.id}" />
            </portlet:renderURL>
            <a class="btn btn-primary" href="${nuevoTexto}"><i class="icon-file icon-white"></i> <s:message code="nuevo.texto" /></a>
        </c:otherwise>
    </c:choose>
    <a class="btn btn-danger"  href="${eliminaUrl}" onclick="return confirm('<s:message code="respuesta.elimina.confirma"/>')"><i class="icon-ban-circle icon-white"></i> <s:message code="respuesta.elimina" /></a>
    
</div>
<div class="row-fluid">
    <div class="span6">
        <h5><s:message code="nombre" /></h5>
        <h3>${respuesta.nombre}</h3>
    </div>
    <div class="span6">
        <h5><s:message code="creador" /></h5>
        <h3>${respuesta.creador}</h3>
    </div>
</div>
<div class="row-fluid">
    <div class="span6">
        <h5><s:message code="fechaCreacion" /></h5>
        <h3>${respuesta.fechaCreacion}</h3>
    </div>
    <div class="span6">
        <h5><s:message code="fechaModificacion" /></h5>
        <h3>${respuesta.fechaModificacion}</h3>
    </div>
</div>
<c:if test="${not empty texto}">
    <div class="row-fluid">
        <div>${texto}</div>
    </div>
</c:if>
