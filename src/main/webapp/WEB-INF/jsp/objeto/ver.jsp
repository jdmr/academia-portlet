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
<div class="row-fluid">
    <div class="span6">
        <label>
            <h3><s:message code="objeto.agrega.contenido" /></h3>
            <input type="text" name="<portlet:namespace />contenidoAC" id="<portlet:namespace />contenidoAC" class="span12" />
        </label>
        <div class="alert alert-block alert-success" id="successMessageDiv" style="display:none;">
            <p id="successMessage">This is a test</p>
        </div>
    </div>
</div>
<div class="row-fluid">
    <div class="span6" id="contenidos">
        <div id="<portlet:namespace />contenidosDiv">
            <c:forEach items="${seleccionados}" var="contenido">
                <div class="ui-state-default" id="${contenido.id}" data-id="${contenido.id}"><span class="ui-icon ui-icon-arrowthick-2-n-s" style="float: left; margin-top: 5px;"></span> ${contenido.codigo} | ${contenido.nombre} <a class="close" data-dismiss="alert">×</a></div>
            </c:forEach>
        </div>
    </div>
</div>
<script type="text/javascript">
    $(document).ready(function() {
        $("div#contenidos").on('closed', 'div.ui-state-default',function() {
            $($(this).data('id')).remove();
            setTimeout(function(){
                var contenidosOrder = $("div#<portlet:namespace />contenidosDiv").sortable('toArray').toString();
                $.post("<portlet:resourceURL id='actualizaContenidos'/>", {id:${objeto.id}, 'contenidos[]':contenidosOrder}, function() {
                    $("p#successMessage").text('<s:message code="objetoAprendizaje.contenido.eliminar" />');
                    var div = $("div#successMessageDiv");
                    div.show('slow', function() {
                        setTimeout(function() {
                            div.hide('slow');
                        }, 2000);
                    });
                });
            }, 500);
        });
        $("div#<portlet:namespace />contenidosDiv").sortable({
            update: function(event, ui) {
                var contenidosOrder = $(this).sortable('toArray').toString();
                $.post("<portlet:resourceURL id='actualizaContenidos'/>", {id:${objeto.id}, 'contenidos[]':contenidosOrder}, function() {
                    $("p#successMessage").text('<s:message code="objetoAprendizaje.contenido.mover" />');
                    $("div#successMessageDiv").toggle().delay(500).toggle();
                    var div = $("div#successMessageDiv");
                    div.show('slow', function() {
                        setTimeout(function() {
                            div.hide('slow');
                        }, 2000);
                    });
                });
            }
        });
        $("input#<portlet:namespace />contenidoAC").autocomplete({
            source: "<portlet:resourceURL id='buscaContenidos'/>",
            select: function(event, ui) {
                $("div#<portlet:namespace />contenidosDiv").append("<div class='ui-state-default' id='"+ui.item.id+"'><span class='ui-icon ui-icon-arrowthick-2-n-s' style='float: left; margin-top: 5px;'></span> " + ui.item.value + "<a class='close' data-dismiss='alert'>×</a></div>");
                $("input#<portlet:namespace />contenidoAC").val("");
                var contenidosOrder = $("div#<portlet:namespace />contenidosDiv").sortable('toArray').toString();
                $.post("<portlet:resourceURL id='actualizaContenidos'/>", {id:${objeto.id}, 'contenidos[]':contenidosOrder}, function() {
                    $("p#successMessage").text('<s:message code="objetoAprendizaje.contenido.agregar" />');
                    var div = $("div#successMessageDiv");
                    div.show('slow', function() {
                        setTimeout(function() {
                            div.hide('slow');
                        }, 2000);
                    });
                });
                return false;
            }
        });
    });
</script>
