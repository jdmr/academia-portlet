<%@ include file="/WEB-INF/jsp/include.jsp" %>
<portlet:renderURL var="nuevoUrl" >
    <portlet:param name="action" value="nuevo" />
</portlet:renderURL>
<portlet:renderURL var="editaUrl" >
    <portlet:param name="action" value="edita" />
    <portlet:param name="id" value="${curso.id}" />
</portlet:renderURL>
<portlet:renderURL var="alumnosUrl" >
    <portlet:param name="action" value="alumnos" />
    <portlet:param name="cursoId" value="${curso.id}" />
</portlet:renderURL>
<portlet:actionURL var="eliminaUrl" >
    <portlet:param name="action" value="elimina" />
    <portlet:param name="id" value="${curso.id}" />
</portlet:actionURL>
<portlet:renderURL var="salonUrl" >
    <portlet:param name="action" value="salon" />
    <portlet:param name="cursoId" value="${curso.id}" />
</portlet:renderURL>

<div class="well">
    <a class="btn btn-primary" href="<portlet:renderURL portletMode='view'/>"><i class="icon-list icon-white"></i> <s:message code="curso.lista" /></a>
    <a class="btn btn-primary" href="${nuevoUrl}"><i class="icon-file icon-white"></i> <s:message code="curso.nuevo" /></a>
    <a class="btn btn-primary" href="${editaUrl}"><i class="icon-edit icon-white"></i> <s:message code="curso.edita" /></a>
    <c:choose>
        <c:when test="${curso.intro == null}">
            <portlet:renderURL var="introUrl" >
                <portlet:param name="action" value="intro" />
                <portlet:param name="id" value="${curso.id}" />
            </portlet:renderURL>
            <a class="btn btn-primary" href="${introUrl}"><i class="icon-edit icon-white"></i> <s:message code="curso.intro.nueva" /></a>
        </c:when>
        <c:otherwise>
            <portlet:renderURL var="introUrl" >
                <portlet:param name="action" value="editaIntro" />
                <portlet:param name="id" value="${curso.id}" />
            </portlet:renderURL>
            <a class="btn btn-primary" href="${introUrl}"><i class="icon-edit icon-white"></i> <s:message code="curso.intro.edita" /></a>
        </c:otherwise>
    </c:choose>
    <c:choose>
        <c:when test="${curso.correoId == null}">
            <portlet:renderURL var="correoUrl" >
                <portlet:param name="action" value="correo" />
                <portlet:param name="id" value="${curso.id}" />
            </portlet:renderURL>
            <a class="btn btn-primary" href="${correoUrl}"><i class="icon-edit icon-white"></i> <s:message code="curso.correo.mensaje" /></a>
        </c:when>
        <c:otherwise>
            <portlet:renderURL var="correoUrl" >
                <portlet:param name="action" value="editaCorreo" />
                <portlet:param name="id" value="${curso.id}" />
            </portlet:renderURL>
            <a class="btn btn-primary" href="${correoUrl}"><i class="icon-edit icon-white"></i> <s:message code="curso.correo.mensaje" /></a>
        </c:otherwise>
    </c:choose>
    <a class="btn btn-primary" href="${alumnosUrl}"><i class="icon-edit icon-white"></i> <s:message code="curso.alumnos" /></a>
    <a class="btn btn-primary" href="${salonUrl}"><i class="icon-edit icon-white"></i> <s:message code="curso.salon" /></a>
    <a class="btn btn-danger"  href="${eliminaUrl}" onclick="return confirm('<s:message code="curso.elimina.confirma"/>')"><i class="icon-ban-circle icon-white"></i> <s:message code="curso.elimina" /></a>
</div>
<c:if test="${not empty message}">
    <div class="alert alert-block ${messageClass} fade in">
        <a class="close" data-dismiss="alert">×</a>
        <s:message code="${message}" />
    </div>
</c:if>
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
    <div class="span6">
        <h5><s:message code="dias" /></h5>
        <h3>${curso.dias}</h3>
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
<div class="row-fluid">
    <div class="span6">
        <h5><s:message code="correo" /></h5>
        <h3>${curso.correo}</h3>
    </div>
    <div class="span6">
        <h5><s:message code="correo2" /></h5>
        <h3>${curso.correo2}</h3>
    </div>
</div>
<div class="row-fluid">
    <div class="span6">
        <label class="checkbox">
            <input type="checkbox" disabled="disabled" <c:if test="${curso.usarServicioPostal}">checked="checked"</c:if> style="margin-top: 14px;" />
            <h3><s:message code="usarServicioPostal" /></h3>
        </label>
    </div>
</div>
<div class="row-fluid">
    <div class="span6">
        <label>
            <h3><s:message code="curso.agrega.objeto" /></h3>
            <input type="text" name="<portlet:namespace />objetoAC" id="<portlet:namespace />objetoAC" class="span12" />
        </label>
        <div class="alert alert-block alert-success" id="successMessageDiv" style="display:none;">
            <p id="successMessage">This is a test</p>
        </div>
    </div>
</div>
<div class="row-fluid">
    <div class="span6" id="objetos">
        <div id="<portlet:namespace />objetosDiv">
            <c:forEach items="${seleccionados}" var="objeto">
                <div class="ui-state-default" id="${objeto.id}" data-id="${objeto.id}"><span class="ui-icon ui-icon-arrowthick-2-n-s" style="float: left; margin-top: 5px;"></span> ${objeto.codigo} | ${objeto.nombre} <a class="close" data-dismiss="alert">×</a></div>
            </c:forEach>
        </div>
    </div>
</div>

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
<portlet:resourceURL var="vistaPreviaCorreoUrl" >
    <portlet:param name="action" value="vistaPreviaCorreo" />
    <portlet:param name="cursoId" value="${curso.id}" />
</portlet:resourceURL>
<div class="well" style="margin-top: 10px;">
    <a id="vistaPreviaLink" class="btn btn-primary" href="${vistaPreviaUrl}"><i class="icon-eye-open icon-white"></i> <s:message code="curso.vista.previa" /></a>
    <a id="vistaPreviaIntroLink" class="btn btn-primary" href="${vistaPreviaIntroUrl}"><i class="icon-eye-open icon-white"></i> <s:message code="curso.vista.previa.intro" /></a>
    <a id="vistaPreviaCorreoLink" class="btn btn-primary" href="${vistaPreviaCorreoUrl}"><i class="icon-envelope icon-white"></i> <s:message code="curso.vista.previa.correo" /></a>
</div>
<div id="vistaPrevia" class="row-fluid">
    <div>${texto}</div>
</div>
    
<script type="text/javascript">
    $(document).ready(function() {
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
        
        $("a#vistaPreviaCorreoLink").click(function(e) {
            e.preventDefault();
            container.hide("slide",{direction:"up"});
            container.load('${vistaPreviaCorreoUrl}', {}, function() {
                container.show("slide",{direction:"up"});
            });
        });
        
        $("div#objetos").on('closed', 'div.ui-state-default',function() {
            $($(this).data('id')).remove();
            setTimeout(function(){
                var objetos = $("div#<portlet:namespace />objetosDiv").sortable('toArray').toString();
                $.post("<portlet:resourceURL id='actualizaObjetos'/>", {id:${curso.id}, 'objetos[]':objetos}, function() {
                    $("p#successMessage").text('<s:message code="curso.objetoAprendizaje.eliminar" />');
                    var div = $("div#successMessageDiv");
                    div.show('slow', function() {
                        setTimeout(function() {
                            div.hide('slow');
                        }, 2000);
                    });
                });
            }, 500);
        });
                $("div#<portlet:namespace />objetosDiv").sortable({
            update: function(event, ui) {
                var objetos = $(this).sortable('toArray').toString();
                $.post("<portlet:resourceURL id='actualizaObjetos'/>", {id:${curso.id}, 'objetos[]':objetos}, function() {
                    $("p#successMessage").text('<s:message code="curso.objetoAprendizaje.mover" />');
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
        $("input#<portlet:namespace />objetoAC").autocomplete({
            source: "<portlet:resourceURL id='buscaObjetos'/>",
            select: function(event, ui) {
                $("div#<portlet:namespace />objetosDiv").append("<div class='ui-state-default' id='"+ui.item.id+"'><span class='ui-icon ui-icon-arrowthick-2-n-s' style='float: left; margin-top: 5px;'></span> " + ui.item.value + "<a class='close' data-dismiss='alert'>×</a></div>");
                $("input#<portlet:namespace />objetoAC").val("");
                var objetos = $("div#<portlet:namespace />objetosDiv").sortable('toArray').toString();
                $.post("<portlet:resourceURL id='actualizaObjetos'/>", {id:${curso.id}, 'objetos[]':objetos}, function() {
                    $("p#successMessage").text('<s:message code="curso.objetoAprendizaje.agregar" />');
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

        $("input#<portlet:namespace />objetoAC").focus();
    });
    
    function cargaContenido(contenidoUrl) {
        var container = $("div#vistaPrevia");
        //container.hide("slide",{direction:"up"});
        container.load(contenidoUrl, {url:'${vistaPreviaUrl}'}, function() {
            //container.show("slide",{direction:"up"});
        });
    }
</script>
