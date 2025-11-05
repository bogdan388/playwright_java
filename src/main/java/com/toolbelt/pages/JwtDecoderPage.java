package com.toolbelt.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class JwtDecoderPage extends BasePage {
    private final String TOKEN_TEXTAREA = "textarea[placeholder*='JWT token']";
    private final String DECODE_BUTTON = "button:has-text('Decode Token')";
    private final String USE_SAMPLE_BUTTON = "button:has-text('Use Sample')";
    private final String COPY_HEADER_BUTTON = "button[title='Copy header']";
    private final String COPY_PAYLOAD_BUTTON = "button[title='Copy payload']";
    private final String SECRET_INPUT = "input[placeholder*='secret key']";
    private final String VERIFY_SIGNATURE_BUTTON = "button:has-text('Verify Signature')";
    private final String H1_TITLE = "h1";
    private final String DESCRIPTION = "text=/Decode and verify JSON Web Tokens/i";
    private final String HEADER_SECTION = "text=/Header/i";
    private final String PAYLOAD_SECTION = "text=/Payload/i";
    private final String TOKEN_INFO_SECTION = "text=/Token Information/i";
    private final String SIGNATURE_SECTION = "text=/Signature Verification/i";
    private final String ALGORITHM_LABEL = "text=/Algorithm:/i";
    private final String TYPE_LABEL = "text=/Type:/i";
    private final String ISSUED_AT_LABEL = "text=/Issued At:/i";
    private final String EXPIRES_AT_LABEL = "text=/Expires At:/i";
    private final String SIGNATURE_LABEL = "text=/Signature:/i";
    private final String EXPIRED_STATUS = "text=/Expired/i";
    private final String INVALID_MESSAGE = "text=/Invalid/i";
    private final String INVALID_BASE64_MESSAGE = "text=/Invalid base64/i";
    private final String INVALID_FORMAT_MESSAGE = "text=/Invalid JWT format/i";
    private final String THREE_PARTS_MESSAGE = "text=/should have 3 parts/i";
    private final String VALID_SIGNATURE = "text=/Valid Signature/i";
    private final String INVALID_SIGNATURE = "text=/Invalid Signature/i";
    private final String PROVIDE_SECRET_MESSAGE = "text=/provide a secret/i";
    private final String SUCCESS_INDICATOR = ".text-green-400";
    private final String BACK_TO_TOOLS = "text=/Back to Tools/i";
    private final String HEADER_PRE = "pre:nth-of-type(1)";
    private final String PAYLOAD_PRE = "pre:nth-of-type(2)";
    private final String JWT_TOKEN_LABEL = "text=/JWT Token/i";
    private final String EXPIRES_STATUS = "text=/Expires in|Expired/i";

    public JwtDecoderPage(Page page) {
        super(page);
    }

    public void fillToken(String token) {
        page.locator(TOKEN_TEXTAREA).fill(token);
    }

    public void clickDecode() {
        page.locator(DECODE_BUTTON).click();
    }

    public void clickUseSample() {
        page.locator(USE_SAMPLE_BUTTON).click();
    }

    public void clickCopyHeader() {
        page.locator(COPY_HEADER_BUTTON).click();
    }

    public void clickCopyPayload() {
        page.locator(COPY_PAYLOAD_BUTTON).click();
    }

    public void fillSecret(String secret) {
        page.fill(SECRET_INPUT, secret);
    }

    public void clickVerifySignature() {
        page.locator(VERIFY_SIGNATURE_BUTTON).click();
    }

    public void pressDecodeShortcut() {
        page.locator(TOKEN_TEXTAREA).press("Control+Enter");
    }

    public String getTokenInput() {
        return page.locator(TOKEN_TEXTAREA).inputValue();
    }

    public String getHeaderContent() {
        return page.locator(HEADER_PRE).textContent();
    }

    public String getPayloadContent() {
        return page.locator(PAYLOAD_PRE).textContent();
    }

    public String getIssuedAtText() {
        return page.locator(ISSUED_AT_LABEL).locator("..").textContent();
    }

    public String getExpiresAtText() {
        return page.locator(EXPIRES_AT_LABEL).locator("..").textContent();
    }

    public Locator getTokenInfoSection() {
        return page.locator(TOKEN_INFO_SECTION).locator("..");
    }

    public boolean isTitleVisible() {
        return page.locator(H1_TITLE).isVisible();
    }

    public String getTitleText() {
        return page.locator(H1_TITLE).textContent();
    }

    public boolean isDescriptionVisible() {
        return page.locator(DESCRIPTION).isVisible();
    }

    public boolean isTokenTextareaVisible() {
        return page.locator(TOKEN_TEXTAREA).isVisible();
    }

    public boolean isDecodeButtonVisible() {
        return page.locator(DECODE_BUTTON).isVisible();
    }

    public boolean isHeaderSectionVisible() {
        return page.locator(HEADER_SECTION).isVisible();
    }

    public boolean isPayloadSectionVisible() {
        return page.locator(PAYLOAD_SECTION).isVisible();
    }

    public boolean isTokenInfoSectionVisible() {
        return page.locator(TOKEN_INFO_SECTION).isVisible();
    }

    public boolean isSignatureSectionVisible() {
        return page.locator(SIGNATURE_SECTION).isVisible();
    }

    public boolean isAlgorithmLabelVisible() {
        return page.locator(ALGORITHM_LABEL).isVisible();
    }

    public boolean isTypeLabelVisible() {
        return page.locator(TYPE_LABEL).isVisible();
    }

    public boolean isIssuedAtLabelVisible() {
        return page.locator(ISSUED_AT_LABEL).isVisible();
    }

    public boolean isExpiresAtLabelVisible() {
        return page.locator(EXPIRES_AT_LABEL).isVisible();
    }

    public boolean isSignatureLabelVisible() {
        return page.locator(SIGNATURE_LABEL).isVisible();
    }

    public boolean isSecretInputVisible() {
        return page.locator(SECRET_INPUT).isVisible();
    }

    public boolean isVerifySignatureButtonVisible() {
        return page.locator(VERIFY_SIGNATURE_BUTTON).isVisible();
    }

    public boolean isExpiredStatusVisible() {
        return page.locator(EXPIRED_STATUS).isVisible();
    }

    public boolean isInvalidMessageVisible() {
        return page.locator(INVALID_MESSAGE).isVisible();
    }

    public boolean isInvalidBase64MessageVisible() {
        return page.locator(INVALID_BASE64_MESSAGE).isVisible();
    }

    public boolean isInvalidFormatMessageVisible() {
        return page.locator(INVALID_FORMAT_MESSAGE).isVisible();
    }

    public boolean isThreePartsMessageVisible() {
        return page.locator(THREE_PARTS_MESSAGE).isVisible();
    }

    public boolean isValidOrInvalidSignatureVisible() {
        return page.locator(VALID_SIGNATURE).or(page.locator(INVALID_SIGNATURE)).isVisible();
    }

    public boolean isProvideSecretMessageVisible() {
        return page.locator(PROVIDE_SECRET_MESSAGE).isVisible();
    }

    public boolean isSuccessIndicatorVisible() {
        return page.locator(SUCCESS_INDICATOR).isVisible();
    }

    public boolean isJwtTokenLabelVisible() {
        return page.locator(JWT_TOKEN_LABEL).isVisible();
    }

    public boolean isExpiresStatusVisible() {
        return page.locator(EXPIRES_STATUS).isVisible();
    }

    public void clickBackToTools() {
        page.click(BACK_TO_TOOLS);
    }
}
