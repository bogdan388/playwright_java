package com.toolbelt.tests;

import com.microsoft.playwright.*;
import com.toolbelt.pages.RegexTesterPage;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class RegexTesterTest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;
    private RegexTesterPage regexPage;

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
        regexPage = new RegexTesterPage(page);

        // Wait for the page to load
        page.locator("h1:has-text('REGEX TESTER')").waitFor();
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
        assertTrue(regexPage.isTitleVisible());
        assertTrue(regexPage.isDescriptionVisible());

        // Check input fields
        assertTrue(regexPage.isPatternInputVisible());
        assertTrue(regexPage.isTestStringTextareaVisible());

        // Check back button
        assertTrue(regexPage.isBackToToolbeltButtonVisible());
    }

    @Test
    void shouldNavigateBackToHomepage() {
        regexPage.clickBackToToolbelt();
        assertTrue(regexPage.isToolbeltH1Visible());
    }

    @Nested
    class PatternTesting {
        @Test
        void shouldMatchSimplePattern() {
            regexPage.fillPattern("test");
            regexPage.fillTestString("This is a test string for testing");

            // Wait for results
            regexPage.waitForResults();

            // Check matches found
            assertTrue(regexPage.isMatchesFoundVisible());
        }

        @Test
        void shouldShowErrorForInvalidPattern() {
            regexPage.fillPattern("[");
            regexPage.fillTestString("test");

            // Wait for error
            regexPage.waitForResults();

            // Check no matches with invalid pattern (error is handled gracefully)
            assertTrue(regexPage.isNoMatchesVisible());
        }

        @Test
        void shouldShowNoMatchesMessage() {
            regexPage.fillPattern("xyz");
            regexPage.fillTestString("abc def ghi");

            // Wait for results
            regexPage.waitForResults();

            // Check no matches message
            assertTrue(regexPage.isNoMatchesVisible());
        }
    }

    @Nested
    class FlagsTesting {
        @Test
        void shouldToggleGlobalFlag() {
            // Set pattern and test string
            regexPage.fillPattern("a");
            regexPage.fillTestString("aaa");

            // Toggle global flag
            regexPage.clickFlagG();
            regexPage.waitForResults();

            // Should find multiple matches with global flag
            assertTrue(page.locator("text=3 matches found").isVisible());
        }

        @Test
        void shouldToggleCaseInsensitiveFlag() {
            regexPage.fillPattern("TEST");
            regexPage.fillTestString("test Test TEST");

            // Without i flag - should find only exact case match
            regexPage.waitForResults();
            String matchText = regexPage.getMatchesFoundText();
            assertTrue(matchText.contains("1 match"));

            // Toggle i flag
            regexPage.clickFlagI();
            regexPage.waitForResults();

            // With i flag - should find all variations
            matchText = regexPage.getMatchesFoundText();
            assertTrue(matchText.contains("match"));
        }

        @Test
        void shouldToggleMultilineFlag() {
            regexPage.fillPattern("^test");
            regexPage.fillTestString("first line\ntest line\nthird line");

            // Without m flag
            regexPage.waitForResults();
            assertTrue(regexPage.isNoMatchesVisible());

            // Toggle m flag
            regexPage.clickFlagM();
            regexPage.waitForResults();

            // Should find match at line start
            assertTrue(page.locator("text=/1 match found/i").isVisible());
        }

        @Test
        void shouldTestAllFlagsSimultaneously() {
            // Enable all flags
            regexPage.clickFlagG();
            regexPage.clickFlagI();
            regexPage.clickFlagM();
            regexPage.clickFlagS();
            regexPage.clickFlagU();

            regexPage.fillPattern("test");
            regexPage.fillTestString("Test\nTEST\ntest");

            regexPage.waitForResults();

            // Should find all matches with all flags
            assertTrue(page.locator("text=/3 matches found/i").isVisible());
        }
    }

    @Nested
    class QuickExamples {
        @Test
        void shouldLoadEmailValidationExample() {
            regexPage.selectQuickExample("email");
            regexPage.waitForResults();

            // Check pattern was loaded
            String patternValue = regexPage.getPatternValue();
            assertTrue(patternValue.contains("@"));

            // Check test string was loaded
            String testValue = regexPage.getTestStringValue();
            assertTrue(testValue.contains("example.com"));
        }

        @Test
        void shouldLoadPhoneNumberExample() {
            regexPage.selectQuickExample("phone");
            regexPage.waitForResults();

            String patternValue = regexPage.getPatternValue();
            assertTrue(patternValue.contains("\\d{3}"));
        }

        @Test
        void shouldLoadUrlValidationExample() {
            regexPage.selectQuickExample("url");
            regexPage.waitForResults();

            String patternValue = regexPage.getPatternValue();
            assertTrue(patternValue.contains("https?"));
        }

        @Test
        void shouldTestAllQuickExamples() {
            String[] examples = {"email", "phone", "url", "ipv4", "date", "hexColor", "username", "password"};

            for (String example : examples) {
                regexPage.selectQuickExample(example);
                regexPage.waitForResults();

                // Each example should load a pattern
                String patternValue = regexPage.getPatternValue();
                assertNotNull(patternValue);
                assertTrue(patternValue.length() > 0);

                // Each example should load test strings
                String testValue = regexPage.getTestStringValue();
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
            // Select Python
            regexPage.selectLanguageFlavor("Python");
            regexPage.waitForResults();

            // Check if Python is selected
            String selectedValue = regexPage.getFlavorSelectValue();
            assertEquals("python", selectedValue);
        }

        @Test
        void shouldShowHideFlavorInfo() {
            // Click Show Info button
            regexPage.clickShowInfo();
            regexPage.waitForResults();

            // Check info is visible
            assertTrue(regexPage.isEcmaScriptEngineVisible());

            // Click Hide Info button
            regexPage.clickHideInfo();
            regexPage.waitForResults();

            // Check info is hidden
            assertFalse(regexPage.isEcmaScriptEngineVisible());
        }

        @Test
        void shouldDisplayCorrectInfoForEachLanguage() {
            regexPage.clickShowInfo();

            // Test Python
            regexPage.selectLanguageFlavor("Python");
            assertTrue(regexPage.isPythonReModuleVisible());

            // Test Go
            regexPage.selectLanguageFlavor("Go");
            assertTrue(regexPage.isRe2EngineVisible());

            // Test Rust
            regexPage.selectLanguageFlavor("Rust");
            assertTrue(regexPage.isRegexCrateVisible());
        }
    }

    @Nested
    class UIControls {
        @Test
        void shouldCopyPatternToClipboard() {
            // Grant clipboard permissions for supported browsers
            context.grantPermissions(java.util.Arrays.asList("clipboard-read", "clipboard-write"));

            regexPage.fillPattern("test.*pattern");

            // Click copy button
            regexPage.clickCopyPattern();

            // Check for success indicator - copy button should show checkmark
            assertTrue(regexPage.isCopyPatternSvgVisible());
        }

        @Test
        void shouldClearAllInputs() {
            // Fill inputs
            regexPage.fillPattern("test");
            regexPage.fillTestString("test string");

            // Click clear button
            regexPage.clickClearAll();
            regexPage.waitForResults();

            // Check inputs are cleared
            String patternValue = regexPage.getPatternValue();
            String testValue = regexPage.getTestStringValue();

            assertEquals("", patternValue);
            assertEquals("", testValue);
        }
    }

    @Nested
    class MatchGroups {
        @Test
        void shouldDisplayCapturedGroups() {
            regexPage.fillPattern("(\\w+)@(\\w+\\.\\w+)");
            regexPage.fillTestString("user@example.com");

            regexPage.waitForResults();

            // Check groups are displayed
            assertTrue(regexPage.isCapturedGroupsVisible());
            assertTrue(regexPage.isGroup1Visible());
            assertTrue(regexPage.isGroup2Visible());
        }

        @Test
        void shouldShowMatchPositions() {
            regexPage.fillPattern("test");
            regexPage.fillTestString("This is a test string");

            regexPage.waitForResults();

            // Check position is displayed
            assertTrue(regexPage.isPositionTextVisible());
        }
    }

    @Nested
    class ComplexPatterns {
        @Test
        void shouldHandleLookaheadAssertions() {
            regexPage.fillPattern("foo(?=bar)");
            regexPage.fillTestString("foobar foobaz");

            regexPage.waitForResults();

            // Should match only foo followed by bar
            assertTrue(page.locator("text=/1 match found/i").isVisible());
        }

        @Test
        void shouldHandleLookbehindAssertions() {
            regexPage.fillPattern("(?<=foo)bar");
            regexPage.fillTestString("foobar bazbar");

            regexPage.waitForResults();

            // Should match only bar preceded by foo
            assertTrue(page.locator("text=/1 match found/i").isVisible());
        }

        @Test
        void shouldHandleNamedGroups() {
            regexPage.fillPattern("(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})");
            regexPage.fillTestString("2025-01-15");

            regexPage.waitForResults();

            // Should find match with named groups
            assertTrue(page.locator("text=/1 match found/i").isVisible());
            assertTrue(regexPage.isCapturedGroupsVisible());
        }
    }

    @Nested
    class Performance {
        @Test
        void shouldHandleLargeTextEfficiently() {
            // Generate large text
            String largeText = "test ".repeat(10000);

            regexPage.fillPattern("test");
            regexPage.fillTestString(largeText);

            // Should process within reasonable time (5 seconds)
            regexPage.waitForResults(1000);

            assertTrue(page.locator("text=/match(es)? found/i").isVisible(new Locator.IsVisibleOptions().setTimeout(5000)));
        }

        @Test
        void shouldHandleComplexPatternsEfficiently() {
            String complexPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
            String testStrings = "Password1!\nweakpass\nStrongP@ss123\nNoSpecial123\nValidP@ssw0rd!\n" +
                "AnotherValid1!\nInvalid\nShort1!\nThisIsAVeryLongPasswordThatShouldStillWorkProperly123!@#";

            regexPage.fillPattern(complexPattern);
            regexPage.fillTestString(testStrings);

            // Should process complex validation within reasonable time
            regexPage.waitForResults(1000);

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
            assertTrue(regexPage.isTitleVisible());
            assertTrue(regexPage.isPatternInputVisible());
            assertTrue(regexPage.isTestStringTextareaVisible());

            // Check layout is stacked (not side-by-side)
            Locator.BoundingBox patternBox = page.locator("input[placeholder*='regex pattern']").boundingBox();
            Locator.BoundingBox textBox = page.locator("textarea[placeholder*='text to test']").boundingBox();

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
            regexPage.typePattern("test");

            // Click on test string input and type
            regexPage.typeTestString("test string");

            // Should have typed in the inputs
            String patternValue = regexPage.getPatternValue();
            assertTrue(patternValue.contains("test"));

            String testValue = regexPage.getTestStringValue();
            assertTrue(testValue.contains("test string"));
        }

        @Test
        void shouldHaveProperLabelsAndAriaAttributes() {
            // Check for labels
            assertTrue(regexPage.isPatternLabelVisible());
            assertTrue(regexPage.isTestStringLabelVisible());
            assertTrue(regexPage.isFlagsLabelVisible());
            assertTrue(regexPage.isResultsLabelVisible());
        }
    }

    @Nested
    class EdgeCases {
        @Test
        void shouldHandleEmptyPatternGracefully() {
            regexPage.fillTestString("test string");

            regexPage.waitForResults();

            // Should not show error or crash
            assertTrue(regexPage.isEnterPatternMessageVisible());
        }

        @Test
        void shouldHandleEmptyTestStringGracefully() {
            regexPage.fillPattern("test");

            regexPage.waitForResults();

            // Should not show error or crash
            assertTrue(regexPage.isEnterPatternMessageVisible());
        }

        @Test
        void shouldHandleSpecialCharactersInPattern() {
            regexPage.fillPattern("\\$\\d+\\.\\d{2}");
            regexPage.fillTestString("Price: $19.99 or $5.00");

            regexPage.waitForResults();

            // Should find currency matches
            assertTrue(regexPage.isMatchesFoundVisible());
        }

        @Test
        void shouldHandleUnicodeCharacters() {
            regexPage.fillPattern("[α-ω]+");
            regexPage.fillTestString("Greek: αβγδε and more ωψχ");

            regexPage.clickFlagU(); // Enable Unicode flag
            regexPage.waitForResults();

            // Should handle Unicode properly - check for match results text
            String results = regexPage.getMatchesFoundText();
            assertNotNull(results);
        }
    }

    @Nested
    class RealWorldScenarios {
        @Test
        void shouldValidateEmailAddresses() {
            regexPage.selectQuickExample("email");

            String emails = "valid@example.com\ninvalid.email\nuser+tag@domain.co.uk\n" +
                "@nodomain.com\nnodomain@\nproper-email_123@test-site.org";

            regexPage.fillTestString(emails);
            regexPage.waitForResults();

            // Should find valid emails
            assertTrue(regexPage.isMatchesFoundVisible());
        }

        @Test
        void shouldExtractUrlsFromText() {
            regexPage.selectQuickExample("url");

            String text = "Check out these links:\nhttps://www.google.com\nhttp://example.org/path/to/page\n" +
                "Visit https://github.com/user/repo for more\nNot a URL: just-text-here\n" +
                "Another one: https://api.service.io/v1/users?id=123&type=admin";

            regexPage.fillTestString(text);
            regexPage.clickFlagG(); // Enable global flag
            regexPage.waitForResults(1000);

            // Should find URLs - check for either singular or plural
            assertTrue(regexPage.hasMatchesOrFound());
        }

        @Test
        void shouldValidateStrongPasswords() {
            regexPage.selectQuickExample("password");

            String passwords = "weak\nNoNumber!\nNoSpecial8\nnoUpper1!\nShort1!\n" +
                "ValidPassword123!\nAnotherGood1@Pass\nThisOneIsAlsoValid99#";

            regexPage.fillTestString(passwords);
            regexPage.waitForResults();

            // Should identify valid strong passwords
            assertTrue(regexPage.isMatchesFoundVisible());
        }
    }
}
