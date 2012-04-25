<%@ include file="/WEB-INF/jsp/include.jsp" %>
<portlet:renderURL var="nuevoUrl" >
    <portlet:param name="action" value="nuevo" />
</portlet:renderURL>
<portlet:renderURL var="editaUrl" >
    <portlet:param name="action" value="edita" />
    <portlet:param name="id" value="${curso.id}" />
</portlet:renderURL>
<portlet:actionURL var="eliminaUrl" >
    <portlet:param name="action" value="elimina" />
    <portlet:param name="id" value="${curso.id}" />
</portlet:actionURL>

<div class="well">
    <a class="btn btn-primary" href="<portlet:renderURL portletMode='view'/>"><i class="icon-list icon-white"></i> <s:message code="curso.lista" /></a>
    <a class="btn btn-primary" href="${nuevoUrl}"><i class="icon-file icon-white"></i> <s:message code="curso.nuevo" /></a>
    <a class="btn btn-primary" href="${editaUrl}"><i class="icon-edit icon-white"></i> <s:message code="curso.edita" /></a>
    <a class="btn btn-danger"  href="${eliminaUrl}" onclick="return confirm('<s:message code="curso.elimina.confirma"/>')"><i class="icon-ban-circle icon-white"></i> <s:message code="curso.elimina" /></a>
</div>
<div class="row-fluid">
    <div class="span6">
        <h5><s:message code="codigo" /></h5>
        <h3>${curso.codigo}</h3>
    </div>
    <div class="span6">
        <h5><s:message code="nombre" /></h5>
        <h3>${curso.nombre}</h3>
    </div>
</div>
<div class="row-fluid">
    <div class="span6">
        <h5><s:message code="creador" /></h5>
        <h3>${curso.creador}</h3>
    </div>
</div>
<div class="row-fluid">
    <div class="span6">
        <h5><s:message code="fechaCreacion" /></h5>
        <h3>${curso.fechaCreacion}</h3>
    </div>
    <div class="span6">
        <h5><s:message code="fechaModificacion" /></h5>
        <h3>${curso.fechaModificacion}</h3>
    </div>
</div>
<portlet:actionURL var="agregaObjetosURL">
    <portlet:param name="action" value="agregaObjetos"/>
</portlet:actionURL>
<form id="<portlet:namespace />agregaObjetosForm" action="${agregaObjetosURL}" method="post" class="form-vertical">
    <input type="hidden" id="<portlet:namespace />cursoId" name="<portlet:namespace />cursoId" value="${curso.id}"/>
    <div class="row-fluid">
        <select id="<portlet:namespace />objetos" name="<portlet:namespace />objetos" multiple="multiple" data-placeholder="<s:message code="curso.elija.objeto" />" class="span4" >
            <c:forEach items="${seleccionados}" var="objeto">
                <option value="${objeto.id}" selected="selected">${objeto.codigo} | ${objeto.nombre}</option>
            </c:forEach>
            <c:forEach items="${disponibles}" var="objeto">
                <option value="${objeto.id}">${objeto.codigo} | ${objeto.nombre}</option>
            </c:forEach>
        </select>
    </div>
    <div class="row-fluid">
        <button type="submit" class="btn btn-primary"><i class="icon-file icon-white"></i> <s:message code="curso.agrega.objeto" /></button>
    </div>
</form>
<script type="text/javascript">
    $(document).ready(function() {
        $("select#<portlet:namespace />objetos").chosen();
    });
</script>
    