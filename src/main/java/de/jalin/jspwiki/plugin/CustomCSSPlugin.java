package de.jalin.jspwiki.plugin;

import java.util.Map;
import javax.servlet.http.HttpSession;
import org.apache.wiki.WikiContext;
import org.apache.wiki.api.exceptions.PluginException;
import org.apache.wiki.api.plugin.WikiPlugin;
import org.apache.wiki.plugin.DefaultPluginManager;
import org.apache.wiki.ui.TemplateManager;

public class CustomCSSPlugin implements WikiPlugin {

    public static final String CUSTOM_CSS_ATTRIBUTE = "dejalincustomcss";

    @Override
    public String execute(final WikiContext context, final Map<String, String> params) throws PluginException {
        TemplateManager.addResourceRequest(context, TemplateManager.RESOURCE_STYLESHEET, "/custom-css-plugin.css");
        final String cssBody = params.get( DefaultPluginManager.PARAM_BODY );
        final HttpSession session = context.getHttpRequest().getSession();
        session.setAttribute(CUSTOM_CSS_ATTRIBUTE, cssBody);
        return "";
    }
    
}
