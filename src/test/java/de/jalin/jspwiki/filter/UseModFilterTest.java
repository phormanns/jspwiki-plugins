package de.jalin.jspwiki.filter;

import static org.junit.Assert.*;

import org.apache.wiki.api.exceptions.FilterException;
import org.apache.wiki.api.filters.PageFilter;
import org.junit.Test;

public class UseModFilterTest {

	@Test
    public void testPreTranslateTitle()
    {
		final PageFilter useModFilter = new UseModFilter();
        try
        {
            assertEquals("*Item\n", useModFilter.preTranslate(null, "*Item\n"));
            assertEquals("__Item__\n", useModFilter.preTranslate(null, "====Item====\n"));
            assertEquals("!! Title\n", useModFilter.preTranslate(null, "!! Title\n"));
            assertEquals("!!!Title\n", useModFilter.preTranslate(null, "=Title=\n"));
            assertEquals("!!!Title \nText.\n", useModFilter.preTranslate(null, "=Title\n=\nText.\n"));
            assertEquals("!!!Title Title\n", useModFilter.preTranslate(null, "=Title\nTitle=\n"));
            assertEquals("! SubSubtitle \n", useModFilter.preTranslate(null, "=== SubSubtitle ===\n"));
            assertEquals("!! Subtitle \n", useModFilter.preTranslate(null, "== Subtitle ==\n"));
            assertEquals("== Subtitle =\n", useModFilter.preTranslate(null, "== Subtitle =\n"));
            assertEquals("== Subtitle\n", useModFilter.preTranslate(null, "== Subtitle\n"));
            assertEquals("== Subtitle Text1 Text2 Text3\n", useModFilter.preTranslate(null, "== Subtitle\nText1\nText2\nText3\n"));
        }
        catch (FilterException e)
        {
            fail(e.getMessage());
        }
    }

	@Test
    public void testPreTranslateItemize()
    {
		final PageFilter useModFilter = new UseModFilter();
        try
        {
            assertEquals("*Item\n", useModFilter.preTranslate(null, "*Item\n"));
            assertEquals("*Item Item\n", useModFilter.preTranslate(null, "*Item\nItem\n"));
            assertEquals("*Item Item\n*Item\n", useModFilter.preTranslate(null, "*Item\nItem\n*Item\n"));
            assertEquals("#Item\n", useModFilter.preTranslate(null, "#Item\n"));
            assertEquals("#Item Item\n", useModFilter.preTranslate(null, "#Item\nItem\n"));
            assertEquals("#Item __Item__\n", useModFilter.preTranslate(null, "#Item\n'''Item'''\n"));
            assertEquals("#Item Item\n\nItem\n", useModFilter.preTranslate(null, "#Item\nItem\n\nItem\n"));
            assertEquals("*__Item__ Item\n#Item\n", useModFilter.preTranslate(null, "*'''Item'''\nItem\n#Item\n"));
        }
        catch (FilterException e)
        {
            fail(e.getMessage());
        }
    }

	@Test
    public void testPreTranslateBoldItalics()
    {
		final PageFilter useModFilter = new UseModFilter();
        try
        {
            assertEquals("*__''Item''__\n", useModFilter.preTranslate(null, "*'''''Item'''''\n"));
            assertEquals("__Item__\n", useModFilter.preTranslate(null, "'''Item'''\n"));
            assertEquals("!!!Title \n__Text__.\n", useModFilter.preTranslate(null, "=Title\n=\n'''Text'''.\n"));
        }
        catch (FilterException e)
        {
            fail(e.getMessage());
        }
    }

	@Test
    public void testPreTranslatePre()
    {
		final PageFilter useModFilter = new UseModFilter();
        try
        {
            assertEquals("{{{\n$ cat file.txt\n}}}\n", useModFilter.preTranslate(null, "<pre>\n$ cat file.txt\n</pre>\n"));
            assertEquals("{{{\n==Header==\n}}}\n", useModFilter.preTranslate(null, "<pre>\n==Header==\n</pre>\n"));
            assertEquals("{{{\n==Header==\n}}}\n", useModFilter.preTranslate(null, "{{{\n==Header==\n}}}\n"));
            assertEquals("__{{{Item}}}__\n", useModFilter.preTranslate(null, "'''<pre>Item</pre>'''\n"));
            assertEquals("{{{\n  Item\n  Item\n}}}\n", useModFilter.preTranslate(null, "  Item\n  Item\n"));
            assertEquals("{{{\n  Item\n  Item Item\n}}}\n", useModFilter.preTranslate(null, "  Item\n  Item\nItem\n"));
        }
        catch (FilterException e)
        {
            fail(e.getMessage());
        }
    }

	@Test
    public void testPreTranslateIndent()
    {
		final PageFilter useModFilter = new UseModFilter();
        try
        {
            assertEquals("%%(margin-bottom: 12pt; margin-top: 12pt; margin-left: 24pt)\nMein Kommentar\n%%\n", useModFilter.preTranslate(null, ":Mein Kommentar\n"));
            assertEquals("%%(margin-bottom: 12pt; margin-top: 12pt; margin-left: 48pt)\nMein Kommentar\n%%\n", useModFilter.preTranslate(null, "::Mein Kommentar\n"));
            assertEquals("%%(margin-bottom: 12pt; margin-top: 12pt; margin-left: 48pt)\nKommentar Eins\n%%\n%%(margin-bottom: 12pt; margin-top: 12pt; margin-left: 72pt)\nKommentar Zwo\n%%\n", useModFilter.preTranslate(null, "::Kommentar Eins\n:::Kommentar Zwo\n"));
            assertEquals("%%(margin-bottom: 12pt; margin-top: 12pt; margin-left: 48pt)\nMein Kommentar\nmit Folgezeile\n%%\n", useModFilter.preTranslate(null, "::Mein Kommentar\nmit Folgezeile\n"));
        }
        catch (FilterException e)
        {
            fail(e.getMessage());
        }
    }
    
	@Test
    public void testPreTranslateLink() {
        final PageFilter useModFilter = new UseModFilter();
        try
        {
            assertEquals("Der Link ist [http://www.hormanns-wenz.de]\n", useModFilter.preTranslate(null, "Der Link ist http://www.hormanns-wenz.de\n"));
            assertEquals("Unsere [Homepage|http://www.hormanns-wenz.de]\n", useModFilter.preTranslate(null, "Unsere [http://www.hormanns-wenz.de Homepage]\n"));
            assertEquals("Unsere [Homepage|http://www.hormanns-wenz.de]\n", useModFilter.preTranslate(null, "Unsere [http://www.hormanns-wenz.de\nHomepage]\n"));
            assertEquals("\n{{{\n  deb http://ftp.freenet.de/debian/ woody main non-free contrib\n}}}\n", useModFilter.preTranslate(null, "\n  deb http://ftp.freenet.de/debian/ woody main non-free contrib\n"));
            assertEquals("Der Link ist [http://www.hormanns-wenz.de]\n", useModFilter.preTranslate(null, "Der Link ist [http://www.hormanns-wenz.de]\n"));
            assertEquals("Unsere [tolle Homepage|http://www.hormanns-wenz.de]\n", useModFilter.preTranslate(null, "Unsere [tolle Homepage|http://www.hormanns-wenz.de]\n"));
            assertEquals("Unsere [tolle Homepage|http://www.hormanns-wenz.de:8080/wiki/Start]\n", useModFilter.preTranslate(null, "Unsere [tolle Homepage|http://www.hormanns-wenz.de:8080/wiki/Start]\n"));
            assertEquals("Unsere [tolle Homepage|http://www.hormanns-wenz.de/wiki/Start,01]\n", useModFilter.preTranslate(null, "Unsere [tolle Homepage|http://www.hormanns-wenz.de/wiki/Start,01]\n"));
        }
        catch (FilterException e)
        {
            fail(e.getMessage());
        }
    }

	@Test
    public void testPreTranslatePlugin()
    {
		final PageFilter useModFilter = new UseModFilter();
        try
        {
        	final String markup1 = "[{com.example.WikiPlugin param='value'\n\n    Text im Plugin-Bereich\n}]\n";
			assertEquals(markup1, useModFilter.preTranslate(null, markup1));
        	final String markup2 = "  [{OtherWikiPlugin param='value'\n\n      egal was hier steht\n  }]\n";
			assertEquals(markup2, useModFilter.preTranslate(null, markup2));
        }
        catch (FilterException e)
        {
            fail(e.getMessage());
        }
    }

}
