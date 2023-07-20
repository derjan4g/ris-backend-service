import { defineStore } from "pinia"
import { ref } from "vue"
import documentUnitService from "@/services/documentUnitService"

export type ValidationResponse = "Ok" | "Validation error"

export const useSingleNormValidationsStore = defineStore(
  "single-norm-validations",
  () => {
    const validations = ref<ValidationResponse[]>(["Ok"])

    async function getValidations(uuid: string): Promise<void> {
      const response = await documentUnitService.validateSingleNorms(uuid)
      if (response.data) validations.value = response.data
    }
    return { validations, getValidations }
  },
)
