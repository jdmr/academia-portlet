<%@ include file="/WEB-INF/jsp/include.jsp" %>
<portlet:renderURL var="actionUrl" >
    <portlet:param name="action" value="lista" />
</portlet:renderURL>

<div class="well">
    <form name="<portlet:namespace />filtrarPregunta" method="post" action="${actionUrl}" class="form-search" >
        <a class="btn btn-primary" href='<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" ><portlet:param name="action" value="nuevo"/></portlet:renderURL>'><i class="icon-file icon-white"></i> <s:message code="pregunta.nuevo" /></a>
        <input name="<portlet:namespace />filtro" type="text" class="input-medium search-query" value="${param.filtro}">
        <button type="submit" class="btn" name="<portlet:namespace />_busca"><i class="icon-search"></i><s:message code="buscar" /></button>
    </form>
</div>
<c:if test="${preguntas != null}">
    <table id="<portlet:namespace />preguntas" class="table table-striped">
        <thead>
            <tr>

                <th><s:message code="nombre" /></th>
                
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${preguntas}" var="pregunta">
                <portlet:renderURL var="verPregunta" >
                    <portlet:param name="action" value="ver" />
                    <portlet:param name="id" value="${pregunta.id}" />
                </portlet:renderURL>
                <tr>

                    <td><a href="${verPregunta}">${pregunta.nombre}</a></td>

                </tr>
            </c:forEach>
        </tbody>
    </table>
    <portlet:renderURL var="anterior" >
        <portlet:param name="max" value="${max}" />
        <portlet:param name="offset" value="${offset}" />
        <portlet:param name="direccion" value="0" />
    </portlet:renderURL>

    <portlet:renderURL var="siguiente" >
        <portlet:param name="max" value="${max}" />
        <portlet:param name="offset" value="${offset}" />
        <portlet:param name="direccion" value="1" />
    </portlet:renderURL>

    <div>
        <c:if test="${offset > 0}">
            <a href="${anterior}"><i class="icon-chevron-left"></i> <s:message code="anterior" /></a>
        </c:if>
        <c:if test="${cantidad > max}">
            <a href="${siguiente}"><s:message code="siguiente" /> <i class="icon-chevron-right"></i></a>
        </c:if>
    </div>
</c:if>
<c:if test="${preguntas != null}">
    <aui:script>
        highlightTableRows("<portlet:namespace />preguntas")
    </aui:script>
</c:if>
