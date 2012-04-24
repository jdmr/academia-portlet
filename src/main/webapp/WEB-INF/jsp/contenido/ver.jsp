<%@ include file="/WEB-INF/jsp/include.jsp" %>

<div class="well">
    <portlet:renderURL var="nuevoUrl" >
        <portlet:param name="action" value="nuevo" />
    </portlet:renderURL>
    <portlet:renderURL var="editaUrl" >
        <portlet:param name="action" value="edita" />
        <portlet:param name="id" value="${contenido.id}" />
    </portlet:renderURL>
    <portlet:renderURL var="eliminaUrl" >
        <portlet:param name="action" value="elimina" />
        <portlet:param name="id" value="${contenido.id}" />
    </portlet:renderURL>

    <a class="btn btn-primary" href="<portlet:renderURL portletMode='view'/>"><i class="icon-list icon-white"></i> <s:message code="contenido.lista" /></a>
    <a class="btn btn-primary" href="${nuevoUrl}"><i class="icon-file icon-white"></i> <s:message code="contenido.nuevo" /></a>
    <a class="btn btn-primary" href="${editaUrl}"><i class="icon-edit icon-white"></i> <s:message code="contenido.edita" /></a>
    <c:choose>
        <c:when test="${'TEXTO' == contenido.tipo}">
            <c:choose>
                <c:when test="${empty contenido.contenidoId}">
                    <portlet:renderURL var="nuevoTexto" >
                        <portlet:param name="action" value="nuevoTexto" />
                        <portlet:param name="id" value="${contenido.id}" />
                    </portlet:renderURL>
                    <a class="btn btn-primary" href="${nuevoTexto}"><i class="icon-file icon-white"></i> <s:message code="contenido.nuevo.texto" /></a>
                </c:when>
                <c:otherwise>
                    <portlet:renderURL var="editaTexto" >
                        <portlet:param name="action" value="editaTexto" />
                        <portlet:param name="id" value="${contenido.id}" />
                    </portlet:renderURL>
                    <a class="btn btn-primary" href="${editaTexto}"><i class="icon-file icon-white"></i> <s:message code="contenido.edita.texto" /></a>
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:when test="${'VIDEO' == contenido.tipo}">
            <portlet:renderURL var="nuevoVideo" >
                <portlet:param name="action" value="nuevoVideo" />
                <portlet:param name="id" value="${contenido.id}" />
            </portlet:renderURL>
            <a class="btn btn-primary" href="${nuevoVideo}"><i class="icon-file icon-white"></i> <s:message code="contenido.nuevo.video" /></a>
        </c:when>
        <c:when test="${'URL' == contenido.tipo}">
            <portlet:renderURL var="nuevaURL" >
                <portlet:param name="action" value="nuevaURL" />
                <portlet:param name="id" value="${contenido.id}" />
            </portlet:renderURL>
            <a class="btn btn-primary" href="${nuevaURL}"><i class="icon-file icon-white"></i> <s:message code="contenido.nuevo.url" /></a>
        </c:when>
        <c:when test="${'IMAGEN' == contenido.tipo}">
            <portlet:renderURL var="nuevaImagen" >
                <portlet:param name="action" value="nuevaImagen" />
                <portlet:param name="id" value="${contenido.id}" />
            </portlet:renderURL>
            <a class="btn btn-primary" href="${nuevaImagen}"><i class="icon-file icon-white"></i> <s:message code="contenido.nuevo.imagen" /></a>
        </c:when>
        <c:when test="${'EXAMEN' == contenido.tipo}">
            <portlet:renderURL var="nuevoExamen" >
                <portlet:param name="action" value="nuevoExamen" />
                <portlet:param name="id" value="${contenido.id}" />
            </portlet:renderURL>
            <a class="btn btn-primary" href="${nuevoExamen}"><i class="icon-file icon-white"></i> <s:message code="contenido.nuevo.examen" /></a>
        </c:when>
    </c:choose>
    <a class="btn btn-danger"  href="${eliminaUrl}" onclick="return confirm('<s:message code="contenido.elimina.confirma"/>')"><i class="icon-ban-circle icon-white"></i> <s:message code="contenido.elimina" /></a>
    
</div>
<div class="row-fluid">
    <div class="span6">
        <h5><s:message code="codigo" /></h5>
        <h3>${contenido.codigo}</h3>
    </div>
    <div class="span6">
        <h5><s:message code="nombre" /></h5>
        <h3>${contenido.nombre}</h3>
    </div>
</div>
<div class="row-fluid">
    <div class="span6">
        <h5><s:message code="contenido.tipo" /></h5>
        <h3><s:message code="${contenido.tipo}" /></h3>
    </div>
    <div class="span6">
        <h5><s:message code="creador" /></h5>
        <h3>${contenido.creador}</h3>
    </div>
</div>
<div class="row-fluid">
    <div class="span6">
        <h5><s:message code="fechaCreacion" /></h5>
        <h3>${contenido.fechaCreacion}</h3>
    </div>
    <div class="span6">
    <h5><s:message code="fechaModificacion" /></h5>
    <h3>${contenido.fechaModificacion}</h3>
    </div>
</div>
<c:if test="${not empty texto}">
    <div class="row-fluid">
        <div>${texto}</div>
    </div>
</c:if>
    