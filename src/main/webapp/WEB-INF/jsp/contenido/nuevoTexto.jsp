<%@ include file="/WEB-INF/jsp/include.jsp" %>
<h1><s:message code="contenido.nuevo.texto" /></h1>
<portlet:actionURL var="actionUrl">
    <portlet:param name="action" value="creaTexto"/>
</portlet:actionURL>

<form:form name="contenidoForm" commandName="contenido" method="post" action="${actionUrl}" onSubmit="extractCodeFromEditor()" >
    <form:hidden path="id" />
    <form:hidden path="contenidoId" />
    <fieldset>
        <div class="control-group">
            <label for="texto"><s:message code="TEXTO" /></label>
            <liferay-ui:input-editor width="100%"/>
            <input name="<portlet:namespace />texto" type="hidden" value="" />
        </div>
        <div>
            <button type="submit" name="<portlet:namespace />_crea" class="btn btn-primary btn-large" id="<portlet:namespace />_crea" ><i class="icon-ok icon-white"></i>&nbsp;<s:message code='contenido.crea.texto' /></button>
            <a class="btn btn-large" href="<portlet:renderURL portletMode='view'/>"><i class="icon-remove"></i> <s:message code="regresa" /></a>
        </div>
    </fieldset>
</form:form>
<aui:script>
    function <portlet:namespace />initEditor() {
        return "";
    }
    
    function extractCodeFromEditor() { 
        var x = document.contenidoForm.<portlet:namespace />texto.value = window.<portlet:namespace />editor.getHTML();  
        return true;
    }
</aui:script>
