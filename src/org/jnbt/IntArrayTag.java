package org.jnbt;

public final class IntArrayTag extends Tag
{
  private final int[] value;

  public IntArrayTag(String name, int[] value)
  {
    super(name);
    this.value = value;
  }

  public int[] getValue()
  {
    return this.value;
  }

  public String toString()
  {
    StringBuilder hex = new StringBuilder();
    for (int b : this.value) {
      String hexDigits = Integer.toHexString(b).toUpperCase();
      if (hexDigits.length() == 1) {
        hex.append("0");
      }
      hex.append(hexDigits).append(" ");
    }
    String name = getName();
    String append = "";
    if ((name != null) && (!name.equals(""))) {
      append = new StringBuilder().append("(\"").append(getName()).append("\")").toString();
    }
    return new StringBuilder().append("TAG_Int_Array").append(append).append(": ").append(hex.toString()).toString();
  }
}
