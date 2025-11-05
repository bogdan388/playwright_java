package com.toolbelt.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TextCaseConverterTest {
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
        page.navigate("/text-case");
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void shouldLoadTheTextCaseConverterPageCorrectly() {
        assertTrue(page.locator("h1").textContent().contains("Text Case Converter"));
        assertTrue(page.locator("text=/Convert text between different case formats/i").isVisible());
        assertTrue(page.locator("textarea").first().isVisible());
        assertTrue(page.locator("textarea").nth(1).isVisible());
    }

    @Test
    void shouldNavigateBackToHomepage() {
        page.click("a[href='/']");
        assertTrue(page.url().endsWith("/"));
    }

    @Nested
    class CaseConversions {
        @Test
        void shouldConvertToUppercase() {
            String input = "hello world";

            page.locator("button:has-text('UPPERCASE')").click();
            page.locator("textarea").first().fill(input);

            // Auto-converts, wait for processing
            page.waitForTimeout(300);

            String output = page.locator("textarea").nth(1).inputValue();
            assertEquals("HELLO WORLD", output);
        }

        @Test
        void shouldConvertToLowercase() {
            String input = "HELLO WORLD";

            page.locator("button:has-text('lowercase')").click();
            page.locator("textarea").first().fill(input);

            // Auto-converts, wait for processing
            page.waitForTimeout(300);

            String output = page.locator("textarea").nth(1).inputValue();
            assertEquals("hello world", output);
        }

        @Test
        void shouldConvertToTitleCase() {
            String input = "hello world test";

            page.locator("button:has-text('Title Case')").click();
            page.locator("textarea").first().fill(input);

            // Auto-converts, wait for processing
            page.waitForTimeout(300);

            String output = page.locator("textarea").nth(1).inputValue();
            assertEquals("Hello World Test", output);
        }

        @Test
        void shouldConvertToCamelCase() {
            String input = "hello world test";

            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^camelCase$")).click();
            page.locator("textarea").first().fill(input);

            // Auto-converts, wait for processing
            page.waitForTimeout(300);

            String output = page.locator("textarea").nth(1).inputValue();
            assertEquals("helloWorldTest", output);
        }

        @Test
        void shouldConvertToPascalCase() {
            String input = "hello world test";

            page.locator("button:has-text('PascalCase')").click();
            page.locator("textarea").first().fill(input);

            // Auto-converts, wait for processing
            page.waitForTimeout(300);

            String output = page.locator("textarea").nth(1).inputValue();
            assertEquals("HelloWorldTest", output);
        }

        @Test
        void shouldConvertToSnakeCase() {
            String input = "hello world test";

            page.locator("button:has-text('snake_case')").click();
            page.locator("textarea").first().fill(input);

            // Auto-converts, wait for processing
            page.waitForTimeout(300);

            String output = page.locator("textarea").nth(1).inputValue();
            assertEquals("hello_world_test", output);
        }

        @Test
        void shouldConvertToKebabCase() {
            String input = "hello world test";

            page.locator("button:has-text('kebab-case')").click();
            page.locator("textarea").first().fill(input);

            // Auto-converts, wait for processing
            page.waitForTimeout(300);

            String output = page.locator("textarea").nth(1).inputValue();
            assertEquals("hello-world-test", output);
        }

        @Test
        void shouldConvertToConstantCase() {
            String input = "hello world test";

            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^CONSTANT_CASE$")).click();
            page.locator("textarea").first().fill(input);

            // Auto-converts, wait for processing
            page.waitForTimeout(300);

            String output = page.locator("textarea").nth(1).inputValue();
            assertEquals("HELLO_WORLD_TEST", output);
        }
    }

    @Nested
    class UIControls {
        @Test
        void shouldSwapInputAndOutput() {
            String input = "hello world";

            page.locator("button:has-text('UPPERCASE')").click();
            page.locator("textarea").first().fill(input);

            // Auto-converts, wait for processing
            page.waitForTimeout(300);

            Locator swapButton = page.locator("button:has-text('Swap')");
            if (swapButton.isVisible()) {
                swapButton.click();

                String newInput = page.locator("textarea").first().inputValue();
                assertEquals("HELLO WORLD", newInput);
            }
        }

        @Test
        void shouldCopyOutputToClipboard() {
            context.grantPermissions(java.util.Arrays.asList("clipboard-read", "clipboard-write"));

            page.locator("button:has-text('UPPERCASE')").click();
            page.locator("textarea").first().fill("test");

            // Auto-converts, wait for processing
            page.waitForTimeout(300);

            Locator copyButton = page.locator("button:has-text('Copy')");
            if (copyButton.isVisible()) {
                copyButton.click();
                assertTrue(page.locator(".text-green-400").or(page.locator("text=/copied/i"))
                    .isVisible(new Locator.IsVisibleOptions().setTimeout(2000)));
            }
        }

        @Test
        void shouldLoadSampleText() {
            Locator sampleButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)sample"));

            if (sampleButton.count() > 0) {
                sampleButton.first().click();
                String input = page.locator("textarea").first().inputValue();
                assertTrue(input.length() > 0);
            } else {
                // Skip test if sample button doesn't exist
                Assumptions.assumeTrue(false);
            }
        }

        @Test
        void shouldDownloadResult() {
            page.locator("textarea").first().fill("test");
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^UPPERCASE$")).click();

            Locator downloadButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)download"));
            if (downloadButton.count() > 0) {
                Download download = page.waitForDownload(() -> {
                    downloadButton.first().click();
                });
                assertTrue(download.suggestedFilename().contains(".txt"));
            } else {
                // Skip test if download button doesn't exist
                Assumptions.assumeTrue(false);
            }
        }
    }

    @Nested
    class Statistics {
        @Test
        void shouldShowCharacterAndWordCounts() {
            page.locator("textarea").first().fill("hello world test");

            assertTrue(page.locator("text=/characters/i").first().isVisible());
            assertTrue(page.locator("text=/words/i").first().isVisible());
        }
    }

    @Nested
    class ErrorHandling {
        @Test
        void shouldHandleEmptyInput() {
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^UPPERCASE$")).click();

            String output = page.locator("textarea").nth(1).inputValue();
            assertEquals("", output);
        }
    }

    @Nested
    class MobileResponsiveness {
        @Test
        void shouldBeResponsiveOnMobile() {
            page.setViewportSize(375, 667);

            assertTrue(page.locator("h1").isVisible());
            assertTrue(page.locator("textarea").first().isVisible());

            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^UPPERCASE$")).click();
            page.locator("textarea").first().fill("mobile test");

            // Auto-converts, wait for processing
            page.waitForTimeout(300);

            String output = page.locator("textarea").nth(1).inputValue();
            assertEquals("MOBILE TEST", output);
        }
    }

    @Nested
    class History {
        @Test
        void shouldMaintainConversionHistory() {
            page.locator("textarea").first().fill("test1");
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^UPPERCASE$")).click();

            page.locator("textarea").first().fill("test2");
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^lowercase$")).click();

            Locator historySection = page.locator("text=/Recent|History/i");
            if (historySection.count() > 0) {
                assertTrue(historySection.first().isVisible());
            } else {
                // Skip test if history section doesn't exist
                Assumptions.assumeTrue(false);
            }
        }
    }
}
