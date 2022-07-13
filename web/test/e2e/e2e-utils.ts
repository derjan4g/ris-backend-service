import { expect } from "@playwright/test"
import { Browser, Page } from "playwright"

export const authenticate = async (browser: Browser) => {
  browser.newContext({
    httpCredentials: {
      username: process.env.STAGING_USER ?? "",
      password: process.env.STAGING_PASSWORD ?? "",
    },
  })
}

export const generateDocUnit = async (page: Page): Promise<string> => {
  await page.goto("/")
  await page.locator("button >> text=Neue Dokumentationseinheit").click()
  await page.waitForSelector("text=Festplatte durchsuchen")

  await expect(page).toHaveURL(/\/jurisdiction\/docunit\/[A-Z0-9]{14}\/files$/)

  const regex = /jurisdiction\/docunit\/(.*)\/files/g
  const match = regex.exec(page.url())
  if (match) {
    return match[1]
  } else {
    throw new Error("Could not get DocumentNumber")
  }
}

export const deleteDocUnit = async (page: Page, documentNumber: string) => {
  await page.goto("/")
  await page
    .locator("tr", {
      hasText: documentNumber,
    })
    .locator("[aria-label='Dokumentationseinheit löschen']")
    .click()
}

export const navigateToCategories = async (
  page: Page,
  documentNumber: string
) => {
  await page.goto("/")
  await page
    .locator(`a[href*="/jurisdiction/docunit/${documentNumber}/files"]`)
    .click()

  await page
    .locator(
      `a[href*="/jurisdiction/docunit/${documentNumber}/categories"] >> text=Rubriken`
    )
    .click()
}
