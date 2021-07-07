package de.jalin.jspwiki.plugin;

import java.util.Map;
import java.util.Random;

import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.exceptions.PluginException;
import org.apache.wiki.api.plugin.Plugin;
import org.apache.wiki.ui.TemplateManager;

public class OSMMarkerPlugin implements Plugin {

	@Override
	public String execute(final Context context, final Map<String, String> pluginParams) throws PluginException {
		final String lon = getParam(pluginParams, "lon", "6.5616911");
		final String lat = getParam(pluginParams, "lat", "51.330744");
		final String zoom = getParam(pluginParams, "zoom", "10");
		final String height = getParam(pluginParams, "height", "180");
		final String width = getParam(pluginParams, "width", "360");
		final String text = getParam(pluginParams, "text", "genau hier");
		TemplateManager.addResourceRequest(context, TemplateManager.RESOURCE_STYLESHEET, "https://unpkg.com/leaflet@1.7.1/dist/leaflet.css");
		TemplateManager.addResourceRequest(context, TemplateManager.RESOURCE_SCRIPT, "https://unpkg.com/leaflet@1.7.1/dist/leaflet.js");
		final int nextInt = new Random().nextInt(10000) + 10000;
		final String mapId = "map" + Integer.toString(nextInt).substring(1);
		final StringBuilder stringBuild = new StringBuilder("<div style=\"width:" + width + "px;height:" + height + "px;\" id=\"" + mapId + "\">\n</div>\n");
		stringBuild.append("<script type=\"text/javascript\">\n");
		stringBuild.append("  var " + mapId + " = L.map('" + mapId + "').setView([" + lat + ", " + lon + "], " + zoom + ");\n");
		stringBuild.append("  L.tileLayer( 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { attribution: '&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a>', subdomains: ['a','b','c'] }).addTo( " + mapId + ");");
		stringBuild.append("  var marker = L.marker([" + lat + ", " + lon + "]).addTo(" + mapId + ");");
		stringBuild.append("  marker.bindPopup(\"" + text + "\").openPopup();");
		stringBuild.append("</script>\n");
		return stringBuild.toString();
	}

	private String getParam(final Map<String, String> pluginParams, final String name, final String defaultValue) {
		final String value = pluginParams.get(name);
		if (value == null || value.isEmpty()) {
			return defaultValue;
		}
		return value;
	}

}
