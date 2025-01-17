import { CitationStyle } from "@/domain/citationStyle"
import DocumentationOffice from "@/domain/documentationOffice"
import { Court, DocumentType, Procedure } from "@/domain/documentUnit"
import { FieldOfLawNode } from "@/domain/fieldOfLaw"
import { NormAbbreviation } from "@/domain/normAbbreviation"
import { LabelPosition } from "@/shared/components/input/InputField.vue"

export enum InputType {
  TEXT = "text",
  FILE = "file",
  DROPDOWN = "dropdown",
  DATE = "date",
  CHECKBOX = "checkbox",
  RADIO = "radio",
  CHIPS = "chips",
  DATECHIPS = "datechips",
  NESTED = "nested",
  COMBOBOX = "combobox",
  TEXTAREA = "textarea",
  DATE_TIME = "date_time",
  YEAR = "year",
  TIME = "time",
  UNDEFINED_DATE = "undefined_date",
}

//BASE
export interface BaseInputAttributes {
  ariaLabel: string
  validationError?: ValidationError
  labelPosition?: LabelPosition
}

export interface BaseInputField {
  name: string
  type: InputType
  label: string
  required?: boolean
  inputAttributes: BaseInputAttributes
}

//TEXT
export type TextInputModelType = string

export interface TextInputAttributes extends BaseInputAttributes {
  placeholder?: string
  readOnly?: boolean
  maxlength?: string
  autofocus?: boolean
}

export interface TextInputField extends BaseInputField {
  type: InputType.TEXT
  inputAttributes: TextInputAttributes
}

//CHIPS
export type ChipsInputModelType = string[]

export interface ChipsInputAttributes extends BaseInputAttributes {
  placeholder?: string
  readOnly?: boolean
}

export interface ChipsInputField extends BaseInputField {
  type: InputType.CHIPS
  inputAttributes: ChipsInputAttributes
}

export interface DateChipsInputField extends BaseInputField {
  type: InputType.DATECHIPS
  inputAttributes: ChipsInputAttributes
}

//NESTED INPUT
export interface NestedInputModelType {
  fields: {
    parent: ModelType
    child: ModelType
  }
}

export interface NestedInputAttributes extends BaseInputAttributes {
  fields: { parent: InputField; child: InputField }
}

export interface NestedInputField extends Omit<BaseInputField, "name"> {
  name: `nestedInputOf${Capitalize<string>}And${Capitalize<string>}`
  type: InputType.NESTED
  inputAttributes: NestedInputAttributes
}

//DATE
export interface DateAttributes extends BaseInputAttributes {
  isFutureDate?: boolean
}

export interface DateInputField extends BaseInputField {
  placeholder?: string
  type: InputType.DATE
  inputAttributes: DateAttributes
}

export type DateInputModelType = string | undefined

//DROPDOWN
export type DropdownInputModelType = string

export type DropdownItem = {
  label: string
  value: DropdownInputModelType
}

export interface DropdownAttributes extends BaseInputAttributes {
  placeholder?: string
  items: DropdownItem[]
}

export interface DropdownInputField extends BaseInputField {
  type: InputType.DROPDOWN
  inputAttributes: DropdownAttributes
}

//COMBOBOX
export type ComboboxInputModelType =
  | DocumentType
  | Court
  | NormAbbreviation
  | FieldOfLawNode
  | CitationStyle
  | Procedure

export type ComboboxItem = {
  label: string
  value?: ComboboxInputModelType
  additionalInformation?: string
}

export interface ComboboxAttributes extends BaseInputAttributes {
  itemService: (filter?: string) => Promise<{
    status: number
    data?: ComboboxItem[]
    error?: {
      title: string
      description?: string
      validationErrors?: ValidationError[]
    }
  }>
  placeholder?: string
  manualEntry?: boolean
  noClear?: boolean
}

export interface ComboboxInputField extends BaseInputField {
  type: InputType.COMBOBOX
  inputAttributes: ComboboxAttributes
}

//CHECKBOX
export type BooleanModelType = boolean

export type CheckboxInputModelType = boolean

export interface CheckboxInputField extends BaseInputField {
  type: InputType.CHECKBOX
  inputAttributes: BaseInputAttributes
}

//TEXTAREA
export type TextaraInputModelType = string

export interface TextaraInputAttributes extends BaseInputAttributes {
  placeholder?: string
  readOnly?: boolean
  autosize?: boolean
  rows?: number
}

export interface TextaraInputField extends BaseInputField {
  type: InputType.TEXTAREA
  inputAttributes: TextaraInputAttributes
}

export type InputField =
  | TextInputField
  | DropdownInputField
  | DateInputField
  | CheckboxInputField
  | ChipsInputField
  | DateChipsInputField
  | NestedInputField
  | ComboboxInputField
  | TextaraInputField

export type InputAttributes =
  | TextInputAttributes
  | DropdownAttributes
  | ChipsInputAttributes
  | NestedInputAttributes
  | DateAttributes
  | ComboboxAttributes
  | TextaraInputAttributes

export type ModelType =
  | TextInputModelType
  | DateInputModelType
  | DropdownInputModelType
  | BooleanModelType
  | CheckboxInputModelType
  | ChipsInputModelType
  | NestedInputModelType
  | ComboboxInputModelType
  | DocumentationOffice
  | TextaraInputModelType

export type ValidationError = {
  code?: string
  message: string
  instance: string
}
