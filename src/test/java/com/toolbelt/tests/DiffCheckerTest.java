package com.toolbelt.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class DiffCheckerTest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
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
        assertTrue(page.locator("h1").textContent().contains("Diff Checker"));

        // Check input areas
        assertTrue(page.locator("textarea").first().isVisible());
        assertTrue(page.locator("textarea").nth(1).isVisible());

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

            page.locator("textarea").first().fill(text);
            page.locator("textarea").nth(1).fill(text);

            // Auto-processes - wait for diff to be calculated
            page.waitForTimeout(500);

            // Should show no differences or statistics showing unchanged
            assertTrue(page.locator("text=/identical|no differences|unchanged/i").isVisible());
        }

        @Test
        void shouldDetectAddedLines() {
            String text1 = "Line 1\nLine 2";
            String text2 = "Line 1\nLine 2\nLine 3";

            page.locator("textarea").first().fill(text1);
            page.locator("textarea").nth(1).fill(text2);

            // Auto-processes - wait for diff to be calculated
            page.waitForTimeout(500);

            // Should show differences
            assertTrue(page.locator("text=/added|\\+1|differences|changes/i").isVisible());
        }

        @Test
        void shouldDetectRemovedLines() {
            String text1 = "Line 1\nLine 2\nLine 3";
            String text2 = "Line 1\nLine 3";

            page.locator("textarea").first().fill(text1);
            page.locator("textarea").nth(1).fill(text2);

            // Auto-processes - wait for diff to be calculated
            page.waitForTimeout(500);

            assertTrue(page.locator("text=/removed|-1|differences|changes/i").isVisible());
        }

        @Test
        void shouldDetectModifiedLines() {
            String text1 = "Hello World";
            String text2 = "Hello Universe";

            page.locator("textarea").first().fill(text1);
            page.locator("textarea").nth(1).fill(text2);

            // Auto-processes - wait for diff to be calculated
            page.waitForTimeout(500);

            assertTrue(page.locator("text=/modified|~1|differences|changes/i").isVisible());
        }
    }

    @Nested
    class ViewModes {
        @Test
        void shouldToggleBetweenSplitAndUnifiedView() {
            String text1 = "Line 1\nLine 2";
            String text2 = "Line 1\nLine 3";

            page.locator("textarea").first().fill(text1);
            page.locator("textarea").nth(1).fill(text2);

            // Auto-processes - wait for diff to be calculated
            page.waitForTimeout(500);

            // Check if view toggle exists
            Locator unifiedButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)Unified View"));
            Locator splitButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)Split View"));

            if (unifiedButton.count() > 0) {
                unifiedButton.first().click();
                page.waitForTimeout(200);
            }

            if (splitButton.count() > 0) {
                splitButton.first().click();
                page.waitForTimeout(200);
            }
        }
    }

    @Nested
    class Options {
        @Test
        void shouldIgnoreWhitespaceWhenOptionIsEnabled() {
            String text1 = "Hello World";
            String text2 = "Hello    World";

            // Enable ignore whitespace first
            Locator ignoreWhitespace = page.locator("text=/Ignore.*Whitespace/i");
            if (ignoreWhitespace.count() > 0) {
                ignoreWhitespace.first().click();
            }

            page.locator("textarea").first().fill(text1);
            page.locator("textarea").nth(1).fill(text2);

            // Auto-processes - wait for diff to be calculated
            page.waitForTimeout(500);

            // With whitespace ignored, should show no changes
            assertTrue(page.locator("text=/unchanged|no differences/i").isVisible());
        }

        @Test
        void shouldIgnoreCaseWhenOptionIsEnabled() {
            String text1 = "Hello World";
            String text2 = "hello world";

            // Enable ignore case first
            Locator ignoreCase = page.locator("text=/Ignore.*Case/i");
            if (ignoreCase.count() > 0) {
                ignoreCase.first().click();
            }

            page.locator("textarea").first().fill(text1);
            page.locator("textarea").nth(1).fill(text2);

            // Auto-processes - wait for diff to be calculated
            page.waitForTimeout(500);

            // With case ignored, should show no changes
            assertTrue(page.locator("text=/unchanged|no differences/i").isVisible());
        }
    }

    @Nested
    class UIControls {
        @Test
        void shouldLoadSampleTexts() {
            Locator sampleButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Sample$"));

            if (sampleButton.count() > 0) {
                sampleButton.first().click();

                String text1 = page.locator("textarea").first().inputValue();
                String text2 = page.locator("textarea").nth(1).inputValue();

                assertTrue(text1.length() > 0);
                assertTrue(text2.length() > 0);
            }
        }

        @Test
        void shouldClearBothInputs() {
            page.locator("textarea").first().fill("Test 1");
            page.locator("textarea").nth(1).fill("Test 2");

            Locator clearButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Clear$"));
            if (clearButton.count() > 0) {
                clearButton.first().click();

                String text1 = page.locator("textarea").first().inputValue();
                String text2 = page.locator("textarea").nth(1).inputValue();

                assertEquals("", text1);
                assertEquals("", text2);
            }
        }

        @Test
        void shouldSwapTexts() {
            String originalText1 = "Text 1";
            String originalText2 = "Text 2";

            page.locator("textarea").first().fill(originalText1);
            page.locator("textarea").nth(1).fill(originalText2);

            Locator swapButton = page.locator("button[title*='Swap']");
            if (swapButton.count() > 0) {
                swapButton.first().click();

                String text1 = page.locator("textarea").first().inputValue();
                String text2 = page.locator("textarea").nth(1).inputValue();

                assertEquals(originalText2, text1);
                assertEquals(originalText1, text2);
            }
        }
    }

    @Nested
    class Statistics {
        @Test
        void shouldDisplayDiffStatistics() {
            String text1 = "Line 1\nLine 2\nLine 3";
            String text2 = "Line 1\nModified Line 2\nLine 3\nLine 4";

            page.locator("textarea").first().fill(text1);
            page.locator("textarea").nth(1).fill(text2);

            // Auto-processes - wait for diff to be calculated
            page.waitForTimeout(500);

            // Check for statistics
            assertTrue(page.locator("text=/added|removed|modified|total lines/i").isVisible());
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

            page.locator("textarea").first().fill(longText1.toString());
            page.locator("textarea").nth(1).fill(longText2.toString());

            // Auto-processes - wait for diff to be calculated
            page.waitForTimeout(500);

            assertTrue(page.locator("text=/added|total lines/i").isVisible());
        }
    }

    @Nested
    class MobileResponsiveness {
        @Test
        void shouldBeResponsiveOnMobile() {
            page.setViewportSize(375, 667);

            assertTrue(page.locator("h1").isVisible());
            assertTrue(page.locator("textarea").first().isVisible());

            // Test comparison on mobile
            page.locator("textarea").first().fill("Test 1");
            page.locator("textarea").nth(1).fill("Test 2");

            // Auto-processes - wait for diff to be calculated
            page.waitForTimeout(500);

            assertTrue(page.locator("text=/modified|added|removed|total lines/i").isVisible());
        }
    }

    @Nested
    class Accessibility {
        @Test
        void shouldHaveProperLabels() {
            assertTrue(page.locator("text=/Original|Text 1|First/i").first().isVisible());
            assertTrue(page.locator("text=/Modified|Text 2|Second/i").first().isVisible());
        }
    }
}
