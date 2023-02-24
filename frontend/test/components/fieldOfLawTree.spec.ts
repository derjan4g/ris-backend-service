/* eslint-disable jest-dom/prefer-in-document */
import userEvent from "@testing-library/user-event"
import { render, screen } from "@testing-library/vue"
import FieldOfLawTreeVue from "@/components/FieldOfLawTree.vue"
import { FieldOfLawNode } from "@/domain/fieldOfLawTree"
import FieldOfLawService from "@/services/fieldOfLawService"

function renderComponent(
  options: {
    selectedSubjects?: FieldOfLawNode[]
  } = {}
) {
  return render(FieldOfLawTreeVue, {
    props: {
      selectedSubjects: options.selectedSubjects ?? [],
      clickedSubjectFieldNumber: "",
    },
  })
}

describe("SubjectTree", () => {
  const user = userEvent.setup()

  const fetchSpy = vi
    .spyOn(FieldOfLawService, "getChildrenOf")
    .mockImplementation(() =>
      Promise.resolve({
        status: 200,
        data: [
          {
            subjectFieldNumber: "AB-01",
            subjectFieldText: "Text for AB",
            children: [],
            depth: 1,
            isExpanded: false,
            isLeaf: false,
          },
          {
            subjectFieldNumber: "CD-02",
            subjectFieldText: "And text for CD with link to AB-01",
            children: [],
            linkedFields: ["AB-01"],
            depth: 1,
            isExpanded: false,
            isLeaf: false,
          },
        ],
      })
    )

  it("Tree is fully closed upon at start", async () => {
    renderComponent()
    expect(fetchSpy).toBeCalledTimes(0)
    expect(screen.getByText("Alle Sachgebiete anzeigen")).toBeInTheDocument()
    const expandIcons = screen.getAllByLabelText("Sachgebietsbaum aufklappen")
    expect(expandIcons).toHaveLength(1)
    expect(screen.queryByText("Text for AB")).not.toBeInTheDocument()
    expect(screen.queryByText("And text for CD")).not.toBeInTheDocument()
  })

  it("Tree opens top level nodes upon root click", async () => {
    renderComponent()

    await user.click(
      screen.getAllByLabelText("Sachgebietsbaum aufklappen")[0] as HTMLElement
    )

    const expandIcons = screen.getAllByLabelText("Sachgebietsbaum aufklappen")

    expect(fetchSpy).toBeCalledTimes(1)
    expect(expandIcons).toHaveLength(3)
    expect(screen.getByText("Text for AB")).toBeInTheDocument()
    expect(screen.getByText("And text for CD with link to")).toBeInTheDocument()
    expect(screen.getByText("Alle Sachgebiete anzeigen")).toBeInTheDocument()
  })

  it("Linked node gets displayed as link in stext", async () => {
    renderComponent()

    await user.click(
      screen.getAllByLabelText("Sachgebietsbaum aufklappen")[0] as HTMLElement
    )

    const node1ids = screen.getAllByText("AB-01")
    const nonLinkText = screen.getByText("And text for CD with link to")

    expect(node1ids).toHaveLength(2)
    expect(node1ids[1] as HTMLElement).toHaveAttribute("class", "linked-field")
    expect(nonLinkText as HTMLElement).not.toHaveAttribute(
      "class",
      "linked-field"
    )
    // expect(emitted()["linkedField:clicked"]).toHaveLength(1)
  })
})