package com.toolbelt.tests;

import com.microsoft.playwright.*;
import com.toolbelt.pages.JwtDecoderPage;
import com.toolbelt.utils.BrowserFactory;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class JwtDecoderTest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;
    private JwtDecoderPage jwtPage;

    // Sample JWTs for testing
    private static final String VALID_JWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMiwiZXhwIjoxOTUxNjIzOTAyMn0.4Adcj3UFYzPUVaVF43FmMab6RlaQD8A9V8wFzzht-KQ";
    private static final String EXPIRED_JWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkV4cGlyZWQgVG9rZW4iLCJpYXQiOjE1MTYyMzkwMjIsImV4cCI6MTUxNjIzOTAyMn0.2rS7x5IoX0I3sVOJLH2mc1sHXgQZLTLJAg8LCWMkLxc";

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
        page.navigate("/jwt-decoder");
        jwtPage = new JwtDecoderPage(page);
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
        assertNotNull(jwtPage.getTitleText());
        assertTrue(jwtPage.getTitleText().contains("JWT Decoder"));

        // Check description
        assertTrue(jwtPage.isDescriptionVisible());

        // Check input area
        assertTrue(jwtPage.isTokenTextareaVisible());

        // Check decode button
        assertTrue(jwtPage.isDecodeButtonVisible());
    }

    @Test
    void shouldNavigateBackToHomepage() {
        jwtPage.clickBackToTools();
        assertTrue(page.url().endsWith("/"));
    }

    @Nested
    class DecodingFeatures {
        @Test
        void shouldDecodeValidJwtToken() {
            jwtPage.fillToken(VALID_JWT);
            jwtPage.clickDecode();

            // Check header section
            assertTrue(jwtPage.isHeaderSectionVisible());
            String headerContent = jwtPage.getHeaderContent();
            assertTrue(headerContent.contains("\"alg\": \"HS256\""));
            assertTrue(headerContent.contains("\"typ\": \"JWT\""));

            // Check payload section
            assertTrue(jwtPage.isPayloadSectionVisible());
            String payloadContent = jwtPage.getPayloadContent();
            assertTrue(payloadContent.contains("\"name\": \"John Doe\""));
            assertTrue(payloadContent.contains("\"admin\": true"));
        }

        @Test
        void shouldDisplayTokenInformation() {
            jwtPage.fillToken(VALID_JWT);
            jwtPage.clickDecode();

            // Check token info section
            assertTrue(jwtPage.isTokenInfoSectionVisible());
            assertTrue(jwtPage.isAlgorithmLabelVisible());
            assertTrue(jwtPage.isTypeLabelVisible());
            assertTrue(jwtPage.isIssuedAtLabelVisible());
            assertTrue(jwtPage.isExpiresAtLabelVisible());
        }

        @Test
        void shouldShowSignatureSection() {
            jwtPage.fillToken(VALID_JWT);
            jwtPage.clickDecode();

            // Check signature section
            assertTrue(jwtPage.isSignatureSectionVisible());
            assertTrue(jwtPage.isSignatureLabelVisible());
            assertTrue(jwtPage.isSecretInputVisible());
        }

        @Test
        void shouldHandleExpiredToken() {
            jwtPage.fillToken(EXPIRED_JWT);
            jwtPage.clickDecode();

            // Should show expired status
            assertTrue(jwtPage.isExpiredStatusVisible());
        }

        @Test
        void shouldShowErrorForInvalidJwtFormat() {
            String invalidJWT = "not.a.valid.jwt";
            jwtPage.fillToken(invalidJWT);
            jwtPage.clickDecode();

            // Should show error message
            assertTrue(jwtPage.isInvalidMessageVisible());
        }

        @Test
        void shouldShowErrorForMalformedJwt() {
            String malformedJWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid_payload.signature";
            jwtPage.fillToken(malformedJWT);
            jwtPage.clickDecode();

            assertTrue(jwtPage.isInvalidBase64MessageVisible());
        }
    }

    @Nested
    class TokenClaims {
        @Test
        void shouldDisplayStandardClaims() {
            jwtPage.fillToken(VALID_JWT);
            jwtPage.clickDecode();

            // Check for standard claims in token info
            Locator tokenInfo = jwtPage.getTokenInfoSection();
            assertTrue(tokenInfo.textContent().contains("Subject"));
            assertTrue(tokenInfo.textContent().contains("Issuer"));
            assertTrue(tokenInfo.textContent().contains("Audience"));
        }

        @Test
        void shouldDisplayCustomClaimsInPayload() {
            jwtPage.fillToken(VALID_JWT);
            jwtPage.clickDecode();

            String payloadContent = jwtPage.getPayloadContent();
            assertTrue(payloadContent.contains("\"name\""));
            assertTrue(payloadContent.contains("\"admin\""));
        }
    }

    @Nested
    class UIControls {
        @Test
        void shouldLoadSampleJwt() {
            jwtPage.clickUseSample();

            String value = jwtPage.getTokenInput();

            assertTrue(value.contains("eyJ"));
            assertEquals(3, value.split("\\.").length);
        }

        @Test
        void shouldCopyHeaderToClipboard() {
            // Grant clipboard permissions
            context.grantPermissions(java.util.Arrays.asList("clipboard-read", "clipboard-write"));

            jwtPage.fillToken(VALID_JWT);
            jwtPage.clickDecode();

            // Click copy button for header
            jwtPage.clickCopyHeader();

            // Check for success indicator
            assertTrue(page.locator(".text-green-400").first().isVisible());
        }

        @Test
        void shouldCopyPayloadToClipboard() {
            // Grant clipboard permissions
            context.grantPermissions(java.util.Arrays.asList("clipboard-read", "clipboard-write"));

            jwtPage.fillToken(VALID_JWT);
            jwtPage.clickDecode();

            // Click copy button for payload
            jwtPage.clickCopyPayload();

            // Check for success indicator
            assertTrue(page.locator(".text-green-400").nth(1).isVisible());
        }
    }

    @Nested
    class SignatureVerification {
        @Test
        void shouldShowVerifySignatureButton() {
            jwtPage.fillToken(VALID_JWT);
            jwtPage.clickDecode();

            assertTrue(jwtPage.isVerifySignatureButtonVisible());
        }

        @Test
        void shouldRequireSecretForVerification() {
            jwtPage.fillToken(VALID_JWT);
            jwtPage.clickDecode();

            // Try to verify without secret
            jwtPage.clickVerifySignature();

            // Should show error about missing secret
            assertTrue(jwtPage.isProvideSecretMessageVisible());
        }

        @Test
        void shouldVerifySignatureWithSecret() {
            jwtPage.fillToken(VALID_JWT);
            jwtPage.clickDecode();

            // Enter secret
            jwtPage.fillSecret("test-secret");
            jwtPage.clickVerifySignature();

            // Should show verification result
            assertTrue(jwtPage.isValidOrInvalidSignatureVisible());
        }
    }

    @Nested
    class KeyboardShortcuts {
        @Test
        void shouldDecodeOnCtrlEnter() {
            jwtPage.fillToken(VALID_JWT);
            jwtPage.pressDecodeShortcut();

            // Check that decoding happened
            assertTrue(jwtPage.isHeaderSectionVisible());
            assertTrue(jwtPage.isPayloadSectionVisible());
        }
    }

    @Nested
    class ErrorHandling {
        @Test
        void shouldHandleEmptyInput() {
            jwtPage.clickDecode();
            assertTrue(jwtPage.isInvalidFormatMessageVisible());
        }

        @Test
        void shouldHandleJwtWithOnlyTwoParts() {
            String invalidJWT = "part1.part2";
            jwtPage.fillToken(invalidJWT);
            jwtPage.clickDecode();

            assertTrue(jwtPage.isThreePartsMessageVisible());
        }

        @Test
        void shouldHandleJwtWithInvalidBase64() {
            String invalidBase64JWT = "invalid!@#$.invalid!@#$.invalid!@#$";
            jwtPage.fillToken(invalidBase64JWT);
            jwtPage.clickDecode();

            assertTrue(jwtPage.isInvalidMessageVisible());
        }
    }

    @Nested
    class DifferentJwtTypes {
        @Test
        void shouldHandleJwtWithMinimalClaims() {
            String minimalJWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.Vg30C57s3l90JNap_VgMhKZjfc-p0fVP6jUxc8S5MqM";
            jwtPage.fillToken(minimalJWT);
            jwtPage.clickDecode();

            String payloadContent = jwtPage.getPayloadContent();
            assertTrue(payloadContent.contains("\"sub\": \"1234567890\""));
        }

        @Test
        void shouldHandleJwtWithNestedObjects() {
            String nestedJWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7Im5hbWUiOiJKb2huIiwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl19fQ.k5GwPcZvGe2BE-Lm9Y3neqJ3lPiT3RnLSw3AOFoVn_4";
            jwtPage.fillToken(nestedJWT);
            jwtPage.clickDecode();

            String payloadContent = jwtPage.getPayloadContent();
            assertTrue(payloadContent.contains("\"user\""));
            assertTrue(payloadContent.contains("\"roles\""));
        }

        @Test
        void shouldHandleJwtWithArrayClaims() {
            String arrayJWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6WyJhZG1pbiIsInVzZXIiLCJlZGl0b3IiXX0.bKy6otoNwRr2J4Y3zo9eXkQvK0rTmSU5BbQJDHmDL1A";
            jwtPage.fillToken(arrayJWT);
            jwtPage.clickDecode();

            String payloadContent = jwtPage.getPayloadContent();
            assertTrue(payloadContent.contains("[\"admin\", \"user\", \"editor\"]"));
        }
    }

    @Nested
    class TimeBasedClaims {
        @Test
        void shouldDisplayIssuedAtTime() {
            jwtPage.fillToken(VALID_JWT);
            jwtPage.clickDecode();

            String issuedAtText = jwtPage.getIssuedAtText();
            assertTrue(issuedAtText.matches(".*\\d{1,2}/\\d{1,2}/\\d{4}.*"));
        }

        @Test
        void shouldDisplayExpirationTime() {
            jwtPage.fillToken(VALID_JWT);
            jwtPage.clickDecode();

            String expiresAtText = jwtPage.getExpiresAtText();
            assertTrue(expiresAtText.matches(".*\\d{1,2}/\\d{1,2}/\\d{4}.*"));
        }

        @Test
        void shouldCalculateTimeUntilExpiration() {
            jwtPage.fillToken(VALID_JWT);
            jwtPage.clickDecode();

            // Should show expiration status
            assertTrue(jwtPage.isExpiresStatusVisible());
        }
    }

    @Nested
    class MobileResponsiveness {
        @Test
        void shouldBeResponsiveOnMobile() {
            page.setViewportSize(375, 667);

            assertTrue(jwtPage.isTitleVisible());
            assertTrue(page.locator("textarea").first().isVisible());
            assertTrue(jwtPage.isDecodeButtonVisible());

            // Test decoding on mobile
            jwtPage.fillToken(VALID_JWT);
            jwtPage.clickDecode();

            assertTrue(jwtPage.isHeaderSectionVisible());
            assertTrue(jwtPage.isPayloadSectionVisible());
        }

        @Test
        void shouldStackSectionsOnMobile() {
            page.setViewportSize(375, 667);

            jwtPage.fillToken(VALID_JWT);
            jwtPage.clickDecode();

            // Check that sections are visible on mobile
            assertTrue(page.locator("text=/Header/i").isVisible());
            assertTrue(page.locator("text=/Payload/i").isVisible());
        }
    }

    @Nested
    class Accessibility {
        @Test
        void shouldHaveProperLabels() {
            assertTrue(jwtPage.isTokenTextareaVisible());

            // Check for label
            assertTrue(jwtPage.isJwtTokenLabelVisible());
        }

        @Test
        void shouldBeKeyboardNavigable() {
            // Tab to sample button
            page.keyboard().press("Tab");
            page.keyboard().press("Tab");
            page.keyboard().press("Enter");

            String value = jwtPage.getTokenInput();
            assertTrue(value.contains("eyJ"));
        }
    }
}
