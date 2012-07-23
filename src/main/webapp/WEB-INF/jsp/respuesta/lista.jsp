<%@ include file="/WEB-INF/jsp/include.jsp" %>
<portlet:renderURL var="actionUrl" >
    <portlet:param name="action" value="lista" />
</portlet:renderURL>

<form id="<portlet:namespace />filtraLista" name="<portlet:namespace />filtraLista" method="post" action="${actionUrl}" class="form-search" >
    <input type="hidden" name="<portlet:namespace />pagina" id="<portlet:namespace />pagina" value="${pagina}" />
    <input type="hidden" name="<portlet:namespace />order" id="<portlet:namespace />order" value="${param.order}" />
    <input type="hidden" name="<portlet:namespace />sort" id="<portlet:namespace />sort" value="${param.sort}" />
    <div class="well">
        <a class="btn btn-primary" href='<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>" ><portlet:param name="action" value="nuevo"/></portlet:renderURL>'><i class="icon-file icon-white"></i> <s:message code="respuesta.nuevo" /></a>
        <input name="<portlet:namespace />filtro" id="<portlet:namespace />filtro" type="text" class="input-medium search-query" value="${param.filtro}">
        <button type="submit" class="btn" name="<portlet:namespace />_busca" id="<portlet:namespace />_busca"><i class="icon-search"></i> <s:message code="buscar" /></button>
    </div>
    <c:if test="${respuestas != null}">
        <table id="<portlet:namespace />respuestas" class="table table-striped">
            <thead>
                <tr>

                    <th><s:message code="nombre" /></th>

                </tr>
            </thead>
            <tbody>
                <c:forEach items="${respuestas}" var="respuesta">
                    <portlet:renderURL var="verRespuesta" >
                        <portlet:param name="action" value="ver" />
                        <portlet:param name="id" value="${respuesta.id}" />
                    </portlet:renderURL>
                    <tr>

                        <td><a href="${verRespuesta}">${respuesta.nombre}</a></td>

                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <div class="row-fluid">
            <div class="pagination">
                <li class="disabled"><a href="#"><s:message code="mensaje.paginacion" arguments="${paginacion}" /></a></li>
                <c:forEach items="${paginas}" var="paginaId">
                    <li <c:if test="${pagina == paginaId}" >class="active"</c:if>>
                        <a href="javascript:buscaPagina(${paginaId});" >${paginaId}</a>
                    </li>
                </c:forEach>
            </div>
        </div>
    </c:if>
</form>
<c:if test="${respuestas != null}">
    <aui:script>
        highlightTableRows("<portlet:namespace />respuestas")
        
        function buscaPagina(paginaId) {
            $('input#<portlet:namespace />pagina').val(paginaId);
            document.forms["<portlet:namespace />filtraLista"].submit();
        }
        
        function ordena(campo) {
            if ($('input#<portlet:namespace />order').val() == campo && $('input#<portlet:namespace />sort').val() == 'asc') {
                $('input#<portlet:namespace />sort').val('desc');
            } else {
                $('input#<portlet:namespace />sort').val('asc');
            }
            $('input#order').val(campo);
            document.forms["<portlet:namespace />filtraLista"].submit();
        }
    </aui:script>
</c:if>
