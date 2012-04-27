<%@ include file="/WEB-INF/jsp/include.jsp" %>
<portlet:renderURL var="actionUrl" >
    <portlet:param name="action" value="lista" />
</portlet:renderURL>

<div class="well">
    <form name="<portlet:namespace />filtrarRespuesta" method="post" action="${actionUrl}" class="form-search" >
        <a class="btn btn-primary" href='<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" ><portlet:param name="action" value="nuevo"/></portlet:renderURL>'><i class="icon-file icon-white"></i> <s:message code="respuesta.nuevo" /></a>
        <input name="<portlet:namespace />filtro" type="text" class="input-medium search-query" value="${param.filtro}">
        <button type="submit" class="btn" name="<portlet:namespace />_busca"><i class="icon-search"></i><s:message code="buscar" /></button>
    </form>
</div>
<c:if test="${respuestas != null}">
    <table id="<portlet:namespace />respuestas" class="table table-striped">
        <thead>
            <tr>

                <th><s:message code="nombre" /></th>
                
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${respuestas}" var="respuesta">
                <portlet:renderURL var="verRespuesta" >
                    <portlet:param name="action" value="ver" />
                    <portlet:param name="id" value="${respuesta.id}" />
                </portlet:renderURL>
                <tr>

                    <td><a href="${verRespuesta}">${respuesta.nombre}</a></td>

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
<c:if test="${respuestas != null}">
    <aui:script>
        highlightTableRows("<portlet:namespace />respuestas")
    </aui:script>
</c:if>
