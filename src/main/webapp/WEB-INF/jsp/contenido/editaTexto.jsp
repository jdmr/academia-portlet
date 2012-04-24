<%@ include file="/WEB-INF/jsp/include.jsp" %>
<h1><s:message code="contenido.edita.texto" /></h1>
<portlet:actionURL var="actionUrl">
    <portlet:param name="action" value="actualizaTexto"/>
</portlet:actionURL>

<form:form name="contenidoForm" commandName="contenido" method="post" action="${actionUrl}" onSubmit="extractCodeFromEditor()" >
    <form:hidden path="id" />
    <form:hidden path="contenidoId" />
    <fieldset>
        <div class="control-group">
            <label for="texto"><s:message code="TEXTO" /></label>
            <liferay-ui:input-editor width="100%" />
            <input name="<portlet:namespace />texto" type="hidden" value="${texto}" />
        </div>
        <div>
            <portlet:renderURL var="verContenido" >
                <portlet:param name="action" value="ver" />
                <portlet:param name="id" value="${contenido.id}" />
            </portlet:renderURL>
            <button type="submit" name="<portlet:namespace />_crea" class="btn btn-primary btn-large" id="<portlet:namespace />_crea" ><i class="icon-ok icon-white"></i>&nbsp;<s:message code='contenido.actualiza.texto' /></button>
            <a class="btn btn-large" href="${verContenido}"><i class="icon-remove"></i> <s:message code="regresa" /></a>
        </div>
    </fieldset>
</form:form>
<aui:script>
    function <portlet:namespace />initEditor() {
        return "${textoUnicode}";
    }
    
    function extractCodeFromEditor() {
        var x = document.contenidoForm.<portlet:namespace />texto.value = window.<portlet:namespace />editor.getHTML();  
        return true;
    }
</aui:script>
