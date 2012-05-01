<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<h1><s:message code="curso.configura.titulo" /></h1>
<portlet:actionURL var="actionUrl">
    <portlet:param name="action" value="configuraCurso"/>
</portlet:actionURL>

<form action="${actionUrl}" method="post">
    <fieldset>
        <div class="row-fluid control-group">
            <select name="<portlet:namespace />cursoId" id="<portlet:namespace />cursoId" class="span12">
                <option value=""><s:message code="curso.configura.elija.curso" /></option>
                <c:forEach items="${cursos}" var="curso">
                    <option value="${curso.id}" 
                            <c:if test="${cursoId == curso.id}">selected="selected"</c:if>
                            >${curso.codigo} | ${curso.nombre}</option>
                </c:forEach>
            </select>
        </div>
        <div>
            <button type="submit" class="btn btn-primary"><i class="icon-wrench icon-white"></i> <s:message code="curso.configura.boton" /></button>
        </div>
    </fieldset>
</form>
<script type="text/javascript">
    $(document).ready(function() {
        $("select#<portlet:namespace />cursoId").chosen().focus();
    });
</script>
