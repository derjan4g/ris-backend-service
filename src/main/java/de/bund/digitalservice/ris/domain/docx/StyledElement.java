package de.bund.digitalservice.ris.domain.docx;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class StyledElement implements DocumentUnitDocx {
  private final List<Style> styles = new ArrayList<>();

  public void addStyle(String property, String value) {
    styles.removeIf(style -> style.property().equals(property));
    styles.add(new Style(property, value));
  }

  public void addStyle(Style newStyle) {
    styles.removeIf(style -> style.property().equals(newStyle.property()));
    styles.add(newStyle);
  }

  public Boolean hasStyle() {
    return !styles.isEmpty();
  }

  public String getStyleString() {
    if (styles.isEmpty()) return "";

    return " style=\""
        + styles.stream().map(Style::toString).collect(Collectors.joining("; "))
        + ";\"";
  }
}
