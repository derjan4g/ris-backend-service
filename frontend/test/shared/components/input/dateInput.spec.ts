import userEvent from "@testing-library/user-event"
import { render, screen } from "@testing-library/vue"
import { nextTick } from "vue"
import DateInput from "@/shared/components/input/DateInput.vue"
import { ValidationError } from "@/shared/components/input/types"

function renderComponent(options?: {
  ariaLabel?: string
  isFutureDate?: boolean
  value?: string
  modelValue?: string
  placeholder?: string
  validationError?: ValidationError
}) {
  const user = userEvent.setup()
  const props = {
    id: "identifier",
    value: options?.value,
    modelValue: options?.modelValue,
    ariaLabel: options?.ariaLabel ?? "aria-label",
    isFutureDate: options?.isFutureDate ?? false,
    placeholder: options?.placeholder,
    validationError: options?.validationError,
  }
  const utils = render(DateInput, { props })
  return { user, props, ...utils }
}

describe("DateInput", () => {
  it("shows an date input element", () => {
    renderComponent()
    const input = screen.queryByLabelText("aria-label") as HTMLInputElement

    expect(input).toBeInTheDocument()
    expect(input?.type).toBe("text")
  })

  it("shows input with an aria label", () => {
    renderComponent({
      ariaLabel: "test-label",
    })
    const input = screen.queryByLabelText("test-label") as HTMLInputElement

    expect(input).toBeInTheDocument()
  })

  it("allows to type date inside input", async () => {
    renderComponent()
    const input = screen.queryByLabelText("aria-label") as HTMLInputElement

    await userEvent.type(input, "12.05.2020")

    expect(input).toHaveValue("12.05.2020")
  })

  it("displays modelValue in correct format", async () => {
    renderComponent({ modelValue: "2022-05-13T18:08:14.036Z" })
    const input = screen.queryByLabelText("aria-label") as HTMLInputElement

    expect(input).toHaveValue("13.05.2022")
  })

  it("emits model update event when input completed and valid", async () => {
    const { emitted } = renderComponent({
      modelValue: "2022-05-13T18:08:14.036Z",
    })
    const input: HTMLInputElement = screen.queryByLabelText(
      "aria-label",
    ) as HTMLInputElement
    expect(input).toHaveValue("13.05.2022")
    await userEvent.clear(input)
    await userEvent.type(input, "14.05.2022")
    await nextTick()

    expect(input).toHaveValue("14.05.2022")

    expect(emitted()["update:modelValue"]).toEqual([
      [undefined],
      ["2022-05-14T00:00:00.000Z"],
    ])
  })

  it("removes validation errors on backspace delete", async () => {
    const { emitted } = renderComponent({
      modelValue: "2022-05-13T18:08:14.036Z",
    })
    const input: HTMLInputElement = screen.queryByLabelText(
      "aria-label",
    ) as HTMLInputElement
    expect(input).toHaveValue("13.05.2022")
    await userEvent.type(input, "{backspace}")

    expect(emitted()["update:validationError"]).toBeTruthy()
  })

  it("does not allow dates in the future", async () => {
    const { emitted } = renderComponent()
    const input: HTMLInputElement = screen.queryByLabelText(
      "aria-label",
    ) as HTMLInputElement
    expect(input).toHaveValue("")
    await userEvent.type(input, "14.05.2099")
    await nextTick()

    expect(input).toHaveValue("14.05.2099")

    expect(emitted()["update:modelValue"]).not.toBeTruthy()

    expect(emitted()["update:validationError"]).toBeTruthy()

    const array = emitted()["update:validationError"] as ValidationError[][]

    expect(
      array.filter((element) => element[0] !== undefined)[0][0].message,
    ).toBe("Das Datum darf nicht in der Zukunft liegen")
  })

  it("it allows dates in the future if flag is set", async () => {
    const { emitted } = renderComponent({ isFutureDate: true })
    const input: HTMLInputElement = screen.queryByLabelText(
      "aria-label",
    ) as HTMLInputElement
    expect(input).toHaveValue("")
    await userEvent.type(input, "14.05.2099")
    await nextTick()

    expect(input).toHaveValue("14.05.2099")

    expect(emitted()["update:modelValue"]).toBeTruthy()
  })

  it("does not allow invalid dates", async () => {
    const { emitted } = renderComponent()
    const input: HTMLInputElement = screen.queryByLabelText(
      "aria-label",
    ) as HTMLInputElement
    await userEvent.type(input, "29.02.2001")
    await nextTick()

    expect(input).toHaveValue("29.02.2001")

    expect(emitted()["update:modelValue"]).not.toBeTruthy()

    expect(emitted()["update:validationError"]).toBeTruthy()

    const array = emitted()["update:validationError"] as ValidationError[][]

    expect(
      array.filter((element) => element[0] !== undefined)[0][0].message,
    ).toBe("Kein valides Datum")
  })

  it("does not allow letters", async () => {
    renderComponent()
    const input = screen.queryByLabelText("aria-label") as HTMLInputElement

    await userEvent.type(input, "AB.CD.EFGH")
    await nextTick()

    expect(input).toHaveValue("")
  })

  it("does not allow incomplete dates", async () => {
    const { emitted } = renderComponent()
    const input = screen.queryByLabelText("aria-label") as HTMLInputElement

    await userEvent.type(input, "03")
    await userEvent.type(input, "{tab}")
    await nextTick()

    expect(emitted()["update:modelValue"]).not.toBeTruthy()
    expect(emitted()["update:validationError"]).toEqual([
      [
        {
          message: "Unvollständiges Datum",
          instance: "identifier",
        },
      ],
    ])
  })
})
