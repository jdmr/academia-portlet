<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="row-fluid">
    <h5><s:message code="inscrito.titulo" /></h5>
    <p><s:message code="inscrito.mensaje" /></p>
    <a href="<portlet:renderURL portletMode='view'/>" class="btn btn-success btn-large"><s:message code="inscrito.ver.curso" /></a>
</div>