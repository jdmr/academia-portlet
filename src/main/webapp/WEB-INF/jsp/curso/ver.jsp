<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<c:choose>
    <c:when test="${not empty message}">
        <div class="alert alert-block alert-error fade in" role="status">
            <a class="close" data-dismiss="alert">×</a>
            <s:message code="${message}" arguments="${messageAttrs}" />
        </div>
    </c:when>
    <c:when test="${objetos != null}">
        <div class="row-fluid">
            <div class="span3">
                <div class="well" style='padding: 0; padding-bottom: 15px;'>
                    <ul class="nav nav-list" style='margin-right: 0;'>
                        <c:forEach items="${objetos}" var="objeto">
                            <li class="nav-header"><h5>${objeto.nombre}</h5></li>
                            <c:forEach items="${objeto.contenidos}" var="contenido">
                                <portlet:renderURL var="verContenidoUrl" >
                                    <portlet:param name="action" value="verContenido" />
                                    <portlet:param name="cursoId" value="${curso.id}" />
                                    <portlet:param name="contenidoId" value="${contenido.id}" />
                                </portlet:renderURL>
                                <li<c:if test="${contenido.activo}"> class="active"</c:if> style="font-size: 0.8em;">
                                    <a href="${verContenidoUrl}">
                                        <c:choose>
                                            <c:when test="${contenido.alumno.iniciado != null && contenido.alumno.terminado == null}">
                                                <i class="icon-eye-open"></i>
                                            </c:when>
                                            <c:when test="${contenido.alumno.terminado == null}">
                                                <i class="icon-ban-circle"></i>
                                            </c:when>
                                            <c:otherwise>
                                                <i class="icon-ok-circle"></i>
                                            </c:otherwise>
                                        </c:choose>
                                        ${contenido.nombre}
                                    </a>
                                </li>
                            </c:forEach>
                        </c:forEach>
                    </ul>
                </div>
            </div>
            <div class="span9">
                <c:choose>
                    <c:when test="${not empty video}">
                        <video id="<portlet:namespace />mediaspace" controls="controls">
                            <source src="${video}" />
                        </video>

                        <aui:script>
                            jwplayer('<portlet:namespace />mediaspace').setup({
                                modes : [
                                    { type : 'html5' },
                                    { type : 'flash', src : '/academia-theme/jwplayer/player.swf'}
                                ]        
                            });
                        </aui:script>
                    </c:when>
                    <c:otherwise>${texto}</c:otherwise>
                </c:choose>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div>${texto}</div>
        <c:choose>
            <c:when test="${sign_in}">
                <a href="${sign_in_url}" class="btn btn-primary btn-large"><i class="icon-key icon-white"></i> <s:message code="curso.registrar.primero" /></a>
            </c:when>
            <c:when test="${not empty curso.tipo && curso.tipo eq 'PAGADO'}">
                <portlet:actionURL var="notificaUrl" >
                    <portlet:param name="action" value="inscribeAlumno" />
                    <portlet:param name="cursoId" value="${curso.id}" />
                </portlet:actionURL>
                <portlet:renderURL var="pagoAprobadoUrl" >
                    <portlet:param name="action" value="pagoAprobado" />
                    <portlet:param name="cursoId" value="${curso.id}" />
                </portlet:renderURL>
                <portlet:renderURL var="pagoDenegadoUrl" >
                    <portlet:param name="action" value="pagoDenegado" />
                    <portlet:param name="cursoId" value="${curso.id}" />
                </portlet:renderURL>

                <form action="https://www.sandbox.paypal.com/cgi-bin/webscr" method="post">
                    <input type="hidden" name="cmd" value="_xclick">
                    <input type="hidden" name="business" value="VNC2SSQ79K5WN">
                    <input type="hidden" name="notify_url" value="${notificaUrl}">
                    <input type="hidden" name="return" value="${pagoAprobadoUrl}">
                    <input type="hidden" name="cancel_return" value="${pagoDenegadoUrl}">
                    <input type="hidden" name="lc" value="US">
                    <input type="hidden" name="item_name" value="TEST Course">
                    <input type="hidden" name="item_number" value="205">
                    <input type="hidden" name="amount" value="10.00">
                    <input type="hidden" name="currency_code" value="USD">
                    <input type="hidden" name="button_subtype" value="services">
                    <input type="hidden" name="no_note" value="1">
                    <input type="hidden" name="no_shipping" value="1">
                    <input type="hidden" name="bn" value="PP-BuyNowBF:btn_paynowCC_LG.gif:NonHosted">
                    <input type="image" src="https://www.sandbox.paypal.com/en_US/i/btn/btn_paynowCC_LG.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">
                    <img alt="" border="0" src="https://www.sandbox.paypal.com/en_US/i/scr/pixel.gif" width="1" height="1">
                </form>
            </c:when>
            <c:otherwise>
                <portlet:actionURL var="inscribeAlumnoUrl" >
                    <portlet:param name="action" value="inscribeAlumno" />
                    <portlet:param name="cursoId" value="${curso.id}" />
                </portlet:actionURL>
                <a href="${inscribeAlumnoUrl}" class="btn btn-primary btn-large"><i class="icon-plus icon-white"></i> <s:message code="curso.inscribe" /></a>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>
