<%@ include file="/WEB-INF/jsp/include.jsp" %>
<portlet:renderURL var="nuevoUrl" >
    <portlet:param name="action" value="nuevo" />
</portlet:renderURL>
<portlet:renderURL var="editaUrl" >
    <portlet:param name="action" value="edita" />
    <portlet:param name="id" value="${curso.id}" />
</portlet:renderURL>
<portlet:renderURL var="introUrl" >
    <portlet:param name="action" value="intro" />
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
    <a class="btn btn-primary" href="${introUrl}"><i class="icon-edit icon-white"></i> <s:message code="curso.intro.nueva" /></a>
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
        <h5><s:message code="curso.tipo" /></h5>
        <h3><s:message code="${curso.tipo}" /></h3>
    </div>
    <div class="span6">
        <h5><s:message code="precio" /></h5>
        <h3>${curso.precio}</h3>
    </div>
</div>
<c:if test="${curso.tipo eq 'PAGADO'}">
    <div class="row-fluid">
        <div class="span6">
            <h5><s:message code="comercio" /></h5>
            <h3>${curso.comercio}</h3>
        </div>
        <div class="span6">
            <h5><s:message code="comercio.id" /></h5>
            <h3>${curso.comercioId}</h3>
        </div>
    </div>
</c:if>
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
    
<portlet:resourceURL var="vistaPreviaUrl" >
    <portlet:param name="action" value="vistaPrevia" />
    <portlet:param name="cursoId" value="${curso.id}" />
    <portlet:param name="posicionObjeto" value="0" />
    <portlet:param name="posicionContenido" value="0" />
</portlet:resourceURL>
<portlet:resourceURL var="vistaPreviaIntroUrl" >
    <portlet:param name="action" value="vistaPreviaIntro" />
    <portlet:param name="cursoId" value="${curso.id}" />
</portlet:resourceURL>
<div class="well" style="margin-top: 10px;">
    <a id="vistaPreviaLink" class="btn btn-primary" href="${vistaPreviaUrl}"><i class="icon-eye-open icon-white"></i> <s:message code="curso.vista.previa" /></a>
    <a id="vistaPreviaIntroLink" class="btn btn-primary" href="${vistaPreviaIntroUrl}"><i class="icon-eye-open icon-white"></i> <s:message code="curso.vista.previa.intro" /></a>
</div>
<div id="vistaPrevia" class="row-fluid">
    <div>${texto}</div>
</div>
    
<script type="text/javascript">
    $(document).ready(function() {
        $("select#<portlet:namespace />objetos").chosen();
        
        var container = $("div#vistaPrevia");
    
        $("a#vistaPreviaLink").click(function(e) {
            e.preventDefault();
            container.hide("slide",{direction:"up"});
            container.load('${vistaPreviaUrl}', {url:'${vistaPreviaUrl}'}, function() {
                container.show("slide",{direction:"up"});
            });
        });
        
        $("a#vistaPreviaIntroLink").click(function(e) {
            e.preventDefault();
            container.hide("slide",{direction:"up"});
            container.load('${vistaPreviaIntroUrl}', {}, function() {
                container.show("slide",{direction:"up"});
            });
        });
    });
    
    function cargaContenido(contenidoUrl) {
        var container = $("div#vistaPrevia");
        //container.hide("slide",{direction:"up"});
        container.load(contenidoUrl, {url:'${vistaPreviaUrl}'}, function() {
            //container.show("slide",{direction:"up"});
        });
    }
</script>
