package com.toolbelt.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class JwtDecoderTest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;

    // Sample JWTs for testing
    private static final String VALID_JWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxOTUxNjIzOTAyMn0.4Adcj3UFYzPUVaVF43FmMab6RlaQD8A9V8wFzzht-KQ";
    private static final String EXPIRED_JWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkV4cGlyZWQgVG9rZW4iLCJpYXQiOjE1MTYyMzkwMjIsImV4cCI6MTUxNjIzOTAyMn0.2rS7x5IoX0I3sVOJLH2mc1sHXgQZLTLJAg8LCWMkLxc";

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
        page.navigate("/jwt-decoder");
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void shouldLoadTheJwtDecoderPageCorrectly() {
        // Check page title
        assertNotNull(page.locator("h1").textContent());
        assertTrue(page.locator("h1").textContent().contains("JWT Decoder"));

        // Check description
        assertTrue(page.locator("text=/Decode and verify JSON Web Tokens/i").isVisible());

        // Check input area
        assertTrue(page.locator("textarea[placeholder*='JWT token']").isVisible());

        // Check decode button
        assertTrue(page.locator("button:has-text('Decode Token')").isVisible());
    }

    @Test
    void shouldNavigateBackToHomepage() {
        page.click("text=/Back to Tools/i");
        assertTrue(page.url().endsWith("/"));
    }

    @Nested
    class DecodingFeatures {
        @Test
        void shouldDecodeValidJwtToken() {
            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(VALID_JWT);
            page.click("button:has-text('Decode Token')");

            // Check header section
            assertTrue(page.locator("text=/Header/i").isVisible());
            String headerContent = page.locator("pre").first().textContent();
            assertTrue(headerContent.contains("\"alg\": \"HS256\""));
            assertTrue(headerContent.contains("\"typ\": \"JWT\""));

            // Check payload section
            assertTrue(page.locator("text=/Payload/i").isVisible());
            String payloadContent = page.locator("pre").nth(1).textContent();
            assertTrue(payloadContent.contains("\"name\": \"John Doe\""));
            assertTrue(payloadContent.contains("\"admin\": true"));
        }

        @Test
        void shouldDisplayTokenInformation() {
            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(VALID_JWT);
            page.click("button:has-text('Decode Token')");

            // Check token info section
            assertTrue(page.locator("text=/Token Information/i").isVisible());
            assertTrue(page.locator("text=/Algorithm:/i").isVisible());
            assertTrue(page.locator("text=/Type:/i").isVisible());
            assertTrue(page.locator("text=/Issued At:/i").isVisible());
            assertTrue(page.locator("text=/Expires At:/i").isVisible());
        }

        @Test
        void shouldShowSignatureSection() {
            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(VALID_JWT);
            page.click("button:has-text('Decode Token')");

            // Check signature section
            assertTrue(page.locator("text=/Signature Verification/i").isVisible());
            assertTrue(page.locator("text=/Signature:/i").isVisible());
            assertTrue(page.locator("input[placeholder*='secret key']").isVisible());
        }

        @Test
        void shouldHandleExpiredToken() {
            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(EXPIRED_JWT);
            page.click("button:has-text('Decode Token')");

            // Should show expired status
            assertTrue(page.locator("text=/Expired/i").isVisible());
        }

        @Test
        void shouldShowErrorForInvalidJwtFormat() {
            String invalidJWT = "not.a.valid.jwt";
            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(invalidJWT);
            page.click("button:has-text('Decode Token')");

            // Should show error message
            assertTrue(page.locator("text=/Invalid/i").isVisible());
        }

        @Test
        void shouldShowErrorForMalformedJwt() {
            String malformedJWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid_payload.signature";
            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(malformedJWT);
            page.click("button:has-text('Decode Token')");

            assertTrue(page.locator("text=/Invalid base64/i").isVisible());
        }
    }

    @Nested
    class TokenClaims {
        @Test
        void shouldDisplayStandardClaims() {
            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(VALID_JWT);
            page.click("button:has-text('Decode Token')");

            // Check for standard claims in token info
            Locator tokenInfo = page.locator("text=/Token Information/i").locator("..");
            assertTrue(tokenInfo.textContent().contains("Subject"));
            assertTrue(tokenInfo.textContent().contains("Issuer"));
            assertTrue(tokenInfo.textContent().contains("Audience"));
        }

        @Test
        void shouldDisplayCustomClaimsInPayload() {
            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(VALID_JWT);
            page.click("button:has-text('Decode Token')");

            String payloadContent = page.locator("pre").nth(1).textContent();
            assertTrue(payloadContent.contains("\"name\""));
            assertTrue(payloadContent.contains("\"admin\""));
        }
    }

    @Nested
    class UIControls {
        @Test
        void shouldLoadSampleJwt() {
            page.click("button:has-text('Use Sample')");

            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            String value = tokenInput.inputValue();

            assertTrue(value.contains("eyJ"));
            assertEquals(3, value.split("\\.").length);
        }

        @Test
        void shouldCopyHeaderToClipboard() {
            // Grant clipboard permissions
            context.grantPermissions(java.util.Arrays.asList("clipboard-read", "clipboard-write"));

            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(VALID_JWT);
            page.click("button:has-text('Decode Token')");

            // Click copy button for header
            page.click("button[title='Copy header']");

            // Check for success indicator
            assertTrue(page.locator(".text-green-400").first().isVisible());
        }

        @Test
        void shouldCopyPayloadToClipboard() {
            // Grant clipboard permissions
            context.grantPermissions(java.util.Arrays.asList("clipboard-read", "clipboard-write"));

            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(VALID_JWT);
            page.click("button:has-text('Decode Token')");

            // Click copy button for payload
            page.click("button[title='Copy payload']");

            // Check for success indicator
            assertTrue(page.locator(".text-green-400").nth(1).isVisible());
        }
    }

    @Nested
    class SignatureVerification {
        @Test
        void shouldShowVerifySignatureButton() {
            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(VALID_JWT);
            page.click("button:has-text('Decode Token')");

            assertTrue(page.locator("button:has-text('Verify Signature')").isVisible());
        }

        @Test
        void shouldRequireSecretForVerification() {
            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(VALID_JWT);
            page.click("button:has-text('Decode Token')");

            // Try to verify without secret
            page.click("button:has-text('Verify Signature')");

            // Should show error about missing secret
            assertTrue(page.locator("text=/provide a secret/i").isVisible());
        }

        @Test
        void shouldVerifySignatureWithSecret() {
            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(VALID_JWT);
            page.click("button:has-text('Decode Token')");

            // Enter secret
            page.fill("input[placeholder*='secret key']", "test-secret");
            page.click("button:has-text('Verify Signature')");

            // Should show verification result
            assertTrue(page.locator("text=/Valid Signature|Invalid Signature/i").isVisible());
        }
    }

    @Nested
    class KeyboardShortcuts {
        @Test
        void shouldDecodeOnCtrlEnter() {
            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(VALID_JWT);
            tokenInput.press("Control+Enter");

            // Check that decoding happened
            assertTrue(page.locator("text=/Header/i").isVisible());
            assertTrue(page.locator("text=/Payload/i").isVisible());
        }
    }

    @Nested
    class ErrorHandling {
        @Test
        void shouldHandleEmptyInput() {
            page.click("button:has-text('Decode Token')");
            assertTrue(page.locator("text=/Invalid JWT format/i").isVisible());
        }

        @Test
        void shouldHandleJwtWithOnlyTwoParts() {
            String invalidJWT = "part1.part2";
            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(invalidJWT);
            page.click("button:has-text('Decode Token')");

            assertTrue(page.locator("text=/should have 3 parts/i").isVisible());
        }

        @Test
        void shouldHandleJwtWithInvalidBase64() {
            String invalidBase64JWT = "invalid!@#$.invalid!@#$.invalid!@#$";
            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(invalidBase64JWT);
            page.click("button:has-text('Decode Token')");

            assertTrue(page.locator("text=/Invalid/i").isVisible());
        }
    }

    @Nested
    class DifferentJwtTypes {
        @Test
        void shouldHandleJwtWithMinimalClaims() {
            String minimalJWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.Vg30C57s3l90JNap_VgMhKZjfc-p0fVP6jUxc8S5MqM";
            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(minimalJWT);
            page.click("button:has-text('Decode Token')");

            String payloadContent = page.locator("pre").nth(1).textContent();
            assertTrue(payloadContent.contains("\"sub\": \"1234567890\""));
        }

        @Test
        void shouldHandleJwtWithNestedObjects() {
            String nestedJWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7Im5hbWUiOiJKb2huIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl19fQ.k5GwPcZvGe2BE-Lm9Y3neqJ3lPiT3RnLSw3AOFoVn_4";
            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(nestedJWT);
            page.click("button:has-text('Decode Token')");

            String payloadContent = page.locator("pre").nth(1).textContent();
            assertTrue(payloadContent.contains("\"user\""));
            assertTrue(payloadContent.contains("\"roles\""));
        }

        @Test
        void shouldHandleJwtWithArrayClaims() {
            String arrayJWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6WyJhZG1pbiIsInVzZXIiLCJlZGl0b3IiXX0.bKy6otoNwRr2J4Y3zo9eXkQvK0rTmSU5BbQJDHmDL1A";
            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(arrayJWT);
            page.click("button:has-text('Decode Token')");

            String payloadContent = page.locator("pre").nth(1).textContent();
            assertTrue(payloadContent.contains("[\"admin\", \"user\", \"editor\"]"));
        }
    }

    @Nested
    class TimeBasedClaims {
        @Test
        void shouldDisplayIssuedAtTime() {
            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(VALID_JWT);
            page.click("button:has-text('Decode Token')");

            String issuedAtText = page.locator("text=/Issued At:/i").locator("..").textContent();
            assertTrue(issuedAtText.matches(".*\\d{1,2}/\\d{1,2}/\\d{4}.*"));
        }

        @Test
        void shouldDisplayExpirationTime() {
            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(VALID_JWT);
            page.click("button:has-text('Decode Token')");

            String expiresAtText = page.locator("text=/Expires At:/i").locator("..").textContent();
            assertTrue(expiresAtText.matches(".*\\d{1,2}/\\d{1,2}/\\d{4}.*"));
        }

        @Test
        void shouldCalculateTimeUntilExpiration() {
            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(VALID_JWT);
            page.click("button:has-text('Decode Token')");

            // Should show expiration status
            assertTrue(page.locator("text=/Expires in|Expired/i").isVisible());
        }
    }

    @Nested
    class MobileResponsiveness {
        @Test
        void shouldBeResponsiveOnMobile() {
            page.setViewportSize(375, 667);

            assertTrue(page.locator("h1").isVisible());
            assertTrue(page.locator("textarea").first().isVisible());
            assertTrue(page.locator("button:has-text('Decode Token')").isVisible());

            // Test decoding on mobile
            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(VALID_JWT);
            page.click("button:has-text('Decode Token')");

            assertTrue(page.locator("text=/Header/i").isVisible());
            assertTrue(page.locator("text=/Payload/i").isVisible());
        }

        @Test
        void shouldStackSectionsOnMobile() {
            page.setViewportSize(375, 667);

            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            tokenInput.fill(VALID_JWT);
            page.click("button:has-text('Decode Token')");

            // Check that header and payload sections are stacked
            BoundingBox headerBox = page.locator("text=/Header/i").locator("..").boundingBox();
            BoundingBox payloadBox = page.locator("text=/Payload/i").locator("..").boundingBox();

            assertTrue(headerBox.y < payloadBox.y);
        }
    }

    @Nested
    class Accessibility {
        @Test
        void shouldHaveProperLabels() {
            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            assertTrue(tokenInput.isVisible());

            // Check for label
            assertTrue(page.locator("text=/JWT Token/i").isVisible());
        }

        @Test
        void shouldBeKeyboardNavigable() {
            // Tab to sample button
            page.keyboard().press("Tab");
            page.keyboard().press("Tab");
            page.keyboard().press("Enter");

            Locator tokenInput = page.locator("textarea[placeholder*='JWT token']");
            String value = tokenInput.inputValue();
            assertTrue(value.contains("eyJ"));
        }
    }
}
