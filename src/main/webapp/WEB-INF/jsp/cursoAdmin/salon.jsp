<%@ include file="/WEB-INF/jsp/include.jsp" %>
<portlet:renderURL var="regresarUrl" >
    <portlet:param name="action" value="ver" />
    <portlet:param name="id" value="${curso.id}" />
</portlet:renderURL>
<portlet:renderURL var="invitacionUrl" >
    <portlet:param name="action" value="invitacion" />
    <portlet:param name="salonId" value="${salon.id}" />
</portlet:renderURL>
<portlet:actionURL var="eliminaUrl" >
    <portlet:param name="action" value="eliminaSalon" />
    <portlet:param name="salonId" value="${salon.id}" />
</portlet:actionURL>

<div class="well">
    <a class="btn btn-primary" href="${regresarUrl}"><i class="icon-file icon-white"></i> <s:message code="curso" /></a>
    <a class="btn btn-primary" href="${invitacionUrl}"><i class="icon-envelope icon-white"></i> <s:message code="salon.invitacion" /></a>
    <a class="btn btn-danger"  href="${eliminaUrl}" onclick="return confirm('<s:message code="salon.elimina.confirma"/>')"><i class="icon-ban-circle icon-white"></i> <s:message code="salon.elimina" /></a>
</div>
<c:if test="${not empty message}">
    <div class="alert alert-block ${messageClass} fade in">
        <a class="close" data-dismiss="alert">×</a>
        <s:message code="${message}" />
    </div>
</c:if>
<div class="row-fluid">
    <div class="span6">
        <h5><s:message code="meetingID" /></h5>
        <h3>${salon.meetingID}</h3>
    </div>
    <div class="span6">
        <h5><s:message code="nombre" /></h5>
        <h3>${salon.name}</h3>
    </div>
</div>
<div class="row-fluid">
    <div class="span6">
        <h5><s:message code="attendeePW" /></h5>
        <h3>${salon.attendeePW}</h3>
    </div>
    <div class="span6">
        <h5><s:message code="moderatorPW" /></h5>
        <h3>${salon.moderatorPW}</h3>
    </div>
</div>
<div class="row-fluid">
    <div class="span12">
        <h5><s:message code="curso.salon" /></h5>
        <a href="${salon.ligaAcceso}" class="btn btn-primary btn-large" target="_blank"><s:message code="salon.entrar" /></a>
    </div>
</div>
<c:if test="${grabaciones != null}">
    <c:forEach items="${grabaciones}" var="grabacion">
        <div class="row-fluid" style="padding-top: 10px;">
            <div class="span4">
                <h5><s:message code="salon.grabacion.inicio" /></h5>
                <h3><fmt:formatDate value="${grabacion.inicio}" timeZone="${timeZone}" pattern="yyyy/MM/dd HH:mm Z"/></h3>
            </div>
            <div class="span4">
                <h5><s:message code="salon.grabacion.termino" /></h5>
                <h3><fmt:formatDate value="${grabacion.termino}" timeZone="${timeZone}" pattern="yyyy/MM/dd HH:mm Z"/></h3>
            </div>
            <div class="span2">
                <h5><s:message code="salon.grabacion.duracion" /></h5>
                <h3>${grabacion.duracion}</h3>
            </div>
            <div class="span2">
                <h5><s:message code="salon.grabacion.url" /></h5>
                <h3>
                    <a href="${grabacion.url}" class="btn btn-primary" target="_blank"><s:message code="salon.grabacion.ver" /></a>
                    <a href="${grabacion.elimina}" class="btn btn-danger" onclick="return confirm('<s:message code="salon.grabacion.elimina.confirma"/>')" target="_blank"><s:message code="salon.grabacion.elimina" /></a>
                </h3>
            </div>
        </div>
    </c:forEach>
</c:if>
