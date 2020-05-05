package qunar.tc.bistoury.ui.filter;

import org.apache.commons.lang.StringEscapeUtils;
import qunar.tc.bistoury.serverside.agile.Strings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author leix.xie
 * @date 2020/4/4 14:21
 * @describe
 */
public class XSSRequestWrapper extends HttpServletRequestWrapper {
    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public XSSRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = escapeHtml(values[i]);
        }
        return encodedValues;
    }

    @Override
    public String getParameter(String name) {
        return escapeHtml(super.getParameter(name));
    }

    @Override
    public String getHeader(String name) {
        return escapeHtml(super.getHeader(name));
    }

    private String escapeHtml(String value) {
        if (!Strings.isEmpty(value)) {
            return StringEscapeUtils.escapeHtml(value);
        }
        return value;
    }
}
