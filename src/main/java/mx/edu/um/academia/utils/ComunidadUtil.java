/*
 * The MIT License
 *
 * Copyright 2012 Universidad de Montemorelos A. C.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package mx.edu.um.academia.utils;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
public class ComunidadUtil {
    private static final Logger log = LoggerFactory.getLogger(ComunidadUtil.class);

    public ComunidadUtil() {
        log.info("Nueva instancia de comunidad util");
    }

    public static Map<Long, String> obtieneComunidades(RenderRequest request) throws SystemException, PortalException {
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        return obtieneComunidades(themeDisplay);
    }
    
    public static Map<Long, String> obtieneComunidades(ActionRequest request) throws SystemException, PortalException {
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        return obtieneComunidades(themeDisplay);
    }
    
    public static Map<Long, String> obtieneComunidades(ResourceRequest request) throws SystemException, PortalException {
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        return obtieneComunidades(themeDisplay);
    }
    
    public static Map<Long, String> obtieneComunidades(ThemeDisplay themeDisplay) throws SystemException, PortalException {
        log.debug("Buscando comunidades");
        List<Integer> types = new ArrayList<>();

        types.add(new Integer(GroupConstants.TYPE_SITE_OPEN));
        types.add(new Integer(GroupConstants.TYPE_SITE_RESTRICTED));
        types.add(new Integer(GroupConstants.TYPE_SITE_PRIVATE));

        LinkedHashMap<String, Object> groupParams = new LinkedHashMap<>();
        groupParams.put("types", types);
        groupParams.put("active", Boolean.TRUE);

        List<Group> comunidadesList = GroupLocalServiceUtil.search(themeDisplay.getCompanyId(), null, null, groupParams, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
        Map<Long, String> comunidades = new LinkedHashMap<>();
        for (Group group : comunidadesList) {
            log.debug("Group |{}|{}|{}|", new Object[]{group.getName(), group.getDescriptiveName(), group.getDescription()});
            comunidades.put(group.getGroupId(), group.getDescriptiveName());
        }
        return comunidades;
    }
    
}
