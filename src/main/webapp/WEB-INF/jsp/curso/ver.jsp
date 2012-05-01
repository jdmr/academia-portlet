<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:choose>
    <c:when test="${not empty message}">
        <div class="alert alert-block alert-error fade in" role="status">
            <a class="close" data-dismiss="alert">×</a>
            <s:message code="${message}" arguments="${messageAttrs}" />
        </div>
    </c:when>
    <c:otherwise>
        <div>${texto}</div>
    </c:otherwise>
</c:choose>
