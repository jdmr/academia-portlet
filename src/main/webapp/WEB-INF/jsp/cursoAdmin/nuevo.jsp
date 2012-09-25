<%@ include file="/WEB-INF/jsp/include.jsp" %>
<portlet:actionURL var="actionUrl">
    <portlet:param name="action" value="crea"/>
</portlet:actionURL>

<h1><s:message code="curso.nuevo" /></h1>

<form:form name="cursoForm" commandName="curso" method="post" action="${actionUrl}" enctype="multipart/form-data">
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
        <div id="tipoCursoDiv"<c:if test="${curso.tipo ne 'PAGADO'}"> style="display: none;" </c:if>>
            <div class="control-group">
                <label for="precio"><s:message code="precio" /></label>
                <form:input path="precio" maxlength="32" cssStyle="text-align: right;"/>
                <form:errors cssClass="errors" path="precio" cssStyle="color:red;" />
            </div>
            <div class="control-group">
                <label for="comercio"><s:message code="comercio" /></label>
                <form:select path="comercio" >
                    <form:options items="${comercios}" />
                </form:select>
                <form:errors cssClass="errors" path="comercio" cssStyle="color:red;" />
            </div>
            <div class="control-group">
                <label for="comercioId"><s:message code="comercio.id" /></label>
                <form:input path="comercioId" maxlength="64" />
                <form:errors cssClass="errors" path="comercioId" cssStyle="color:red;" />
            </div>
        </div>
        <div class="control-group">
            <label for="comunidadId"><s:message code="comunidad" /></label>
            <form:select path="comunidadId" items="${comunidades}" />
            <form:errors cssClass="errors" path="comunidadId" />
        </div>
        <div class="control-group">
            <label for="archivo">
                <s:message code="diploma" />
            </label>
            <input name="archivo" type="file" />
        </div>
        <div class="control-group">
            <label for="dias"><s:message code="dias" /></label>
            <form:input path="dias" type="number" min="0" step="1" />
            <form:errors cssClass="errors" path="dias" cssStyle="color:red;" />
        </div>
        <div class="control-group">
            <label for="correo"><s:message code="correo" /></label>
            <form:input path="correo" type="email" />
            <form:errors cssClass="errors" path="correo" cssStyle="color:red;" />
        </div>
        <div class="control-group">
            <label for="correo2"><s:message code="correo2" /></label>
            <form:input path="correo2" type="email" />
            <form:errors cssClass="errors" path="correo2" cssStyle="color:red;" />
        </div>
        <div class="control-group">
            <label>
                <form:checkbox path="usarServicioPostal" />
                <s:message code="usarServicioPostal" />
                <form:errors cssClass="errors" path="usarServicioPostal" cssStyle="color:red;" />
            </label>
        </div>
        <div class="control-group">
            <label for="horas"><s:message code="horas" /></label>
            <form:input path="horas" type="number" min="0" step="1" />
            <form:errors cssClass="errors" path="horas" cssStyle="color:red;" />
        </div>

        <div>
            <button type="submit" name="<portlet:namespace />_crea" class="btn btn-primary btn-large" id="<portlet:namespace />_crea" ><i class="icon-ok icon-white"></i>&nbsp;<s:message code='curso.crea' /></button>
            <a class="btn btn-large" href="<portlet:renderURL portletMode='view'/>"><i class="icon-remove"></i> <s:message code="regresa" /></a>
        </div>
    </fieldset>
</form:form>
<script type="text/javascript">
    $(document).ready(function() {
        $("select#comercio").chosen();
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
