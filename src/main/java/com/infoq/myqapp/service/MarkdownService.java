package com.infoq.myqapp.service;

import com.github.rjeschke.txtmark.Processor;
import com.infoq.myqapp.domain.GitHubMarkdown;
import com.infoq.myqapp.domain.MyQAppMarkdown;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class MarkdownService {

    private static final String IMAGE_SRC_PREFIX = "/mag4media/repositories/fs/";

    private RestTemplate restTemplate = new RestTemplate();

    public String generateHtml(MyQAppMarkdown markdown) {
        String result = restTemplate.postForObject("https://api.github.com/markdown", new GitHubMarkdown(markdown.getText(), "markdown", null), String.class);

        List<String> imagesSources = getImageSources(markdown.getText());

        return processHtml(result, imagesSources, markdown.isAnArticle(), markdown.getNode());
    }

    private List<String> getImageSources(String markdown) {
        List<String> imagesSources = new ArrayList<>();

        String txtMarkHtml = Processor.process(markdown);
        Document txtMarkDocument = Jsoup.parse(txtMarkHtml);
        for (Element next : txtMarkDocument.getElementsByTag("img")) {
            String src = next.attr("src");
            imagesSources.add(src);
        }
        return imagesSources;
    }

    private String processHtml(String html, List<String> imageSources, boolean isAnArticle, String nodeName) {

        String input = html.replaceAll(" \\?", "&nbsp?").replaceAll(" :", "&nbsp:").replaceAll(" !", "&nbsp!");

        Document myAmazingContent = Jsoup.parse(input);

        removeOcticonSpan(myAmazingContent);
        removeAnchors(myAmazingContent);
        highlightPre(myAmazingContent);
        parseImages(imageSources, myAmazingContent, isAnArticle, nodeName);

        return removeBody(myAmazingContent);
    }

    private void parseImages(List<String> imageSources, Document myAmazingContent, boolean isAnArticle, String nodeName) {
        Elements images = myAmazingContent.getElementsByTag("img");
        Iterator<Element> imagesIterator = images.iterator();
        Iterator<String> sourceIterator = imageSources.iterator();
        while (imagesIterator.hasNext() && sourceIterator.hasNext()) {
            Element image = imagesIterator.next();
            String source = sourceIterator.next();
            parseImageSource(image, source, isAnArticle, nodeName);
        }
    }

    private void parseImageSource(Element image, String source, boolean isAnArticle, String nodeName) {
        String resourceName = source.substring(source.lastIndexOf("/") + 1);
        if (resourceName.contains(";jsessionid=")) {
            resourceName = resourceName.substring(0, resourceName.indexOf(";jsessionid="));
        }

        String src = IMAGE_SRC_PREFIX;
        if (isAnArticle) {
            src += "articles/";
        } else {
            src += "news/";
        }
        src += nodeName + "/fr/resources/" + resourceName;

        image.attr("src", src);
        image.attr("_href", "img://" + resourceName);
        image.removeAttr("style");

        //remove the link to akamai
        Element parent = image.parent();
        if ("a".equals(parent.tagName()) && parent.attr("href").contains("akamai") && parent.attr("href").contains("github")) {
            image.parent().replaceWith(image);
        }
    }

    private String removeBody(Document myAmazingContent) {
        return myAmazingContent.body().outerHtml().replace("<body>", "").replace("</body>", "");
    }

    private void highlightPre(Document myAmazingContent) {
        for (Element pre : myAmazingContent.select("pre")) {
            for (Element span : myAmazingContent.getElementsByTag("span")) {
                String spanClass = span.className();
                span.tagName("font");
                span.removeAttr("class");
                span.attr("color", HighlightClass.getFontColorFromClassName(spanClass));
            }

            final List<TextNode> textNodes = new ArrayList<>();
            pre.traverse(new NodeVisitor() {
                public void head(Node node, int depth) {
                    if (node instanceof TextNode) {
                        textNodes.add((TextNode) node);
                    }
                }

                public void tail(Node node, int depth) {
                    // nothing to do
                }
            });

            for (TextNode textNode : textNodes) {
                textNode.replaceWith(new DataNode(textNode.outerHtml().replace(" ", "&nbsp;").replace("\n", "<br/>"), ""));
            }

            Element parent = pre.parent();
            if (parent.className().equals("highlight") && "div".equals(parent.tagName())) {
                parent.replaceWith(pre);
            }

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
