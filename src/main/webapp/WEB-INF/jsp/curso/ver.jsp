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
        <portlet:renderURL var="verSiguienteUrl" >
            <portlet:param name="action" value="verSiguiente" />
        </portlet:renderURL>
        
        <div class="row-fluid">
            <div class="span3">
                <div class="well" style='padding: 0; padding-bottom: 15px;'>
                    <ul class="nav nav-list" style='margin-right: 0;'>
                        <c:forEach items="${objetos}" var="objeto">
                            <li class="nav-header">
                                <a class="navHeader" href="#" data-id="obj_${objeto.id}"><h5>${objeto.nombre}</h5></a>
                            </li>
                            <c:forEach items="${objeto.contenidos}" var="contenido">
                                <portlet:renderURL var="verContenidoUrl" >
                                    <portlet:param name="action" value="verContenido" />
                                    <portlet:param name="contenidoId" value="${contenido.id}" />
                                </portlet:renderURL>
                                <li class="<c:if test="${contenido.activo}">active </c:if>obj_${objeto.id} objs" style="font-size: 0.8em;<c:if test='${objeto.id != objetoId}'>display:none;</c:if>">
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
                        <li id="<portlet:namespace />expandeObjetosLi" class="nav-header">
                            <a href="#" id="<portlet:namespace />expandeObjetos"><s:message code="expande.objetos" /></a>
                        </li>
                        <li id="<portlet:namespace />contraeObjetosLi" class="nav-header" style="display: none;">
                            <a href="#" id="<portlet:namespace />contraeObjetos"><s:message code="contrae.objetos" /></a>
                        </li>
                    </ul>
                </div>
            </div>
            <div class="span9">
                <c:choose>
                    <c:when test="${concluido}">
                        <h3><s:message code="concluido.titulo" /></h3>
                        <h6><s:message code="concluido.mensaje" /></h6>
                        <c:if test="${conDiploma}">
                            <form name="<portlet:namespace />diplomaForm" action="<portlet:resourceURL id='diploma'/>" method="post">
                                <div class="control-group">
                                    <button type="submit" class="btn btn-primary btn-large"><i class="icon-print icon-white"></i> <s:message code="concluido.diploma" /></button>
                                </div>
                            </form>
                        </c:if>
                    </c:when>
                    <c:when test="${concluidoPorCorreo}">
                        <h3><s:message code="concluido.titulo" /></h3>
                        <p><s:message code="concluido.con.correo.mensaje" /></p>
                        <portlet:actionURL var="direccionUrl" >
                            <portlet:param name="action" value="direccion" />
                            <portlet:param name="cursoId" value="${curso.id}" />
                        </portlet:actionURL>
                        <form name="<portlet:namespace />direccionForm" action="${direccionUrl}" method="post">
                            <div class="control-group row-fluid">
                                <div class="span6">
                                    <label for="<portlet:namespace />nombreCompleto"><s:message code="nombreCompleto" /></label>
                                    <input type="text" id="<portlet:namespace />nombreCompleto" name="<portlet:namespace />nombreCompleto" value="${nombreCompleto}" required="true" class="span12"/>
                                </div>
                            </div>
                            <div class="control-group row-fluid">
                                <div class="span6">
                                    <label for="<portlet:namespace />fechaNacimiento"><s:message code="fechaNacimiento" /></label>
                                    <input type="text" id="<portlet:namespace />fechaNacimiento" name="<portlet:namespace />fechaNacimiento" value="${fechaNacimiento}" required="true" />
                                </div>
                            </div>
                            <div class="control-group row-fluid">
                                <div class="span12">
                                    <label for="<portlet:namespace />direccion"><s:message code="direccion" /></label>
                                    <textarea id="<portlet:namespace />direccion" name="<portlet:namespace />direccion" required="true" class="span12" style="height:100px;"></textarea>
                                </div>
                            </div>
                            <div class="control-group">
                                <button type="submit" class="btn btn-primary btn-large"><i class="icon-envelope icon-white"></i> <s:message code="concluido.direccion" /></button>
                            </div>
                        </form>
                    </c:when>
                    <c:when test="${not empty video}">
                        <video id="<portlet:namespace />mediaspace" controls="controls">
                            <source src="${video}" />
                        </video>
                        <c:if test="${terminado == null}">
                            <div style="margin-top: 20px;"><a href="${verSiguienteUrl}" class="btn btn-primary btn-large"><s:message code="siguiente" /> <i class="icon-chevron-right" ></i></a></div>
                        </c:if>
                        <aui:script>
                            jwplayer('<portlet:namespace />mediaspace').setup({
                                modes : [
                                    { type : 'html5' },
                                    { type : 'flash', src : '${themeRoot}jwplayer/player.swf'}
                                ]        
                            });
                        </aui:script>
                    </c:when>
                    <c:when test="${examen != null}">
                        <div>${texto}</div>
                        <portlet:renderURL var="enviaExamenUrl" >
                            <portlet:param name="action" value="enviaExamen" />
                        </portlet:renderURL>
                        <form name="<portlet:namespace />preguntasForm" action="${enviaExamenUrl}" method="post" class="well">
                            <input type="hidden" name="<portlet:namespace />examenId" id="<portlet:namespace />examenId" value="${examen.id}" />
                            <input type="hidden" name="<portlet:namespace />contenidoId" id="<portlet:namespace />contenidoId" value="${contenidoId}" />
                            <c:forEach items="${preguntas}" var="pregunta">
                                <div class="control-group">
                                    <h5>${pregunta.texto}</h5>
                                    <c:forEach items="${pregunta.respuestas}" var="respuesta">
                                        <c:choose>
                                            <c:when test="${pregunta.esMultiple}">
                                                <label class="checkbox">
                                                    <input type="checkbox" name="<portlet:namespace/>${pregunta.id}" value="${respuesta.id}" />
                                                    ${respuesta.texto}
                                                </label>
                                            </c:when>
                                            <c:otherwise>
                                                <label class="radio">
                                                    <input type="radio" name="<portlet:namespace/>${pregunta.id}" value="${respuesta.id}" />
                                                    ${respuesta.texto}
                                                </label>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </div>
                            </c:forEach>
                            <div class="control-group">
                                <button type="submit" class="btn btn-primary btn-large"><i class="icon-upload icon-white"></i> <s:message code="enviar.respuestas" /></button>
                            </div>
                        </form>
                    </c:when>
                    <c:otherwise>
                        ${texto}
                        <c:if test="${terminado == null}">
                            <div style="margin-top: 20px;"><a href="${verSiguienteUrl}" class="btn btn-primary btn-large"><s:message code="siguiente" /> <i class="icon-chevron-right" ></i></a></div>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <script type="text/javascript">
            $(document).ready(function() {
                $("a.navHeader").click(function(e) {
                    e.preventDefault();
                    var obj = $(this).data("id");
                    $("li."+obj).toggle('fast');
                });
                $("a#<portlet:namespace />expandeObjetos").click(function(e){
                    e.preventDefault();
                    $("li#<portlet:namespace />contraeObjetosLi").toggle();
                    $("li#<portlet:namespace />expandeObjetosLi").toggle();
                    $("li.objs").show('fast');
                });
                $("a#<portlet:namespace />contraeObjetos").click(function(e){
                    e.preventDefault();
                    $("li#<portlet:namespace />expandeObjetosLi").toggle();
                    $("li#<portlet:namespace />contraeObjetosLi").toggle();
                    <c:choose>
                        <c:when test="${objetoId != null}">
                            $("li.objs").hide('fast', function() {
                                $("li.obj_"+${objetoId}).show('fast');
                            });
                        </c:when>
                        <c:otherwise>
                            $("li.objs").hide('fast');
                        </c:otherwise>
                    </c:choose>
                });
            });
        </script>
    </c:when>
    <c:otherwise>
        <div>${texto}</div>
        <c:choose>
            <c:when test="${sign_in}">
                <a href="${sign_in_url}" class="btn btn-primary btn-large"><i class="icon-key icon-white"></i> <s:message code="curso.registrar.primero" /></a>
            </c:when>
            <c:when test="${not empty curso.tipo && curso.tipo eq 'PAGADO'}">
                <c:choose>
                    <c:when test="${curso.comercio eq 'UM'}">
                        <c:url var="pagoURL" value="https://secure.um.edu.mx/umvirtual">
                            <c:param name="cuentaProyecto" value="${curso.comercioId}" />
                            <c:param name="nomina" value="${curso.codigo}" />
                            <c:param name="amount" value="${curso.precio}" />
                            <c:param name="matricula" value="${username}" />
                            <c:param name="email" value="${correo}" />
                            <c:param name="nombreAlumno" value="${nombreAlumno}" />
                        </c:url>
                        <a href="${pagoURL}" class="btn btn-primary btn-large"><i class="icon-plus icon-white"></i> <s:message code="curso.inscribe" /></a>
                    </c:when>
                    <c:otherwise>
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

<!--                        <form action="https://www.sandbox.paypal.com/cgi-bin/webscr" method="post">-->
                        <form action="https://www.paypal.com/cgi-bin/webscr" method="post">
                            <input type="hidden" name="cmd" value="_xclick">
                            <input type="hidden" name="business" value="${curso.comercioId}">
                            <input type="hidden" name="notify_url" value="${notificaUrl}">
                            <input type="hidden" name="return" value="${pagoAprobadoUrl}">
                            <input type="hidden" name="cancel_return" value="${pagoDenegadoUrl}">
                            <input type="hidden" name="lc" value="US">
                            <input type="hidden" name="item_name" value="${curso.nombre}">
                            <input type="hidden" name="item_number" value="${curso.codigo}">
                            <input type="hidden" name="amount" value="${curso.precio}">
                            <input type="hidden" name="currency_code" value="USD">
                            <input type="hidden" name="button_subtype" value="services">
                            <input type="hidden" name="no_note" value="1">
                            <input type="hidden" name="no_shipping" value="1">
                            <input type="hidden" name="bn" value="PP-BuyNowBF:btn_paynowCC_LG.gif:NonHosted">
                            <input type="image" src="https://www.paypalobjects.com/en_US/i/btn/btn_paynowCC_LG.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">
                            <img alt="" border="0" src="https://www.paypalobjects.com/en_US/i/scr/pixel.gif" width="1" height="1">
<!--                            <input type="image" src="https://www.sandbox.paypal.com/en_US/i/btn/btn_paynowCC_LG.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">
                            <img alt="" border="0" src="https://www.sandbox.paypal.com/en_US/i/scr/pixel.gif" width="1" height="1">-->
                        </form>
                    </c:otherwise>
                </c:choose>
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
