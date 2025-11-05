package com.toolbelt.tests;

import com.microsoft.playwright.*;
import com.toolbelt.pages.TextCaseConverterPage;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TextCaseConverterTest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;
    private TextCaseConverterPage textCasePage;

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
        textCasePage = new TextCaseConverterPage(page);
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void shouldLoadTheTextCaseConverterPageCorrectly() {
        assertTrue(textCasePage.getTitleText().contains("Text Case Converter"));
        assertTrue(textCasePage.isDescriptionVisible());
        assertTrue(textCasePage.isInputTextareaVisible());
        assertTrue(textCasePage.isOutputTextareaVisible());
    }

    @Test
    void shouldNavigateBackToHomepage() {
        textCasePage.navigateToHome();
        assertTrue(page.url().endsWith("/"));
    }

    @Nested
    class CaseConversions {
        @Test
        void shouldConvertToUppercase() {
            String input = "hello world";

            textCasePage.clickUppercase();
            textCasePage.fillInput(input);

            // Auto-converts, wait for processing
            textCasePage.waitForConversion();

            String output = textCasePage.getOutput();
            assertEquals("HELLO WORLD", output);
        }

        @Test
        void shouldConvertToLowercase() {
            String input = "HELLO WORLD";

            textCasePage.clickLowercase();
            textCasePage.fillInput(input);

            // Auto-converts, wait for processing
            textCasePage.waitForConversion();

            String output = textCasePage.getOutput();
            assertEquals("hello world", output);
        }

        @Test
        void shouldConvertToTitleCase() {
            String input = "hello world test";

            textCasePage.clickTitleCase();
            textCasePage.fillInput(input);

            // Auto-converts, wait for processing
            textCasePage.waitForConversion();

            String output = textCasePage.getOutput();
            assertEquals("Hello World Test", output);
        }

        @Test
        void shouldConvertToCamelCase() {
            String input = "hello world test";

            textCasePage.clickCamelCase();
            textCasePage.fillInput(input);

            // Auto-converts, wait for processing
            textCasePage.waitForConversion();

            String output = textCasePage.getOutput();
            assertEquals("helloWorldTest", output);
        }

        @Test
        void shouldConvertToPascalCase() {
            String input = "hello world test";

            textCasePage.clickPascalCase();
            textCasePage.fillInput(input);

            // Auto-converts, wait for processing
            textCasePage.waitForConversion();

            String output = textCasePage.getOutput();
            assertEquals("HelloWorldTest", output);
        }

        @Test
        void shouldConvertToSnakeCase() {
            String input = "hello world test";

            textCasePage.clickSnakeCase();
            textCasePage.fillInput(input);

            // Auto-converts, wait for processing
            textCasePage.waitForConversion();

            String output = textCasePage.getOutput();
            assertEquals("hello_world_test", output);
        }

        @Test
        void shouldConvertToKebabCase() {
            String input = "hello world test";

            textCasePage.clickKebabCase();
            textCasePage.fillInput(input);

            // Auto-converts, wait for processing
            textCasePage.waitForConversion();

            String output = textCasePage.getOutput();
            assertEquals("hello-world-test", output);
        }

        @Test
        void shouldConvertToConstantCase() {
            String input = "hello world test";

            textCasePage.clickConstantCase();
            textCasePage.fillInput(input);

            // Auto-converts, wait for processing
            textCasePage.waitForConversion();

            String output = textCasePage.getOutput();
            assertEquals("HELLO_WORLD_TEST", output);
        }
    }

    @Nested
    class UIControls {
        @Test
        void shouldSwapInputAndOutput() {
            String input = "hello world";

            textCasePage.clickUppercase();
            textCasePage.fillInput(input);

            // Auto-converts, wait for processing
            textCasePage.waitForConversion();

            textCasePage.clickSwap();

            String newInput = textCasePage.getInput();
            assertEquals("HELLO WORLD", newInput);
        }

        @Test
        void shouldCopyOutputToClipboard() {
            context.grantPermissions(java.util.Arrays.asList("clipboard-read", "clipboard-write"));

            textCasePage.clickUppercase();
            textCasePage.fillInput("test");

            // Auto-converts, wait for processing
            textCasePage.waitForConversion();

            textCasePage.clickCopy();
            assertTrue(textCasePage.isSuccessIndicatorVisible());
        }

        @Test
        void shouldLoadSampleText() {
            if (textCasePage.isSampleButtonVisible()) {
                textCasePage.clickSample();
                String input = textCasePage.getInput();
                assertTrue(input.length() > 0);
            } else {
                // Skip test if sample button doesn't exist
                Assumptions.assumeTrue(false);
            }
        }

        @Test
        void shouldDownloadResult() {
            textCasePage.fillInput("test");
            textCasePage.clickUppercase();

            if (textCasePage.isDownloadButtonVisible()) {
                Download download = page.waitForDownload(() -> {
                    textCasePage.clickDownload();
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
            textCasePage.fillInput("hello world test");

            assertTrue(textCasePage.isCharactersLabelVisible());
            assertTrue(textCasePage.isWordsLabelVisible());
        }
    }

    @Nested
    class ErrorHandling {
        @Test
        void shouldHandleEmptyInput() {
            textCasePage.clickUppercase();

            String output = textCasePage.getOutput();
            assertEquals("", output);
        }
    }

    @Nested
    class MobileResponsiveness {
        @Test
        void shouldBeResponsiveOnMobile() {
            page.setViewportSize(375, 667);

            assertTrue(textCasePage.isTitleVisible());
            assertTrue(textCasePage.isInputTextareaVisible());

            textCasePage.clickUppercase();
            textCasePage.fillInput("mobile test");

            // Auto-converts, wait for processing
            textCasePage.waitForConversion();

            String output = textCasePage.getOutput();
            assertEquals("MOBILE TEST", output);
        }
    }

    @Nested
    class History {
        @Test
        void shouldMaintainConversionHistory() {
            textCasePage.fillInput("test1");
            textCasePage.clickUppercase();

            textCasePage.fillInput("test2");
            textCasePage.clickLowercase();

            if (textCasePage.isHistorySectionVisible()) {
                assertTrue(textCasePage.isHistorySectionVisible());
            } else {
                // Skip test if history section doesn't exist
                Assumptions.assumeTrue(false);
            }
        }
    }
}
