<%@ include file="/WEB-INF/jsp/include.jsp" %>

<h1><s:message code="alumno.todos" /></h1>

<div class="well">
    <a class="btn btn-primary" href="<portlet:renderURL portletMode='view'/>"><i class="icon-list"></i> <s:message code="regresa" /></a>
</div>

<c:if test="${alumnos != null}">
    <table id="<portlet:namespace />alumnos" class="table table-striped">
        <thead>
            <tr>

                <th><s:message code="usuario" /></th>

                <th><s:message code="nombre" /></th>
                
                <th><s:message code="correo" /></th>
                
                <th><s:message code="cursos" /></th>
                
                <th style="text-align: right;"><s:message code="saldo" /></th>
                
            </tr>
        </thead>
        <tbody>
            <c:forEach items="${alumnos}" var="alumnoCurso">
                <tr>

                    <td>${alumnoCurso.alumno.usuario}</td>

                    <td>${alumnoCurso.alumno.nombreCompleto}</td>
                    
                    <td>${alumnoCurso.alumno.correo}</td>

                    <td>${alumnoCurso.cursos}</td>

                    <td style="text-align: right;"><fmt:formatNumber value="${alumnoCurso.saldo}" type="currency" currencySymbol="$" /></td>

                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>
