<%@ include file="/WEB-INF/jsp/include.jsp" %>
<h1><s:message code="objeto.nuevo" /></h1>
<portlet:actionURL var="actionUrl">
    <portlet:param name="action" value="actualiza"/>
</portlet:actionURL>

<form:form name="objetoForm" commandName="objeto" method="post" action="${actionUrl}" >
    <form:hidden path="id" />
    <form:hidden path="version" />
    <fieldset>
        <div class="control-group">
            <label for="codigo"><s:message code="codigo" /></label>
            <form:input path="codigo" maxlength="32" />
            <form:errors cssClass="errors" path="codigo" cssStyle="color:red;" />
        </div>
        <div class="control-group">
            <label for="nombre"><s:message code="nombre" /></label>
            <form:input path="nombre" maxlength="128" cssClass="span6"/>
            <form:errors cssClass="errors" path="nombre" cssStyle="color:red;" />
        </div>
        <div class="control-group">
            <label for="descripcion"><s:message code="descripcion" /></label>
            <form:textarea path="descripcion" cssClass="span6" cssStyle="height: 150px;" />
            <form:errors cssClass="errors" path="descripcion" />
        </div>
        <div class="control-group">
            <label for="comunidadId"><s:message code="comunidad" /></label>
            <form:select path="comunidadId" items="${comunidades}" />
            <form:errors cssClass="errors" path="comunidadId" />
        </div>
        <div>
            <portlet:renderURL var="verObjeto" >
                <portlet:param name="action" value="ver" />
                <portlet:param name="id" value="${objeto.id}" />
            </portlet:renderURL>
            <button type="submit" name="<portlet:namespace />_crea" class="btn btn-primary btn-large" id="<portlet:namespace />_crea" ><i class="icon-ok icon-white"></i>&nbsp;<s:message code='objeto.crea' /></button>
            <a class="btn btn-large" href="${verObjeto}"><i class="icon-remove"></i> <s:message code="regresa" /></a>
        </div>
    </fieldset>
</form:form>
<script type="text/javascript">
    $(document).ready(function() {
        $("select#comunidadId").chosen();
        $("input#codigo").focus();
    });
</script>
