package com.toolbelt.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class UrlEncoderPage extends BasePage {
    private final String INPUT_TEXTAREA = "textarea:nth-of-type(1)";
    private final String OUTPUT_TEXTAREA = "textarea:nth-of-type(2)";
    private final String MODE_ENCODE_BUTTON = "button:has-text('Encode')";
    private final String MODE_DECODE_BUTTON = "button:has-text('Decode')";
    private final String FULL_URL_BUTTON = "button:has-text('Full URL')";
    private final String COMPONENT_BUTTON = "button:has-text('Component')";
    private final String FORM_DATA_BUTTON = "button:has-text('Form Data')";
    private final String SWAP_BUTTON = "button:has-text('Swap')";
    private final String COPY_BUTTON = "button:has-text('Copy')";
    private final String SAMPLE_URL_BUTTON = "button:has-text('URL')";
    private final String SAMPLE_TEXT_BUTTON = "button:has-text('Text')";
    private final String SAMPLE_PARAMS_BUTTON = "button:has-text('Params')";
    private final String SAMPLE_PATH_BUTTON = "button:has-text('Path')";
    private final String SAMPLE_INTERNATIONAL_BUTTON = "button:has-text('International')";
    private final String H1_TITLE = "h1";
    private final String DESCRIPTION = "text=/Encode and decode URLs/i";
    private final String URL_COMPONENTS_SECTION = "text=/URL Components/i";
    private final String PROTOCOL_LABEL = "text=/Protocol/i";
    private final String HOST_LABEL = "text=/Host/i";
    private final String CHARACTER_REFERENCE_SECTION = "text=/Common Encodings|Character Reference/i";
    private final String PERCENT_20 = "text=/%20/i";
    private final String CHARACTERS_LABEL = "text=/characters/i";
    private final String SUCCESS_INDICATOR = ".text-green-400";
    private final String COPIED_TEXT = "text=/copied/i";

    public UrlEncoderPage(Page page) {
        super(page);
    }

    public void fillInput(String text) {
        page.locator(INPUT_TEXTAREA).fill(text);
    }

    public void clickEncode() {
        page.locator(MODE_ENCODE_BUTTON).first().click();
    }

    public void clickDecode() {
        page.locator(MODE_DECODE_BUTTON).first().click();
    }

    public void clickFullUrl() {
        if (page.locator(FULL_URL_BUTTON).count() > 0) {
            page.locator(FULL_URL_BUTTON).first().click();
        }
    }

    public void clickComponent() {
        if (page.locator(COMPONENT_BUTTON).count() > 0) {
            page.locator(COMPONENT_BUTTON).first().click();
        }
    }

    public void clickFormData() {
        if (page.locator(FORM_DATA_BUTTON).count() > 0) {
            page.locator(FORM_DATA_BUTTON).first().click();
        }
    }

    public void clickSwap() {
        if (page.locator(SWAP_BUTTON).count() > 0) {
            page.locator(SWAP_BUTTON).first().click();
        }
    }

    public void clickCopy() {
        if (page.locator(COPY_BUTTON).count() > 0) {
            page.locator(COPY_BUTTON).first().click();
        }
    }

    public void clickSampleUrl() {
        if (page.locator(SAMPLE_URL_BUTTON).count() > 0) {
            page.locator(SAMPLE_URL_BUTTON).first().click();
        }
    }

    public void clickAnySample() {
        Locator sampleButtons = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)url|text|params|path|international"));
        if (sampleButtons.count() > 0) {
            sampleButtons.first().click();
        }
    }

    public void waitForConversion() {
        page.waitForTimeout(300);
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

    public boolean isEncodeButtonVisible() {
        return page.locator(MODE_ENCODE_BUTTON).first().isVisible();
    }

    public boolean isDecodeButtonVisible() {
        return page.locator(MODE_DECODE_BUTTON).first().isVisible();
    }

    public boolean isUrlComponentsSectionVisible() {
        if (page.locator(URL_COMPONENTS_SECTION).count() > 0) {
            return page.locator(PROTOCOL_LABEL).isVisible() && page.locator(HOST_LABEL).isVisible();
        }
        return false;
    }

    public boolean isCharacterReferenceSectionVisible() {
        if (page.locator(CHARACTER_REFERENCE_SECTION).count() > 0) {
            return page.locator(CHARACTER_REFERENCE_SECTION).first().isVisible() &&
                   page.locator(PERCENT_20).first().isVisible();
        }
        return false;
    }

    public boolean isCharactersLabelVisible() {
        return page.locator(CHARACTERS_LABEL).first().isVisible();
    }

    public boolean isSuccessIndicatorVisible() {
        return page.locator(SUCCESS_INDICATOR).or(page.locator(COPIED_TEXT)).isVisible();
    }

    public boolean isFullUrlButtonVisible() {
        return page.locator(FULL_URL_BUTTON).count() > 0;
    }

    public boolean isComponentButtonVisible() {
        return page.locator(COMPONENT_BUTTON).count() > 0;
    }

    public boolean isFormDataButtonVisible() {
        return page.locator(FORM_DATA_BUTTON).count() > 0;
    }
}
