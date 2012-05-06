<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<div class="row-fluid">
    <h5><s:message code="denegado.titulo" /></h5>
    <p><s:message code="denegado.mensaje" /></p>
    <a href="<portlet:renderURL portletMode='view'/>" class="btn btn-info btn-large"><s:message code="regresa" /></a>
</div>