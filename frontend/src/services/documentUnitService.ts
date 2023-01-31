import DocumentUnit from "../domain/documentUnit"
import { DocumentUnitListEntry } from "../domain/documentUnitListEntry"
import httpClient, {
  ServiceResponse,
  FailedValidationServerResponse,
} from "./httpClient"

interface DocumentUnitService {
  getAllListEntries(): Promise<ServiceResponse<DocumentUnitListEntry[]>>
  getAllListEntriesFlux(
    onFluxUpdate: (listEntries: DocumentUnitListEntry[]) => void
  ): void
  getByDocumentNumber(
    documentNumber: string
  ): Promise<ServiceResponse<DocumentUnit>>
  createNew(
    docCenter: string,
    docType: string
  ): Promise<ServiceResponse<DocumentUnit>>
  update(documentUnit: DocumentUnit): Promise<ServiceResponse<unknown>>
  delete(documentUnitUuid: string): Promise<ServiceResponse<unknown>>
}

const service: DocumentUnitService = {
  async getAllListEntriesFlux(
    onFluxUpdate: (listEntries: DocumentUnitListEntry[]) => void
  ) {
    const backendHost = process.env.BACKEND_HOST ?? ""
    const response = await fetch(`${backendHost}/api/v1/caselaw/documentunits`)
    if (!response.body) return
    const stream = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ""

    while (true) {
      const { done, value } = await stream.read()
      if (done) break
      buffer += decoder.decode(value)
      // console.log(buffer)
      try {
        // The buffer-buildup looks like this:
        // [{...}
        // [{...},{...}
        // [{...},{...},{...}]
        // That's why we can only parse it as array during the loop if we append a closing bracket.
        // Once it fully arrived, the closing bracket also came along, so we must not append it in the last step.
        const jsonData = JSON.parse(buffer + (buffer.endsWith("]") ? "" : "]"))
        onFluxUpdate(jsonData)
      } catch (e) {}
    }
  },

  async getAllListEntries() {
    const response = await httpClient.get<DocumentUnitListEntry[]>(
      "caselaw/documentunits"
    )
    if (response.status >= 300) {
      response.error = {
        title: "Dokumentationseinheiten konnten nicht geladen werden.",
      }
    }
    return response
  },

  async getByDocumentNumber(documentNumber: string) {
    const response = await httpClient.get<DocumentUnit>(
      `caselaw/documentunits/${documentNumber}`
    )
    if (response.status >= 300 || response.error) {
      response.error = {
        title: "Dokumentationseinheit konnten nicht geladen werden.",
      }
    } else {
      response.data = new DocumentUnit(response.data.uuid, { ...response.data })
    }
    return response
  },

  async createNew(docCenter: string, docType: string) {
    const response = await httpClient.post<Partial<DocumentUnit>, DocumentUnit>(
      "caselaw/documentunits",
      {
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json",
        },
      },
      JSON.stringify({
        documentationCenterAbbreviation: docCenter,
        documentType: docType,
      }) as Partial<DocumentUnit>
    )
    if (response.status >= 300) {
      response.error = {
        title: "Neue Dokumentationseinheit konnte nicht erstellt werden.",
      }
    }
    return response
  },

  async update(documentUnit: DocumentUnit) {
    const response = await httpClient.put(
      `caselaw/documentunits/${documentUnit.uuid}`,
      {
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json",
        },
      },
      JSON.stringify(documentUnit)
    )
    if (response.status >= 300) {
      response.error = {
        title: "Dokumentationseinheit konnte nicht aktualisiert werden.",
      }
      // good enough condition to detect validation errors (@Valid)?
      if (
        response.status == 400 &&
        JSON.stringify(response.data).includes("Validation failed")
      ) {
        response.error.validationErrors = (
          response.data as FailedValidationServerResponse
        ).errors
      }
    }
    return response
  },

  async delete(documentUnitUuid: string) {
    const response = await httpClient.delete(
      `caselaw/documentunits/${documentUnitUuid}`
    )
    if (response.status >= 300) {
      response.error = {
        title: "Dokumentationseinheit konnte nicht gelöscht werden.",
      }
    }
    return response
  },
}

export default service
