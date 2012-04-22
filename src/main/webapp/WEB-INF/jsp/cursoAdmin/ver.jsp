<%@ include file="/WEB-INF/jsp/include.jsp" %>
<div class="academia">
    <div class="dialog">
        <table>
            <tbody>
                <tr class="prop">
                    <td class="top" class="name"><liferay-ui:message key="curso.codigo" /></td>
                    <td class="top" class="value">${curso.codigo}</td>
                </tr>
                <tr class="prop">
                    <td class="top" class="name"><liferay-ui:message key="curso.nombre" /></td>
                    <td class="top" class="value">${curso.nombre}</td>
                </tr>
                <tr class="prop">
                    <td class="top" class="name"><liferay-ui:message key="curso.fechaCreacion" /></td>
                    <td class="top" class="value">${curso.fechaCreacion}</td>
                </tr>
                <tr class="prop">
                    <td class="top" class="name"><liferay-ui:message key="curso.fechaCreacion" /></td>
                    <td class="top" class="value">${curso.fechaCreacion}</td>
                </tr>
                <tr class="prop">
                    <td class="top" class="name"><liferay-ui:message key="curso.fechaModificacion" /></td>
                    <td class="top" class="value">${curso.fechaModificacion}</td>
                </tr>
                <tr class="prop">
                    <td class="top" class="name"><liferay-ui:message key="curso.introduccion"</td>
                    <td class="top" class="value">
                        <portlet:renderURL var="creaIntroUrl" >
                            <portlet:param name="action" value="intro" />
                            <portlet:param name="id" value="${curso.id}" />
                        </portlet:renderURL>
                        <span class="menuButton"><a class="edit" href="${creaIntroUrl}"><liferay-ui:message key="curso.creaIntroduccion" /></a></span>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
    <div class="nav">
        <portlet:renderURL var="editaUrl" >
            <portlet:param name="action" value="edita" />
            <portlet:param name="id" value="${curso.id}" />
        </portlet:renderURL>

        <span class="menuButton"><a class="edit" href="${editaUrl}"><liferay-ui:message key="curso.edita" /></a></span>
        <span class="menuButton"><a class="list" href="<portlet:renderURL portletMode="view"/>"><liferay-ui:message key="regresa.label" /></a></span>
    </div>
</div>
