package org.jnbt;

public final class LongTag extends Tag
{
  private final long value;

  public LongTag(String name, long value)
  {
    super(name);
    this.value = value;
  }

  public Long getValue()
  {
    return Long.valueOf(this.value);
  }

  public String toString()
  {
    String name = getName();
    String append = "";
    if ((name != null) && (!name.equals(""))) {
      append = "(\"" + getName() + "\")";
    }
    return "TAG_Long" + append + ": " + this.value;
  }
}
