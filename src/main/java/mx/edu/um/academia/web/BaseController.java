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
package mx.edu.um.academia.web;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;

/**
 *
 * @author J. David Mendoza <jdmendoza@um.edu.mx>
 */
public class BaseController {

    protected final transient Logger log = LoggerFactory.getLogger(getClass());

    protected ThemeDisplay getThemeDisplay(RenderRequest request) {
        return (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
    }

    protected ThemeDisplay getThemeDisplay(ActionRequest request) {
        return (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
    }

    protected void pagina(Map<String, Object> params, Model modelo, String lista, Long pagina) {
        if (pagina != null) {
            params.put("pagina", pagina);
            modelo.addAttribute("pagina", pagina);
        } else {
            pagina = 1L;
            modelo.addAttribute("pagina", pagina);
        }
        // inicia paginado
        Long cantidad = (Long) params.get("cantidad");
        Integer max = (Integer) params.get("max");
        List<Long> paginas = paginacion(pagina, cantidad, max);
        List listado = (List) params.get(lista);
        Long primero = ((pagina - 1) * max) + 1;
        Long ultimo = primero + (listado.size() - 1);
        String[] paginacion = new String[]{primero.toString(), ultimo.toString(), cantidad.toString()};
        modelo.addAttribute("paginacion", paginacion);
        modelo.addAttribute("paginas", paginas);
        // termina paginado
    }

    private List<Long> paginacion(Long pagina, Long cantidad, Integer max) {
        Long cantidadDePaginas = cantidad / max;
        log.debug("Paginacion: {} {} {} {}", new Object[]{pagina, cantidad, max, cantidadDePaginas});
        Set<Long> paginas = new LinkedHashSet<>();
        long h = pagina - 1;
        long i = pagina;
        long j = pagina + 1;
        boolean esMiles = false;
        boolean esQuinientos = false;
        boolean esCientos = false;
        boolean esCincuentas = false;
        boolean esDecenas = false;
        boolean iniciado = false;
        if (h > 0 && h > 1000) {
            for (long y = 0; y < h; y += 1000) {
                if (y == 0) {
                    iniciado = true;
                    paginas.add(1l);
                } else {
                    paginas.add(y);
                }
            }
        } else if (h > 0 && h > 500) {
            for (long y = 0; y < h; y += 500) {
                if (y == 0) {
                    iniciado = true;
                    paginas.add(1l);
                } else {
                    paginas.add(y);
                }
            }
        } else if (h > 0 && h > 100) {
            for (long y = 0; y < h; y += 100) {
                if (y == 0) {
                    iniciado = true;
                    paginas.add(1l);
                } else {
                    paginas.add(y);
                }
            }
        } else if (h > 0 && h > 50) {
            for (long y = 0; y < h; y += 50) {
                if (y == 0) {
                    iniciado = true;
                    paginas.add(1l);
                } else {
                    paginas.add(y);
                }
            }
        } else if (h > 0 && h > 10) {
            for (long y = 0; y < h; y += 10) {
                if (y == 0) {
                    iniciado = true;
                    paginas.add(1l);
                } else {
                    paginas.add(y);
                }
            }
        }
        if (i > 1 && i < 4) {
            for (long x = 1; x < i; x++) {
                paginas.add(x);
            }
        } else if (h > 0) {
            if (!iniciado) {
                paginas.add(1L);
            }
            for (long x = h; x < i; x++) {
                paginas.add(x);
            }
        }
        do {
            paginas.add(i);
            if (i > j) {
                if (esMiles || (i + 1000) < cantidadDePaginas) {
                    esMiles = true;
                    i -= i % 1000;
                    i += 999;
                } else if (esQuinientos || (i + 500) < cantidadDePaginas) {
                    esQuinientos = true;
                    i -= i % 500;
                    i += 499;
                } else if (esCientos || (i + 100) < cantidadDePaginas) {
                    esCientos = true;
                    i -= i % 100;
                    i += 99;
                } else if (esCincuentas || (i + 50) < cantidadDePaginas) {
                    esCincuentas = true;
                    i -= i % 50;
                    i += 49;
                } else if (esDecenas || (i + 10) < cantidadDePaginas) {
                    esDecenas = true;
                    i -= i % 10;
                    i += 9;
                }
            }
        } while (i++ < cantidadDePaginas);
        if (cantidadDePaginas > 0) {
            paginas.add(cantidadDePaginas);
        }

        if (cantidad > max && paginas.size() == 1) {
            paginas.add(2l);
        }

        log.debug("Paginas {}: {}", pagina, paginas);
        return new ArrayList<>(paginas);
    }
}
