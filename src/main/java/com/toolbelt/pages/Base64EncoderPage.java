package com.toolbelt.pages;

import com.microsoft.playwright.Page;

public class Base64EncoderPage extends BasePage {
    private final String INPUT_TEXTAREA = "textarea:nth-of-type(1)";
    private final String OUTPUT_TEXTAREA = "textarea:nth-of-type(2)";
    private final String MODE_ENCODE_BUTTON = "button:has-text('Encode')";
    private final String MODE_DECODE_BUTTON = "button:has-text('Decode')";
    private final String ENCODE_ACTION_BUTTON = "button:has-text('Encode'):nth-of-type(2)";
    private final String DECODE_ACTION_BUTTON = "button:has-text('Decode'):nth-of-type(2)";
    private final String SWAP_BUTTON = "button:has-text('Swap')";
    private final String COPY_BUTTON = "button:has-text('Copy')";
    private final String URL_SAFE_CHECKBOX = "text=/URL Safe/i";
    private final String H1_TITLE = "h1";
    private final String DESCRIPTION = "text=/Encode and decode Base64/i";
    private final String COPIED_INDICATOR = "text=/Copied/i";
    private final String INPUT_LABEL = "text=/Input/i";
    private final String OUTPUT_LABEL = "text=/Output/i";

    public Base64EncoderPage(Page page) {
        super(page);
    }

    public void fillInput(String text) {
        page.locator(INPUT_TEXTAREA).fill(text);
    }

    public void clickEncodeModeButton() {
        page.locator(MODE_ENCODE_BUTTON).first().click();
    }

    public void clickDecodeModeButton() {
        page.locator(MODE_DECODE_BUTTON).first().click();
    }

    public void clickEncodeAction() {
        page.locator(ENCODE_ACTION_BUTTON).click();
    }

    public void clickDecodeAction() {
        page.locator(DECODE_ACTION_BUTTON).click();
    }

    public void clickSwap() {
        page.locator(SWAP_BUTTON).click();
    }

    public void clickCopy() {
        page.locator(COPY_BUTTON).click();
    }

    public void checkUrlSafe() {
        page.check(URL_SAFE_CHECKBOX);
    }

    public String getOutput() {
        return page.locator(OUTPUT_TEXTAREA).inputValue();
    }

    public String getInput() {
        return page.locator(INPUT_TEXTAREA).inputValue();
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

    public boolean isInputTextareaVisible() {
        return page.locator(INPUT_TEXTAREA).isVisible();
    }

    public boolean isOutputTextareaVisible() {
        return page.locator(OUTPUT_TEXTAREA).isVisible();
    }

    public boolean isEncodeModeButtonVisible() {
        return page.locator(MODE_ENCODE_BUTTON).first().isVisible();
    }

    public boolean isDecodeModeButtonVisible() {
        return page.locator(MODE_DECODE_BUTTON).first().isVisible();
    }

    public boolean isCopiedIndicatorVisible() {
        return page.locator(COPIED_INDICATOR).isVisible();
    }

    public boolean isInputLabelVisible() {
        return page.locator(INPUT_LABEL).isVisible();
    }

    public boolean isOutputLabelVisible() {
        return page.locator(OUTPUT_LABEL).isVisible();
    }
}
