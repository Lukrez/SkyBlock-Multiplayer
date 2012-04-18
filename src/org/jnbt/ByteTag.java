package org.jnbt;

public final class ByteTag extends Tag
{
  private final byte value;

  public ByteTag(String name, byte value)
  {
    super(name);
    this.value = value;
  }

  public Byte getValue()
  {
    return Byte.valueOf(this.value);
  }

  public String toString()
  {
    String name = getName();
    String append = "";
    if ((name != null) && (!name.equals(""))) {
      append = "(\"" + getName() + "\")";
    }
    return "TAG_Byte" + append + ": " + this.value;
  }
}
