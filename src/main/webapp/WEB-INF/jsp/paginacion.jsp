<div class="pagination">
    <ul>
        <li class="disabled"><a href="#"><s:message code="mensaje.paginacion" arguments="${paginacion}" /></a></li>
        <c:forEach items="${paginas}" var="paginaId">
            <li <c:if test="${pagina == paginaId}" >class="active"</c:if>>
                <a href="javascript:buscaPagina(${paginaId});" >${paginaId}</a>
            </li>
        </c:forEach>
    </ul>
</div>
