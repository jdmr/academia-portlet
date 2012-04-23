<%@ include file="/WEB-INF/jsp/include.jsp" %>

<div class="well">
    <portlet:renderURL var="editaUrl" >
        <portlet:param name="action" value="edita" />
        <portlet:param name="id" value="${contenido.id}" />
    </portlet:renderURL>
    <portlet:renderURL var="eliminaUrl" >
        <portlet:param name="action" value="elimina" />
        <portlet:param name="id" value="${contenido.id}" />
    </portlet:renderURL>

    <a class="btn btn-primary" href="<portlet:renderURL portletMode='view'/>"><i class="icon-list icon-white"></i> <liferay-ui:message key="contenido.lista" /></a>
    <a class="btn btn-primary" href="${editaUrl}"><i class="icon-edit icon-white"></i> <liferay-ui:message key="contenido.edita" /></a>
    <a class="btn btn-danger"  href="${eliminaUrl}"><i class="icon-ban-circle icon-white"></i> <liferay-ui:message key="contenido.elimina" /></a>
    
</div>
<div class="row-fluid">
    <h5><liferay-ui:message key="contenido.codigo" /></h5>
    <h3>${contenido.codigo}</h3>
</div>
<div class="row-fluid">
    <h5><liferay-ui:message key="contenido.nombre" /></h5>
    <h3>${contenido.nombre}</h3>
</div>
<div class="row-fluid">
    <h5><liferay-ui:message key="contenido.fechaCreacion" /></h5>
    <h3>${contenido.fechaCreacion}</h3>
</div>
<div class="row-fluid">
    <h5><liferay-ui:message key="contenido.fechaModificacion" /></h5>
    <h3>${contenido.fechaModificacion}</h3>
</div>
<div class="row-fluid">
    <h5><liferay-ui:message key="contenido.creador" /></h5>
    <h3>${contenido.creador}</h3>
</div>
