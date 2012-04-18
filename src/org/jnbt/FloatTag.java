package org.jnbt;

public final class FloatTag extends Tag
{
  private final float value;

  public FloatTag(String name, float value)
  {
    super(name);
    this.value = value;
  }

  public Float getValue()
  {
    return Float.valueOf(this.value);
  }

  public String toString()
  {
    String name = getName();
    String append = "";
    if ((name != null) && (!name.equals(""))) {
      append = "(\"" + getName() + "\")";
    }
    return "TAG_Float" + append + ": " + this.value;
  }
}
