package com.infoq.myqapp.service;

import com.github.rjeschke.txtmark.Processor;
import com.infoq.myqapp.domain.GitHubMarkdown;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class MarkdownService {

    private RestTemplate restTemplate = new RestTemplate();

    public String generateHtml(String markdown) {
        String result = restTemplate.postForObject("https://api.github.com/markdown", new GitHubMarkdown(markdown, "markdown", null), String.class);

        List<String> imagesSources = getImageSources(markdown);

        return processHtml(result, imagesSources);
    }

    private List<String> getImageSources(String markdown) {
        List<String> imagesSources = new ArrayList<>();

        String txtMarkHtml = Processor.process(markdown);
        Document txtMarkDocument = Jsoup.parse(txtMarkHtml);
        Elements img = txtMarkDocument.getElementsByTag("img");
        Iterator<Element> iterator = img.iterator();
        while (iterator.hasNext()) {
            Element next = iterator.next();
            String src = next.attr("src");
            imagesSources.add(src);
        }
        return imagesSources;
    }

    private String processHtml(String html, List<String> imageSources) {

        Document myAmazingContent = Jsoup.parse(html);

        removeOcticonSpan(myAmazingContent);

        removeAnchors(myAmazingContent);

        highlightPre(myAmazingContent);

        parseHighlight(myAmazingContent);

        parseImages(imageSources, myAmazingContent);

        return removeBody(myAmazingContent);
    }

    private void parseImages(List<String> imageSources, Document myAmazingContent) {
        Elements img = myAmazingContent.getElementsByTag("img");
        Iterator<Element> iterator = img.iterator();
        Iterator<String> iteratorSource = imageSources.iterator();
        while (iterator.hasNext() && iteratorSource.hasNext()) {
            Element next = iterator.next();
            String nextSource = iteratorSource.next();
            next.attr("src", parseImageSource(nextSource));
        }
    }

    private String parseImageSource(String nextSource) {
        //TODO appliquer la regle des images
        return nextSource;
    }

    private String removeBody(Document myAmazingContent) {
        return myAmazingContent.body().outerHtml().replace("<body>", "").replace("</body>", "");
    }

    private void parseHighlight(Document myAmazingContent) {
        for (Element highlight : myAmazingContent.select("div.highlight")) {
            highlight.tagName("p");
            highlight.removeAttr("class");
        }
    }

    private void highlightPre(Document myAmazingContent) {
        for (Element pre : myAmazingContent.select("pre")) {
            for (Element span : myAmazingContent.getElementsByTag("span")) {
                String spanClass = span.className();
                span.tagName("font");
                span.removeAttr("class");
                span.attr("color", HighlightClass.getFontColorFromClassName(spanClass));
            }

            for (TextNode textNode : pre.textNodes()) {
                String text = textNode.text();
                String outerHtml = textNode.outerHtml();
                textNode.replaceWith(new DataNode(textNode.outerHtml().replace(" ", "&nbsp;").replace("\n", "<br/>"), ""));
            }
            pre.outerHtml();
        }
    }

    private void removeAnchors(Document myAmazingContent) {
        for (Element anchor : myAmazingContent.select("a.anchor")) {
            anchor.remove();
        }
    }

    private void removeOcticonSpan(Document myAmazingContent) {
        for (Element octiconSpan : myAmazingContent.select("span.octicon")) {
            octiconSpan.remove();
        }
    }

    private static enum HighlightClass {
        C("999988"),
        ERR("a61717"),
        CM("999988"),
        CP("999999"),
        C1("999988"),
        CS("999999"),
        GD("000000"),
        GR("aa0000"),
        GH("999999"),
        GI("000000"),
        GO("888888"),
        GP("555555"),
        GU("800080"),
        GT("aa0000"),
        KT("445588"),
        M("009999"),
        S("d14"),
        N("333333"),
        NA("008080"),
        NB("0086B3"),
        NC("445588"),
        NO("008080"),
        NI("800080"),
        NE("990000"),
        NF("990000"),
        NN("555555"),
        NT("000080"),
        NV("008080"),
        W("bbbbbb"),
        MF("009999"),
        MH("009999"),
        MI("009999"),
        MO("009999"),
        SB("d14"),
        SC("d14"),
        SD("d14"),
        S2("d14"),
        SE("d14"),
        SH("d14"),
        SX("d14"),
        SI("d14"),
        SR("009926"),
        S1("d14"),
        SS("990073"),
        BP("999999"),
        VC("008080"),
        VG("008080"),
        VI("008080"),
        IL("009999"),
        GC("999");

        private final String color;

        private HighlightClass(String color) {
            this.color = color;
        }

        private static String getFontColorFromClassName(String className) {
            try {
                return HighlightClass.valueOf(className.toUpperCase()).color;
            } catch (IllegalArgumentException e) {
                return "000000"; //return black by default
            }
        }
    }
}
