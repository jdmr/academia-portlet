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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalArticleConstants;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import java.util.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
@Component
public class TextoUtil {

    private static final Logger log = LoggerFactory.getLogger(TextoUtil.class);
    
    public JournalArticle crea(String titulo, String descripcion, String contenido, Calendar displayDate, Long userId, Long comunidadId, ServiceContext serviceContext) throws PortalException, SystemException {
        log.debug("Creando articulo {}", titulo);
        JournalArticle article = JournalArticleLocalServiceUtil.addArticle(
                userId, // UserId
                comunidadId, // GroupId
                "", // ArticleId
                true, // AutoArticleId
                JournalArticleConstants.DEFAULT_VERSION, // Version
                titulo, // Titulo
                descripcion, // Descripcion
                contenido, // Contenido
                "general", // Tipo
                "", // Estructura
                "", // Template
                displayDate.get(Calendar.MONTH), // displayDateMonth,
                displayDate.get(Calendar.DAY_OF_MONTH), // displayDateDay,
                displayDate.get(Calendar.YEAR), // displayDateYear,
                displayDate.get(Calendar.HOUR_OF_DAY), // displayDateHour,
                displayDate.get(Calendar.MINUTE), // displayDateMinute,
                0, // expirationDateMonth, 
                0, // expirationDateDay, 
                0, // expirationDateYear, 
                0, // expirationDateHour, 
                0, // expirationDateMinute, 
                true, // neverExpire
                0, // reviewDateMonth, 
                0, // reviewDateDay, 
                0, // reviewDateYear, 
                0, // reviewDateHour, 
                0, // reviewDateMinute, 
                true, // neverReview
                true, // indexable
                false, // SmallImage
                "", // SmallImageUrl
                null, // SmallFile
                null, // Images
                "", // articleURL 
                serviceContext // serviceContext
                );

        return article;
    }
    
    public String obtieneTexto(Long textoId, ThemeDisplay themeDisplay) throws PortalException, SystemException {
        JournalArticle ja = JournalArticleLocalServiceUtil.getArticle(textoId);
        String texto = null;
        if (ja != null) {
            texto = JournalArticleLocalServiceUtil.getArticleContent(ja.getGroupId(), ja.getArticleId(), "view", "" + themeDisplay.getLocale(), themeDisplay);
        }

        return texto;
    }
}
