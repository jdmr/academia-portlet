<%@ include file="/WEB-INF/jsp/include.jsp" %>
<portlet:renderURL var="actionUrl" >
    <portlet:param name="action" value="lista" />
</portlet:renderURL>

<div class="well">
    <form name="<portlet:namespace />filtrarExamen" method="post" action="${actionUrl}" class="form-search" >
        <a class="btn btn-primary" href='<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" ><portlet:param name="action" value="nuevo"/></portlet:renderURL>'><i class="icon-file icon-white"></i> <s:message code="examen.nuevo" /></a>
        <input name="<portlet:namespace />filtro" type="text" class="input-medium search-query" value="${param.filtro}">
        <button type="submit" class="btn" name="<portlet:namespace />_busca"><i class="icon-search"></i> <s:message code="buscar" /></button>
    </form>
</div>
<c:if test="${examenes != null}">
    <table id="<portlet:namespace />examenes" class="table table-striped">
        <thead>
            <tr>

                <th><s:message code="nombre" /></th>
                
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${examenes}" var="examen">
                <portlet:renderURL var="verExamen" >
                    <portlet:param name="action" value="ver" />
                    <portlet:param name="id" value="${examen.id}" />
                </portlet:renderURL>
                <tr>

                    <td><a href="${verExamen}">${examen.nombre}</a></td>

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
<c:if test="${examenes != null}">
    <aui:script>
        highlightTableRows("<portlet:namespace />examenes")
    </aui:script>
</c:if>
