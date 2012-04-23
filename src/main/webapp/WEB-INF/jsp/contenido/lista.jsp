<%@ include file="/WEB-INF/jsp/include.jsp" %>
<portlet:renderURL var="actionUrl" >
    <portlet:param name="action" value="lista" />
</portlet:renderURL>

<div class="well">
    <form name="<portlet:namespace />filtrarContenido" method="post" action="${actionUrl}" class="form-search" >
        <a class="btn btn-primary" href='<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" ><portlet:param name="action" value="nuevo"/></portlet:renderURL>'><i class="icon-file icon-white"></i> <liferay-ui:message key="contenido.nuevo" /></a>
        <input name="<portlet:namespace />filtro" type="text" class="input-medium search-query" value="${param.filtro}">
        <button type="submit" class="btn" name="<portlet:namespace />_busca"><i class="icon-search"></i><liferay-ui:message key="contenido.buscar" /></button>
    </form>
</div>
<c:if test="${contenidos != null}">
    <table id="<portlet:namespace />contenidos" class="table table-striped">
        <thead>
            <tr>

                <th><liferay-ui:message key="contenido.codigo" /></th>

                <th><liferay-ui:message key="contenido.nombre" /></th>

            </tr>
        </thead>
        <tbody>
            <c:forEach items="${contenidos}" var="contenido">
                <portlet:renderURL var="verContenido" >
                    <portlet:param name="action" value="ver" />
                    <portlet:param name="id" value="${contenido.id}" />
                </portlet:renderURL>
                <tr>

                    <td><a href="${verContenido}">${contenido.codigo}</a></td>

                    <td>${contenido.nombre}</td>

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

    <div class="paginateButtons">
        <c:if test="${offset > 0}">
            <a href="${anterior}"><i class="icon-chevron-left"></i> <liferay-ui:message key="contenido.anterior" /></a>
        </c:if>
        <c:if test="${cantidad > max}">
            <a href="${siguiente}"><liferay-ui:message key="contenido.siguiente" /> <i class="icon-chevron-right"></i></a>
        </c:if>
    </div>
</c:if>
<div>
    
</div>
<c:if test="${cursos != null}">
    <script type="text/javascript">
        highlightTableRows("<portlet:namespace />cursos")
    </script>
</c:if>
