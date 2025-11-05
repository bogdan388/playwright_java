package com.toolbelt.pages;

import com.microsoft.playwright.Page;

public class HashGeneratorPage extends BasePage {
    private final String INPUT_TEXTAREA = "textarea";
    private final String ALGORITHM_SELECT = "select";
    private final String HASH_OUTPUT = "[class*='hash']";
    private final String HASH_OUTPUT_CODE = "code";
    private final String HASH_OUTPUT_MONO = "div.font-mono";
    private final String COPY_BUTTON = "button[title*='Copy']";
    private final String COPY_BUTTON_TEXT = "button:has-text('Copy')";
    private final String SAMPLE_BUTTON = "button:has-text('Sample')";
    private final String BATCH_BUTTON = "button:has-text('All')";
    private final String VERIFY_CHECKBOX = "text=/Verify Mode/i";
    private final String VERIFY_INPUT = "input[placeholder*='hash']";
    private final String VERIFY_INPUT_ALT = "input[placeholder*='verify']";
    private final String VERIFY_BUTTON = "button:has-text('Verify')";
    private final String H1_TITLE = "h1";
    private final String MD5_TEXT = "text=/MD5/i";
    private final String SHA256_TEXT = "text=/SHA-256/i";
    private final String MATCH_VALID_TEXT = "text=/match|valid/i";
    private final String SUCCESS_INDICATOR = ".text-green-400";
    private final String COPIED_TEXT = "text=/copied/i";

    public HashGeneratorPage(Page page) {
        super(page);
    }

    public void fillInput(String text) {
        page.locator(INPUT_TEXTAREA).fill(text);
    }

    public void selectAlgorithm(String algorithm) {
        page.selectOption(ALGORITHM_SELECT, algorithm);
    }

    public void clickCopy() {
        if (page.locator(COPY_BUTTON).count() > 0) {
            page.locator(COPY_BUTTON).first().click();
        } else if (page.locator(COPY_BUTTON_TEXT).count() > 0) {
            page.locator(COPY_BUTTON_TEXT).first().click();
        }
    }

    public void clickSample() {
        if (page.locator(SAMPLE_BUTTON).count() > 0) {
            page.locator(SAMPLE_BUTTON).first().click();
        }
    }

    public void clickBatch() {
        if (page.locator(BATCH_BUTTON).count() > 0) {
            page.locator(BATCH_BUTTON).first().click();
        }
    }

    public void clickVerifyMode() {
        if (page.locator(VERIFY_CHECKBOX).count() > 0) {
            page.locator(VERIFY_CHECKBOX).first().click();
        }
    }

    public void fillVerifyInput(String hash) {
        if (page.locator(VERIFY_INPUT).count() > 0) {
            page.locator(VERIFY_INPUT).first().fill(hash);
        } else if (page.locator(VERIFY_INPUT_ALT).count() > 0) {
            page.locator(VERIFY_INPUT_ALT).first().fill(hash);
        }
    }

    public void clickVerify() {
        if (page.locator(VERIFY_BUTTON).count() > 0) {
            page.locator(VERIFY_BUTTON).first().click();
        }
    }

    public void waitForHashGeneration() {
        page.waitForTimeout(500);
    }

    public String getHashOutput() {
        if (page.locator(HASH_OUTPUT).count() > 0) {
            return page.locator(HASH_OUTPUT).first().textContent();
        } else if (page.locator(HASH_OUTPUT_CODE).count() > 0) {
            return page.locator(HASH_OUTPUT_CODE).first().textContent();
        } else if (page.locator(HASH_OUTPUT_MONO).count() > 0) {
            return page.locator(HASH_OUTPUT_MONO).first().textContent();
        }
        return "";
    }

    public String getInputValue() {
        return page.locator(INPUT_TEXTAREA).inputValue();
    }

    public boolean isTitleVisible() {
        return page.locator(H1_TITLE).isVisible();
    }

    public String getTitleText() {
        return page.locator(H1_TITLE).textContent();
    }

    public boolean isTextareaVisible() {
        return page.locator(INPUT_TEXTAREA).isVisible();
    }

    public boolean isMd5Visible() {
        return page.locator(MD5_TEXT).isVisible();
    }

    public boolean isSha256Visible() {
        return page.locator(SHA256_TEXT).isVisible();
    }

    public boolean isMatchValidVisible() {
        return page.locator(MATCH_VALID_TEXT).isVisible();
    }

    public boolean isSuccessIndicatorVisible() {
        return page.locator(SUCCESS_INDICATOR).or(page.locator(COPIED_TEXT)).isVisible();
    }

    public boolean isHashOutputVisible() {
        return page.locator(HASH_OUTPUT).or(page.locator(HASH_OUTPUT_CODE)).or(page.locator(HASH_OUTPUT_MONO)).isVisible();
    }
}
