package de.jalin.jspwiki.servlet;

import de.jalin.jspwiki.plugin.CustomCSSPlugin;
import de.jalin.jspwiki.plugin.tag.TagManager;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.wiki.WikiEngine;
import org.apache.wiki.api.exceptions.ProviderException;

@WebServlet(name = "CustomCSSPluginServlet", urlPatterns = { CustomCSSPlugin.CSS_SERVLET_PATH } )
public class CustomCSSPluginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		final WikiEngine wikiEngine = WikiEngine.getInstance(config);
		try {
			TagManager.getTagManager(wikiEngine);
		} catch (ProviderException e) {
			throw new ServletException(e);
		}
	}
	
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final HttpSession session = req.getSession();
        final Object cssContentObject = session.getAttribute(CustomCSSPlugin.CUSTOM_CSS_ATTRIBUTE);
        if (cssContentObject instanceof String) {
            final Writer cssWriter = resp.getWriter();
            cssWriter.write((String) cssContentObject);
            cssWriter.close();
        }
    }
    
}
