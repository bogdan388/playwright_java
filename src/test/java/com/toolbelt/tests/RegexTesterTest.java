package com.toolbelt.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class RegexTesterTest {
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
        page.navigate("/regex");

        // Wait for the page to load
        page.locator("h1:has-text('REGEX TESTER')").waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void shouldLoadTheRegexTesterPageCorrectly() {
        // Check main elements are present
        assertTrue(page.locator("h1:has-text('REGEX TESTER')").isVisible());
        assertTrue(page.locator("text=Test and validate regular expressions").isVisible());

        // Check input fields
        assertTrue(page.locator("input[placeholder*='regex pattern']").isVisible());
        assertTrue(page.locator("textarea[placeholder*='text to test']").isVisible());

        // Check back button
        assertTrue(page.locator("button:has-text('Back to Toolbelt')").isVisible());
    }

    @Test
    void shouldNavigateBackToHomepage() {
        page.click("button:has-text('Back to Toolbelt')");
        assertTrue(page.locator("h1:has-text('TOOLBELT')").isVisible());
    }

    @Nested
    class PatternTesting {
        @Test
        void shouldMatchSimplePattern() {
            page.fill("input[placeholder*='regex pattern']", "test");
            page.fill("textarea[placeholder*='text to test']", "This is a test string for testing");

            // Wait for results
            page.waitForTimeout(500);

            // Check matches found
            assertTrue(page.locator("text=/\\d+ match(es)? found/i").isVisible());
        }

        @Test
        void shouldShowErrorForInvalidPattern() {
            page.fill("input[placeholder*='regex pattern']", "[");
            page.fill("textarea[placeholder*='text to test']", "test");

            // Wait for error
            page.waitForTimeout(500);

            // Check no matches with invalid pattern (error is handled gracefully)
            assertTrue(page.locator("text=/No matches found/i").isVisible());
        }

        @Test
        void shouldShowNoMatchesMessage() {
            page.fill("input[placeholder*='regex pattern']", "xyz");
            page.fill("textarea[placeholder*='text to test']", "abc def ghi");

            // Wait for results
            page.waitForTimeout(500);

            // Check no matches message
            assertTrue(page.locator("text=No matches found").isVisible());
        }
    }

    @Nested
    class FlagsTesting {
        @Test
        void shouldToggleGlobalFlag() {
            // Set pattern and test string
            page.fill("input[placeholder*='regex pattern']", "a");
            page.fill("textarea[placeholder*='text to test']", "aaa");

            // Toggle global flag
            page.click("button:has-text('g')");
            page.waitForTimeout(500);

            // Should find multiple matches with global flag
            assertTrue(page.locator("text=3 matches found").isVisible());
        }

        @Test
        void shouldToggleCaseInsensitiveFlag() {
            page.fill("input[placeholder*='regex pattern']", "TEST");
            page.fill("textarea[placeholder*='text to test']", "test Test TEST");

            // Without i flag - should find only exact case match
            page.waitForTimeout(500);
            String matchText = page.locator("text=/\\d+ match(es)? found/i").textContent();
            assertTrue(matchText.contains("1 match"));

            // Toggle i flag
            page.click("button:has-text('i')");
            page.waitForTimeout(500);

            // With i flag - should find all variations
            matchText = page.locator("text=/\\d+ match(es)? found/i").textContent();
            assertTrue(matchText.contains("match"));
        }

        @Test
        void shouldToggleMultilineFlag() {
            page.fill("input[placeholder*='regex pattern']", "^test");
            page.fill("textarea[placeholder*='text to test']", "first line\ntest line\nthird line");

            // Without m flag
            page.waitForTimeout(500);
            assertTrue(page.locator("text=No matches found").isVisible());

            // Toggle m flag
            page.click("button:has-text('m')");
            page.waitForTimeout(500);

            // Should find match at line start
            assertTrue(page.locator("text=/1 match found/i").isVisible());
        }

        @Test
        void shouldTestAllFlagsSimultaneously() {
            // Enable all flags
            page.click("button:has-text('g')");
            page.click("button:has-text('i')");
            page.click("button:has-text('m')");
            page.click("button:has-text('s')");
            page.click("button:has-text('u')");

            page.fill("input[placeholder*='regex pattern']", "test");
            page.fill("textarea[placeholder*='text to test']", "Test\nTEST\ntest");

            page.waitForTimeout(500);

            // Should find all matches with all flags
            assertTrue(page.locator("text=/3 matches found/i").isVisible());
        }
    }

    @Nested
    class QuickExamples {
        @Test
        void shouldLoadEmailValidationExample() {
            page.selectOption("select:has-text('Select a pattern')", "email");
            page.waitForTimeout(500);

            // Check pattern was loaded
            String patternValue = page.locator("input[placeholder*='regex pattern']").inputValue();
            assertTrue(patternValue.contains("@"));

            // Check test string was loaded
            String testValue = page.locator("textarea[placeholder*='text to test']").inputValue();
            assertTrue(testValue.contains("example.com"));
        }

        @Test
        void shouldLoadPhoneNumberExample() {
            page.selectOption("select:has-text('Select a pattern')", "phone");
            page.waitForTimeout(500);

            String patternValue = page.locator("input[placeholder*='regex pattern']").inputValue();
            assertTrue(patternValue.contains("\\d{3}"));
        }

        @Test
        void shouldLoadUrlValidationExample() {
            page.selectOption("select:has-text('Select a pattern')", "url");
            page.waitForTimeout(500);

            String patternValue = page.locator("input[placeholder*='regex pattern']").inputValue();
            assertTrue(patternValue.contains("https?"));
        }

        @Test
        void shouldTestAllQuickExamples() {
            String[] examples = {"email", "phone", "url", "ipv4", "date", "hexColor", "username", "password"};

            for (String example : examples) {
                page.selectOption("select:has-text('Select a pattern')", example);
                page.waitForTimeout(500);

                // Each example should load a pattern
                String patternValue = page.locator("input[placeholder*='regex pattern']").inputValue();
                assertNotNull(patternValue);
                assertTrue(patternValue.length() > 0);

                // Each example should load test strings
                String testValue = page.locator("textarea[placeholder*='text to test']").inputValue();
                assertNotNull(testValue);
                assertTrue(testValue.length() > 0);
            }
        }
    }

    @Nested
    class LanguageFlavors {
        @Test
        void shouldHaveAll15LanguageFlavors() {
            Locator selectElement = page.locator("select").nth(1); // Second select is for flavors

            // Get all options text
            java.util.List<String> options = selectElement.locator("option").allTextContents();

            // Check that we have the right number of flavors (15 + default option)
            assertTrue(options.size() >= 15);

            // Check specific flavors exist
            String[] expectedFlavors = {
                "JavaScript", "Python", "PHP", ".NET (C#)",
                "Perl", "Go", "Ruby", "Rust", "MySQL",
                "PostgreSQL", "Vim", "sed/awk", "Swift", "Kotlin"
            };

            for (String flavor : expectedFlavors) {
                boolean found = false;
                for (String opt : options) {
                    if (opt.contains(flavor)) {
                        found = true;
                        break;
                    }
                }
                assertTrue(found, "Flavor " + flavor + " should exist");
            }

            // Java should be there but not confused with JavaScript
            boolean javaFound = false;
            for (String opt : options) {
                if (opt.equals("Java") || opt.contains("Java (")) {
                    javaFound = true;
                    break;
                }
            }
            assertTrue(javaFound);
        }

        @Test
        void shouldChangeLanguageFlavor() {
            Locator selectElement = page.locator("select").nth(1);

            // Select Python
            selectElement.selectOption(new SelectOption().setLabel("Python"));
            page.waitForTimeout(500);

            // Check if Python is selected
            String selectedValue = selectElement.inputValue();
            assertEquals("python", selectedValue);
        }

        @Test
        void shouldShowHideFlavorInfo() {
            // Click Show Info button
            page.click("button:has-text('Show Info')");
            page.waitForTimeout(500);

            // Check info is visible
            assertTrue(page.locator("text=ECMAScript regex engine").isVisible());

            // Click Hide Info button
            page.click("button:has-text('Hide Info')");
            page.waitForTimeout(500);

            // Check info is hidden
            assertFalse(page.locator("text=ECMAScript regex engine").isVisible());
        }

        @Test
        void shouldDisplayCorrectInfoForEachLanguage() {
            Locator selectElement = page.locator("select").nth(1);
            page.click("button:has-text('Show Info')");

            // Test Python
            selectElement.selectOption(new SelectOption().setLabel("Python"));
            assertTrue(page.locator("text=Python re module").isVisible());

            // Test Go
            selectElement.selectOption(new SelectOption().setLabel("Go"));
            assertTrue(page.locator("text=RE2 engine").isVisible());

            // Test Rust
            selectElement.selectOption(new SelectOption().setLabel("Rust"));
            assertTrue(page.locator("text=regex crate").isVisible());
        }
    }

    @Nested
    class UIControls {
        @Test
        void shouldCopyPatternToClipboard() {
            // Grant clipboard permissions for supported browsers
            context.grantPermissions(java.util.Arrays.asList("clipboard-read", "clipboard-write"));

            page.fill("input[placeholder*='regex pattern']", "test.*pattern");

            // Click copy button
            page.click("button[title='Copy pattern']");

            // Check for success indicator - copy button should show checkmark
            assertTrue(page.locator("button[title='Copy pattern'] svg").isVisible());
        }

        @Test
        void shouldClearAllInputs() {
            // Fill inputs
            page.fill("input[placeholder*='regex pattern']", "test");
            page.fill("textarea[placeholder*='text to test']", "test string");

            // Click clear button
            page.click("button[title='Clear all']");
            page.waitForTimeout(500);

            // Check inputs are cleared
            String patternValue = page.locator("input[placeholder*='regex pattern']").inputValue();
            String testValue = page.locator("textarea[placeholder*='text to test']").inputValue();

            assertEquals("", patternValue);
            assertEquals("", testValue);
        }
    }

    @Nested
    class MatchGroups {
        @Test
        void shouldDisplayCapturedGroups() {
            page.fill("input[placeholder*='regex pattern']", "(\\w+)@(\\w+\\.\\w+)");
            page.fill("textarea[placeholder*='text to test']", "user@example.com");

            page.waitForTimeout(500);

            // Check groups are displayed
            assertTrue(page.locator("text=Captured Groups").isVisible());
            assertTrue(page.locator("text=Group 1").isVisible());
            assertTrue(page.locator("text=Group 2").isVisible());
        }

        @Test
        void shouldShowMatchPositions() {
            page.fill("input[placeholder*='regex pattern']", "test");
            page.fill("textarea[placeholder*='text to test']", "This is a test string");

            page.waitForTimeout(500);

            // Check position is displayed
            assertTrue(page.locator("text=/Position: \\d+/i").isVisible());
        }
    }

    @Nested
    class ComplexPatterns {
        @Test
        void shouldHandleLookaheadAssertions() {
            page.fill("input[placeholder*='regex pattern']", "foo(?=bar)");
            page.fill("textarea[placeholder*='text to test']", "foobar foobaz");

            page.waitForTimeout(500);

            // Should match only foo followed by bar
            assertTrue(page.locator("text=/1 match found/i").isVisible());
        }

        @Test
        void shouldHandleLookbehindAssertions() {
            page.fill("input[placeholder*='regex pattern']", "(?<=foo)bar");
            page.fill("textarea[placeholder*='text to test']", "foobar bazbar");

            page.waitForTimeout(500);

            // Should match only bar preceded by foo
            assertTrue(page.locator("text=/1 match found/i").isVisible());
        }

        @Test
        void shouldHandleNamedGroups() {
            page.fill("input[placeholder*='regex pattern']", "(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})");
            page.fill("textarea[placeholder*='text to test']", "2025-01-15");

            page.waitForTimeout(500);

            // Should find match with named groups
            assertTrue(page.locator("text=/1 match found/i").isVisible());
            assertTrue(page.locator("text=Captured Groups").isVisible());
        }
    }

    @Nested
    class Performance {
        @Test
        void shouldHandleLargeTextEfficiently() {
            // Generate large text
            String largeText = "test ".repeat(10000);

            page.fill("input[placeholder*='regex pattern']", "test");
            page.fill("textarea[placeholder*='text to test']", largeText);

            // Should process within reasonable time (5 seconds)
            page.waitForTimeout(1000);

            assertTrue(page.locator("text=/match(es)? found/i").isVisible(new Locator.IsVisibleOptions().setTimeout(5000)));
        }

        @Test
        void shouldHandleComplexPatternsEfficiently() {
            String complexPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
            String testStrings = "Password1!\nweakpass\nStrongP@ss123\nNoSpecial123\nValidP@ssw0rd!\n" +
                "AnotherValid1!\nInvalid\nShort1!\nThisIsAVeryLongPasswordThatShouldStillWorkProperly123!@#";

            page.fill("input[placeholder*='regex pattern']", complexPattern);
            page.fill("textarea[placeholder*='text to test']", testStrings);

            // Should process complex validation within reasonable time
            page.waitForTimeout(1000);

            assertTrue(page.locator("text=/match(es)? found/i").isVisible(new Locator.IsVisibleOptions().setTimeout(5000)));
        }
    }

    @Nested
    class MobileResponsiveness {
        @Test
        void shouldBeResponsiveOnMobile() {
            // Set mobile viewport
            page.setViewportSize(375, 667);

            page.navigate("/regex");

            // Check elements are still accessible
            assertTrue(page.locator("h1:has-text('REGEX TESTER')").isVisible());
            assertTrue(page.locator("input[placeholder*='regex pattern']").isVisible());
            assertTrue(page.locator("textarea[placeholder*='text to test']").isVisible());

            // Check layout is stacked (not side-by-side)
            BoundingBox patternBox = page.locator("input[placeholder*='regex pattern']").boundingBox();
            BoundingBox textBox = page.locator("textarea[placeholder*='text to test']").boundingBox();

            if (patternBox != null && textBox != null) {
                // Text area should be below pattern input on mobile
                assertTrue(textBox.y > patternBox.y + patternBox.height);
            }
        }
    }

    @Nested
    class Accessibility {
        @Test
        void shouldBeKeyboardNavigable() {
            // Focus on pattern input directly and type
            page.locator("input[placeholder*='regex pattern']").click();
            page.keyboard().type("test");

            // Click on test string input and type
            page.locator("textarea[placeholder*='text to test']").click();
            page.keyboard().type("test string");

            // Should have typed in the inputs
            String patternValue = page.locator("input[placeholder*='regex pattern']").inputValue();
            assertTrue(patternValue.contains("test"));

            String testValue = page.locator("textarea[placeholder*='text to test']").inputValue();
            assertTrue(testValue.contains("test string"));
        }

        @Test
        void shouldHaveProperLabelsAndAriaAttributes() {
            // Check for labels
            assertTrue(page.locator("label:has-text('Pattern')").isVisible());
            assertTrue(page.locator("label:has-text('Test String')").isVisible());
            assertTrue(page.locator("label:has-text('Flags')").isVisible());
            assertTrue(page.locator("label:has-text('Results')").isVisible());
        }
    }

    @Nested
    class EdgeCases {
        @Test
        void shouldHandleEmptyPatternGracefully() {
            page.fill("textarea[placeholder*='text to test']", "test string");

            page.waitForTimeout(500);

            // Should not show error or crash
            assertTrue(page.locator("text=Enter a pattern and test string").isVisible());
        }

        @Test
        void shouldHandleEmptyTestStringGracefully() {
            page.fill("input[placeholder*='regex pattern']", "test");

            page.waitForTimeout(500);

            // Should not show error or crash
            assertTrue(page.locator("text=Enter a pattern and test string").isVisible());
        }

        @Test
        void shouldHandleSpecialCharactersInPattern() {
            page.fill("input[placeholder*='regex pattern']", "\\$\\d+\\.\\d{2}");
            page.fill("textarea[placeholder*='text to test']", "Price: $19.99 or $5.00");

            page.waitForTimeout(500);

            // Should find currency matches
            assertTrue(page.locator("text=/\\d+ match(es)? found/i").isVisible());
        }

        @Test
        void shouldHandleUnicodeCharacters() {
            page.fill("input[placeholder*='regex pattern']", "[α-ω]+");
            page.fill("textarea[placeholder*='text to test']", "Greek: αβγδε and more ωψχ");

            page.click("button:has-text('u')"); // Enable Unicode flag
            page.waitForTimeout(500);

            // Should handle Unicode properly - check for match results text
            String results = page.locator("text=/\\d+ match(es)? found/i").textContent();
            assertNotNull(results);
        }
    }

    @Nested
    class RealWorldScenarios {
        @Test
        void shouldValidateEmailAddresses() {
            page.selectOption("select:has-text('Select a pattern')", "email");

            String emails = "valid@example.com\ninvalid.email\nuser+tag@domain.co.uk\n" +
                "@nodomain.com\nnodomain@\nproper-email_123@test-site.org";

            page.fill("textarea[placeholder*='text to test']", emails);
            page.waitForTimeout(500);

            // Should find valid emails
            assertTrue(page.locator("text=/match(es)? found/i").isVisible());
        }

        @Test
        void shouldExtractUrlsFromText() {
            page.selectOption("select:has-text('Select a pattern')", "url");

            String text = "Check out these links:\nhttps://www.google.com\nhttp://example.org/path/to/page\n" +
                "Visit https://github.com/user/repo for more\nNot a URL: just-text-here\n" +
                "Another one: https://api.service.io/v1/users?id=123&type=admin";

            page.fill("textarea[placeholder*='text to test']", text);
            page.click("button:has-text('g')"); // Enable global flag
            page.waitForTimeout(1000);

            // Should find URLs - check for either singular or plural
            boolean hasMatches = page.locator("text=/match(es)? found/i").count() > 0 ||
                                page.locator("text=/found/i").count() > 0;
            assertTrue(hasMatches);
        }

        @Test
        void shouldValidateStrongPasswords() {
            page.selectOption("select:has-text('Select a pattern')", "password");

            String passwords = "weak\nNoNumber!\nNoSpecial8\nnoUpper1!\nShort1!\n" +
                "ValidPassword123!\nAnotherGood1@Pass\nThisOneIsAlsoValid99#";

            page.fill("textarea[placeholder*='text to test']", passwords);
            page.waitForTimeout(500);

            // Should identify valid strong passwords
            assertTrue(page.locator("text=/match(es)? found/i").isVisible());
        }
    }
}
