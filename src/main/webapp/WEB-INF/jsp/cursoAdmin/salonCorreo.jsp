<%@ include file="/WEB-INF/jsp/include.jsp" %>

<h1><s:message code="salon.correo.mensaje" /></h1>

<portlet:actionURL var="actionUrl">
    <portlet:param name="action" value="enviaInvitacion"/>
    <portlet:param name="salonId" value="${salon.id}" />
</portlet:actionURL>
<form:form name="salonForm" commandName="salon" method="post" action="${actionUrl}" onSubmit="extractCodeFromEditor()" >
    <form:hidden path="id" />
    <form:hidden path="version" />
    <fieldset>
        <div class="control-group">
            <label for="<portlet:namespace />subject"><s:message code="salon.correo.asunto" /></label>
            <input type="text" name="<portlet:namespace />subject" id="<portlet:namespace />subject" value="${salon.subject}" required="true" class="span6" />
        </div>
        <div class="control-group">
            <label for="<portlet:namespace />texto"><s:message code="curso.correo.mensaje" /></label>
            <liferay-ui:input-editor width="100%"/>
            <input name="<portlet:namespace />texto" type="hidden" value="" />
        </div>
        <div>
            <button type="submit" name="<portlet:namespace />_crea" class="btn btn-primary btn-large" id="<portlet:namespace />_crea" ><i class="icon-ok icon-white"></i>&nbsp;<s:message code='curso.correo.actualiza' /></button>
            <portlet:renderURL var="verUrl" >
                <portlet:param name="action" value="salon" />
                <portlet:param name="id" value="${salon.curso.id}" />
            </portlet:renderURL>
            <a class="btn btn-large" href="${verUrl}"><i class="icon-remove"></i> <s:message code="regresa" /></a>
        </div>
    </fieldset>
</form:form>
<aui:script>
    function <portlet:namespace />initEditor() {
        document.salonForm.<portlet:namespace />subject.focus();
        return "${textoUnicode}";
    }
    
    function extractCodeFromEditor() { 
        var x = document.salonForm.<portlet:namespace />texto.value = window.<portlet:namespace />editor.getHTML();  
        return true;
    }
</aui:script>
