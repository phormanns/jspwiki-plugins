package de.jalin.jspwiki.plugin.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Page;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.pages.PageManager;
import org.apache.wiki.render.RenderingManager;

public class TagManager {

	private static TagManager instance = null;
	private ConcurrentHashMap<String, String[]> tagsPerPage;
	
	private TagManager() {
		tagsPerPage = new ConcurrentHashMap<String, String[]>();
	}
	
	public static TagManager getTagManager(final Engine wiki) throws ProviderException {
		if (instance == null) {
			instance = new TagManager();
			final PageManager pageManager = wiki.getManager(PageManager.class);
			final RenderingManager renderingManager = wiki.getManager(RenderingManager.class);
			final Collection<Page> allPages = pageManager.getAllPages();
			for (final Page page : allPages) {
				renderingManager.getHTML(page.getName());
			}
		}
		return instance;
	}

	public synchronized void register(final String pageName, String[] tags) {
		tagsPerPage.put(pageName, tags);
	}
	
	public synchronized List<String> pagesPerTag(final String tag) {
		final List<String> res = new ArrayList<>();
		final Enumeration<String> enumeration = tagsPerPage.keys();
		while (enumeration.hasMoreElements()) {
			final String page = enumeration.nextElement();
			final String[] tags = tagsPerPage.get(page);
			final HashSet<String> hash = new HashSet<String>(Arrays.asList(tags));
			if (hash.contains(tag)) {
				res.add(page);
			}
		}
		return res;
	}

	public Map<String, Integer> tagUsage(int mincount) {
		final Map<String, Integer> res = new HashMap<>();
		final Enumeration<String> enumeration = tagsPerPage.keys();
		while (enumeration.hasMoreElements()) {
			final String page = enumeration.nextElement();
			final String[] tags = tagsPerPage.get(page);
			for (final String tag : tags) {
				if (res.containsKey(tag)) {
					int oldValue = res.get(tag);
					res.put(tag, new Integer(oldValue + 1));
				} else {
					res.put(tag, new Integer(1));
				}
			}
		}
		return res;
	}

}
