<%@ include file="/WEB-INF/jsp/include.jsp" %>
<h1><s:message code="examen.nuevo" /></h1>
<portlet:actionURL var="actionUrl">
    <portlet:param name="action" value="crea"/>
</portlet:actionURL>

<form:form name="examenForm" commandName="examen" method="post" action="${actionUrl}" >
    <fieldset>
        <div class="control-group">
            <label for="nombre"><s:message code="nombre" /></label>
            <form:input path="nombre" maxlength="128"/>
            <form:errors cssClass="errors" path="nombre" cssStyle="color:red;" />
        </div>
        <div class="control-group">
            <label for="puntos"><s:message code="puntos" /></label>
            <form:input path="puntos" type="number" step="1" min="0" cssStyle="text-align: right;"/>
            <form:errors cssClass="errors" path="puntos" cssStyle="color:red;" />
        </div>
        <div class="control-group">
            <label for="comunidadId"><s:message code="comunidad" /></label>
            <form:select path="comunidadId" items="${comunidades}" />
            <form:errors cssClass="errors" path="comunidadId" />
        </div>
        <div>
            <button type="submit" name="<portlet:namespace />_crea" class="btn btn-primary btn-large" id="<portlet:namespace />_crea" ><i class="icon-ok icon-white"></i>&nbsp;<s:message code='examen.crea' /></button>
            <a class="btn btn-large" href="<portlet:renderURL portletMode='view'/>"><i class="icon-remove"></i> <s:message code="regresa" /></a>
        </div>
    </fieldset>
</form:form>
<script type="text/javascript">
    $(document).ready(function() {
        $("select#comunidadId").chosen();
        $("input#nombre").focus();
    });
</script>
