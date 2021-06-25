package de.jalin.jspwiki.plugin;

import java.util.List;
import java.util.Map;

import org.apache.wiki.WikiContext;
import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.exceptions.PluginException;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.plugin.Plugin;
import org.apache.wiki.plugin.DefaultPluginManager;

import de.jalin.jspwiki.plugin.tag.TagManager;

public class HasTagOf implements Plugin {

	@Override
	public String execute(final Context wikiContext, final Map<String, String> pluginParams) throws PluginException {
		try {
			final String queriedTag = pluginParams.get( DefaultPluginManager.PARAM_CMDLINE );
			final TagManager tagManager = TagManager.getTagManager(wikiContext.getEngine());
			final List<String> pages = tagManager.pagesPerTag(queriedTag);
			final StringBuilder stringBuild = new StringBuilder("<div class=\"hastag\">\n");
			boolean isFirstLoop = true;
			for (final String page : pages) {
				if (isFirstLoop) {
					isFirstLoop = false;
				} else {
					stringBuild.append("<br />\n");
				}
				stringBuild.append(" <a href=\"" + 
						wikiContext.getURL(WikiContext.VIEW, page) + "\">" + page + "</a>");
			}
			stringBuild.append("</div>\n");
			return stringBuild.toString();
		} catch (ProviderException e) {
			throw new PluginException(e.getMessage(), e);
		}
	}

}
