package de.l3s.boilerpipe.demo;

import java.net.URL;
import java.util.List;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.document.Media;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.HtmlArticleExtractor;
import de.l3s.boilerpipe.sax.MediaExtractor;

/**
 * @author manuel.codiga@gmail.com
 */
public final class HtmlArticleExtractorDemo {
	public static void main(String[] args) throws Exception {
		URL url = new URL(
				"http://finance.yahoo.com/news/may-able-collection-accounts-off-113000716.html");
		final BoilerpipeExtractor extractor = CommonExtractors.ARTICLE_EXTRACTOR;

		final HtmlArticleExtractor htmlExtr = HtmlArticleExtractor.INSTANCE;
		
		String html = htmlExtr.process(extractor, url);
		
		System.out.println(html);

	}
}
