import { expect, Page } from "@playwright/test"
import { openNorm, saveNormFrame } from "./e2e-utils"
import { getNormBySections, testWithImportedNorm } from "./fixtures"
import { newNorm } from "./testdata/norm_edited_fields"
import { normEmptyMandatoryFields } from "./testdata/norm_empty_mandatory_fields"
import { AnyField, fillInputField, fillMetadataInputSection } from "./utilities"

async function isValidatonErrorPresent(
  page: Page,
  id: string,
  isPresent: boolean,
) {
  const error = page.locator(`div[data-testid='${id}-validationError']`)
  if (isPresent) {
    await expect(error).toBeVisible()
    await expect(error).toHaveText("Feld muss befüllt sein")
  } else {
    await expect(error).toBeHidden()
  }
}

async function validateNormFrame(page: Page) {
  await page.locator("[aria-label='Daten prüfen']:not(:disabled)").click()
  await expect(
    page.locator("[aria-label='Daten prüfen']:not(:disabled)"),
  ).toBeVisible()
}

testWithImportedNorm(
  "Check if missing mandatory fields are correctly validated",
  async ({ page, guid }) => {
    await openNorm(page, guid)
    const locatorFrameButton = page.locator("a:has-text('Rahmen')")
    await expect(locatorFrameButton).toBeVisible()

    await locatorFrameButton.click()
    await expect(page).toHaveURL(`/norms/norm/${guid}/frame`)

    const sectionsWithMandatoryFields = getNormBySections(
      normEmptyMandatoryFields,
    )
    const sectionsToEdit = getNormBySections(newNorm)

    for (const section of sectionsWithMandatoryFields) {
      await fillMetadataInputSection(page, section)
    }

    await validateNormFrame(page)
    for (const section of sectionsWithMandatoryFields) {
      const mandatoryFields = (section.fields ?? []).filter(
        (field: AnyField) => field.isMandatory,
      )
      for (const field of mandatoryFields) {
        await isValidatonErrorPresent(page, field.id, true)
        const sectionFromEdit = sectionsToEdit.find(
          (sectionToEdit) => sectionToEdit.id === section.id,
        )
        const fieldFromEdit = sectionFromEdit?.fields?.find(
          (fieldToEdit) => fieldToEdit.id === field.id,
        )
        await fillInputField(
          page,
          field.type,
          field.id,
          fieldFromEdit?.value ?? "",
        )
      }
    }

    await saveNormFrame(page)
    await page.reload()

    await validateNormFrame(page)
    for (const section of sectionsWithMandatoryFields) {
      const mandatoryFields = (section.fields ?? []).filter(
        (field) => field.isMandatory,
      )
      for (const field of mandatoryFields) {
        await isValidatonErrorPresent(page, field.id, false)
      }
    }
  },
)
