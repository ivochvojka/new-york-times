package cz.ich.newyorktimes.cz.ich.newyorktimes.utils;

import org.junit.Test;

import java.util.Locale;
import java.util.TimeZone;

import cz.ich.newyorktimes.utils.ArticleUtils;

import static org.junit.Assert.assertEquals;

/**
 * Test of {@link ArticleUtils}.
 *
 * @author Ivo Chvojka
 */
public class ArticleUtilsTest {

    @Test
    public void testGetLocaleDateFromGMT() {
        final String gmtTime = "2016-10-02T00:00:00Z";

        // setup Czech locale
        Locale.setDefault(new Locale("cs", "CZ"));
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Prague"));

        final String czechLocaleDate = ArticleUtils.getLocaleDateFromGMT(gmtTime); // GMT + 1 hour
        assertEquals("2.10.2016 2:00:00", czechLocaleDate);

        // setup Canada locale
        Locale.setDefault(Locale.UK);
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));

        final String canadaLocaleDate = ArticleUtils.getLocaleDateFromGMT(gmtTime);
        assertEquals("02-Oct-2016 01:00:00", canadaLocaleDate); // GMT + 1 hour
    }

}
