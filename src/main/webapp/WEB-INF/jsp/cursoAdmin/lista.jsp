<%@ include file="/WEB-INF/jsp/include.jsp" %>
<div class="academia">
    <portlet:renderURL var="actionUrl" >
        <portlet:param name="action" value="lista" />
    </portlet:renderURL>

    <div class="well">
        <form name="<portlet:namespace />filtrarCurso" method="post" action="${actionUrl}" class="form-search" >
            <input name="<portlet:namespace />filtro" type="text" class="input-medium search-query" value="${param.filtro}">
            <button type="submit" class="btn" name="<portlet:namespace />_busca"><i class="icon-search"></i><liferay-ui:message key="curso.buscar" /></button>
        </form>
    </div>
    <c:if test="${cursos != null}">
        <table id="<portlet:namespace />cursos">
            <thead>
                <tr>

                    <th><liferay-ui:message key="curso.codigo" /></th>

                    <th><liferay-ui:message key="curso.nombre" /></th>

                </tr>
            </thead>
            <tbody>
                <c:forEach items="${cursos}" var="curso" varStatus="status">
                    <portlet:renderURL var="verCurso" >
                        <portlet:param name="action" value="ver" />
                        <portlet:param name="id" value="${curso.id}" />
                    </portlet:renderURL>
                    <tr class="${(status.count % 2) == 0 ? 'odd' : 'even'}">

                        <td><a href="${verCurso}">${curso.codigo}</a></td>

                        <td>${curso.nombre}</td>

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
                <a href="${anterior}" class="prevLink"><liferay-ui:message key="anterior.label" /></a>
            </c:if>
            <c:if test="${cantidad > max}">
                <a href="${siguiente}" class="nextLink"><liferay-ui:message key="siguiente.label" /></a>
            </c:if>
        </div>
    </c:if>
    <div class="nav">
        <span class="menuButton"><a class="create" href='<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" ><portlet:param name="action" value="nuevo"/></portlet:renderURL>'><liferay-ui:message key="curso.nuevo" /></a></span>
    </div>
    <c:if test="${cursos != null}">
        <script type="text/javascript">
            highlightTableRows("<portlet:namespace />cursos")
        </script>
    </c:if>
</div>
