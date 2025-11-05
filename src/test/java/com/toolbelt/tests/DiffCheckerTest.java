package com.toolbelt.tests;

import com.microsoft.playwright.*;
import com.toolbelt.pages.DiffCheckerPage;
import com.toolbelt.utils.BrowserFactory;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class DiffCheckerTest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;
    private DiffCheckerPage diffPage;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = BrowserFactory.launchBrowser(playwright);
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext(new Browser.NewContextOptions().setBaseURL("https://toolbelt.site"));
        page = context.newPage();
        page.navigate("/diff");
        diffPage = new DiffCheckerPage(page);
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void shouldLoadTheDiffCheckerPageCorrectly() {
        // Check page title
        assertTrue(diffPage.getTitleText().contains("Diff Checker"));

        // Check input areas
        assertTrue(diffPage.isText1TextareaVisible());
        assertTrue(diffPage.isText2TextareaVisible());

        // Check view buttons
        Locator viewButtons = page.locator("button:has-text('Split View')").or(page.locator("button:has-text('Unified View')"));
        assertTrue(viewButtons.isVisible());
    }

    @Test
    void shouldNavigateBackToHomepage() {
        page.click("a[href='/']");
        assertTrue(page.url().endsWith("/"));
    }

    @Nested
    class ComparisonFeatures {
        @Test
        void shouldDetectIdenticalTexts() {
            String text = "Same text";

            diffPage.fillText1(text);
            diffPage.fillText2(text);

            // Auto-processes - wait for diff to be calculated
            diffPage.waitForDiffCalculation();

            // Should show no differences or statistics showing unchanged
            assertTrue(diffPage.isIdenticalMessageVisible());
        }

        @Test
        void shouldDetectAddedLines() {
            String text1 = "Line 1\nLine 2";
            String text2 = "Line 1\nLine 2\nLine 3";

            diffPage.fillText1(text1);
            diffPage.fillText2(text2);

            // Auto-processes - wait for diff to be calculated
            diffPage.waitForDiffCalculation();

            // Should show differences
            assertTrue(diffPage.isAddedMessageVisible());
        }

        @Test
        void shouldDetectRemovedLines() {
            String text1 = "Line 1\nLine 2\nLine 3";
            String text2 = "Line 1\nLine 3";

            diffPage.fillText1(text1);
            diffPage.fillText2(text2);

            // Auto-processes - wait for diff to be calculated
            diffPage.waitForDiffCalculation();

            assertTrue(diffPage.isRemovedMessageVisible());
        }

        @Test
        void shouldDetectModifiedLines() {
            String text1 = "Hello World";
            String text2 = "Hello Universe";

            diffPage.fillText1(text1);
            diffPage.fillText2(text2);

            // Auto-processes - wait for diff to be calculated
            diffPage.waitForDiffCalculation();

            assertTrue(diffPage.isModifiedMessageVisible());
        }
    }

    @Nested
    class ViewModes {
        @Test
        void shouldToggleBetweenSplitAndUnifiedView() {
            String text1 = "Line 1\nLine 2";
            String text2 = "Line 1\nLine 3";

            diffPage.fillText1(text1);
            diffPage.fillText2(text2);

            // Auto-processes - wait for diff to be calculated
            diffPage.waitForDiffCalculation();

            // Check if view toggle exists
            diffPage.clickUnifiedView();
            page.waitForTimeout(200);

            diffPage.clickSplitView();
            page.waitForTimeout(200);
        }
    }

    @Nested
    class Options {
        @Test
        void shouldIgnoreWhitespaceWhenOptionIsEnabled() {
            String text1 = "Hello World";
            String text2 = "Hello    World";

            // Enable ignore whitespace first
            diffPage.clickIgnoreWhitespace();

            diffPage.fillText1(text1);
            diffPage.fillText2(text2);

            // Auto-processes - wait for diff to be calculated
            diffPage.waitForDiffCalculation();

            // With whitespace ignored, should show no changes
            assertTrue(diffPage.isIdenticalMessageVisible());
        }

        @Test
        void shouldIgnoreCaseWhenOptionIsEnabled() {
            String text1 = "Hello World";
            String text2 = "hello world";

            // Enable ignore case first
            diffPage.clickIgnoreCase();

            diffPage.fillText1(text1);
            diffPage.fillText2(text2);

            // Auto-processes - wait for diff to be calculated
            diffPage.waitForDiffCalculation();

            // With case ignored, should show no changes
            assertTrue(diffPage.isIdenticalMessageVisible());
        }
    }

    @Nested
    class UIControls {
        @Test
        void shouldLoadSampleTexts() {
            diffPage.clickSample();

            String text1 = diffPage.getText1();
            String text2 = diffPage.getText2();

            assertTrue(text1.length() > 0);
            assertTrue(text2.length() > 0);
        }

        @Test
        void shouldClearBothInputs() {
            diffPage.fillText1("Test 1");
            diffPage.fillText2("Test 2");

            diffPage.clickClear();

            String text1 = diffPage.getText1();
            String text2 = diffPage.getText2();

            assertEquals("", text1);
            assertEquals("", text2);
        }

        @Test
        void shouldSwapTexts() {
            String originalText1 = "Text 1";
            String originalText2 = "Text 2";

            diffPage.fillText1(originalText1);
            diffPage.fillText2(originalText2);

            diffPage.clickSwap();

            String text1 = diffPage.getText1();
            String text2 = diffPage.getText2();

            assertEquals(originalText2, text1);
            assertEquals(originalText1, text2);
        }
    }

    @Nested
    class Statistics {
        @Test
        void shouldDisplayDiffStatistics() {
            String text1 = "Line 1\nLine 2\nLine 3";
            String text2 = "Line 1\nModified Line 2\nLine 3\nLine 4";

            diffPage.fillText1(text1);
            diffPage.fillText2(text2);

            // Auto-processes - wait for diff to be calculated
            diffPage.waitForDiffCalculation();

            // Check for statistics
            assertTrue(diffPage.isStatisticsVisible());
        }
    }

    @Nested
    class ErrorHandling {
        @Test
        void shouldHandleEmptyInputs() {
            // Empty inputs - should still show statistics
            assertTrue(page.locator("text=/total lines|diff statistics/i").isVisible());
        }

        @Test
        void shouldHandleVeryLongTexts() {
            StringBuilder longText1 = new StringBuilder();
            StringBuilder longText2 = new StringBuilder();

            for (int i = 0; i < 100; i++) {
                longText1.append("Line\n");
                longText2.append("Line\n");
            }
            longText2.append("Line\n");

            diffPage.fillText1(longText1.toString());
            diffPage.fillText2(longText2.toString());

            // Auto-processes - wait for diff to be calculated
            diffPage.waitForDiffCalculation();

            assertTrue(page.locator("text=/added|total lines/i").isVisible());
        }
    }

    @Nested
    class MobileResponsiveness {
        @Test
        void shouldBeResponsiveOnMobile() {
            page.setViewportSize(375, 667);

            assertTrue(diffPage.isTitleVisible());
            assertTrue(diffPage.isText1TextareaVisible());

            // Test comparison on mobile
            diffPage.fillText1("Test 1");
            diffPage.fillText2("Test 2");

            // Auto-processes - wait for diff to be calculated
            diffPage.waitForDiffCalculation();

            assertTrue(page.locator("text=/modified|added|removed|total lines/i").isVisible());
        }
    }

    @Nested
    class Accessibility {
        @Test
        void shouldHaveProperLabels() {
            assertTrue(diffPage.isOriginalLabelVisible());
            assertTrue(diffPage.isModifiedLabelVisible());
        }
    }
}
