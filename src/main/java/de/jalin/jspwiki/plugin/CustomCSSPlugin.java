package de.jalin.jspwiki.plugin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.exceptions.PluginException;
import org.apache.wiki.api.plugin.Plugin;
import org.apache.wiki.plugin.DefaultPluginManager;
import org.apache.wiki.ui.TemplateManager;

public class CustomCSSPlugin implements Plugin {

    public static final String CUSTOM_CSS_ATTRIBUTE = "dejalincustomcss";
    public static final String CSS_SERVLET_PATH = "/custom-css-plugin.css";

	@Override
	public String execute(final Context context, final Map<String, String> params) throws PluginException {
        TemplateManager.addResourceRequest(context, TemplateManager.RESOURCE_STYLESHEET, CSS_SERVLET_PATH);
        final String cssBody = params.get( DefaultPluginManager.PARAM_BODY );
        final HttpServletRequest httpRequest = context.getHttpRequest();
        if (httpRequest != null) {
    		final HttpSession session = httpRequest.getSession();
            session.setAttribute(CUSTOM_CSS_ATTRIBUTE, cssBody);
        }
        return "";
	}
    
}
