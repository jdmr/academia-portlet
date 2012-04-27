<%@ include file="/WEB-INF/jsp/include.jsp" %>

<div class="well">
    <portlet:renderURL var="nuevoUrl" >
        <portlet:param name="action" value="nuevo" />
    </portlet:renderURL>
    <portlet:renderURL var="editaUrl" >
        <portlet:param name="action" value="edita" />
        <portlet:param name="id" value="${examen.id}" />
    </portlet:renderURL>
    <portlet:actionURL var="eliminaUrl" >
        <portlet:param name="action" value="elimina" />
        <portlet:param name="id" value="${examen.id}" />
    </portlet:actionURL>

    <a class="btn btn-primary" href="<portlet:renderURL portletMode='view'/>"><i class="icon-list icon-white"></i> <s:message code="examen.lista" /></a>
    <a class="btn btn-primary" href="${nuevoUrl}"><i class="icon-file icon-white"></i> <s:message code="examen.nuevo" /></a>
    <a class="btn btn-primary" href="${editaUrl}"><i class="icon-edit icon-white"></i> <s:message code="examen.edita" /></a>
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
<c:if test="${not empty texto}">
    <div class="row-fluid">
        <div>${texto}</div>
    </div>
</c:if>
