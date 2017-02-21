package com.xc.news;

/**
 * Created by gexiaocun on 2017/2/20.
 */


import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.HtmlArticleExtractor;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

public class ExtractorServer extends AbstractHandler {
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException,
            ServletException {
        // Declare response encoding and types
        response.setContentType("application/json; charset=utf-8");

        response.setStatus(HttpServletResponse.SC_OK);

        String urlParam = request.getParameter("url");

        if (StringUtils.isEmpty(urlParam)) {
            response.getWriter().print("no url given");
            response.setStatus(400);
            return;
        }


        URL url = new URL(urlParam);
        final BoilerpipeExtractor extractor = CommonExtractors.ARTICLE_EXTRACTOR;

        final HtmlArticleExtractor htmlExtr = HtmlArticleExtractor.INSTANCE;

        String html = null;
        try {
            html = htmlExtr.process(extractor, url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.getWriter().println(html);

        baseRequest.setHandled(true);
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(28080);
        server.setHandler(new ExtractorServer());

        server.start();
        server.join();
    }
}
