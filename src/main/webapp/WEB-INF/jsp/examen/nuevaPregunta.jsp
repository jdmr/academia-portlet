<%@ include file="/WEB-INF/jsp/include.jsp" %>
<h1><s:message code="examen.pregunta.nueva" /></h1>
<portlet:actionURL var="actionUrl">
    <portlet:param name="action" value="asignaPregunta"/>
</portlet:actionURL>

<form:form name="examenForm" commandName="examenPregunta" method="post" action="${actionUrl}" >
    <form:hidden path="id.examen.id" />
    <fieldset>
        <div class="control-group">
            <label for="preguntas"><s:message code="pregunta.lista" /></label>
            <form:select path="id.pregunta.id" id="preguntas" >
                <form:option value=""><s:message code="examen.pregunta.elija" /></form:option>
                <form:options items="${preguntas}" itemLabel="nombre" itemValue="id" />
            </form:select>
            <form:errors cssClass="errors" path="id.pregunta" />
        </div>
        <div class="control-group">
            <label for="puntos"><s:message code="puntos" /></label>
            <form:input path="puntos" maxlength="128" cssStyle="text-align:right;" type="number" step="1" min="0" required="true" />
            <form:errors cssClass="errors" path="puntos" cssStyle="color:red;" />
        </div>
        <div class="control-group">
            <label for="porPregunta"><s:message code="por.pregunta" /></label>
            <form:checkbox path="porPregunta" />
            <form:errors cssClass="errors" path="porPregunta" cssStyle="color:red;" />
        </div>
        <div>
            <button type="submit" name="<portlet:namespace />_crea" class="btn btn-primary btn-large" id="<portlet:namespace />_crea" ><i class="icon-ok icon-white"></i>&nbsp;<s:message code='examen.pregunta.nueva' /></button>
            <a class="btn btn-large" href="<portlet:renderURL portletMode='view'/>"><i class="icon-remove"></i> <s:message code="regresa" /></a>
        </div>
    </fieldset>
</form:form>
<script type="text/javascript">
    $(document).ready(function() {
        $("select#preguntas").chosen().focus();
    });
</script>
