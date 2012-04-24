<%@ include file="/WEB-INF/jsp/include.jsp" %>
<h1><s:message code="contenido.nuevo.video" /></h1>
<portlet:actionURL var="actionUrl">
    <portlet:param name="action" value="creaVideo"/>
</portlet:actionURL>

<form:form name="contenidoForm" commandName="contenido" method="post" action="${actionUrl}" enctype="multipart/form-data" >
    <form:hidden path="id" />
    <form:hidden path="contenidoId" />
    <fieldset>
        <div class="control-group">
            <label for="texto"><s:message code="VIDEO" /></label>
            <input type="file" name="archivo" />
        </div>
        <div>
            <button type="submit" name="<portlet:namespace />_crea" class="btn btn-primary btn-large" id="<portlet:namespace />_crea" ><i class="icon-ok icon-white"></i>&nbsp;<s:message code='contenido.crea.video' /></button>
            <a class="btn btn-large" href="<portlet:renderURL portletMode='view'/>"><i class="icon-remove"></i> <s:message code="regresa" /></a>
        </div>
    </fieldset>
</form:form>
