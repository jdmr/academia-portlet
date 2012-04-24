<%@ include file="/WEB-INF/jsp/include.jsp" %>

<div class="well">
    <portlet:renderURL var="nuevoUrl" >
        <portlet:param name="action" value="nuevo" />
    </portlet:renderURL>
    <portlet:renderURL var="editaUrl" >
        <portlet:param name="action" value="edita" />
        <portlet:param name="id" value="${objeto.id}" />
    </portlet:renderURL>
    <portlet:renderURL var="eliminaUrl" >
        <portlet:param name="action" value="elimina" />
        <portlet:param name="id" value="${objeto.id}" />
    </portlet:renderURL>

    <a class="btn btn-primary" href="<portlet:renderURL portletMode='view'/>"><i class="icon-list icon-white"></i> <s:message code="objeto.lista" /></a>
    <a class="btn btn-primary" href="${nuevoUrl}"><i class="icon-file icon-white"></i> <s:message code="objeto.nuevo" /></a>
    <a class="btn btn-primary" href="${editaUrl}"><i class="icon-edit icon-white"></i> <s:message code="objeto.edita" /></a>
    <a class="btn btn-danger"  href="${eliminaUrl}" onclick="return confirm('<s:message code="objeto.elimina.confirma"/>')"><i class="icon-ban-circle icon-white"></i> <s:message code="objeto.elimina" /></a>
    
</div>
<div class="row-fluid">
    <div class="span6">
        <h5><s:message code="codigo" /></h5>
        <h3>${objeto.codigo}</h3>
    </div>
    <div class="span6">
        <h5><s:message code="nombre" /></h5>
        <h3>${objeto.nombre}</h3>
    </div>
</div>
<div class="row-fluid">
    <div class="span6">
        <h5><s:message code="descripcion" /></h5>
        <h3>${objeto.descripcion}</h3>
    </div>
    <div class="span6">
        <h5><s:message code="creador" /></h5>
        <h3>${objeto.creador}</h3>
    </div>
</div>
<div class="row-fluid">
    <div class="span6">
        <h5><s:message code="fechaCreacion" /></h5>
        <h3>${objeto.fechaCreacion}</h3>
    </div>
    <div class="span6">
    <h5><s:message code="fechaModificacion" /></h5>
    <h3>${objeto.fechaModificacion}</h3>
    </div>
</div>
