<%@ include file="/WEB-INF/jsp/include.jsp" %>
<portlet:actionURL var="actionUrl">
    <portlet:param name="action" value="actualiza"/>
</portlet:actionURL>
<portlet:renderURL var="cancelaUrl" >
    <portlet:param name="action" value="ver" />
    <portlet:param name="id" value="${curso.id}" />
</portlet:renderURL>

<h1><s:message code="curso.edita" /></h1>

<form:form name="cursoForm" commandName="curso" method="post" action="${actionUrl}" >
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
            <label for="tipo"><s:message code="curso.tipo" /></label>
            <form:select path="tipo" items="${tipos}" />
            <form:errors cssClass="errors" path="tipo" />
        </div>
        <div class="control-group" id="tipoCursoDiv"<c:if test="${curso.tipo ne 'PAGADO'}"> style="display: none;" </c:if>>
            <label for="precio"><s:message code="precio" /></label>
            <form:input path="precio" maxlength="32" cssStyle="text-align: right;"/>
            <form:errors cssClass="errors" path="precio" cssStyle="color:red;" />
        </div>
        <div class="control-group">
            <label for="comunidadId"><s:message code="comunidad" /></label>
            <form:select path="comunidadId" items="${comunidades}" />
            <form:errors cssClass="errors" path="comunidadId" />
        </div>
        <div>
            <button type="submit" name="<portlet:namespace />_crea" class="btn btn-primary btn-large" id="<portlet:namespace />_crea" ><i class="icon-ok icon-white"></i>&nbsp;<s:message code='curso.actualiza' /></button>
            <a class="btn btn-large" href="${cancelaUrl}"><i class="icon-remove"></i> <s:message code="regresa" /></a>
        </div>
    </fieldset>
</form:form>
<script type="text/javascript">
    $(document).ready(function() {
        $("select#tipo")
            .chosen()
            .change(function() {
                if ($(this).val() == 'PAGADO') {
                    $("div#tipoCursoDiv").slideDown(function() {
                        $("input#precio").focus();
                    });
                } else {
                    $("div#tipoCursoDiv").slideUp(function() {
                        $("input#precio").val("0");
                    });
                }
            });
        $("select#comunidadId").chosen();
        $("input#codigo").focus();
    });
</script>
