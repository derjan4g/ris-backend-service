import { defineStore } from "pinia"
import { ref } from "vue"
import DocumentUnit from "@/domain/documentUnit"
import documentUnitService from "@/services/documentUnitService"

export type ValidationResponse = "Ok" | "Validation error"

export const useSingleNormValidationsStore = defineStore(
  "single-norm-validations",
  () => {
    const validations = ref<ValidationResponse[]>()
    const documentUnitUuid = ref<DocumentUnit["uuid"]>()

    async function getValidations(uuid?: string): Promise<void> {
      const response = await documentUnitService.validateSingleNorms(
        uuid ?? (documentUnitUuid.value as string),
      )

      if (response.data) {
        validations.value = response.data
        documentUnitUuid.value = uuid
      }
    }

    return { validations, getValidations }
  },
)
