<%@ include file="/WEB-INF/jsp/include.jsp" %>
<portlet:renderURL var="nuevoUrl" >
    <portlet:param name="action" value="nuevo" />
</portlet:renderURL>
<portlet:renderURL var="editaUrl" >
    <portlet:param name="action" value="edita" />
    <portlet:param name="id" value="${objeto.id}" />
</portlet:renderURL>
<portlet:actionURL var="eliminaUrl" >
    <portlet:param name="action" value="elimina" />
    <portlet:param name="id" value="${objeto.id}" />
</portlet:actionURL>

<div class="well">

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
<c:if test="${not empty vistaPrevia}">
    <div class="row-fluid">
        <div class="span12">
            <h3>${vistaPrevia}</h3>
        </div>
    </div>
</c:if>
<portlet:actionURL var="agregaContenidoURL">
    <portlet:param name="action" value="agregaContenido"/>
</portlet:actionURL>
<form id="<portlet:namespace />agregaContenidoForm" action="${agregaContenidoURL}" method="post" class="form-vertical">
    <input type="hidden" id="<portlet:namespace />objetoId" name="<portlet:namespace />objetoId" value="${objeto.id}"/>
    <div class="row-fluid">
        <select id="<portlet:namespace />contenidos" name="<portlet:namespace />contenidos" multiple="multiple" data-placeholder="<s:message code="objeto.elija.contenido" />" class="span4">
            <c:forEach items="${seleccionados}" var="contenido">
                <option value="${contenido.id}" selected="selected">${contenido.codigo} | ${contenido.nombre}</option>
            </c:forEach>
            <c:forEach items="${disponibles}" var="contenido">
                <option value="${contenido.id}">${contenido.codigo} | ${contenido.nombre}</option>
            </c:forEach>
        </select>
    </div>
    <div class="row-fluid">
        <button type="submit" class="btn btn-primary"><i class="icon-file icon-white"></i> <s:message code="objeto.agrega.contenido" /></button>
    </div>
</form>
<script type="text/javascript">
    $(document).ready(function() {
        $("select#<portlet:namespace />contenidos").chosen();
    });
</script>
