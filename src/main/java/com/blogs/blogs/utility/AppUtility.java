package com.blogs.blogs.utility;

import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AppUtility {

    private static final Logger log = LoggerFactory.getLogger(AppUtility.class);

    // Regex to detect URLs in plain text
    private static final Pattern URL_PATTERN = Pattern.compile("https?://[^\\s]+|www\\.[^\\s]+");

    public static void validateBlogContent(String content) {
        log.info("Original content: {}", content);

        if (content == null || content.trim().isEmpty()) {
            return;
        }

        // Unescape HTML entities
        String unescapedContent = StringEscapeUtils.unescapeHtml4(content);
        log.info("Unescaped content: {}", unescapedContent);

        // Parse the unescaped content
        Document doc = Jsoup.parse(unescapedContent);
        // Validate plain text URLs
        Matcher matcher = URL_PATTERN.matcher(unescapedContent);
        int urlCount = 0;
        while (matcher.find()) {
            String url = matcher.group();
            if (isExternalLink(url)) {
                urlCount++;
            }
        }

        // Ensure total links (HTML + plain text) do not exceed the limit
        if (urlCount > 1) {
            throw new IllegalArgumentException("Blog content cannot contain more than one hyperlink.");
        }
    }

    private static boolean isExternalLink(String url) {
        return url.startsWith("http") || url.startsWith("www");
    }

    public static String calculateTimeAgo(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();

        // Calculate the difference in terms of days, hours, and minutes
        long years = ChronoUnit.YEARS.between(createdAt, now);
        long months = ChronoUnit.MONTHS.between(createdAt, now);
        long days = ChronoUnit.DAYS.between(createdAt, now);
        long hours = ChronoUnit.HOURS.between(createdAt, now);
        long minutes = ChronoUnit.MINUTES.between(createdAt, now);

        // Return the time ago in a human-readable format
        if (years > 0) {
            return years == 1 ? "1 year ago" : years + " years ago";
        } else if (months > 0) {
            return months == 1 ? "1 month ago" : months + " months ago";
        } else if (days > 0) {
            return days == 1 ? "1 day ago" : days + " days ago";
        } else if (hours > 0) {
            return hours == 1 ? "1 hour ago" : hours + " hours ago";
        } else if (minutes > 0) {
            return minutes == 1 ? "1 minute ago" : minutes + " minutes ago";
        } else {
            return "Just now";
        }
    }


}
