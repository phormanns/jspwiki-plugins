package de.jalin.jspwiki.plugin;

import java.util.Map;

import org.apache.wiki.WikiContext;
import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.exceptions.PluginException;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.plugin.Plugin;
import org.apache.wiki.plugin.DefaultPluginManager;

import de.jalin.jspwiki.plugin.tag.TagManager;

public class Tag implements Plugin {

	@Override
	public String execute(final Context wikiContext, final Map<String, String> pluginParams) throws PluginException {
		try {
			final TagManager tagManager = TagManager.getTagManager(wikiContext.getEngine());
			final StringBuilder stringBuild = new StringBuilder("<div class=\"tags\">\n");
			stringBuild.append("<b>Tags:</b>&nbsp; ");
			final String providedTags = pluginParams.get( DefaultPluginManager.PARAM_CMDLINE );
			final String[] tagsArray = providedTags.split("[ ,;]+");
			boolean isFirstLoop = true;
			for (final String tag : tagsArray) {
				if (isFirstLoop) {
					isFirstLoop = false;
				} else {
					stringBuild.append(",");
				}
				stringBuild.append(" <a href=\"" + 
						wikiContext.getURL(WikiContext.VIEW, tag) + "\">" + tag + "</a>");
			}
			tagManager.register(wikiContext.getPage().getName(), tagsArray);
			stringBuild.append("<br />\n");
			stringBuild.append("</div>\n");
			return stringBuild.toString();
		} catch (ProviderException e) {
			throw new PluginException(e.getMessage(), e);
		}
	}

}
