<%@ include file="/WEB-INF/jsp/include.jsp" %>
<h1><liferay-ui:message key="contenido.nuevo" /></h1>
<portlet:actionURL var="actionUrl">
    <portlet:param name="action" value="crea"/>
</portlet:actionURL>

<form:form name="contenidoForm" commandName="contenido" method="post" action="${actionUrl}" >
    <fieldset>
        <div class="control-group">
            <label for="codigo"><liferay-ui:message key="contenido.codigo" /></label>
            <form:input path="codigo" maxlength="32"/>
            <form:errors cssClass="errors" path="codigo" cssStyle="color:red;" />
        </div>
        <div class="control-group">
            <label for="nombre"><liferay-ui:message key="contenido.nombre" /></label>
            <form:input path="nombre" maxlength="128"/>
            <form:errors cssClass="errors" path="nombre" cssStyle="color:red;" />
        </div>
        <div class="control-group">
            <label for="comunidadId"><liferay-ui:message key="contenido.comunidad" /></label>
            <form:select path="comunidadId" items="${comunidades}" />
            <form:errors cssClass="errors" path="comunidadId" />
        </div>
        <div>
            <button type="submit" name="<portlet:namespace />_crea" class="btn btn-primary btn-large" id="<portlet:namespace />_crea" ><i class="icon-ok icon-white"></i>&nbsp;<liferay-ui:message key='contenido.crea' /></button>
            <a class="btn btn-large" href="<portlet:renderURL portletMode='view'/>"><i class="icon-remove"></i> <liferay-ui:message key="contenido.regresa" /></a>
        </div>
    </fieldset>
</form:form>
<script type="text/javascript">
    $(document).ready(function() {
        $("input#codigo").focus();
    });
</script>
