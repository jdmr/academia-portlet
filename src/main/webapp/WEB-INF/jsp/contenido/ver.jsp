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
    <a class="btn btn-primary" href="${nuevoUrl}"><i class="icon-edit icon-white"></i> <s:message code="contenido.nuevo" /></a>
    <a class="btn btn-primary" href="${editaUrl}"><i class="icon-edit icon-white"></i> <s:message code="contenido.edita" /></a>
    <a class="btn btn-danger"  href="${eliminaUrl}" onclick="return confirm('<s:message code="contenido.elimina.confirma"/>')"><i class="icon-ban-circle icon-white"></i> <s:message code="contenido.elimina" /></a>
    
</div>
<div class="row-fluid">
    <h5><s:message code="codigo" /></h5>
    <h3>${contenido.codigo}</h3>
</div>
<div class="row-fluid">
    <h5><s:message code="nombre" /></h5>
    <h3>${contenido.nombre}</h3>
</div>
<div class="row-fluid">
    <h5><s:message code="contenido.tipo" /></h5>
    <h3><s:message code="${contenido.tipo}" /></h3>
</div>
<div class="row-fluid">
    <h5><s:message code="fechaCreacion" /></h5>
    <h3>${contenido.fechaCreacion}</h3>
</div>
<div class="row-fluid">
    <h5><s:message code="fechaModificacion" /></h5>
    <h3>${contenido.fechaModificacion}</h3>
</div>
<div class="row-fluid">
    <h5><s:message code="creador" /></h5>
    <h3>${contenido.creador}</h3>
</div>
