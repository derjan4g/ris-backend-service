import { expect } from "@playwright/test"
import { navigateToCategories, waitForSaving } from "../../e2e-utils"
import { testWithDocumentUnit as test } from "../../fixtures"

test.describe("court", () => {
  test("input value in court field, press enter and reload", async ({
    page,
    documentNumber,
  }) => {
    await navigateToCategories(page, documentNumber)

    await page.locator("[aria-label='Gericht']").fill("BGH")

    await expect(page.locator("[aria-label='dropdown-option']")).toHaveCount(1)

    await page.keyboard.press("ArrowDown")
    await page.keyboard.press("Enter")

    await waitForSaving(page)

    await page.reload()

    expect(await page.inputValue("[aria-label='Gericht']")).toBe("BGH")
  })

  test("open incorrect court field, input one, save and reload", async ({
    page,
    documentNumber,
  }) => {
    await navigateToCategories(page, documentNumber)

    await page.locator("[aria-label='Fehlerhaftes Gericht anzeigen']").click()

    await page
      .locator("[aria-label='Fehlerhaftes Gericht']")
      .fill("incorrectCourt1")
    await page.keyboard.press("Enter")

    await page.locator("[aria-label='Stammdaten Speichern Button']").click()
    await waitForSaving(page)

    await page.reload()

    await page.locator("[aria-label='Fehlerhaftes Gericht anzeigen']").click()

    await expect(page.locator("text=IncorrectCourt1").first()).toBeVisible()
  })

  test("open incorrect court field, input two, save, reload, remove first, save and reload", async ({
    page,
    documentNumber,
  }) => {
    await navigateToCategories(page, documentNumber)

    await page.locator("[aria-label='Fehlerhaftes Gericht anzeigen']").click()

    await page
      .locator("[aria-label='Fehlerhaftes Gericht']")
      .fill("incorrectCourt1")
    await page.keyboard.press("Enter")
    await page
      .locator("[aria-label='Fehlerhaftes Gericht']")
      .fill("incorrectCourt2")
    await page.keyboard.press("Enter")

    await waitForSaving(page)

    await page.reload()

    await page.locator("[aria-label='Fehlerhaftes Gericht anzeigen']").click()

    await expect(page.locator("text=IncorrectCourt1")).toBeVisible()
    await expect(page.locator("text=IncorrectCourt2")).toBeVisible()

    await page
      .locator(":text('IncorrectCourt1') + div > [aria-label='Löschen']")
      .click()

    await waitForSaving(page)

    await page.reload()

    await page.locator("[aria-label='Fehlerhaftes Gericht anzeigen']").click()

    await expect(page.locator("text=IncorrectCourt1")).toHaveCount(0)
    await expect(page.locator("text=IncorrectCourt2")).toBeVisible()
  })

  test("test court dropdown", async ({ page, documentNumber }) => {
    await navigateToCategories(page, documentNumber)
    const totalCourts = 3925

    // on start: closed dropdown, no input text
    expect(await page.inputValue("[aria-label='Gericht']")).toBe("")
    await expect(page.locator("text=AG Aachen")).toBeHidden()
    await expect(page.locator("[aria-label='dropdown-option']")).toBeHidden()

    // open dropdown
    await page
      .locator("[aria-label='Gericht'] + button.input-expand-icon")
      .click()
    await expect(page.locator("[aria-label='dropdown-option']")).toHaveCount(
      totalCourts
    )
    await expect(page.locator("text=AG Aachen")).toBeVisible()
    await expect(page.locator("text=AG Aalen")).toBeVisible()

    // type search string: 2 results for "bayern"
    await page.locator("[aria-label='Gericht']").fill("bayern")
    expect(await page.inputValue("[aria-label='Gericht']")).toBe("bayern")
    await expect(page.locator("[aria-label='dropdown-option']")).toHaveCount(2)

    // use the clear icon
    await page.locator("[aria-label='Auswahl zurücksetzen']").click()
    expect(await page.inputValue("[aria-label='Gericht']")).toBe("")
    await expect(page.locator("[aria-label='dropdown-option']")).toHaveCount(
      totalCourts
    )

    // close dropdown
    await page
      .locator("[aria-label='Gericht'] + button.input-expand-icon")
      .click()
    expect(await page.inputValue("[aria-label='Gericht']")).toBe("")
    await expect(page.locator("[aria-label='dropdown-option']")).toBeHidden()

    // open dropdown again by typing a search string
    await page.locator("[aria-label='Gericht']").fill("bayern")
    await expect(page.locator("[aria-label='dropdown-option']")).toHaveCount(2)
    // first search result displays a revoked string
    await expect(page.locator("text=aufgehoben seit: 1973")).toBeVisible()

    // close dropdown using the esc key, user input text gets removed and last saved value restored
    await page.keyboard.down("Escape")
    await expect(page.locator("[aria-label='dropdown-option']")).toBeHidden()
    expect(await page.inputValue("[aria-label='Gericht']")).toBe("")
  })

  test("test correct esc/tab behaviour in court dropdown", async ({
    page,
    documentNumber,
  }) => {
    await navigateToCategories(page, documentNumber)

    expect(await page.inputValue("[aria-label='Gericht']")).toBe("")
    await expect(page.locator("[aria-label='dropdown-option']")).toBeHidden()

    await page.locator("[aria-label='Gericht']").fill("BVerfG")
    await page.locator("text=BVerfG").click()

    await expect(page.locator("[aria-label='dropdown-option']")).toBeHidden()
    await expect(page.locator("[aria-label='Gericht']")).toHaveValue("BVerfG")

    await page.locator("[aria-label='Gericht']").fill("BGH")

    await expect(page.locator("[aria-label='dropdown-option']")).toHaveCount(1)

    await page.keyboard.press("Escape") // reset to last saved value

    await expect(page.locator("[aria-label='dropdown-option']")).toBeHidden()
    await expect(page.locator("[aria-label='Gericht']")).toHaveValue("BVerfG")

    await page.locator("[aria-label='Gericht']").fill("BGH")
    await page.keyboard.press("Tab") // reset to last saved value

    await expect(page.locator("[aria-label='dropdown-option']")).toBeHidden()
    await expect(page.locator("[aria-label='Gericht']")).toHaveValue("BVerfG")
  })

  test("test that setting a court sets the region automatically", async ({
    page,
    documentNumber,
  }) => {
    await navigateToCategories(page, documentNumber)

    await page.locator("[aria-label='Gericht']").fill("aalen")

    // clicking on dropdown item triggers auto save
    await page.locator("text=AG Aalen").click()
    expect(await page.inputValue("[aria-label='Gericht']")).toBe("AG Aalen")

    // saving... and then saved
    await waitForSaving(page)
    await expect(page.locator("text=Region")).toBeVisible()

    // region was set by the backend based on state database table
    expect(await page.inputValue("[aria-label='Region']")).toBe(
      "Baden-Württemberg"
    )

    // clear the court
    await page.locator("[aria-label='Auswahl zurücksetzen']").click()
    await expect(page.getByText("AG Aalen")).toBeHidden()
    expect(await page.inputValue("[aria-label='Gericht']")).toBe("")
    // dropdown should not open
    await expect(page.locator("[aria-label='dropdown-option']")).toBeHidden()

    await waitForSaving(page)
    await expect(page.locator("text=Region")).toBeVisible()

    // region was cleared by the backend
    expect(await page.inputValue("[aria-label='Region']")).toBe("")
  })

  test("test that setting a special court sets legal effect to yes, but it can be changed afterwards", async ({
    page,
    documentNumber,
  }) => {
    await navigateToCategories(page, documentNumber)

    expect(await page.inputValue("[aria-label='Rechtskraft']")).toBe(
      "Keine Angabe"
    )

    await page.locator("[aria-label='Gericht']").fill("bgh")
    await page.locator("text=BGH").click()
    expect(await page.inputValue("[aria-label='Gericht']")).toBe("BGH")
    await waitForSaving(page)

    expect(await page.inputValue("[aria-label='Rechtskraft']")).toBe("Ja")

    await page
      .locator("[aria-label='Rechtskraft'] + button.input-expand-icon")
      .click()

    await page.locator("text=Nein").click()
    expect(await page.inputValue("[aria-label='Rechtskraft']")).toBe("Nein")

    await waitForSaving(page)

    expect(await page.inputValue("[aria-label='Rechtskraft']")).toBe("Nein")
  })

  test("test that setting a non-special court leaves legal effect unchanged", async ({
    page,
    documentNumber,
  }) => {
    await navigateToCategories(page, documentNumber)

    expect(await page.inputValue("[aria-label='Rechtskraft']")).toBe(
      "Keine Angabe"
    )

    await page.locator("[aria-label='Gericht']").fill("aachen")
    await page.locator("text=AG Aachen").click()
    expect(await page.inputValue("[aria-label='Gericht']")).toBe("AG Aachen")
    await waitForSaving(page)

    await page.reload() // TODO remove reload when update via response.data works
    expect(await page.inputValue("[aria-label='Rechtskraft']")).toBe(
      "Keine Angabe"
    )
  })
})