<%--
 * Copyright 2005-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
--%>

<%@ include file="/WEB-INF/jsp/include.jsp" %>

<div class="academia">

    <div class="portlet-section-body">

        <div class="alert alert-error">${exception.localizedMessage == null ? exception : exception.localizedMessage }</div>

        <h5><s:message code="exception.contactAdmin"/></h5>

    </div>

    <div class="portlet-section-footer">
        <a href="<portlet:renderURL portletMode='view'/>" class="btn btn-primary btn-large"><s:message code="button.home"/></a>
    </div>
</div>