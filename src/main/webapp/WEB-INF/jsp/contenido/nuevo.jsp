<%@ include file="/WEB-INF/jsp/include.jsp" %>
<h1><s:message code="contenido.nuevo" /></h1>
<portlet:actionURL var="actionUrl">
    <portlet:param name="action" value="crea"/>
</portlet:actionURL>

<form:form name="contenidoForm" commandName="contenido" method="post" action="${actionUrl}" >
    <fieldset>
        <div class="control-group">
            <label for="codigo"><s:message code="codigo" /></label>
            <form:input path="codigo" maxlength="32"/>
            <form:errors cssClass="errors" path="codigo" cssStyle="color:red;" />
        </div>
        <div class="control-group">
            <label for="nombre"><s:message code="nombre" /></label>
            <form:input path="nombre" maxlength="128"/>
            <form:errors cssClass="errors" path="nombre" cssStyle="color:red;" />
        </div>
        <div class="control-group">
            <label for="tipo"><s:message code="contenido.tipo" /></label>
            <form:select path="tipo" items="${tipos}" />
            <form:errors cssClass="errors" path="tipo" />
        </div>
        <div class="control-group">
            <label for="comunidadId"><s:message code="comunidad" /></label>
            <form:select path="comunidadId" items="${comunidades}" />
            <form:errors cssClass="errors" path="comunidadId" />
        </div>
        <div>
            <button type="submit" name="<portlet:namespace />_crea" class="btn btn-primary btn-large" id="<portlet:namespace />_crea" ><i class="icon-ok icon-white"></i>&nbsp;<s:message code='contenido.crea' /></button>
            <a class="btn btn-large" href="<portlet:renderURL portletMode='view'/>"><i class="icon-remove"></i> <s:message code="regresa" /></a>
        </div>
    </fieldset>
</form:form>
<script type="text/javascript">
    $(document).ready(function() {
        $("select#tipo").chosen();
        $("select#comunidadId").chosen();
        $("input#codigo").focus();
    });
</script>
