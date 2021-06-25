package de.jalin.jspwiki.plugin;

import java.util.Map;

import org.apache.wiki.WikiContext;
import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.exceptions.PluginException;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.plugin.Plugin;

import de.jalin.jspwiki.plugin.tag.TagManager;

public class TagCloud implements Plugin {

	@Override
	public String execute(final Context wikiContext, final Map<String, String> pluginParams) throws PluginException {
		try {
			final String linkTypeParam = pluginParams.get("link");
			String linkType = "search";
			if (linkTypeParam != null && linkTypeParam instanceof String) {
				linkType = linkTypeParam.trim();
			}
			final String mincountParam = pluginParams.get("mincount");
			int mincount = 1;
			if (mincountParam != null && mincountParam instanceof String) {
				mincount = Integer.parseInt(mincountParam.trim());
			}
			final TagManager tagManager = TagManager.getTagManager(wikiContext.getEngine());
			final Map<String, Integer> tagUsage = tagManager.tagUsage(mincount);
			int max = 0;
			for (final String page : tagUsage.keySet()) {
				if (tagUsage.get(page) > max) {
					max = tagUsage.get(page); 
				}
			}
			float factor = 1.0f;
			if (max > 5) {
				factor = 5.0f / max;
			}
			final StringBuilder stringBuild = new StringBuilder("<div class=\"tagcloud\">\n");
			for (final String tag : tagUsage.keySet()) {
				final Integer count = tagUsage.get(tag);
				if (count >= mincount) {
					float f = count * factor;
					if (f < 1.0f) f = 1.0f;
					int cs = Math.round(f);
					final String linkUrl = "page".equals(linkType) ? 
							wikiContext.getURL(WikiContext.VIEW, tag) : 
							wikiContext.getURL(WikiContext.FIND, tag) + "?query=" + tag;  
					stringBuild.append(" <a href=\"" + linkUrl + "\"" + 
							" class=\"s" + cs + "\"" + ">" + tag + "</a>");
				}
			}
			stringBuild.append("</div>\n");
			return stringBuild.toString();
		} catch (ProviderException e) {
			throw new PluginException(e.getMessage(), e);
		}
	}

}
